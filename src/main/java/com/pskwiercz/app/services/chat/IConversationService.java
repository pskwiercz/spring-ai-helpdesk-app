package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatMessageDto;

public interface IConversationService {

    String handleChatMessage(ChatMessageDto msg);

}
