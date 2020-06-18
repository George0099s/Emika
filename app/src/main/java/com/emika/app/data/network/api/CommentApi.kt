package com.emika.app.data.network.api

import com.emika.app.data.network.pojo.comment.CommentDeleted
import com.emika.app.data.network.pojo.comment.ModelComment
import retrofit2.Call
import retrofit2.http.*

interface CommentApi {
    @POST("public_api/tasks/{task_id}/comments/{comment_id}/delete")
    fun deleteComment(
            @Path("comment_id") commentId: String,
            @Path("task_id") taskId: String,
            @Query("token") token: String): Call<CommentDeleted>

    @FormUrlEncoded
    @POST("public_api/tasks/{task_id}/comments/{comment_id}/update")
    fun updateComment(
            @Path("task_id") taskId: String,
            @Path("comment_id") commentId: String,
            @Query("token") token: String,
            @Field("text") text: String): Call<ModelComment>


    @FormUrlEncoded
    @POST("public_api/tasks/{task_id}/comment")
    fun createComment(
            @Path("task_id") taskId: String,
            @Query("token") token: String,
            @Field("text") name: String): Call<ModelComment>
}