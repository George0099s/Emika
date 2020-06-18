package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.callback.calendar.CommentCallback
import com.emika.app.data.network.pojo.comment.CommentDeleted
import com.emika.app.data.network.pojo.comment.ModelComment
import com.emika.app.data.network.pojo.subTask.Comment
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.utils.Converter

class CommentViewModel : ViewModel(), CommentCallback {
    var commentsMutableLiveData: MutableLiveData<ModelComment> = MutableLiveData()
    private var repository: CalendarRepository = CalendarRepository(EmikaApplication.instance.sharedPreferences.getString("token", null))
    private val converter: Converter = Converter()
    fun createComment(text: String, taskId: String){
        repository.createComment(this, text, taskId)
    }

    fun updateComment(text: String, taskId: String, commentId: String){
        repository.updateComment(this, text, taskId, commentId)
    }

    fun deleteComment(taskId: String, comment: Comment){
        repository.deleteComment(this, taskId, comment.id)
        repository.deleteDbComment(converter.fromCommentToCommentEntity(comment))
    }


    override fun onCommentCreated(model: ModelComment) {
        commentsMutableLiveData.postValue(model)
    }

    override fun onCommentUpdated(model: ModelComment) {
        commentsMutableLiveData.postValue(model)
    }

    override fun onCommentDeleted(model: CommentDeleted) {
//        commentsMutableLiveData.postValue(model)
    }

    fun insertComm(comment: Comment) {
        repository.insertComment(converter.fromCommentToCommentEntity(comment))
    }


}