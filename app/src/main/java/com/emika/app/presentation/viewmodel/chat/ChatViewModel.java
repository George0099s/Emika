package com.emika.app.presentation.viewmodel.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.chat.MessagesCallback;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.domain.repository.chat.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel implements MessagesCallback {
    private MutableLiveData<List<Message>> messageMutableLiveData;

    private String token;
    private ChatRepository repository;
    public ChatViewModel(String token) {
        repository = new ChatRepository(token);
        messageMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Message>> getMessageMutableLiveData(int offset, int limit) {
        repository.getAllMessages(this, offset, limit);
        return messageMutableLiveData;
    }

    @Override
    public void onMessagesLoaded(List<Message> messages) {
        messageMutableLiveData.postValue(messages);
    }

    public void sendMessage(Message message) {
        repository.sendMessage(message);
    }
}
