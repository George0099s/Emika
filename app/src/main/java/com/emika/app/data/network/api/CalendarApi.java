package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.task.Model;
import com.emika.app.data.network.pojo.task.PayloadTask;

import org.json.JSONObject;

import java.sql.SQLTransactionRollbackException;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CalendarApi {
    @GET("public_api/tasks/fetch")
    Call<Model> fetchAllTasks(@Query("token") String token);


    @POST("public_api/tasks/{task_id}/update")
    Call<Model> updateTask(@Query("token") String token,
                           @Path("task_id") String taskId);

    @FormUrlEncoded
    @POST("public_api/tasks/create")
    Call<Model> addTask(@Query("token") String token,
                           @Field("task") JSONObject task);



}
