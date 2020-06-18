package com.emika.app.data.network.pojo.comment


import com.emika.app.data.network.pojo.subTask.Comment
import com.google.gson.annotations.SerializedName

data class ModelComment(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("payload")
    val payload: Comment
)