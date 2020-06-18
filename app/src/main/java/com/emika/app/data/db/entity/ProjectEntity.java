package com.emika.app.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.emika.app.data.db.utils.ActiveFieldsTypeConverter;
import com.emika.app.data.db.utils.EpicLinksTypeConverter;
import com.emika.app.data.db.utils.MembersTypeConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Project")
public class ProjectEntity {
    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String status;
    private String createdBy;
    @TypeConverters({MembersTypeConverter.class})
    private List<String> members = new ArrayList<>();
    @TypeConverters({ActiveFieldsTypeConverter.class})
    private List<String> activeFields = new ArrayList<>();
    private String description ;
    private Boolean isCompanyWide;
    private Boolean isPersonal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getCompanyWide() {
        return isCompanyWide;
    }

    public void setCompanyWide(Boolean companyWide) {
        isCompanyWide = companyWide;
    }

    public Boolean getPersonal() {
        return isPersonal;
    }

    public void setPersonal(Boolean personal) {
        isPersonal = personal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDefaultSectionId() {
        return defaultSectionId;
    }

    public void setDefaultSectionId(String defaultSectionId) {
        this.defaultSectionId = defaultSectionId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private String updatedAt;
    private String createdAt;
    private String companyId;
    private String defaultSectionId;
    private String color;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getActiveFields() {
        return activeFields;
    }

    public void setActiveFields(List<String> activeFields) {
        this.activeFields = activeFields;
    }
}
