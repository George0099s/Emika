package com.emika.app.presentation.viewmodel.calendar;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

public class CalendarViewModel extends ViewModel implements TaskListCallback, TaskDbCallback, TaskCallback, ShortMemberCallback {
    private static final String TAG = "CalendarViewModel";
    private MutableLiveData<List<PayloadTask>> taskListMutableLiveData;
    private MutableLiveData<Boolean> currentColumn;
    private MutableLiveData<List<PayloadShortMember>> membersMutableLiveData;
    private CalendarRepository repository;
    private String token;
    private Context context;
    private Converter converter;

    public CalendarViewModel(String token) {
        this.token = token;
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.currentColumn = new MutableLiveData<>();
        membersMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
    }


    public MutableLiveData<List<PayloadTask>> getListMutableLiveData() {
        repository.getPayloadTaskList(this, this, context);
        return taskListMutableLiveData;
    }

    public void updateTask(PayloadTask task) {
        repository.updateTask(task);
    }

    @Override
    public void setTaskList(List<PayloadTask> taskList) {
        repository.sedDbData(taskList);
        ArrayList<PayloadTask> plannedTask = new ArrayList<>();
        for (int j = 0; j < taskList.size(); j++) {
            if (taskList.get(j).getPlanDate() != null) {
                plannedTask.add(taskList.get(j));
            }
        }
        taskListMutableLiveData.postValue(plannedTask);
    }

    @Override
    public void setDbTask(List<TaskEntity> taskList) {
        List<PayloadTask> payloadTasks = converter.fromTaskEntityToPayloadTaskList(taskList);
        ArrayList<PayloadTask> plannedTask = new ArrayList<>();
        for (int j = 0; j < payloadTasks.size(); j++) {
            if (taskList.get(j).getPlanDate() != null) {
                plannedTask.add(payloadTasks.get(j));
            }
        }
        taskListMutableLiveData.postValue(plannedTask);
    }

    public void setCurrentColumn() {
        getCurrentDate();
    }

    public LiveData<Boolean> getCurrentDate() {
        currentColumn.setValue(true);
        return currentColumn;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void getAddedTask(PayloadTask task) {

    }

    public MutableLiveData<List<PayloadShortMember>> getMembersMutableLiveData() {
        if (membersMutableLiveData.getValue() == null) {
            repository.getAllMembers(this);
            return membersMutableLiveData;
        } else return membersMutableLiveData;
    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        membersMutableLiveData.postValue(shortMembers);
    }
}
