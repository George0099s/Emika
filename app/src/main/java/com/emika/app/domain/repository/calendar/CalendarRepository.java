package com.emika.app.domain.repository.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback;
import com.emika.app.data.db.callback.calendar.CommentDbCallback;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.callback.calendar.SubTaskCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.dao.TaskDao;
import com.emika.app.data.db.dbmanager.ActualDurationDbManager;
import com.emika.app.data.db.dbmanager.EpicLinksDbManager;
import com.emika.app.data.db.dbmanager.MemberDbManager;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.dbmanager.SectionDbManager;
import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.CommentEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.CommentCallback;
import com.emika.app.data.network.callback.calendar.DurationActualCallback;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadProjectCreation;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.utils.Converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CalendarRepository implements Parcelable {

    public static final Creator<CalendarRepository> CREATOR = new Creator<CalendarRepository>() {
        @Override
        public CalendarRepository createFromParcel(Parcel in) {
            return new CalendarRepository(in);
        }

        @Override
        public CalendarRepository[] newArray(int size) {
            return new CalendarRepository[size];
        }
    };
    private static final String TAG = "CalendarRepository";
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

    protected CalendarRepository(Parcel in) {
        payloadTaskList = in.createTypedArrayList(PayloadTask.CREATOR);
        token = in.readString();
        byte tmpFirstRun = in.readByte();
        firstRun = tmpFirstRun == 0 ? null : tmpFirstRun == 1;
    }

    public void downloadTasks(TaskListCallback taskListCallback) {
//        if(NetworkState.getInstance(context).isOnline() && firstRun) {
        calendarNetworkManager.getAllTask(taskListCallback);
//        }/**/
    }

    public void getDbTaskList(TaskDbCallback callback) {
        taskDbManager.getAllDbTask(callback);
    }

    public void updateTask(PayloadTask task) {
        calendarNetworkManager.updateTask(task);
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
    }

    public void updateSocketTask(PayloadTask task) {
        calendarNetworkManager.updateTask(task);
    }

    public void updateDbTask(PayloadTask task) {
        taskDbManager.updateTask(converter.fromPayloadTaskToTaskEntity(task));
    }

    public void sedDbData(List<PayloadTask> taskList) {
        taskDbManager.addAllProjects(converter.fromPayloadTaskToTaskEntityList(taskList));
    }

    public void addTask(TaskCallback callback, PayloadTask task, JSONArray epicLinks, JSONArray subTasks) {
        calendarNetworkManager.addTask(callback, task, epicLinks, subTasks);
    }

    public void addDbTask(PayloadTask task) {
        taskDbManager.addTask(converter.fromPayloadTaskToTaskEntity(task));
    }

    public void addSubTask(SubTask subTask, SubTaskCallback callback) {
        calendarNetworkManager.addSubTask(subTask, callback);
    }

    public void downloadAllMembers(ShortMemberCallback callback) {
        calendarNetworkManager.getAllShortMembers(callback);
    }

    public void getAllDbMembers(MemberDbCallback callback) {
        memberDbManager.getAllMembers(callback);
    }

    public void getAllProjects(ProjectDbCallback callback) {
        projectDbManager.getAllDbProjects(callback);
    }

    public void downloadAllProject(ProjectsCallback callback) {
        calendarNetworkManager.getAllProjects(callback);
    }

    public void getAllSections(SectionDbCallback callback) {
        sectionDbManager.getAllSections(callback);
    }

    public void downloadSections(ProjectsCallback callback) {
        calendarNetworkManager.getAllSections(callback);
    }

    public void insertDbProject(List<ProjectEntity> projectEntities, ProjectDbCallback callback) {
        projectDbManager.addAllProjects(projectEntities, callback);
    }

    public void insertDbSections(List<SectionEntity> sectionEntities, SectionDbCallback callback) {
        sectionDbManager.addAllSections(sectionEntities, callback);
    }

    public void insertDbMembers(List<PayloadShortMember> members, MemberDbCallback callback) {
//        memberDbManager.deleteAllMembers();
        memberDbManager.addAllMembers(converter.fromPayloadMemberToMemberEntity(members), callback);
    }

    public void insertDbUser(Payload user) {
        userDbManager.addUser(converter.fromUserToUserEntity(user));
    }

    public void downloadEpicLinks(EpicLinksCallback callback) {
        calendarNetworkManager.getAllEpicLinks(callback);
    }

    public void getDbEpicLinks(EpicLinksDbCallback callback) {
        epicLinksDbManager.getAllMembers(callback);
    }

    public void insertDbEpicLinks(List<EpicLinksEntity> epicLinks, EpicLinksDbCallback callback) {
//        epicLinksDbManager.deleteAllEpicLinks();
        epicLinksDbManager.insertAllEpicLinks(epicLinks, callback);
    }

    public void downloadDurationActualLog(DurationActualCallback callback) {
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

    public void getDBTaskListById(TaskDbCallback callback, String assignee) {
        taskDbManager.getAllDbTaskById(callback, assignee);
    }

    public void getDBTaskById(TaskDbCallback callback, String id) {
        taskDbManager.getTaskById(callback, id);
    }

    public void getDBTaskListByDateId(TaskDbCallback callback, String assignee, String planDate) {
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

    public LiveData<List<TaskEntity>> getLiveDataTasks(TaskDao taskDao) {
        return taskDao.getAllTaskLiveData();
    }

    public LiveData<List<TaskEntity>> getLiveDataTasksByAssignee(TaskDao taskDao, String assignee) {
        return taskDao.getAllTaskLiveDataByAssignee(assignee);
    }

    public void deleteDuration(PayloadDurationActual durationActual) {
        actualDurationDbManager.deleteDuration(converter.frompayload(durationActual));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(payloadTaskList);
        dest.writeString(token);
        dest.writeByte((byte) (firstRun == null ? 0 : firstRun ? 1 : 2));
    }

    public void insertAllComments(List<CommentEntity> comments) {
        taskDbManager.insertAllComments(comments);
    }

    public void insertComment(CommentEntity comment) {
        taskDbManager.insertComment(comment);
    }

    public void getDbComments(CommentDbCallback callback, String taskId) {
        taskDbManager.getDbComments(callback, taskId);
    }

    public void createComment(CommentCallback callback, String text, String taskId) {
        calendarNetworkManager.createComment(text, taskId, callback);
    }

    public void updateComment(CommentCallback callback, String text, String taskId, String commentId) {
        calendarNetworkManager.updateComment(text, taskId, commentId, callback);
    }

    public void deleteComment(CommentCallback callback, String taskId, String commentId) {
        calendarNetworkManager.deleteComment(callback, taskId, commentId);
    }

    public void deleteDbComment(CommentEntity comment) {
        taskDbManager.deleteComment(comment);
    }

    public void createProject(ProjectsCallback callback, String projectName) {
        calendarNetworkManager.createProject(callback, projectName);
    }

    public void insertProject(@NotNull PayloadProjectCreation project) {
        projectDbManager.insertProject(converter.fromPayloadProjectToProjectEntity(project));
    }

    public void updateProject(@NotNull PayloadProject project, ProjectsCallback callback) {
        projectDbManager.updateProject(converter.fromPayloadProjectToProjectEntity(project));
        calendarNetworkManager.updateProject(callback, project);
    }

    public void createSection(ProjectsCallback callback, String name, String status, String order, String projectId) {
//        projectDbManager.inserSection(converter.fromPayloadProjectToProjectEntity(project));
        calendarNetworkManager.createSection(callback, name, status, order, projectId);
    }


    public void updateTasksOrder(List<String> list) {
        calendarNetworkManager.updateTasksOrder(converter.fromListToJSONArray(list));
    }

    @NotNull
    public LiveData<List<SectionEntity>> getSectionsDbLiveData(String projectId) {
        return sectionDbManager.getAllSectionsByProjectIdMutableLiveData(projectId);
    }

    public void insertDbSection(@NotNull PayloadSection section) {
        sectionDbManager.insertDbSection(converter.fromPayloadSectionToSectionEntity(section));
    }

    public void updateSection(@NotNull PayloadSection payloadSection) {
        sectionDbManager.updateDbSection(converter.fromPayloadSectionToSectionEntity(payloadSection));
        calendarNetworkManager.updateSection(payloadSection);
    }

    public void updateSectionsOrder(@NotNull ArrayList<String> newList) {
        calendarNetworkManager.updateSectionsOrder(converter.fromListToJSONArray(newList));
    }

    @NotNull
    public LiveData<List<EpicLinksEntity>> getEpicLinksDbLiveData(@NotNull String projectId) {
        return epicLinksDbManager.getEpicLinksDbLiveData(projectId);
    }

    public void updateEpicLinksOrder(@NotNull ArrayList<String> newEpicLinkList) {
        calendarNetworkManager.updateEpicLinksOrder(converter.fromListToJSONArray(newEpicLinkList));
    }

    public void updateEpicLink(@NotNull PayloadEpicLinks payloadEpicLinks) {
        epicLinksDbManager.updateEpicLink(converter.fromPayloadEpicLinkToEpicLinkEntity(payloadEpicLinks));
        calendarNetworkManager.updateEpicLink(payloadEpicLinks);
    }

    public void createEpicLink(EpicLinksCallback callback, @NotNull String name, @NotNull String status, @NotNull String order, @NotNull String projectId) {
        calendarNetworkManager.createEpicLink(callback, name, status, order, projectId);
    }

    public void insertEpicLink(@Nullable PayloadEpicLinks epicLink) {
        epicLinksDbManager.insertEpicLink(converter.fromPayloadEpicLinkToEpicLinkEntity(epicLink));
    }
}
