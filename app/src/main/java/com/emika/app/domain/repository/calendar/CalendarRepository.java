package com.emika.app.domain.repository.calendar;

import android.content.Context;

import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;

public class CalendarRepository {

    private List<PayloadTask> payloadTaskList;
    private TaskDbManager taskDbManager;
    private CalendarNetworkManager calendarNetworkManager;
    private Converter converter;
    private String token;

    public CalendarRepository(String token) {
        this.token = token;
        this.calendarNetworkManager = new CalendarNetworkManager(token);
        converter = new Converter();
        taskDbManager = new TaskDbManager();
        payloadTaskList = new ArrayList<>();
    }

    public List<PayloadTask> getPayloadTaskList(TaskCallback taskCallback, TaskDbCallback taskDbCallback, Context context) {
        if(NetworkState.getInstance(context).isOnline())
            calendarNetworkManager.getAllTask(taskCallback);
        else
            taskDbManager.getAllTask(taskDbCallback);

        return payloadTaskList;
    }

    public void updateTask(PayloadTask task){
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
//        calendarNetworkManager.updateTask(task);
    }

    public void sedDbData(List<PayloadTask> taskList) {
        taskDbManager.deleteAll();
        taskDbManager.insertDbAllTask(converter.fromPayloadTaskToTaskEntityList(taskList));
    }
}
