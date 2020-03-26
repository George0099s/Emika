package com.emika.app.data.network.callback.chat;

import com.emika.app.data.network.pojo.chat.Message;

import java.util.List;

public interface MessagesCallback {
    void onMessagesLoaded(List<Message> messages);
}
