package com.emika.app.presentation.viewmodel.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddTaskListViewModel extends ViewModel implements Parcelable, TaskCallback {
    private static final String TAG = "AddTaskListViewModel";
    private PayloadTask task;
    private String token;
    private CalendarRepository repository;
    private MutableLiveData<PayloadTask> mutableLiveData;
    private MutableLiveData<Assignee> assignee;
    private MutableLiveData<Project> projectMutableLiveData;

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

    public void setEpicLinksMutableLiveData(MutableLiveData<List<PayloadEpicLinks>> epicLinksMutableLiveData) {
        this.epicLinksMutableLiveData = epicLinksMutableLiveData;
    }

    private MutableLiveData<List<PayloadEpicLinks>> epicLinksMutableLiveData;



    private List<PayloadEpicLinks>  epicLinksList;
    private EmikaApplication app = EmikaApplication.instance;
    private Converter converter;
    @Inject
    Assignee assigneeModel;
    @Inject
    Project projectDi;
    @Inject
    EpicLinks epicLinksDi;


    public AddTaskListViewModel(String token) {
        epicLinksList = new ArrayList<>();
        this.token = token;
        repository = new CalendarRepository(token);
        mutableLiveData = new MutableLiveData<>();
        assignee = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();
        epicLinksMutableLiveData = new MutableLiveData<>();
        converter = new Converter();
        app.getComponent().inject(this);
    }

    public void setEpicLinksList(List<PayloadEpicLinks> epicLinksList) {
        this.epicLinksList = epicLinksList;
    }

    protected AddTaskListViewModel(Parcel in) {
        task = in.readParcelable(PayloadTask.class.getClassLoader());
        token = in.readString();
    }

    @Override
    public void getAddedTask(PayloadTask task) {
        this.task = task;
        mutableLiveData.postValue(task);
    }

    public PayloadTask getTask() {
        return task;
    }

    public MutableLiveData<PayloadTask> getMutableLiveData(PayloadTask task) {
        repository.addTask(this, task, converter.fromListToJSONArray(task.getEpicLinks()), converter.formListSubTaskToJsonArray(task.getSubTaskList()));
        return mutableLiveData;
    }


    public MutableLiveData<Assignee> getAssignee() {
        assignee.setValue(assigneeModel);
        return assignee;
    }

    public MutableLiveData<Project> getProjectMutableLiveData() {
        projectMutableLiveData.setValue(projectDi);
        return projectMutableLiveData;
    }

    public MutableLiveData<List<PayloadEpicLinks>> getEpicLinksMutableLiveData() {
        epicLinksMutableLiveData.setValue(epicLinksList);
        return epicLinksMutableLiveData;
    }

    public List<PayloadEpicLinks> getEpicLinksList() {
        return epicLinksList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(task, flags);
        dest.writeString(token);
        dest.writeParcelable(assigneeModel, flags);
    }
}
