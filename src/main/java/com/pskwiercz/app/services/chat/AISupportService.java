package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatEntry;
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

import static com.pskwiercz.app.util.PromptTemplates.SUPPORT_PROMPT_TEMPLATE;
import static com.pskwiercz.app.util.PromptTemplates.USER_CONFIRMATION_PROMPT_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AISupportService {

    private final ChatClient chatClient;

    public Mono<String> chatWithHistory(List<ChatEntry> history) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SUPPORT_PROMPT_TEMPLATE));

        for (ChatEntry chatEntry : history) {
            switch (chatEntry.role()) {
                case "user":
                    messages.add(new UserMessage(chatEntry.content()));
                    break;
                case "assistant":
                    messages.add(new AssistantMessage(chatEntry.content()));
                    break;
                default: {
                    log.info("Role isn't supported: {}", chatEntry.role());
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
                throw new IllegalStateException("Content is null");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
