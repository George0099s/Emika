package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.task.PayloadTask;

public interface TaskCallback {
    void getAddedTask (PayloadTask task);
}
