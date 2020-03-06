package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.task.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CalendarApi {
    @GET("public_api/tasks/fetch")
    Call<Model> fetchAllTasks(@Query("token") String token);
}
