package com.emika.app.domain.repository.calendar;

import android.content.Context;
import android.util.Log;

import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.dbmanager.EpicLinksDbManager;
import com.emika.app.data.db.dbmanager.MemberDbManager;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CalendarRepository {

    private List<PayloadTask> payloadTaskList;
    private TaskDbManager taskDbManager;
    private CalendarNetworkManager calendarNetworkManager;
    private Converter converter;
    private String token;
    private Boolean firstRun = true;
    private ProjectDbManager projectDbManager;
    private MemberDbManager memberDbManager;
    private UserDbManager userDbManager;
    private EpicLinksDbManager epicLinksDbManager;
    private static final String TAG = "CalendarRepository";
    public CalendarRepository(String token) {
        this.token = token;
        this.calendarNetworkManager = new CalendarNetworkManager(token);
        converter = new Converter();
        taskDbManager = new TaskDbManager();
        projectDbManager = new ProjectDbManager();
        memberDbManager = new MemberDbManager();
        userDbManager = new UserDbManager();
        payloadTaskList = new ArrayList<>();
        epicLinksDbManager = new EpicLinksDbManager();
    }
    public List<PayloadTask> getPayloadTaskList(TaskListCallback taskListCallback, TaskDbCallback taskDbCallback, Context context) {
        if(NetworkState.getInstance(context).isOnline() && firstRun) {
            calendarNetworkManager.getAllTask(taskListCallback);
            firstRun = false;
            return payloadTaskList;
        } else {
            taskDbManager.getAllTask(taskDbCallback);
            return payloadTaskList;
        }
    }

    public void updateTask(PayloadTask task){
        calendarNetworkManager.updateTask(task);
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
    }

    public void sedDbData(List<PayloadTask> taskList) {
        taskDbManager.deleteAll();
        taskDbManager.insertDbAllTask(converter.fromPayloadTaskToTaskEntityList(taskList));
    }

    public void addTask(TaskCallback callback, PayloadTask task, JSONArray epicLinks){
        calendarNetworkManager.addTask(callback, task, epicLinks);
    }

    public void downloadAllMembers(ShortMemberCallback callback){
        calendarNetworkManager.getAllMembers(callback);
    }

    public void getAllDbMembers(MemberDbCallback callback){
        memberDbManager.getAllMembers(callback);
    }

    public void getAllProjects(ProjectDbCallback callback){
        projectDbManager.getAllDbProjects(callback);
    }
    public void downloadAllProject(ProjectsCallback callback){
        calendarNetworkManager.getAllProjects(callback);
    }
    public void getAllSections(ProjectsCallback callback){
        calendarNetworkManager.getAllSections(callback);
    }

    public void insertDbProject(List<ProjectEntity> projectEntities, ProjectDbCallback callback){
//        projectDbManager.deleteAllProjects();
        projectDbManager.addAllProjects(projectEntities, callback);
    }

    public void insertDbSections(){

    }

    public void insertDbMembers(List<PayloadShortMember> members, MemberDbCallback callback){
        memberDbManager.deleteAllMembers();
        memberDbManager.addAllMembers(converter.fromPayloadMemberToMemberEntity(members), callback);
    }

    public void insertDbUser(Payload user) {
        userDbManager.addUser(converter.fromUserToUserEntity(user));
    }

    public void downloadEpicLinks(EpicLinksCallback callback){
        calendarNetworkManager.getAllEpicLinks(callback);
    }

    public void getDbEpicLinks(EpicLinksDbCallback callback){
        epicLinksDbManager.getAllMembers(callback);
    }

    public void insertDbEpicLinks(List<EpicLinksEntity> epicLinks, EpicLinksDbCallback callback) {
//        epicLinksDbManager.deleteAllEpicLinks();
        epicLinksDbManager.insertAllEpicLinks(epicLinks, callback);
    }
}
