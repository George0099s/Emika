package com.emika.app.data.network.callback.calendar

import com.emika.app.data.network.pojo.comment.CommentDeleted
import com.emika.app.data.network.pojo.comment.ModelComment

interface CommentCallback {
    fun onCommentCreated(model: ModelComment)
    fun onCommentUpdated(model: ModelComment)
    fun onCommentDeleted(model: CommentDeleted)
}