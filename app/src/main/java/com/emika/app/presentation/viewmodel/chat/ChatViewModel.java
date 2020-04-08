package com.emika.app.presentation.viewmodel.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.emika.app.data.db.dbmanager.ChatDbManager;
import com.emika.app.data.db.entity.MessageEntity;
import com.emika.app.data.network.callback.chat.MessagesCallback;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.PayloadChat;
import com.emika.app.domain.repository.chat.ChatRepository;
import com.emika.app.presentation.chat.ItemDataSource;
import com.emika.app.presentation.chat.ItemDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel implements MessagesCallback {
    private static final String TAG = "ChatViewModel";
    LiveData<PagedList<Message>> itemPagedList;
    List<Message> itemPagedList2;
    LiveData<PageKeyedDataSource<Integer, Message>> liveDataSource;
    private MutableLiveData<PayloadChat> messageMutableLiveData;
    private String token;
    private ChatRepository repository;
    private ItemDataSource itemDataSource;

    public ChatViewModel(String token) {
        this.token = token;
        repository = new ChatRepository(token);
        messageMutableLiveData = new MutableLiveData<>();
        itemPagedList2 = new ArrayList<>();
        ItemDataSourceFactory itemDataSourceFactory = new ItemDataSourceFactory(token);
        liveDataSource = itemDataSourceFactory.getItemLiveDataSource();
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(ItemDataSource.LIMIT)
                        .build();
        itemPagedList = (new LivePagedListBuilder(itemDataSourceFactory, config)).build();
    }

    public LiveData<PagedList<Message>> getItemPagedList() {
        return itemPagedList;
    }

    public MutableLiveData<PayloadChat> getMessageMutableLiveData(int offset, int limit) {
        repository.getAllMessages(this, offset, limit);
        return messageMutableLiveData;
    }


    public void sendMessage(Message message) {
        repository.sendMessage(message);
    }

    public void updateMessage(Message message) {
        ChatDbManager chatDbManager = new ChatDbManager();

        MessageEntity messageEntity = new MessageEntity(
                message.getId(),
                message.getType(),
                message.getIsEmika(),
                message.getAccountId(),
                message.getText(),
                message.getCreatedAt(),
                message.getChainPosition(),
                message.getDelay(),
                message.getIsPassword(),
                message.getIsSeen(),
                message.getUpdatedAt()
        );
        chatDbManager.insertMessage(messageEntity, null);
        itemPagedList2.add(0, message);
        liveDataSource.getValue().invalidate();
//        Objects.requireNonNull(liveDataSource.getValue()).invalidate();
    }

    @Override
    public void onMessagesLoaded(PayloadChat chat) {
        messageMutableLiveData.postValue(chat);
    }
}
