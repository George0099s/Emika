package com.emika.app.presentation.viewmodel.calendar;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.domain.repository.calendar.CalendarRepository;

public class AddTaskListViewModel extends ViewModel implements TaskCallback {
    private static final String TAG = "AddTaskListViewModel";
    private PayloadTask task;
    private String token;
    private CalendarRepository repository;
    private MutableLiveData<PayloadTask> mutableLiveData;
    public AddTaskListViewModel(String token) {
        this.token = token;
        repository = new CalendarRepository(token);
        mutableLiveData = new MutableLiveData<>();
    }

    public void addTask(String name, String projectId, String planDate, String deadlineDate, String assignee, String estimatedTime, String description, String priority) {
        repository.addTask(this, name, projectId, planDate, deadlineDate, assignee, estimatedTime, description, priority);
    }

    @Override
    public void getAddedTask(PayloadTask task) {
        this.task = task;
        mutableLiveData.postValue(task);
    }

    public PayloadTask getTask() {
        return task;
    }

    public MutableLiveData<PayloadTask> getMutableLiveData(String name, String projectId, String planDate, String deadlineDate, String assignee, String estimatedTime, String description, String priority) {
        repository.addTask(this, name, projectId, planDate, deadlineDate, assignee, estimatedTime, description, priority);
        return mutableLiveData;
    }

    public void setMutableLiveData(MutableLiveData<PayloadTask> mutableLiveData) {
        this.mutableLiveData = mutableLiveData;
    }
}
