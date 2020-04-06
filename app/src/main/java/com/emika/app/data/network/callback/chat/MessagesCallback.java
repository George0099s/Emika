package com.emika.app.data.network.callback.chat;

import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.PayloadChat;
import com.emika.app.data.network.pojo.chat.Suggestion;

import java.util.List;

public interface MessagesCallback {
    void onMessagesLoaded(PayloadChat chat);
}
