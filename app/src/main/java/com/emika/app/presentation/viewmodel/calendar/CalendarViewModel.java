package com.emika.app.presentation.viewmodel.calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.calendar.DurationActualCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.List;

import javax.inject.Inject;

public class CalendarViewModel extends ViewModel implements TaskListCallback, TaskDbCallback,
        TaskCallback, MemberDbCallback,  EpicLinksDbCallback, ProjectDbCallback, ActualDurationDbCallback, DurationActualCallback, SectionDbCallback, Parcelable{
    private static final String TAG = "CalendarViewModel";
    @Inject
    Assignee assignee;
    @Inject
    User userDi;
    private MutableLiveData<List<PayloadTask>> taskListMutableLiveData;
    private MutableLiveData<List<PayloadTask>> filteredTaskListMutableLiveData;
    private LiveData<List<TaskEntity>> taskEntity;
    private MutableLiveData<Boolean> currentColumn;
    private MutableLiveData<List<PayloadShortMember>> membersMutableLiveData;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private MutableLiveData<List<EpicLinksEntity>> epicLinksMutableLiveData;
    private MutableLiveData<List<ProjectEntity>> projectMutableLiveData;
    private MutableLiveData<List<PayloadSection>> sectionListMutableLiveData;
    private MutableLiveData<PayloadTask> taskMutableLiveData;
    private LiveData<List<TaskEntity>> taskLiveData;
    private MutableLiveData<List<PayloadDurationActual>> durationMutableLiveData;
    private CalendarRepository repository;
    private String token;
    private Context context;
    private Converter converter;

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

    public void setFirstRun(Boolean firstRun) {
        this.firstRun = firstRun;
    }

    private Boolean firstRun = true;
    EmikaApplication emikaApplication = EmikaApplication.instance;


    public CalendarViewModel(String token) {
        emikaApplication.getComponent().inject(this);
        this.token = token;
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.currentColumn = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
        membersMutableLiveData = new MutableLiveData<>();
        epicLinksMutableLiveData = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();
        durationMutableLiveData = new MutableLiveData<>();
        sectionListMutableLiveData = new MutableLiveData<>();
        filteredTaskListMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
        taskMutableLiveData = new MutableLiveData<>();
        taskEntity = new MutableLiveData<>();
        taskLiveData = new MutableLiveData<>();
    }
    public LiveData<List<TaskEntity>> liveData(String assignee){
        Log.d(TAG, "liveData: ");
        return repository.getLiveDataTasksByAssignee(EmikaApplication.instance.getDatabase().taskDao(), assignee);
    }

    public void getTaskDbLiveDataByAssignee(String assignee){
        taskLiveData  = new MutableLiveData<>();
        taskLiveData = repository.getLiveDataTasksByAssignee(EmikaApplication.instance.getDatabase().taskDao(), assignee);
    }

    protected CalendarViewModel(Parcel in) {
        assignee = in.readParcelable(Assignee.class.getClassLoader());
        token = in.readString();
        byte tmpFirstRun = in.readByte();
        firstRun = tmpFirstRun == 0 ? null : tmpFirstRun == 1;
    }



    public void getAllDbTask(){
        repository.getDbTaskList(this);
    }

    public void downloadTasks(){
        repository.downloadTasks(this);
    }

    public MutableLiveData<List<PayloadTask>> getListMutableLiveData() {
            return taskListMutableLiveData;
    }

    public void getAllDbTaskByAssignee(String assignee){
        repository.getDBTaskListById(this, assignee);
    }

    public void getDbTaskById(String id){
        repository.getDBTaskById(this, id);
    }

    public void downloadDurationActualLog (){
        repository.downloadDurationActualLog(this);
    }


    public void downloadTasksByAssignee(String assignee){
        repository.downloadTasksByAssignee(this, assignee);
    }

    public void getAllDbTaskByAssigneeDate(String assignee, String planDate){
        repository.getDBTaskListByDateId(this, assignee, planDate);
    }


    public MutableLiveData<List<PayloadTask>> getTasks() {
        return taskListMutableLiveData;
    }

    public void insertDbDuration(PayloadDurationActual durationActual){
        repository.insertDbDurations(converter.fromPayloadDurationToDurationEntity(durationActual),this);
    }
    public void updateDbDuration(PayloadDurationActual durationActual){
        repository.updateDbDurations(converter.fromPayloadDurationToDurationEntity(durationActual));
    }

    public void updateTask(PayloadTask task) {
        repository.updateTask(task);
    }

    public void updateSocketTask(PayloadTask task) {
        repository.updateTask(task);
    }

    public void updateDbTask(PayloadTask task) {
        repository.updateDbTask(task);
    }

    @Override
    public void setTaskList(List<PayloadTask> taskList) {
//        ArrayList<PayloadTask> plannedTask = new ArrayList<>();
//        for (int j = 0; j < taskList.size(); j++) {
//                if (taskList.get(j).getAssignee().equals(userDi.getId()))
//                plannedTask.add(taskList.get(j));
//        }
        taskListMutableLiveData.postValue(taskList);
        repository.sedDbData(taskList);

    }

    @Override
    public void onTasksLoaded(List<TaskEntity> taskList) {
            taskListMutableLiveData.postValue(converter.fromTaskEntityToPayloadTaskList(taskList));
    }

    @Override
    public void onFilteredTasksLoaded(List<TaskEntity> taskList) {
        taskListMutableLiveData.postValue(converter.fromTaskEntityToPayloadTaskList(taskList));
    }

    @Override
    public void onOneTaskLoaded(TaskEntity taskEntity) {
        taskMutableLiveData.postValue(converter.fromTaskEntityToPayloadTask(taskEntity));
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

    public MutableLiveData<List<PayloadDurationActual>> getDurationMutableLiveData() {
        return durationMutableLiveData;
    }

    public void getAllDbDurations(){
        repository.getAllDbDurations(this);
    }

    public void getDbDurationsByAssignee(String assigneeId, String date){
        repository.getAllDbDurationsByAssignee(this, assigneeId, date);
    }

    public void updateSubTask(SubTask subTask) {
        repository.updateSubTask(subTask);
    }

    @Override
    public void onActualDurationLoaded(List<ActualDurationEntity> actualDurationEntities) {
//        Log.d(TAG, "onActualDurationLoaded: " + actualDurationEntities.size());
//        if (actualDurationEntities.size() == 0)
//            repository.downloadDurationActualLog(this);
//        else
        durationMutableLiveData.postValue(converter.fromEntityListDurationToPayloadListDuration(actualDurationEntities));
    }

    @Override
    public void onDurationLogDownloaded(List<PayloadDurationActual> durationActualList) {
        durationMutableLiveData.postValue(durationActualList);
    }

    public MutableLiveData<List<PayloadSection>> getSectionListMutableLiveData() {
        repository.getAllSections(this);
        return sectionListMutableLiveData;
    }

    @Override
    public void onSectionLoaded(List<SectionEntity> sections) {
        sectionListMutableLiveData.postValue(converter.fromListEntitySectionToPayloadSection(sections));
    }

    public void addDbTask(PayloadTask task) {
        repository.addDbTask(task);
    }

    public MutableLiveData<List<PayloadTask>> getFilteredTaskListMutableLiveData() {
        return filteredTaskListMutableLiveData;
    }

    public void setFilteredTaskListMutableLiveData(MutableLiveData<List<PayloadTask>> filteredTaskListMutableLiveData) {
        this.filteredTaskListMutableLiveData = filteredTaskListMutableLiveData;
    }



    public MutableLiveData<PayloadTask> taskMutableLiveData() {
        return taskMutableLiveData;
    }

    public void deleteDuration(PayloadDurationActual durationActual) {
        repository.deleteDuration(durationActual);
    }

    public LiveData<List<TaskEntity>> getTaskLiveData() {
        return taskLiveData;
    }

    public void setTaskLiveData(LiveData<List<TaskEntity>> taskLiveData) {
        this.taskLiveData = taskLiveData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(assignee, flags);
        dest.writeString(token);
        dest.writeByte((byte) (firstRun == null ? 0 : firstRun ? 1 : 2));
    }
}
