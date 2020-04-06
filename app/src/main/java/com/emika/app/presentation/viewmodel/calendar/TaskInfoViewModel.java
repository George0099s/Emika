package com.emika.app.presentation.viewmodel.calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.calendar.TaskInfoRepository;
import com.emika.app.features.calendar.swipe.ListSwipeHelper;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TaskInfoViewModel extends ViewModel implements Parcelable, EpicLinksDbCallback {
    private MutableLiveData<PayloadTask> taskMutableLiveData;

    public PayloadTask getTask() {
        return task;
    }

    private PayloadTask task;
    private CalendarRepository repository;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private MutableLiveData<Project> projectMutableLiveData;
    private MutableLiveData<List<EpicLinksEntity>> epicLinksMutableLiveData;
    private List<String> taskEpicLinks;
    private List<PayloadEpicLinks>  epicLinksList;
    private Converter converter;
    private TaskInfoRepository taskInfoRepository;
    private String token;
    private static final String TAG = "TaskInfoViewModel";


    @Inject
    Assignee assignee;
    @Inject
    Project projectDi;
    @Inject
    EpicLinks epicLinksDi;
    public TaskInfoViewModel(String token) {
        this.token = token;
        EmikaApplication.getInstance().getComponent().inject(this);
        repository = new CalendarRepository(token);
        taskMutableLiveData = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();
        epicLinksMutableLiveData = new MutableLiveData<>();
        epicLinksList = new ArrayList<>();
        converter = new Converter();
        taskEpicLinks =  new ArrayList<>();
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

    public MutableLiveData<List<EpicLinksEntity>> getEpicLinksMutableLiveData() {
        repository.getDbEpicLinks(this);
        return epicLinksMutableLiveData;
    }

    public MutableLiveData<Project> getProjectMutableLiveData() {
        projectMutableLiveData.setValue(projectDi);
        return projectMutableLiveData;
    }

    @Override
    public void onEpicLinksLoaded(List<EpicLinksEntity> epicLinksEntities) {
        List<EpicLinksEntity> epicLinksEntities1 = new ArrayList<>();

        for (int i = 0; i < task.getEpicLinks().size(); i++) {
            for (int j = 0; j < epicLinksEntities.size(); j++) {
                if (task.getEpicLinks().get(i).equals(epicLinksEntities.get(j).getId()))
                    epicLinksEntities1.add(epicLinksEntities.get(j));
            }
        }
        epicLinksMutableLiveData.postValue(epicLinksEntities1);
    }

    public List<PayloadEpicLinks> getEpicLinksList() {
        return epicLinksList;
    }
}

