package com.emika.app.presentation.viewmodel.calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class CalendarViewModel extends ViewModel implements TaskListCallback, TaskDbCallback, TaskCallback, MemberDbCallback, Parcelable, EpicLinksDbCallback, ProjectDbCallback {
    private static final String TAG = "CalendarViewModel";
    @Inject
    Assignee assignee;
    private MutableLiveData<List<PayloadTask>> taskListMutableLiveData;
    private MutableLiveData<Boolean> currentColumn;
    private MutableLiveData<List<PayloadShortMember>> membersMutableLiveData;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private MutableLiveData<List<EpicLinksEntity>> epicLinksMutableLiveData;
    private MutableLiveData<List<ProjectEntity>> projectMutableLiveData;
    private CalendarRepository repository;
    private String token;
    private Context context;
    private Converter converter;
    private Boolean firstRun = true;
    private JSONObject tokenJson;
    EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private Socket socket;
    private Manager manager;
    private JSONObject taskJSON;
    public CalendarViewModel(String token) {
        emikaApplication.getComponent().inject(this);
        this.token = token;
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.currentColumn = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
        membersMutableLiveData = new MutableLiveData<>();
        epicLinksMutableLiveData = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
        manager = emikaApplication.getManager();
        socket = manager.socket("/all");
        tokenJson = new JSONObject();
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("server_create_connection", tokenJson);
        socket.on("update_task", onUpdateTask);
        socket.connect();
    }

    private Emitter.Listener onUpdateTask = args -> {
        String name, id;
        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            name = jsonObject.getString("name");
            id = jsonObject.getString("_id");

//            Log.d(TAG, ":  " + name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

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

    public LiveData<List<EpicLinksEntity>> getEpicLinksMutableLiveData() {
        repository.getDbEpicLinks(this);
        return epicLinksMutableLiveData;
    }

    @Override
    public void onEpicLinksLoaded(List<EpicLinksEntity> epicLinksEntities) {
        epicLinksMutableLiveData.postValue(epicLinksEntities);
    }

    public LiveData<List<ProjectEntity>> getProjectMutableLiveData() {
        repository.getAllProjects(this);
        return projectMutableLiveData;
    }

    @Override
    public void onProjectLoaded(List<ProjectEntity> projectEntities) {
        projectMutableLiveData.postValue(projectEntities);
    }
}
