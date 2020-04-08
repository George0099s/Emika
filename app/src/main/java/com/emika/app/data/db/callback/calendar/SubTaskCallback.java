package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;

import java.util.List;

public interface SubTaskCallback {
    void onSubTaskLoaded(List<SubTask> subTasks);
}
