package com.emika.app.domain.repository.chat;

import com.emika.app.data.network.callback.chat.MessagesCallback;
import com.emika.app.data.network.networkManager.chat.ChatNetworkManager;
import com.emika.app.data.network.pojo.chat.Message;

public class ChatRepository {
    private String token;
    private ChatNetworkManager chatNetworkManager;
    public ChatRepository(String token) {
        this.token = token;
        chatNetworkManager = new ChatNetworkManager(token);
    }

    public void getAllMessages(MessagesCallback callback, int offset, int limit) {
        chatNetworkManager.getAllMessages(callback, offset, limit);
    }

    public void sendMessage(Message message) {
        chatNetworkManager.sendMessage(message);
    }
}
