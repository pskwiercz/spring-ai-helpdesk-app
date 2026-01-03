package com.pskwiercz.app.controller;

import com.pskwiercz.app.dto.ChatMessageDTO;
import com.pskwiercz.app.services.chat.IConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IConversationService conversationService;

    @PostMapping
    public ResponseEntity<String> handleChatMessage(@RequestBody ChatMessageDTO msg) {
        String response = conversationService.handleChatMessage(msg);
        log.info("Controller response: {} ", response);
        return ResponseEntity.ok(response);
    }
}
