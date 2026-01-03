package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatEntryDTO;
import com.pskwiercz.app.dto.ChatMessageDTO;
import com.pskwiercz.app.helper.UserInfo;
import com.pskwiercz.app.helper.UserInfoHelper;
import com.pskwiercz.app.model.Conversation;
import com.pskwiercz.app.model.Ticket;
import com.pskwiercz.app.model.User;
import com.pskwiercz.app.repository.ConversationRepository;
import com.pskwiercz.app.repository.UserRepository;
import com.pskwiercz.app.services.ticket.ITicketService;
import com.pskwiercz.app.websocket.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.pskwiercz.app.util.PromptTemplates.USER_INFORMATION_ERROR_PROMPT_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService implements IConversationService {

    private final AISupportService aiSupportService;
    private final ITicketService iTicketService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final WebSocketMessageSender webSocketMessageSender;

    private final Map<String, List<ChatEntryDTO>> activeConversations = new ConcurrentHashMap<>();

    @Override
    public String handleChatMessage(ChatMessageDTO chatMessage) {
        String sessionId = chatMessage.sessionId();
        String useMessage = chatMessage.message() != null ? chatMessage.message().trim() : "";
        log.info("Session Id: {} Content: {}", sessionId, useMessage);

        List<ChatEntryDTO> history = activeConversations.computeIfAbsent(sessionId,
                k -> Collections.synchronizedList(new ArrayList<>()));
        history.add(new ChatEntryDTO("user", useMessage));

        String aiResponseText;
        try {
            aiResponseText = aiSupportService.chatWithHistory(history).block();
        } catch (Exception e) {
            aiResponseText = "Sorry, I'm having trouble processing your request right now.";
        }
        if (aiResponseText == null) {
            return "";
        }
        history.add(new ChatEntryDTO("assistant", aiResponseText));

        if (aiResponseText.contains("TICKET_CREATION_READY")) {
            try {
                String confirmationMsg = aiSupportService.generateUserConfirmationMessage().block();
                history.add(new ChatEntryDTO("assistant", confirmationMsg));
                CompletableFuture.runAsync(() -> {
                    try {
                        Ticket tempTicket = finalizeConversationAndCreateTicket(sessionId);
                        if (tempTicket != null) {
                            history.add(new ChatEntryDTO("system", "The email notification has been sent."));
                            // Ask the AI to generate th email message and send to the customer
                            String feedbackMessage = aiSupportService.generateEmailNotificationMessage().block();

                            if (feedbackMessage != null) {
                                List<ChatEntryDTO> currentHistory = activeConversations.get(sessionId);
                                if (currentHistory != null) {
                                    currentHistory.add(new ChatEntryDTO("assistant", feedbackMessage));
                                }
                                webSocketMessageSender.sendMessageToUser(sessionId, feedbackMessage);
                            }

                        }

                    } catch (Exception e) {
                        // log any error here
                    }

                });
                return confirmationMsg;
            } catch (Exception e) {
                return "There is a problem with processing your request: " + e.getMessage();
            }
        }

        return aiResponseText;
    }

    private Ticket finalizeConversationAndCreateTicket(String sessionId) {
        List<ChatEntryDTO> history = activeConversations.get(sessionId);
        User user = getCustomerInformation(history);
        log.info("This is the user information : {}", user);

        if (user == null) {
            // Send the message to the user via Websocket
            webSocketMessageSender.sendMessageToUser(sessionId, USER_INFORMATION_ERROR_PROMPT_TEMPLATE);
            if (history != null) {
                history.add(new ChatEntryDTO("user", USER_INFORMATION_ERROR_PROMPT_TEMPLATE));
            }
            return null;
        }

        Conversation conversation = getConversation(user);
        try {
            List<ChatEntryDTO> userConversation = history
                    .stream()
                    .filter(entry -> "user".equals(entry.role()))
                    .toList();

            String conversationSummary = aiSupportService.summarizeUserConversation(userConversation.toString()).block();
            log.info("*************The conversation summary : {}", conversationSummary);
            String conversationTitle = aiSupportService.generateConversationTitle(conversationSummary).block();
            log.info("*************The conversation title : {}", conversationTitle);

            conversation.setConversationTitle(conversationTitle != null ? conversationTitle.trim() : "Untitled Conversation");
            conversation.setConversationSummary(conversationSummary);
            Conversation savedConversation = conversationRepository.save(conversation);

            //Create and save the ticket for the conversation
            Ticket savedTicket = iTicketService.createTicketForConversation(conversation);

            savedConversation.setTicket(savedTicket);
            savedConversation.setTicketCreated(true);
            conversationRepository.save(savedConversation);

            //Send notification email to the customer
            //Remove the customer conversation from the memory
            activeConversations.remove(sessionId);
            return savedTicket;

        } catch (Exception e) {
            String errorMsg = "Error occurred during conversation creation." + e.getMessage();
            webSocketMessageSender.sendMessageToUser(sessionId, errorMsg);
            return null;
        }

    }

    private User getCustomerInformation(List<ChatEntryDTO> history) {
        UserInfo userInfo = UserInfoHelper.extractUserInformationFromChatHistory(history);
        log.info("Here is the customer information : {}", userInfo);
        return userRepository.findByEmailAddressAndPhoneNumber(
                userInfo.emailAddress(), userInfo.phoneNumber());
    }

    private static Conversation getConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTicketCreated(false);
        return conversation;
    }
}
