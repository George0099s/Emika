package com.emika.app.presentation.viewmodel.calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.domain.repository.calendar.CalendarRepository;

import javax.inject.Inject;

public class AddTaskListViewModel extends ViewModel implements TaskCallback, Parcelable {
    private static final String TAG = "AddTaskListViewModel";
    private PayloadTask task;
    private String token;
    private CalendarRepository repository;
    private MutableLiveData<PayloadTask> mutableLiveData;
    private MutableLiveData<Assignee> assignee;
    private EmikaApplication app = EmikaApplication.getInstance();
    @Inject
    Assignee assigneeModel;
    public AddTaskListViewModel(String token) {
        this.token = token;
        repository = new CalendarRepository(token);
        mutableLiveData = new MutableLiveData<>();
        assignee = new MutableLiveData<>();
        app.getComponent().inject(this);
    }

    protected AddTaskListViewModel(Parcel in) {
        task = in.readParcelable(PayloadTask.class.getClassLoader());
        token = in.readString();
    }

    public static final Creator<AddTaskListViewModel> CREATOR = new Creator<AddTaskListViewModel>() {
        @Override
        public AddTaskListViewModel createFromParcel(Parcel in) {
            return new AddTaskListViewModel(in);
        }

        @Override
        public AddTaskListViewModel[] newArray(int size) {
            return new AddTaskListViewModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(task, flags);
        dest.writeString(token);
    }


    public MutableLiveData<Assignee> getAssignee() {
        assignee.setValue(assigneeModel);
        return assignee;
    }
}