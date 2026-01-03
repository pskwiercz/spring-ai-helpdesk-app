package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatEntryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.pskwiercz.app.util.PromptTemplates.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AISupportService {

    private final ChatClient chatClient;

    public Mono<String> chatWithHistory(List<ChatEntryDTO> history) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SUPPORT_PROMPT_TEMPLATE));

        for (ChatEntryDTO chatEntryDTO : history) {
            switch (chatEntryDTO.role()) {
                case "user":
                    messages.add(new UserMessage(chatEntryDTO.content()));
                    break;
                case "assistant":
                    messages.add(new AssistantMessage(chatEntryDTO.content()));
                    break;
                case "system":
                    messages.add(new SystemMessage(chatEntryDTO.content()));
                    break;
                default: {
                    log.info("Role isn't supported: {}", chatEntryDTO.role());
                }
            }
        }

        return Mono.fromCallable(() -> {
            String content = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();
            if (content == null) {
                throw new IllegalStateException("Content is null");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> generateUserConfirmationMessage() {
        String prompt = String.format(USER_CONFIRMATION_PROMPT_TEMPLATE);
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));

        return Mono.fromCallable(() -> {
            String content = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();
            if (content == null) {
                throw new IllegalStateException("AI response content is null");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> summarizeUserConversation(String userConversationText) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(CUSTOMER_CONVERSATION_SUMMARY_PROMPT));
        messages.add(new UserMessage(userConversationText));
        return Mono.fromCallable(() -> {
            ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                    .messages(messages)
                    .call();
            String content = responseSpec.content();
            if (content == null) {
                throw new IllegalStateException("AI response content is null");
            }
            return content.trim();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> generateConversationTitle(String summarizedConversation) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(TITLE_GENERATION_PROMPT));
        messages.add(new SystemMessage(summarizedConversation));
        return Mono.fromCallable(() -> {
            ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                    .messages(messages)
                    .call();
            String content = responseSpec.content();
            if (content == null) {
                throw new IllegalStateException("AI response content is null");
            }
            return content.trim();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> generateEmailNotificationMessage() {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(EMAIL_NOTIFICATION_PROMPT));
        return Mono.fromCallable(() -> {
            ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                    .messages(messages)
                    .call();
            String content = responseSpec.content();
            if (content == null) {
                throw new IllegalStateException("AI response content is null");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
