package com.emika.app.presentation.viewmodel.calendar;

import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.domain.repository.calendar.CalendarRepository;

import org.json.JSONObject;

import java.util.List;

public class AddTaskViewModel extends ViewModel implements TaskCallback {
    private JSONObject task;
    private String token;
    private CalendarRepository repository;
    public AddTaskViewModel(String token) {
        this.token = token;
        repository = new CalendarRepository(token);
    }

    public void addTask(JSONObject task){
        repository.addTask(this, task);
    }

    @Override
    public void setTask(List<PayloadTask> taskList) {

    }
}
