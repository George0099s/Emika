package com.emika.app.data.network.networkManager.chat;

import android.util.Log;

import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.ChatApi;
import com.emika.app.data.network.callback.chat.MessagesCallback;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.ModelChat;
import com.emika.app.data.network.pojo.chat.PayloadChat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatNetworkManager {
    private static final String TAG = "ChatNetworkManager";
    private String token;
    private NetworkService networkService = NetworkService.getInstance();
    public ChatNetworkManager(String token) {
        this.token = token;
    }

    public void getAllMessages(MessagesCallback callback, int offset, int limit)  {
        Retrofit retrofit = networkService.getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.fetchAllMessage(token, offset, limit);
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                if (response.body() != null) {
                    ModelChat model = response.body();
                    PayloadChat payloadChat = model.getPayload();
                    if (payloadChat != null)
                        callback.onMessagesLoaded(payloadChat);
                    else callback.onMessagesLoaded(new PayloadChat());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelChat> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void sendMessage(Message message)  {
        Retrofit retrofit = networkService.getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.sendMessage(token, message.getText());
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                if (response.body() != null) {
                    ModelChat model = response.body();
//                    PayloadChat payloadChat = model.getPayload();
//                    List<Message> messages = payloadChat.getMessages();
//                    if (messages != null)
//                        callback.onMessagesLoaded(messages);
//                    else callback.onMessagesLoaded(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelChat> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

}
