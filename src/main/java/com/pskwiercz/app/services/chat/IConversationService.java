package com.pskwiercz.app.services.chat;

import com.pskwiercz.app.dto.ChatMessageDTO;

public interface IConversationService {

    String handleChatMessage(ChatMessageDTO msg);

}
