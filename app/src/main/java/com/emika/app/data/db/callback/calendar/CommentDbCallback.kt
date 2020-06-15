package com.emika.app.data.db.callback.calendar

import com.emika.app.data.network.pojo.subTask.Comment

interface CommentDbCallback {
    fun onCommentsLoaded(comment: List<Comment>)
}