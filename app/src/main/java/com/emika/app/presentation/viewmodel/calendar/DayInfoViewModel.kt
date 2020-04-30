package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback
import com.emika.app.data.db.callback.calendar.TaskDbCallback
import com.emika.app.data.db.entity.ActualDurationEntity
import com.emika.app.data.db.entity.TaskEntity
import com.emika.app.data.network.callback.calendar.DurationActualCallback
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.utils.Converter

class DayInfoViewModel(private val token: String):ViewModel(), TaskDbCallback, ActualDurationDbCallback, DurationActualCallback{
    val taskListMutableLiveData: MutableLiveData<List<PayloadTask>> = MutableLiveData()
    val durationMutableLiveData: MutableLiveData<List<PayloadDurationActual>> = MutableLiveData()

    private val repository: CalendarRepository = CalendarRepository(token)
    private val converter: Converter = Converter()
    override fun onOneTaskLoaded(taskEntity: TaskEntity?) {
        TODO("Not yet implemented")
    }

    override fun onFilteredTasksLoaded(taskList: MutableList<TaskEntity>?) {
        taskListMutableLiveData?.postValue(converter.fromTaskEntityToPayloadTaskList(taskList))
    }

    override fun onTasksLoaded(taskList: List<TaskEntity>) {
        taskListMutableLiveData?.postValue(converter.fromTaskEntityToPayloadTaskList(taskList))
    }

    fun getAllDbTaskByAssignee(assignee: String?) {
        repository.getDBTaskListById(this, assignee)
    }
    fun getDurations() {
        repository.downloadDurationActualLog(this)
    }

    fun getAllDbDurationByAssignee(assignee: String?, date: String?) {
        repository.getAllDbDurationsByAssignee(this,assignee, date);
    }

    override fun onActualDurationLoaded(actualDurationEntities: MutableList<ActualDurationEntity>?) {
        durationMutableLiveData.postValue(converter.fromEntityListDurationToPayloadListDuration(actualDurationEntities))
    }

    override fun onDurationLogDownloaded(durationActualList: MutableList<PayloadDurationActual>?) {
        durationMutableLiveData.postValue(durationActualList)
    }

}