package com.emika.app.presentation.viewmodel.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.CommentDbCallback;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.SubTaskCallback;
import com.emika.app.data.db.entity.CommentEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.Comment;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.calendar.TaskInfoRepository;
import com.emika.app.presentation.utils.Converter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TaskInfoViewModel extends ViewModel implements  Parcelable, EpicLinksDbCallback, SubTaskCallback, MemberDbCallback, CommentDbCallback {
    private MutableLiveData<PayloadTask> taskMutableLiveData;

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

    public PayloadTask getTask() {
        return task;
    }

    private PayloadTask task;
    private CalendarRepository repository;
    private MutableLiveData<Assignee> assigneeMutableLiveData;
    private MutableLiveData<Project> projectMutableLiveData;
    private MutableLiveData<List<PayloadSection>> sectionListMutableLiveData;
    private MutableLiveData<List<EpicLinksEntity>> epicLinksMutableLiveData;

    public List<MemberEntity> getMemberEntities() {
        return memberEntities;
    }

    private List<MemberEntity> memberEntities;
    private List<String> taskEpicLinks;

    private MutableLiveData<List<SubTask>> subTaskMutableLiveData;
    private MutableLiveData<List<Comment>> commentsMutableLiveData;

    public void setEpicLinksList(List<PayloadEpicLinks> epicLinksList) {
        this.epicLinksList = epicLinksList;
    }

    private List<PayloadEpicLinks>  epicLinksList;
    private Converter converter;
    private TaskInfoRepository taskInfoRepository;
    private String token;
    private static final String TAG = "TaskInfoViewModel";
    private MutableLiveData<String> subTaskIdMutableLiveData;

    @Inject
    Assignee assignee;
    @Inject
    Project projectDi;
    @Inject
    ProjectsDi projectsDagger;
    @Inject
    EpicLinks epicLinksDi;
    @Inject
    User user;
    public TaskInfoViewModel(String token) {
        this.token = token;
        EmikaApplication.instance.getComponent().inject(this);
        repository = new CalendarRepository(token);
        getMembers();
        taskMutableLiveData = new MutableLiveData<>();
        assigneeMutableLiveData = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();
        epicLinksMutableLiveData = new MutableLiveData<>();
        epicLinksList = new ArrayList<>();
        converter = new Converter();
        taskEpicLinks =  new ArrayList<>();
        commentsMutableLiveData = new MutableLiveData<>();
        subTaskMutableLiveData = new MutableLiveData<>();
        memberEntities = new ArrayList<>();
        subTaskIdMutableLiveData = new MutableLiveData<>();
    }
    protected TaskInfoViewModel(Parcel in) {
        task = in.readParcelable(PayloadTask.class.getClassLoader());
        token = in.readString();
    }

    public MutableLiveData<List<SubTask>> getSubTaskMutableLiveData(String taskId) {
        repository.getSubTask(taskId, this);
        return subTaskMutableLiveData;
    }

    public MutableLiveData<List<Comment>> getCommentsMutableLiveData(String taskId) {
        repository.getDbComments(this, taskId);
        return commentsMutableLiveData;
    }

    public MutableLiveData<PayloadTask> getTaskMutableLiveData() {
        taskMutableLiveData.setValue(task);
        return taskMutableLiveData;
    }

    public void updateTask(PayloadTask task){
        repository.updateTask(task);
    }
    public void updateDbTask(PayloadTask task){
        repository.updateSocketTask(task);
    }

    public void setTask(PayloadTask task) {
        this.task = task;
        for (int i = 0; i < memberEntities.size(); i++) {
            if (memberEntities.get(i).equals(task.getAssignee())){
                assignee.setFirstName(memberEntities.get(i).getFirstName());
                assignee.setLastName(memberEntities.get(i).getLastName());
                assignee.setJobTitle(memberEntities.get(i).getJobTitle());
                assignee.setId(memberEntities.get(i).getId());
                assignee.setPictureUrl(memberEntities.get(i).getPictureUrl());
            }
        }
    }


    public MutableLiveData<Assignee> getAssigneeMutableLiveData() {
         assigneeMutableLiveData.setValue(assignee);
        return assigneeMutableLiveData;
    }

    public void getMembers(){
        repository.getAllDbMembers(this);
    }

    public void getDbEpicLinks(){
        repository.getDbEpicLinks(this);
    }

    public MutableLiveData<List<EpicLinksEntity>> getEpicLinksMutableLiveData() {
          repository.getDbEpicLinks(this);
        return epicLinksMutableLiveData;
    }

    public MutableLiveData<Project> getProjectMutableLiveData() {
            projectDi.setProjectId(task.getProjectId());
            projectDi.setProjectSectionId(task.getSectionId());
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

    @Override
    public void onSubTaskListLoaded(List<SubTask> subTasks) {
        subTaskMutableLiveData.postValue(subTasks);
    }

    @Override
    public void onCommentListLoaded(List<Comment> comments) {
        commentsMutableLiveData.postValue(comments);
        repository.insertAllComments(converter.fromListCommentsToListEntity(comments));
    }

    @Override
    public void onSubTaskLoaded(String id) {
        subTaskIdMutableLiveData.postValue(id);
    }


    public void updateSubTask(SubTask subTask) {
        repository.updateSubTask(subTask);
    }

    public void addSubTask(SubTask subTask) {
        repository.addSubTask(subTask, this);
    }

    @Override
    public void onMembersLoaded(List<MemberEntity> memberEntityList) {
        memberEntities = memberEntityList;
    }

    public MutableLiveData<String>  getSubTaskIdMutableLiveData() {
        return subTaskIdMutableLiveData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(task, flags);
        dest.writeStringList(taskEpicLinks);
        dest.writeString(token);
        dest.writeParcelable(assignee, flags);
    }

    @Override
    public void onCommentsLoaded(@NotNull List<CommentEntity> comments) {
        commentsMutableLiveData.postValue(converter.fromListCommentsEntityToList(comments));
    }

    public void updateTaskOrder(@NotNull List<String> list) {
        repository.updateTasksOrder(list);
    }
}

