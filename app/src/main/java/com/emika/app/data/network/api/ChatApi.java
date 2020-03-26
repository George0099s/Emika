package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.chat.ModelChat;
import com.emika.app.data.network.pojo.task.Model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApi {

    @GET("public_api/chat/fetch")
    Call<ModelChat> fetchAllMessage(@Query("token") String token,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit);
    @FormUrlEncoded
    @POST("public_api/chat/send")
    Call<ModelChat> sendMessage(@Query("token") String token,
                                    @Field("text") String text);

    
}
