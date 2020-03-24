package com.emika.app.presentation.viewmodel.calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CalendarViewModel extends ViewModel implements TaskListCallback, TaskDbCallback, TaskCallback, MemberDbCallback, Parcelable {
    private static final String TAG = "CalendarViewModel";
    @Inject
    Assignee assignee;
    private MutableLiveData<List<PayloadTask>> taskListMutableLiveData;
    private MutableLiveData<Boolean> currentColumn;
    private MutableLiveData<List<PayloadShortMember>> membersMutableLiveData;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private CalendarRepository repository;
    private String token;
    private Context context;
    private Converter converter;
    private Boolean firstRun = true;
    EmikaApplication emikaApplication = EmikaApplication.getInstance();

    public CalendarViewModel(String token) {
        emikaApplication.getComponent().inject(this);
        this.token = token;
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.currentColumn = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
        membersMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
    }


    protected CalendarViewModel(Parcel in) {
        token = in.readString();
    }

    public static final Creator<CalendarViewModel> CREATOR = new Creator<CalendarViewModel>() {
        @Override
        public CalendarViewModel createFromParcel(Parcel in) {
            return new CalendarViewModel(in);
        }

        @Override
        public CalendarViewModel[] newArray(int size) {
            return new CalendarViewModel[size];
        }
    };

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
        for (int j = 0; j < taskList.size(); j++) {
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
            repository.getAllDbMembers(this);
            return membersMutableLiveData;
    }

    public MutableLiveData<Assignee> getAssigneeMutableLiveData() {
        assigneeMutableLiveData.setValue(assignee);
        return assigneeMutableLiveData;
    }

    public void insertDbUser(Payload user){
        repository.insertDbUser(user);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }

    @Override
    public void onMembersLoaded(List<MemberEntity> memberEntityList) {
            membersMutableLiveData.postValue(converter.fromMemberEntityToPayloadMember(memberEntityList));
    }
}
