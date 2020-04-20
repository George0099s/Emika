package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.domain.repository.calendar.CalendarRepository

class InboxViewModel(token: String) : ViewModel() {
    val taskList: MutableList<PayloadTask> = arrayListOf()
     var addedTaskList: List<PayloadTask> = arrayListOf()
        set(value) {
            field = value
        }

    private var repository: CalendarRepository? = CalendarRepository(token)
    private var taskListMutableLiveData: MutableLiveData<List<PayloadTask>>? = MutableLiveData()

    var date: String? = null
        set(value) {
            field = value
        }

    fun addTaskFromBacklog(tasks: List<PayloadTask>) {
        for (task in tasks) {
            task.planDate = date
            repository!!.updateTask(task)
        }
    }
}