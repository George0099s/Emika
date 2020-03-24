package com.emika.app.presentation.viewmodel.calendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.calendar.TaskInfoRepository;

import javax.inject.Inject;

public class TaskInfoViewModel extends ViewModel {
    private MutableLiveData<PayloadTask> taskMutableLiveData;

    private PayloadTask task;
    private TaskInfoRepository taskInfoRepository;
    private CalendarRepository repository;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private String token;

    @Inject
    Assignee assignee;

    public TaskInfoViewModel(String token) {
        this.token = token;
        EmikaApplication.getInstance().getComponent().inject(this);
        repository = new CalendarRepository(token);
        taskMutableLiveData = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
    }
    public MutableLiveData<PayloadTask> getTaskMutableLiveData() {
        return taskMutableLiveData;
    }
    public void updateTask(PayloadTask task){
        repository.updateTask(task);
    }
    public void setTask(PayloadTask task) {
        this.task = task;
    }

    public MutableLiveData<Assignee> getAssigneeMutableLiveData() {
        assigneeMutableLiveData.setValue(assignee);
        return assigneeMutableLiveData;
    }
}

