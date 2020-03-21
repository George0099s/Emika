package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.task.PayloadTask;

import java.util.List;

public interface TaskListCallback {
    void setTaskList(List<PayloadTask> taskList);
}
