package com.emika.app.data.network.pojo.invites


import com.google.gson.annotations.SerializedName

data class InviteModel(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("payload")
    val payload: Boolean
)