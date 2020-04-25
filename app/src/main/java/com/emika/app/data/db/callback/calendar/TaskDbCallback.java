package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.TaskEntity;

import java.util.List;

public interface TaskDbCallback {
    void onTasksLoaded(List<TaskEntity> taskList);
    void onFilteredTasksLoaded(List<TaskEntity> taskList);
    void onOneTaskLoaded(TaskEntity taskEntity);
}
