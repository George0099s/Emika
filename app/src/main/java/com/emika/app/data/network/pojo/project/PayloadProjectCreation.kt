package com.emika.app.data.network.pojo.project


import com.google.gson.annotations.SerializedName

data class PayloadProjectCreation(
    @SerializedName("color")
    val color: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("default_duration")
    val defaultDuration: Int,
    @SerializedName("default_epic_links")
    val defaultEpicLinks: List<Any>,
    @SerializedName("default_priority")
    val defaultPriority: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("is_company_wide")
    val isCompanyWide: Boolean,
    @SerializedName("is_personal")
    val isPersonal: Boolean,
    @SerializedName("managers")
    val managers: List<String>,
    @SerializedName("members")
    val members: List<String>,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String
)