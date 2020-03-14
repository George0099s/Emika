package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.TaskEntity;

import java.util.List;

public interface TaskDbCallback {
    void setDbTask(List<TaskEntity> taskList);
}
