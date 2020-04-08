package com.emika.app.presentation.chat;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.emika.app.data.db.callback.chat.MessagesDbCallback;
import com.emika.app.data.db.dbmanager.ChatDbManager;
import com.emika.app.data.db.entity.MessageEntity;
import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.ChatApi;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.ModelChat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ItemDataSource extends PageKeyedDataSource<Integer, Message> implements MessagesDbCallback {

    public static final int LIMIT = 50;
    private static final int OFFSET = 0;
    private static final String SITE_NAME = "stackoverflow";
    private static final String TAG = "ItemDataSource";
    private String token;
    private ChatDbManager chatDbManager;
    private List<Message> messages;

    public ItemDataSource(String token) {
        this.token = token;
        chatDbManager = new ChatDbManager();
        messages = new ArrayList<>();
    }

    public void update(Message message) {
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Message> callback) {


        chatDbManager.getAllMessages(this);
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.fetchAllMessage(token, OFFSET, LIMIT);
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                if (response.body() != null) {
                    List<Message> messageList = response.body().getPayload().getMessages();
                    List<MessageEntity> messageEntities = new ArrayList<>();
                    for (Message message : messageList) {
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
                        messageEntities.add(messageEntity);
                    }
                    callback.onResult(messageList, null, OFFSET + 25);
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ModelChat> call, Throwable t) {
            }
        });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Message> callback) {


    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Message> callback) {
        chatDbManager.getAllMessages(this);
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.fetchAllMessage(token, params.key, LIMIT);
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                if (response.body() != null) {
                    List<Message> messageList = response.body().getPayload().getMessages();
                    List<MessageEntity> messageEntities = new ArrayList<>();
                    for (Message message : messageList) {
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
                        messageEntities.add(messageEntity);
                    }

                    Integer key = (params.key > 1) ? params.key + 25 : null;
                    callback.onResult(messageList, key);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelChat> call, Throwable t) {
            }
        });
    }

    @Override
    public void onMessagesLoaded(List<MessageEntity> messageEntities) {
        for (MessageEntity message:
             messageEntities) {
            Message message1 = new Message(
                    message.getId(),
                    message.getType(),
                    message.getEmika(),
                    message.getAccountId(),
                    message.getText(),
                    message.getDelay(),
                    message.getChainPosition(),
                    message.getPassword(),
                    message.getSeen(),
                    message.getCreatedAt(),
                    message.getUpdatedAt());
                messages.add(message1);
        }
        Log.d(TAG, "onMessagesLoaded: " + messageEntities.size());
    }
}
