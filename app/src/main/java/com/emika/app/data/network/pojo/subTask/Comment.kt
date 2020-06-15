package com.emika.app.data.network.pojo.subTask


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("task_id")
    val taskId: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("updated_at")
    val updatedAt: String
)