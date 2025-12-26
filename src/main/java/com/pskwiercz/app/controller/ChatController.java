package com.pskwiercz.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final String SYSTEM_PROMPT = """
            You are a helpful agent. Your goal is to listen to the user question
             and provide a response
            """;

    @GetMapping
    public String chat(@RequestParam String msg) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(msg)
                .call()
                .content();
    }
}
