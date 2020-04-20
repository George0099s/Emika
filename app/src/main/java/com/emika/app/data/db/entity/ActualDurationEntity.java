package com.emika.app.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Actual duration")
public class ActualDurationEntity {
    @NonNull
    @PrimaryKey
    private String id;
    private String status;
    private String taskId;
    private String projectId;
    private String companyId;
    private String date;
    private String person;
    private Integer value;
    private String createdAt;

    public ActualDurationEntity(@NonNull String id, String status, String taskId, String projectId, String companyId, String date, String person, Integer value, String createdAt, String createdBy) {
        this.id = id;
        this.status = status;
        this.taskId = taskId;
        this.projectId = projectId;
        this.companyId = companyId;
        this.date = date;
        this.person = person;
        this.value = value;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    private String createdBy;
}
