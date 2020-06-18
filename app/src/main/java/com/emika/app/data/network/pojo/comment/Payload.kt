package com.emika.app.data.network.pojo.comment


import com.google.gson.annotations.SerializedName

data class Payload(
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