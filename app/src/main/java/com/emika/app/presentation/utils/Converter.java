package com.emika.app.presentation.utils;

import android.hardware.ConsumerIrManager;
import android.util.Log;

import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadMember;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;

import org.json.JSONArray;

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

    private PayloadTask payloadTask;
    private TaskEntity taskEntity;

    public Converter() {
        taskEntities = new ArrayList<>();
        payloadTaskList = new ArrayList<>();
        projectEntities = new ArrayList<>();
        payloadTaskList = new ArrayList<>();
        payloadProjects = new ArrayList<>();
        memberEntities = new ArrayList<>();
        payloadMembers = new ArrayList<>();
        payloadEpicLinks = new ArrayList<>();
        epicLinksEntities = new ArrayList<>();
    }

    public List<TaskEntity> fromPayloadTaskToTaskEntityList(List<PayloadTask> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskList.get(i).getId());
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
            taskEntity.setDescription(taskList.get(i).getDescription());
            taskEntity.setDuration(taskList.get(i).getDuration());
            taskEntity.setPlanDate(taskList.get(i).getPlanDate());
            taskEntity.setName(taskList.get(i).getName());
            taskEntities.add(taskEntity);
        }
        return taskEntities;
    }

    public List<PayloadTask> fromTaskEntityToPayloadTaskList(List<TaskEntity> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            PayloadTask payloadTask = new PayloadTask();
            payloadTask.setId(taskList.get(i).getId());
            payloadTask.setAssignee(taskList.get(i).getAssignee());
            payloadTask.setCompanyId(taskList.get(i).getCompanyId());
            payloadTask.setCreatedAt(taskList.get(i).getCreatedAt());
            payloadTask.setCreatedBy(taskList.get(i).getCreatedBy());
            payloadTask.setPriority(taskList.get(i).getPriority());
            payloadTask.setDeadlineDate(taskList.get(i).getDeadlineDate());
            payloadTask.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
            payloadTask.setDeadlineTime(taskList.get(i).getDeadlineTime());
            payloadTask.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
            payloadTask.setDescription(taskList.get(i).getDescription());
            payloadTask.setStatus(taskList.get(i).getStatus());
            payloadTask.setDuration(taskList.get(i).getDuration());
            payloadTask.setPlanDate(taskList.get(i).getPlanDate());
            payloadTask.setName(taskList.get(i).getName());
            payloadTaskList.add(payloadTask);
        }
        return payloadTaskList;
    }

    public PayloadTask fromTaskEntityToPayloadTask(TaskEntity task) {
        this.payloadTask = new PayloadTask();
        payloadTask.setId(task.getId());
        payloadTask.setAssignee(task.getAssignee());
        payloadTask.setCompanyId(task.getCompanyId());
        payloadTask.setCreatedAt(task.getCreatedAt());
        payloadTask.setCreatedBy(task.getCreatedBy());
        payloadTask.setDeadlineDate(task.getDeadlineDate());
        payloadTask.setDeadlineEmika(task.getDeadlineEmika());
        payloadTask.setDeadlineTime(task.getDeadlineTime());
        payloadTask.setDeadlinePeriod(task.getDeadlinePeriod());
        payloadTask.setDescription(task.getDescription());
        payloadTask.setDuration(task.getDuration());
        payloadTask.setPlanDate(task.getPlanDate());
        payloadTask.setName(task.getName());
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
        taskEntity.setDeadlineDate(task.getDeadlineDate());
        taskEntity.setDeadlineEmika(task.getDeadlineEmika());
        taskEntity.setDeadlineTime(task.getDeadlineTime());
        taskEntity.setDeadlinePeriod(task.getDeadlinePeriod());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDuration(task.getDuration());
        taskEntity.setPlanDate(task.getPlanDate());
        taskEntity.setName(task.getName());
        return taskEntity;
    }

    public List<PayloadProject> fromProjectEntityToPayloadProjectList(List<ProjectEntity> projectEntities){
        for (int i = 0; i < projectEntities.size(); i++) {
            PayloadProject project = new PayloadProject();
            project.setId(projectEntities.get(i).getId());
            Log.d(TAG, "fromProjectEntityToPayloadProjectList: " + project.getId());
            project.setName(projectEntities.get(i).getName());
            project.setColor(projectEntities.get(i).getColor());
            project.setCompanyId(projectEntities.get(i).getCompanyId());
            project.setCreatedAt(projectEntities.get(i).getCreatedAt());
            project.setCreatedBy(projectEntities.get(i).getCreatedBy());
            project.setDefaultSectionId(projectEntities.get(i).getDefaultSectionId());
            project.setIsCompanyWide(projectEntities.get(i).getCompanyWide());
            project.setIsPersonal(projectEntities.get(i).getPersonal());
            project.setMembers(Collections.singletonList(projectEntities.get(i).getMembers()));
            project.setStatus(projectEntities.get(i).getStatus());
            project.setUpdatedAt(projectEntities.get(i).getUpdatedAt());
            payloadProjects.add(project);
        }
        return payloadProjects;
    }

    public List<ProjectEntity> fromPayloadProjectToProjectEntityList(List<PayloadProject> projects){
        for (int i = 0; i < projects.size(); i++) {
            ProjectEntity projectEntity = new ProjectEntity();
            projectEntity.setId(projects.get(i).getId());
            Log.d(TAG, "fromPayloadProjectToProjectEntityList: " + projectEntity.getId());
            projectEntity.setName(projects.get(i).getName());
            projectEntity.setColor(projects.get(i).getColor());
            projectEntity.setCompanyId(projects.get(i).getCompanyId());
            projectEntity.setCreatedAt(projects.get(i).getCreatedAt());
            projectEntity.setCreatedBy(projects.get(i).getCreatedBy());
            projectEntity.setDefaultSectionId(projects.get(i).getDefaultSectionId());
            projectEntity.setCompanyWide(projects.get(i).getIsCompanyWide());
            projectEntity.setPersonal(projects.get(i).getIsPersonal());
            projectEntity.setMembers(String.valueOf(projects.get(i).getMembers().size()));
            projectEntity.setStatus(projects.get(i).getStatus());
            projectEntity.setUpdatedAt(projects.get(i).getUpdatedAt());
            projectEntities.add(projectEntity);
        }
        return projectEntities;
    }

    public List<PayloadShortMember> fromMemberEntityToPayloadMember(List<MemberEntity> memberEntityList){
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
    public List<MemberEntity> fromPayloadMemberToMemberEntity(List<PayloadShortMember> payloadShortMemberList){
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

    public List<EpicLinksEntity> fromPayloadEpicLinksToEpicLinksEntity(List<PayloadEpicLinks> payloadEpicLinks){
//       epicLinksEntities = new ArrayList<>();
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

    public List<PayloadEpicLinks> fromEpicLinksEntityToPayloadEpicLinks(List<EpicLinksEntity> epicLinksEntityList){
//        payloadEpicLinks = new ArrayList<>();
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

    public JSONArray fromListToJSONArray(List<String> epicLinks){
        JSONArray jsonArray = new JSONArray();
        if (epicLinks!= null)
        for (int i = 0; i < epicLinks.size(); i++) {
            jsonArray.put(epicLinks.get(i));
        }
        return jsonArray;
    }
}


