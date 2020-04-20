package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.db.callback.calendar.TaskDbCallback
import com.emika.app.data.db.entity.TaskEntity
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.utils.Converter
import java.util.*

class DayInfoViewModel(private val token: String):ViewModel(), TaskDbCallback{
    private val taskListMutableLiveData: MutableLiveData<List<PayloadTask?>?>? = null

    private val repository: CalendarRepository = CalendarRepository(token)
    private val converter: Converter = Converter()
    override fun setDbTask(taskList: List<TaskEntity>) {
//        val payloadTasks: List<PayloadTask> = converter.fromTaskEntityToPayloadTaskList(taskList)
//        val plannedTask = ArrayList<PayloadTask>()
//        for (j in taskList.indices) {
//            if (taskList[j].planDate != null) {
//                plannedTask.add(payloadTasks[j])
//            }
//        }
        taskListMutableLiveData?.postValue(converter.fromTaskEntityToPayloadTaskList(taskList))
    }
    fun getTasks(): MutableLiveData<List<PayloadTask?>?>? {
        repository.getDbTaskList(this)
        return taskListMutableLiveData
    }


}