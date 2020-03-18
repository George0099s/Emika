package com.emika.app.presentation.viewmodel.calendar;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

public class CalendarViewModel extends ViewModel implements TaskCallback, TaskDbCallback {
    private MutableLiveData<List<PayloadTask>> taskListMutableLiveData;
    private MutableLiveData<Boolean> currentColumn;
    private static final String TAG = "CalendarViewModel";
    private CalendarRepository repository;
    private String token;
    private Context context;
    private Converter converter;
    public CalendarViewModel(String token) {
        this.token = token;
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.currentColumn = new MutableLiveData<>();
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
    public void setTask(List<PayloadTask> taskList) {
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
//        for (int i = 0; i < taskList.size(); i++) {
//            PayloadTask payloadTask = new PayloadTask();
//            payloadTask.setId(taskList.get(i).getId());
//            payloadTask.setPriority(taskList.get(i).getPriority());
//            payloadTask.setAssignee(taskList.get(i).getAssignee());
//            payloadTask.setCompanyId(taskList.get(i).getCompanyId());
//            payloadTask.setCreatedAt(taskList.get(i).getCreatedAt());
//            payloadTask.setCreatedBy(taskList.get(i).getCreatedBy());
//            payloadTask.setDeadlineDate(taskList.get(i).getDeadlineDate());
//            payloadTask.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
//            payloadTask.setDeadlineTime(taskList.get(i).getDeadlineTime());
//            payloadTask.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
//            payloadTask.setDescription(taskList.get(i).getDescription());
//            payloadTask.setDuration(taskList.get(i).getDuration());
//            payloadTask.setPlanDate(taskList.get(i).getPlanDate());
//            payloadTask.setName(taskList.get(i).getName());
//            payloadTasks.add(payloadTask);
//        }
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
}
