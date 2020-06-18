package com.emika.app.presentation.utils;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.CommentEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadProjectCreation;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.Comment;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Contact;
import com.emika.app.data.network.pojo.user.Payload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converter {
    private static final String TAG = "Converter";
    private List<TaskEntity> taskEntities;
    private List<PayloadTask> payloadTaskList;
    private List<ProjectEntity> projectEntities;
    private List<PayloadProject> payloadProjects;
    private List<MemberEntity> memberEntities;
    private List<PayloadShortMember> payloadMembers;
    private List<PayloadEpicLinks> payloadEpicLinks;
    private List<EpicLinksEntity> epicLinksEntities;
    private List<Message> messages;
    private JSONArray subTasks;


    private PayloadTask payloadTask;
    private TaskEntity taskEntity;

    public Converter() {
        taskEntities = new ArrayList<>();
        projectEntities = new ArrayList<>();
        payloadProjects = new ArrayList<>();
        memberEntities = new ArrayList<>();
        payloadMembers = new ArrayList<>();
        payloadEpicLinks = new ArrayList<>();
        epicLinksEntities = new ArrayList<>();
        messages = new ArrayList<>();
        subTasks = new JSONArray();
    }

    public List<TaskEntity> fromPayloadTaskToTaskEntityList(List<PayloadTask> taskList) {
        taskEntities = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskList.get(i).getId());
            taskEntity.setPlanOrder(Integer.valueOf(taskList.get(i).getPlanOrder()));
            taskEntity.setAssignee(taskList.get(i).getAssignee());
            taskEntity.setCompanyId(taskList.get(i).getCompanyId());
            taskEntity.setCreatedAt(taskList.get(i).getCreatedAt());
            taskEntity.setCreatedBy(taskList.get(i).getCreatedBy());
            taskEntity.setPriority(taskList.get(i).getPriority());
            taskEntity.setStatus(taskList.get(i).getStatus());
            taskEntity.setDeadlineDate(taskList.get(i).getDeadlineDate());
            taskEntity.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
            taskEntity.setDeadlineTime(taskList.get(i).getDeadlineTime());
            taskEntity.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
            taskEntity.setSubTaskCount(taskList.get(i).getSubTaskCount());
            taskEntity.setDoneSubTaskCount(taskList.get(i).getDoneSubTaskCount());
            taskEntity.setDescription(taskList.get(i).getDescription());
            taskEntity.setDuration(taskList.get(i).getDuration());
            taskEntity.setEpicLinks(taskList.get(i).getEpicLinks());
            taskEntity.setDurationActual(taskList.get(i).getDurationActual());
            taskEntity.setPlanDate(taskList.get(i).getPlanDate());
            taskEntity.setName(taskList.get(i).getName());
            taskEntity.setProjectId(taskList.get(i).getProjectId());
            taskEntity.setSectionId(taskList.get(i).getSectionId());
            taskEntity.setCustomFieldsLink(taskList.get(i).getCustomFieldsLink());
            taskEntity.setCustomFieldsText(taskList.get(i).getCustomFieldsText());
            taskEntity.setWorkflowLink(taskList.get(i).getWorkflowLink());
            taskEntity.setWorkFlowText(taskList.get(i).getWorkFlowText());
            Log.d(TAG, "fromPayloadTaskToTaskEntityList: " + taskList.get(i).getCustomFieldsText());
            taskEntities.add(taskEntity);
        }
        return taskEntities;
    }

    public List<PayloadTask> fromTaskEntityToPayloadTaskList(List<TaskEntity> taskList) {
        payloadTaskList = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            PayloadTask payloadTask = new PayloadTask();
            payloadTask.setId(taskList.get(i).getId());
            payloadTask.setAssignee(taskList.get(i).getAssignee());
            payloadTask.setCompanyId(taskList.get(i).getCompanyId());
            payloadTask.setCreatedAt(taskList.get(i).getCreatedAt());
            payloadTask.setCreatedBy(taskList.get(i).getCreatedBy());
            payloadTask.setPriority(taskList.get(i).getPriority());
            payloadTask.setSubTaskCount(taskList.get(i).getSubTaskCount());
            payloadTask.setDoneSubTaskCount(taskList.get(i).getDoneSubTaskCount());
            payloadTask.setDeadlineDate(taskList.get(i).getDeadlineDate());
            payloadTask.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
            payloadTask.setDeadlineTime(taskList.get(i).getDeadlineTime());
            payloadTask.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
            payloadTask.setDescription(taskList.get(i).getDescription());
            payloadTask.setStatus(taskList.get(i).getStatus());
            payloadTask.setDuration(taskList.get(i).getDuration());
            payloadTask.setPlanDate(taskList.get(i).getPlanDate());
            payloadTask.setDurationActual(taskList.get(i).getDurationActual());
            payloadTask.setName(taskList.get(i).getName());
            payloadTask.setEpicLinks(taskList.get(i).getEpicLinks());
            payloadTask.setProjectId(taskList.get(i).getProjectId());
            payloadTask.setPlanOrder(String.valueOf(taskList.get(i).getPlanOrder()));
            payloadTask.setSectionId(taskList.get(i).getSectionId());
            payloadTask.setCustomFieldsLink(taskList.get(i).getCustomFieldsLink());
            payloadTask.setCustomFieldsText(taskList.get(i).getCustomFieldsText());
            payloadTask.setWorkflowLink(taskList.get(i).getWorkflowLink());
            payloadTask.setWorkFlowText(taskList.get(i).getWorkFlowText());
//            Log.d(TAG, "fromTaskEntityToPayloadTaskList: " + taskList.get(i).getCustomFieldsLink());
            payloadTaskList.add(payloadTask);
        }
        return payloadTaskList;
    }

    public PayloadTask fromTaskEntityToPayloadTask(TaskEntity task) {
        PayloadTask payloadTask = new PayloadTask();
        payloadTask.setId(task.getId());
        payloadTask.setAssignee(task.getAssignee());
        payloadTask.setCompanyId(task.getCompanyId());
        payloadTask.setCreatedAt(task.getCreatedAt());
        payloadTask.setProjectId(task.getProjectId());
        payloadTask.setSectionId(task.getSectionId());
        payloadTask.setStatus(task.getStatus());
        payloadTask.setSubTaskCount(task.getSubTaskCount());
        payloadTask.setDoneSubTaskCount(task.getDoneSubTaskCount());
        payloadTask.setCreatedBy(task.getCreatedBy());
        payloadTask.setDeadlineDate(task.getDeadlineDate());
        payloadTask.setDeadlineEmika(task.getDeadlineEmika());
        payloadTask.setDeadlineTime(task.getDeadlineTime());
        payloadTask.setDeadlinePeriod(task.getDeadlinePeriod());
        payloadTask.setDescription(task.getDescription());
        payloadTask.setDuration(task.getDuration());
        payloadTask.setPlanDate(task.getPlanDate());
        payloadTask.setPlanOrder(String.valueOf(task.getPlanOrder()));
        payloadTask.setEpicLinks(task.getEpicLinks());
        payloadTask.setName(task.getName());
        payloadTask.setCustomFieldsLink(task.getCustomFieldsLink());
        payloadTask.setCustomFieldsText(task.getCustomFieldsText());
        payloadTask.setWorkflowLink(task.getWorkflowLink());
        payloadTask.setWorkFlowText(task.getWorkFlowText());
        return payloadTask;
    }

    public TaskEntity fromPayloadTaskToTaskEntity(PayloadTask task) {
        this.taskEntity = new TaskEntity();
        taskEntity.setId(task.getId());
        taskEntity.setAssignee(task.getAssignee());
        taskEntity.setCompanyId(task.getCompanyId());
        taskEntity.setCreatedAt(task.getCreatedAt());
        taskEntity.setCreatedBy(task.getCreatedBy());
        taskEntity.setPriority(task.getPriority());
        taskEntity.setStatus(task.getStatus());
        taskEntity.setSubTaskCount(task.getSubTaskCount());
        taskEntity.setDoneSubTaskCount(task.getDoneSubTaskCount());
        taskEntity.setDeadlineDate(task.getDeadlineDate());
        taskEntity.setDeadlineEmika(task.getDeadlineEmika());
        taskEntity.setDeadlineTime(task.getDeadlineTime());
        taskEntity.setDeadlinePeriod(task.getDeadlinePeriod());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDuration(task.getDuration());
        taskEntity.setDurationActual(task.getDurationActual());
        taskEntity.setPlanDate(task.getPlanDate());
        taskEntity.setEpicLinks(task.getEpicLinks());
        taskEntity.setProjectId(task.getProjectId());
        taskEntity.setSectionId(task.getSectionId());
        taskEntity.setPlanOrder(Integer.valueOf(task.getPlanOrder()));
        taskEntity.setName(task.getName());
        taskEntity.setCustomFieldsLink(task.getCustomFieldsLink());
        taskEntity.setCustomFieldsText(task.getCustomFieldsText());
        taskEntity.setWorkflowLink(task.getWorkflowLink());
        taskEntity.setWorkFlowText(task.getWorkFlowText());
        return this.taskEntity;
    }

    public List<PayloadProject> fromProjectEntityToPayloadProjectList(List<ProjectEntity> projectEntities) {
        payloadProjects = new ArrayList<>();
        for (int i = 0; i < projectEntities.size(); i++) {
            PayloadProject project = new PayloadProject();
            project.setId(projectEntities.get(i).getId());
            project.setName(projectEntities.get(i).getName());
            project.setColor(projectEntities.get(i).getColor());
            project.setCompanyId(projectEntities.get(i).getCompanyId());
            project.setCreatedAt(projectEntities.get(i).getCreatedAt());
            project.setCreatedBy(projectEntities.get(i).getCreatedBy());
            project.setDefaultSectionId(projectEntities.get(i).getDefaultSectionId());
            project.setIsCompanyWide(projectEntities.get(i).getCompanyWide());
            project.setIsPersonal(projectEntities.get(i).getPersonal());
            project.setMembers(projectEntities.get(i).getMembers());
            project.setStatus(projectEntities.get(i).getStatus());
            project.setUpdatedAt(projectEntities.get(i).getUpdatedAt());
            project.setDescription(projectEntities.get(i).getDescription());
            project.setActiveFields(projectEntities.get(i).getActiveFields());
            payloadProjects.add(project);
        }
        return payloadProjects;
    }

    public List<ProjectEntity> fromPayloadProjectToProjectEntityList(List<PayloadProject> projects) {
        projectEntities = new ArrayList<>();
        for (int i = 0; i < projects.size(); i++) {
            ProjectEntity projectEntity = new ProjectEntity();
            projectEntity.setId(projects.get(i).getId());
            projectEntity.setName(projects.get(i).getName());
            projectEntity.setColor(projects.get(i).getColor());
            projectEntity.setCompanyId(projects.get(i).getCompanyId());
            projectEntity.setCreatedAt(projects.get(i).getCreatedAt());
            projectEntity.setCreatedBy(projects.get(i).getCreatedBy());
            projectEntity.setDefaultSectionId(projects.get(i).getDefaultSectionId());
            projectEntity.setCompanyWide(projects.get(i).getIsCompanyWide());
            projectEntity.setPersonal(projects.get(i).getIsPersonal());
            projectEntity.setMembers(projects.get(i).getMembers());
            projectEntity.setActiveFields(projects.get(i).getActiveFields());
            projectEntity.setDescription(projects.get(i).getDescription());
            projectEntity.setStatus(projects.get(i).getStatus());
            projectEntity.setUpdatedAt(projects.get(i).getUpdatedAt());
            projectEntities.add(projectEntity);
        }
//        Log.d(TAG, "fromPayloadProjectToProjectEntityList: " + projects.get(0).getActiveFields().size());
        return projectEntities;
    }

    public List<PayloadShortMember> fromMemberEntityToPayloadMember(List<MemberEntity> memberEntityList) {
        payloadMembers = new ArrayList<>();
        for (int i = 0; i < memberEntityList.size(); i++) {
            PayloadShortMember member = new PayloadShortMember();
            member.setId(memberEntityList.get(i).getId());
            member.setFirstName(memberEntityList.get(i).getFirstName());
            member.setLastName(memberEntityList.get(i).getLastName());
            member.setJobTitle(memberEntityList.get(i).getJobTitle());
            member.setPictureUrl(memberEntityList.get(i).getPictureUrl());
            payloadMembers.add(member);
        }
        return payloadMembers;
    }

    public List<MemberEntity> fromPayloadMemberToMemberEntity(List<PayloadShortMember> payloadShortMemberList) {
        memberEntities = new ArrayList<>();
        for (int i = 0; i < payloadShortMemberList.size(); i++) {
            MemberEntity member = new MemberEntity();
            member.setId(payloadShortMemberList.get(i).getId());
            member.setFirstName(payloadShortMemberList.get(i).getFirstName());
            member.setLastName(payloadShortMemberList.get(i).getLastName());
            member.setJobTitle(payloadShortMemberList.get(i).getJobTitle());
            member.setPictureUrl(payloadShortMemberList.get(i).getPictureUrl());
            memberEntities.add(member);
        }
        return memberEntities;
    }

    public UserEntity fromUserToUserEntity(Payload user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setPictureUrl(user.getPictureUrl());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setActivationCode(user.getActivationCode());
        userEntity.setAdmin(user.getIsAdmin());
        userEntity.setBio(user.getBio());
        userEntity.setJobTitle(user.getJobTitle());

        return userEntity;
    }

    public Payload fromUserEntityToPayloadUser(UserEntity user) {
        Payload userPayload = new Payload();
        userPayload.setPictureUrl(user.getPictureUrl());
        userPayload.setId(user.getId());
        userPayload.setFirstName(user.getFirstName());
        userPayload.setLastName(user.getLastName());
        userPayload.setActivationCode(user.getActivationCode());
        userPayload.setIsAdmin(user.getAdmin());
        userPayload.setBio(user.getBio());
        userPayload.setJobTitle(user.getJobTitle());

        return userPayload;
    }

    public List<EpicLinksEntity> fromPayloadEpicLinksToEpicLinksEntity(List<PayloadEpicLinks> payloadEpicLinks) {
        epicLinksEntities = new ArrayList<>();
        for (int i = 0; i < payloadEpicLinks.size(); i++) {
            EpicLinksEntity epicLinksEntity = new EpicLinksEntity();
            epicLinksEntity.setId(payloadEpicLinks.get(i).getId());
            epicLinksEntity.setProjectId(payloadEpicLinks.get(i).getProjectId());
            epicLinksEntity.setCreatedAt(payloadEpicLinks.get(i).getCreatedAt());
            epicLinksEntity.setEmoji(payloadEpicLinks.get(i).getEmoji());
            epicLinksEntity.setName(payloadEpicLinks.get(i).getName());
            epicLinksEntity.setStatus(payloadEpicLinks.get(i).getStatus());
            epicLinksEntity.setUpdatedAt(payloadEpicLinks.get(i).getUpdatedAt());
            epicLinksEntity.setOrder(payloadEpicLinks.get(i).getOrder());
            epicLinksEntities.add(epicLinksEntity);
        }
        return epicLinksEntities;
    }

    public List<PayloadEpicLinks> fromEpicLinksEntityToPayloadEpicLinks(List<EpicLinksEntity> epicLinksEntityList) {
        payloadEpicLinks = new ArrayList<>();
        for (int i = 0; i < epicLinksEntityList.size(); i++) {
            PayloadEpicLinks epicLinks = new PayloadEpicLinks();
            epicLinks.setId(epicLinksEntityList.get(i).getId());
            epicLinks.setProjectId(epicLinksEntityList.get(i).getProjectId());
            epicLinks.setCreatedAt(epicLinksEntityList.get(i).getCreatedAt());
            epicLinks.setEmoji(epicLinksEntityList.get(i).getEmoji());
            epicLinks.setName(epicLinksEntityList.get(i).getName());
            epicLinks.setStatus(epicLinksEntityList.get(i).getStatus());
            epicLinks.setUpdatedAt(epicLinksEntityList.get(i).getUpdatedAt());
            epicLinks.setOrder(epicLinksEntityList.get(i).getOrder());
            payloadEpicLinks.add(epicLinks);
        }
        return payloadEpicLinks;
    }

    public JSONArray fromListToJSONArray(List<String> epicLinks) {
        JSONArray jsonArray = new JSONArray();
        if (epicLinks != null)
            for (int i = 0; i < epicLinks.size(); i++) {
                jsonArray.put(epicLinks.get(i));
            }
        return jsonArray;
    }

    public JSONArray formListSubTaskToJsonArray(List<String> subTaskList) {
        subTasks = new JSONArray();
        for (String subTask : subTaskList) {
            subTasks.put(subTask);
        }
        return subTasks;
    }

    public JSONArray fromListContactToJSON(List<Contact> contactList) {

        JSONArray contacts = new JSONArray();
        JSONObject contactJson;
        for (int i = 0; i < contactList.size(); i++) {
            contactJson = new JSONObject();
            try {
                contactJson.put("type", contactList.get(i).getType());
                contactJson.put("value_type", contactList.get(i).getValueType());
                contactJson.put("value", contactList.get(i).getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            contacts.put(contactJson);
        }
        return contacts;
    }

    public List<SectionEntity> fromListPayloadSectionToSectionEntity(List<PayloadSection> sections) {
        List<SectionEntity> sectionEntities = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            PayloadSection section = sections.get(i);
            SectionEntity sectionEntity = new SectionEntity(section.getId(), section.getName(), section.getStatus(),
                    section.getOrder(), section.getProjectId(), section.getCompanyId(), section.getUpdatedAt(), section.getCreatedAt());
            sectionEntities.add(sectionEntity);
        }
        return sectionEntities;
    }

    public List<PayloadSection> fromListEntitySectionToPayloadSection(List<SectionEntity> sections) {
        List<PayloadSection> payloadSections = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            SectionEntity section = sections.get(i);
            PayloadSection payloadSection = new PayloadSection(section.getId(), section.getName(), section.getStatus(),
                    section.getOrder(), section.getProjectId(), section.getCompanyId(), section.getUpdatedAt(), section.getCreatedAt());
            payloadSections.add(payloadSection);
        }
        return payloadSections;
    }

    public List<ActualDurationEntity> fromPayloadListDurationToListDurationEntity(List<PayloadDurationActual> durationActualList) {
        List<ActualDurationEntity> durationEntities = new ArrayList<>();
        for (int i = 0; i < durationActualList.size(); i++) {
            PayloadDurationActual durationActual = durationActualList.get(i);
            ActualDurationEntity durationEntity = new ActualDurationEntity(durationActual.getId(), durationActual.getStatus(),
                    durationActual.getTaskId(), durationActual.getProjectId(), durationActual.getCompanyId(), durationActual.getDate(),
                    durationActual.getPerson(), durationActual.getValue(), durationActual.getCreatedAt(), durationActual.getCreatedBy());
            durationEntities.add(durationEntity);
        }
        return durationEntities;
    }

    public List<PayloadDurationActual> fromEntityListDurationToPayloadListDuration(List<ActualDurationEntity> durationActualList) {
        List<PayloadDurationActual> durationActuals = new ArrayList<>();
        for (int i = 0; i < durationActualList.size(); i++) {
            ActualDurationEntity durationActual = durationActualList.get(i);
            PayloadDurationActual payloadDurationActual = new PayloadDurationActual(durationActual.getId(), durationActual.getStatus(),
                    durationActual.getTaskId(), durationActual.getProjectId(), durationActual.getCompanyId(), durationActual.getDate(),
                    durationActual.getPerson(), durationActual.getValue(), durationActual.getCreatedAt(), durationActual.getCreatedBy());
            durationActuals.add(payloadDurationActual);
        }
        Log.d(TAG, "fromEntityListDurationToPayloadListDuration: " + durationActuals.size() + " " + durationActualList.size());
        return durationActuals;
    }

    public ActualDurationEntity fromPayloadDurationToDurationEntity(PayloadDurationActual durationActual) {
        ActualDurationEntity actualDurationEntity = new ActualDurationEntity(
                durationActual.getId(),
                durationActual.getStatus(),
                durationActual.getTaskId(),
                durationActual.getProjectId(),
                durationActual.getCompanyId(),
                durationActual.getDate(),
                durationActual.getPerson(),
                durationActual.getValue(),
                durationActual.getCreatedAt(),
                durationActual.getCreatedBy());
        return actualDurationEntity;
    }

    public MutableLiveData<List<PayloadTask>> fromTaskEntityToPayloadTaskList(LiveData<List<TaskEntity>> taskList) {
        MutableLiveData<List<PayloadTask>> payloadTaskList = new MutableLiveData<>();
        for (int i = 0; i < taskList.getValue().size(); i++) {
            PayloadTask payloadTask = new PayloadTask();
            payloadTask.setId(taskList.getValue().get(i).getId());
            payloadTask.setAssignee(taskList.getValue().get(i).getAssignee());
            payloadTask.setCompanyId(taskList.getValue().get(i).getCompanyId());
            payloadTask.setCreatedAt(taskList.getValue().get(i).getCreatedAt());
            payloadTask.setCreatedBy(taskList.getValue().get(i).getCreatedBy());
            payloadTask.setPriority(taskList.getValue().get(i).getPriority());
            payloadTask.setDeadlineDate(taskList.getValue().get(i).getDeadlineDate());
            payloadTask.setDeadlineEmika(taskList.getValue().get(i).getDeadlineEmika());
            payloadTask.setDeadlineTime(taskList.getValue().get(i).getDeadlineTime());
            payloadTask.setDeadlinePeriod(taskList.getValue().get(i).getDeadlinePeriod());
            payloadTask.setDescription(taskList.getValue().get(i).getDescription());
            payloadTask.setStatus(taskList.getValue().get(i).getStatus());
            payloadTask.setDuration(taskList.getValue().get(i).getDuration());
            payloadTask.setPlanDate(taskList.getValue().get(i).getPlanDate());
            payloadTask.setDurationActual(taskList.getValue().get(i).getDurationActual());
            payloadTask.setName(taskList.getValue().get(i).getName());
            payloadTask.setProjectId(taskList.getValue().get(i).getProjectId());
            payloadTask.setPlanOrder(String.valueOf(taskList.getValue().get(i).getPlanOrder()));
            payloadTask.setSectionId(taskList.getValue().get(i).getSectionId());
            payloadTask.setCustomFieldsLink(taskList.getValue().get(i).getCustomFieldsLink());
            payloadTask.setCustomFieldsText(taskList.getValue().get(i).getCustomFieldsText());
            payloadTask.setWorkflowLink(taskList.getValue().get(i).getWorkflowLink());
            payloadTask.setWorkFlowText(taskList.getValue().get(i).getWorkFlowText());
            payloadTaskList.getValue().add(payloadTask);
        }
        return payloadTaskList;

    }

    public ActualDurationEntity frompayload(PayloadDurationActual durationActual) {
        ActualDurationEntity actualDuration = new ActualDurationEntity(
                durationActual.getId(), durationActual.getStatus(),
                durationActual.getTaskId(), durationActual.getProjectId(),
                durationActual.getCompanyId(), durationActual.getDate(), durationActual.getPerson(),
                durationActual.getValue(), durationActual.getCreatedAt(), durationActual.getCreatedBy()
        );
        return actualDuration;
    }

    public List<CommentEntity> fromListCommentsToListEntity(List<Comment> comments) {
        List<CommentEntity> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentEntity commentEntity = new CommentEntity(
                    comment.getId(),
                    comment.getCompanyId(),
                    comment.getCreatedAt(),
                    comment.getCreatedBy(),
                    comment.getTaskId(),
                    comment.getText(),
                    comment.getUpdatedAt()
            );
            commentList.add(commentEntity);
        }
        return commentList;
    }

    public List<Comment> fromListCommentsEntityToList(List<CommentEntity> comments) {
        List<Comment> commentList = new ArrayList<>();
        for (CommentEntity commentEntity : comments) {
            Comment comment = new Comment(
                    commentEntity.getCompanyId(),
                    commentEntity.getCreatedAt(),
                    commentEntity.getCreatedBy(),
                    commentEntity.getId(),
                    commentEntity.getTaskId(),
                    commentEntity.getText(),
                    commentEntity.getUpdatedAt()
            );
            commentList.add(comment);
        }
        return commentList;
    }


    public CommentEntity fromCommentToCommentEntity(Comment comment){
        CommentEntity commentEntity = new CommentEntity(
                comment.getId(),
                comment.getCompanyId(),
                comment.getCreatedAt(),
                comment.getCreatedBy(),
                comment.getTaskId(),
                comment.getText(),
                comment.getUpdatedAt()
        );
        return commentEntity;
    }

    public Comment fromCommentToCommentEntity(CommentEntity commentEntity){
        Comment comment = new Comment(
                commentEntity.getCompanyId(),
                commentEntity.getCreatedAt(),
                commentEntity.getCreatedBy(),
                commentEntity.getId(),
                commentEntity.getTaskId(),
                commentEntity.getText(),
                commentEntity.getUpdatedAt()
        );
        return comment;
    }

    public ProjectEntity fromPayloadProjectToProjectEntity(PayloadProject project) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(project.getId());
        projectEntity.setName(project.getName());
        projectEntity.setColor(project.getColor());
        projectEntity.setCompanyId(project.getCompanyId());
        projectEntity.setCreatedAt(project.getCreatedAt());
        projectEntity.setCreatedBy(project.getCreatedBy());
        projectEntity.setDefaultSectionId(project.getDefaultSectionId());
        projectEntity.setCompanyWide(project.getIsCompanyWide());
        projectEntity.setPersonal(project.getIsPersonal());
        projectEntity.setMembers(project.getMembers());
        projectEntity.setActiveFields(project.getActiveFields());
        projectEntity.setDescription(project.getDescription());
        projectEntity.setStatus(project.getStatus());
        projectEntity.setUpdatedAt(project.getUpdatedAt());

        return projectEntity;
    }  public ProjectEntity fromPayloadProjectToProjectEntity(PayloadProjectCreation project) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(project.getId());
        projectEntity.setName(project.getName());
        projectEntity.setColor(project.getColor());
        projectEntity.setCreatedAt(project.getCreatedAt());
        projectEntity.setCreatedBy(project.getCreatedBy());
        projectEntity.setMembers(project.getMembers());
        projectEntity.setStatus(project.getStatus());
        projectEntity.setUpdatedAt(project.getUpdatedAt());

        return projectEntity;
    }

    public SectionEntity fromPayloadSectionToSectionEntity(PayloadSection section) {

        SectionEntity sectionEntity = new SectionEntity(section.getId(), section.getName(), section.getStatus(),
                    section.getOrder(), section.getProjectId(), section.getCompanyId(), section.getUpdatedAt(), section.getCreatedAt());
        return sectionEntity;
    }

    public PayloadSection fromSectionEntityToPayloadSection(SectionEntity section) {

        PayloadSection sectionPayload = new PayloadSection(section.getId(), section.getName(), section.getStatus(),
                    section.getOrder(), section.getProjectId(), section.getCompanyId(), section.getUpdatedAt(), section.getCreatedAt());
        return sectionPayload;
    }
}


