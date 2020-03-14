package com.emika.app.presentation.viewmodel.calendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.domain.repository.calendar.TaskInfoRepository;

public class TaskInfoViewModel extends ViewModel {
    private MutableLiveData<PayloadTask> taskMutableLiveData;

    private PayloadTask task;
    private TaskInfoRepository taskInfoRepository;
    private String token;
    public TaskInfoViewModel(String token) {
        this.token = token;
        taskMutableLiveData = new MutableLiveData<>();
    }
    public MutableLiveData<PayloadTask> getTaskMutableLiveData() {

        return taskMutableLiveData;
    }

    public void setTask(PayloadTask task) {
        this.task = task;
    }
}

