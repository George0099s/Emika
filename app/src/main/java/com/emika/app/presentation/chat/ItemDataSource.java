package com.emika.app.presentation.chat;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.ChatApi;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.ModelChat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ItemDataSource extends PageKeyedDataSource<Integer, Message> {

    public static final int LIMIT = 50;
    private static final int OFFSET = 0;
    private static final String SITE_NAME = "stackoverflow";
    private String token;
    private static final String TAG = "ItemDataSource";

    public ItemDataSource(String token) {
        this.token = token;
    }
    public void update(Message message){
    }
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Message> callback) {

        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.fetchAllMessage(token, OFFSET, LIMIT);
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                callback.onResult(response.body().getPayload().getMessages(), null, OFFSET +25);
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

        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        ChatApi service = retrofit.create(ChatApi.class);
        Call<ModelChat> call = service.fetchAllMessage(token, params.key, LIMIT);
        call.enqueue(new Callback<ModelChat>() {
            @Override
            public void onResponse(retrofit2.Call<ModelChat> call, Response<ModelChat> response) {
                if (response.body() != null) {
                    Integer key = (params.key > 1) ? params.key + 25 : null;
                    callback.onResult(response.body().getPayload().getMessages(), key);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelChat> call, Throwable t) {
            }
        });


    }
}
