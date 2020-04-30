package com.emika.app.domain.repository.calendar;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.callback.calendar.SubTaskCallback;
import com.emika.app.data.db.dao.TaskDao;
import com.emika.app.data.db.dbmanager.ActualDurationDbManager;
import com.emika.app.data.db.dbmanager.EpicLinksDbManager;
import com.emika.app.data.db.dbmanager.MemberDbManager;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.dbmanager.SectionDbManager;
import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.DurationActualCallback;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;
import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.google.android.gms.tasks.Tasks;

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
    private SectionDbManager sectionDbManager;
    private ActualDurationDbManager actualDurationDbManager;
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
        sectionDbManager = new SectionDbManager();
        actualDurationDbManager = new ActualDurationDbManager();
    }
    public void downloadTasks(TaskListCallback taskListCallback) {
//        if(NetworkState.getInstance(context).isOnline() && firstRun) {
            calendarNetworkManager.getAllTask(taskListCallback);
//        }/**/
    }

    public void getDbTaskList(TaskDbCallback callback){
        taskDbManager.getAllDbTask(callback);
    }

    public void updateTask(PayloadTask task){
        calendarNetworkManager.updateTask(task);
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
    }
     public void updateDbTask(PayloadTask task){
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
    }

    public void sedDbData(List<PayloadTask> taskList) {
        taskDbManager.addAllProjects(converter.fromPayloadTaskToTaskEntityList(taskList));
    }

    public void addTask(TaskCallback callback, PayloadTask task, JSONArray epicLinks, JSONArray subTasks){
        calendarNetworkManager.addTask(callback, task, epicLinks, subTasks);
    }
    public void addDbTask(PayloadTask task){
        taskDbManager.addTask(converter.fromPayloadTaskToTaskEntity(task));
    }
    public void addSubTask(SubTask subTask, SubTaskCallback callback){
        calendarNetworkManager.addSubTask(subTask, callback);
    }

    public void downloadAllMembers(ShortMemberCallback callback){
        calendarNetworkManager.getAllShortMembers(callback);
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
    public void getAllSections(SectionDbCallback callback){
        sectionDbManager.getAllSections(callback);
    }

    public void downloadSections(ProjectsCallback callback){
        calendarNetworkManager.getAllSections(callback);
    }

    public void insertDbProject(List<ProjectEntity> projectEntities, ProjectDbCallback callback){
        projectDbManager.addAllProjects(projectEntities, callback);
    }

    public void insertDbSections(List<SectionEntity> sectionEntities, SectionDbCallback callback){
        sectionDbManager.addAllSections(sectionEntities, callback);
    }

    public void insertDbMembers(List<PayloadShortMember> members, MemberDbCallback callback){
//        memberDbManager.deleteAllMembers();
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

    public void downloadDurationActualLog(DurationActualCallback callback){
        calendarNetworkManager.downLoadDurationLog(callback);
    }

    public void sendRegistrationKey(String key) {
        calendarNetworkManager.sendRegistrationKey(key);
    }

    public void getSubTask(String taskId, SubTaskCallback callback) {
        calendarNetworkManager.getSubTaskList(taskId, callback);
    }

    public void updateSubTask(SubTask subTask) {
        calendarNetworkManager.updateSubTask(subTask);
    }

    public void downloadCompanyInfo(CompanyInfoCallback callback) {
        calendarNetworkManager.downLoadCompanyInfo(callback);
    }

    public void insertAllDbDurations(List<ActualDurationEntity> durationEntities, ActualDurationDbCallback callback) {
        actualDurationDbManager.addAllDurations(durationEntities, callback);
    }

    public void insertDbDuration(ActualDurationEntity durationEntity) {
        actualDurationDbManager.addDuration(durationEntity);
    }


    public void insertDbDurations(ActualDurationEntity durationEntity, ActualDurationDbCallback callback) {
        actualDurationDbManager.insertDurations(durationEntity, callback);
    }

    public void getAllDbDurations(ActualDurationDbCallback callback) {
        actualDurationDbManager.getAllDurations(callback);
    }

    public void getDBTaskListById(TaskDbCallback callback,String assignee) {
        taskDbManager.getAllDbTaskById(callback, assignee);
    }
    public void getDBTaskById(TaskDbCallback callback,String id) {
        taskDbManager.getTaskById(callback, id);
    }

    public void getDBTaskListByDateId(TaskDbCallback callback,String assignee, String planDate) {
        taskDbManager.getAllDbTaskByDateId(callback, assignee, planDate);
    }

    public void getAllDbDurationsByAssignee(ActualDurationDbCallback callback, String assigneeId, String date) {
        actualDurationDbManager.getDurationsByAssignee(callback, assigneeId, date);
    }

    public void downloadTasksByAssignee(TaskListCallback callback, String assignee) {
        calendarNetworkManager.downloadTaskByAssignee(callback, assignee);
    }

    public void updateDbDurations(ActualDurationEntity actualDurationEntity) {
        actualDurationDbManager.updateDbDuration(actualDurationEntity);
    }

    public LiveData<List<TaskEntity>> getLiveDataTasks(TaskDao taskDao){
        return taskDao.getAllTaskLiveData();
    }

    public LiveData<List<TaskEntity>> getLiveDataTasksByAssignee(TaskDao taskDao, String assignee){
        return taskDao.getAllTaskLiveDataByAssignee(assignee);
    }

    public void deleteDuration(PayloadDurationActual durationActual) {
        actualDurationDbManager.deleteDuration(converter.frompayload(durationActual));
    }
}
