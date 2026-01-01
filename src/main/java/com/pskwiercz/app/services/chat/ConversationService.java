package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatEntry;
import com.pskwiercz.app.dto.ChatMessageDto;
import com.pskwiercz.app.helper.UserInfo;
import com.pskwiercz.app.helper.UserInfoHelper;
import com.pskwiercz.app.model.Ticket;
import com.pskwiercz.app.model.User;
import com.pskwiercz.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pskwiercz.app.util.PromptTemplates.USER_INFORMATION_ERROR_PROMPT_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService implements IConversationService {

    private final AISupportService aiSupportService;
    private final UserRepository userRepository;

    private final Map<String, List<ChatEntry>> activeConversations = new ConcurrentHashMap<>();

    @Override
    public String handleChatMessage(ChatMessageDto chatMessage) {
        String sessionId = chatMessage.sessionId();
        String useMessage = chatMessage.message() != null ? chatMessage.message().trim() : "";
        log.info("Session Id: {} Content: {}", sessionId, useMessage);

        List<ChatEntry> history = activeConversations.computeIfAbsent(sessionId,
                k -> Collections.synchronizedList(new ArrayList<>()));
        history.add(new ChatEntry("user", useMessage));

        String aiResponseText;
        try {
            aiResponseText = aiSupportService.chatWithHistory(history).block();
        } catch (Exception e) {
            aiResponseText = "Sorry, I'm having trouble processing your request right now.";
        }
        if (aiResponseText == null) {
            return "";
        }
        history.add(new ChatEntry("assistant", aiResponseText));

        if (aiResponseText.contains("TICKET_CREATION_READY")) {
            try {
                String confirmationMsg = aiSupportService.generateUserConfirmationMessage().block();
                history.add(new ChatEntry("assistant", confirmationMsg));
                Ticket tempTicket = finalizeConversationAndCreateTicket(sessionId);
                log.info("Ticket : {}", tempTicket);
                return confirmationMsg;
            } catch (Exception e) {
                return "There is a problem with processing your request: " + e.getMessage();
            }
        }

        return aiResponseText;
    }

    private Ticket finalizeConversationAndCreateTicket(String sessionId) {
        List<ChatEntry> history = activeConversations.get(sessionId);
        User user = getCustomerInformation(history);
        log.info("This is the user information : {}", user);
        if (user == null) {
            //Here we are going to send the message to the user. (Websocket)
            if (history != null) {
                history.add(new ChatEntry("user", USER_INFORMATION_ERROR_PROMPT_TEMPLATE));
            }
            return null;
        }
        return null;

    }

    private User getCustomerInformation(List<ChatEntry> history) {
        UserInfo userInfo = UserInfoHelper.extractUserInformationFromChatHistory(history);
        log.info("Here is the customer information : {}", userInfo);
        return userRepository.findByEmailAddressAndPhoneNumber(
                userInfo.emailAddress(), userInfo.phoneNumber());
    }

}
