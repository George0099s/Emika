package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.domain.repository.calendar.CalendarRepository

class InboxViewModel(token: String) : ViewModel() {
    val taskList: MutableList<PayloadTask> = arrayListOf()
     var addedTaskList: MutableList<PayloadTask> = ArrayList()

    private var repository: CalendarRepository? = CalendarRepository(token)
    var taskListMutableLiveData: MutableLiveData<MutableList<PayloadTask>>? = MutableLiveData()

    init{
        taskListMutableLiveData = MutableLiveData()
    }
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