package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.task.Model;
import com.emika.app.data.network.pojo.task.ModelTask;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CalendarApi {
    @GET("public_api/tasks/fetch")
    Call<Model> fetchAllTasks(@Query("token") String token);

    @FormUrlEncoded
    @POST("public_api/tasks/{task_id}/update")
    Call<ModelTask> updateTask(@Path("task_id") String taskId,
                               @Query("token") String token,
                               @Field("plan_date") String planDate,
                               @Field("name") String taskName,
                               @Field("project_id") String projectId,
                               @Field("deadline_date") String deadlineDate,
                               @Field("duration") int estimatedTime,
                               @Field("actual_duration") int spentTime,
                               @Field("status") String status,
                               @Field("description") String description,
                               @Field("priority") String priority);

    @FormUrlEncoded
    @POST("public_api/tasks/create")
    Call<ModelTask> addTask(@Query("token") String token,
                            @Field("name") String name,
                            @Field("project_id") String projectId,
                            @Field("plan_date") String planDate,
                            @Field("deadline_date") String deadlineDate,
                            @Field("assignee") String assignee,
                            @Field("duration") String estimatedTime,
                            @Field("description") String description,
                            @Field("priority") String priority,
                            @Field("section_id") String sectionId);


}
