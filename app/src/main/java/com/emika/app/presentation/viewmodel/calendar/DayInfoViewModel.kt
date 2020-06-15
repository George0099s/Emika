package com.emika.app.presentation.viewmodel.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback
import com.emika.app.data.db.callback.calendar.TaskDbCallback
import com.emika.app.data.db.entity.ActualDurationEntity
import com.emika.app.data.db.entity.TaskEntity
import com.emika.app.data.network.callback.calendar.DurationActualCallback
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.utils.Converter

class DayInfoViewModel(private val token: String):ViewModel(), TaskDbCallback, ActualDurationDbCallback{
    val taskListMutableLiveData: MutableLiveData<List<PayloadTask>> = MutableLiveData()
    val durationMutableLiveData: MutableLiveData<List<PayloadDurationActual>> = MutableLiveData()
    var taskLiveData: LiveData<List<TaskEntity>> = MutableLiveData()
    private val repository: CalendarRepository = CalendarRepository(token)
    private val converter: Converter = Converter()
    private var durations: List<PayloadDurationActual> = arrayListOf()
    private var taskList: MutableList<PayloadTask> = arrayListOf()

    override fun onOneTaskLoaded(taskEntity: TaskEntity?) {
        TODO("Not yet implemented")
    }

    override fun onFilteredTasksLoaded(taskList: MutableList<TaskEntity>?) {
        this.taskList = converter.fromTaskEntityToPayloadTaskList(taskList)
        val tasks: MutableList<TaskEntity> = arrayListOf()
        for (duration in durations) {
            for (task in taskList!!)
                if (task.id == duration.taskId)
                    tasks.add(task)
        }
        taskListMutableLiveData.postValue(converter.fromTaskEntityToPayloadTaskList(tasks))
    }

    override fun onTasksLoaded(taskList: List<TaskEntity>) {
        this.taskList = converter.fromTaskEntityToPayloadTaskList(taskList)
        val tasks: MutableList<TaskEntity> = arrayListOf()
        for (duration in durations) {
            for (task in taskList!!)
                if (task.id == duration.taskId)
                    tasks.add(task)
        }
        taskListMutableLiveData.postValue(converter.fromTaskEntityToPayloadTaskList(tasks))
//        taskListMutableLiveData.postValue(converter.fromTaskEntityToPayloadTaskList(taskList))
    }

    fun getAllDbTaskByAssignee(assignee: String?) {
        repository.getDBTaskListById(this, assignee)
    }

    fun getLiveDataTasksByAssignee(assignee: String?) {
         taskLiveData = repository.getLiveDataTasksByAssignee(EmikaApplication.instance.database?.taskDao(), assignee).value as LiveData<List<TaskEntity>>
    }

    fun getAllDbDurationByAssignee(assignee: String?, date: String?) {
        repository.getAllDbDurationsByAssignee(this,assignee, date)
    }

    override fun onActualDurationLoaded(actualDurationEntities: MutableList<ActualDurationEntity>?) {
        durations = converter.fromEntityListDurationToPayloadListDuration(actualDurationEntities)
        durationMutableLiveData.postValue(converter.fromEntityListDurationToPayloadListDuration(actualDurationEntities))
    }
}