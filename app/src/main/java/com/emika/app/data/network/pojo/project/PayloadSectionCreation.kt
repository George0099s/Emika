package com.emika.app.data.network.pojo.project


import com.google.gson.annotations.SerializedName

data class PayloadSectionCreation(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: Int,
    @SerializedName("project_id")
    val projectId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String
) {
}