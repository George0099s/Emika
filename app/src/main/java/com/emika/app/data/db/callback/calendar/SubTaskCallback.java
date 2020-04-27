package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.network.pojo.subTask.SubTask;

import java.util.List;

public interface SubTaskCallback {
    void onSubTaskListLoaded(List<SubTask> subTasks);
    void onSubTaskLoaded(String id);
}
