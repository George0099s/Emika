package com.emika.app.data.network.pojo.invites


import com.google.gson.annotations.SerializedName

data class PayloadInvite(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("invite_code")
    val inviteCode: String,
    @SerializedName("invite_sent_at")
    val inviteSentAt: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("projects")
    val projects: List<Any>,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String
)