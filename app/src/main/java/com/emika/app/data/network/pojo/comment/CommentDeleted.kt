package com.emika.app.data.network.pojo.comment


import com.google.gson.annotations.SerializedName

data class CommentDeleted(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("payload")
    val payload: Boolean
)