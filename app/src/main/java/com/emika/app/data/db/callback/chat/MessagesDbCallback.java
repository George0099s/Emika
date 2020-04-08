package com.emika.app.data.db.callback.chat;

import com.emika.app.data.db.entity.MessageEntity;

import java.util.List;

public interface MessagesDbCallback {
    void onMessagesLoaded(List<MessageEntity> messageEntities);
}
