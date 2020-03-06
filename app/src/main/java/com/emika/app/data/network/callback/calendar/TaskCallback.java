package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.task.PayloadTask;

import java.util.List;

public interface TaskCallback {
    void setTask(List<PayloadTask> taskList);
}
