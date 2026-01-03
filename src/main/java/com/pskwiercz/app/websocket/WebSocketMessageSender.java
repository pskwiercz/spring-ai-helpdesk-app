package com.pskwiercz.app.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketMessageSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUser(String sessionId, String message) {
        String destination = "/topic/message/" + sessionId;
        messagingTemplate.convertAndSend(destination, message);
    }
}
