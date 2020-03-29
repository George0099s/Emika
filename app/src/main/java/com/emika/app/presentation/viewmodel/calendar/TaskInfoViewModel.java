package com.emika.app.presentation.viewmodel.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.calendar.TaskInfoRepository;

import javax.inject.Inject;

public class TaskInfoViewModel extends ViewModel implements Parcelable {
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

    protected TaskInfoViewModel(Parcel in) {
        task = in.readParcelable(PayloadTask.class.getClassLoader());
        token = in.readString();
    }

    public static final Creator<TaskInfoViewModel> CREATOR = new Creator<TaskInfoViewModel>() {
        @Override
        public TaskInfoViewModel createFromParcel(Parcel in) {
            return new TaskInfoViewModel(in);
        }

        @Override
        public TaskInfoViewModel[] newArray(int size) {
            return new TaskInfoViewModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(task, flags);
        dest.writeString(token);
    }
}

