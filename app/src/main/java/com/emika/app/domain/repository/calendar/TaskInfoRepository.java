package com.emika.app.domain.repository.calendar;

import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

public class TaskInfoRepository {
    private List<PayloadTask> payloadTaskList;
    private TaskDbManager taskDbManager;
    private CalendarNetworkManager calendarNetworkManager;
    private Converter converter;
    private String token;

    public TaskInfoRepository(String token) {
        this.token = token;
        this.calendarNetworkManager = new CalendarNetworkManager(token);
        converter = new Converter();
        taskDbManager = new TaskDbManager();
        payloadTaskList = new ArrayList<>();
    }


}
