package com.emika.app.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Section")
public class SectionEntity {
    @NonNull
    @PrimaryKey
    private String id;
    private String name;

    public SectionEntity(@NonNull String id, String name, String status, Integer order, String projectId, String companyId, String updatedAt, String createdAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.order = order;
        this.projectId = projectId;
        this.companyId = companyId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    private String status;
    private Integer order;
    private String projectId;
    private String companyId;
    private String updatedAt;
    private String createdAt;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
