package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatEntry;
import com.pskwiercz.app.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService implements IConversationService {

    private final AISupportService aiSupportService;
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

        return aiResponseText;
    }
}
