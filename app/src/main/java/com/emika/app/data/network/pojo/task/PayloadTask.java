package com.emika.app.data.network.pojo.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayloadTask {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("assignee")
    @Expose
    private String assignee;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("plan_date")
    @Expose
    private String planDate;
    @SerializedName("plan_emika")
    @Expose
    private Boolean planEmika;
    @SerializedName("plan_period")
    @Expose
    private String planPeriod;
    @SerializedName("plan_time")
    @Expose
    private Object planTime;
    @SerializedName("plan_order")
    @Expose
    private Integer planOrder;
    @SerializedName("deadline_date")
    @Expose
    private String deadlineDate;
    @SerializedName("deadline_emika")
    @Expose
    private Boolean deadlineEmika;
    @SerializedName("deadline_period")
    @Expose
    private String deadlinePeriod;
    @SerializedName("deadline_time")
    @Expose
    private Object deadlineTime;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("duration_actual")
    @Expose
    private Integer durationActual;
    @SerializedName("attachments")
    @Expose
    private List<Object> attachments = null;
    @SerializedName("epic_links")
    @Expose
    private List<Object> epicLinks = null;
    @SerializedName("epic_links_emika")
    @Expose
    private Boolean epicLinksEmika;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("parent_task_id")
    @Expose
    private Object parentTaskId;
    @SerializedName("section_id")
    @Expose
    private Object sectionId;
    @SerializedName("duration_logged")
    @Expose
    private Object durationLogged;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public Boolean getPlanEmika() {
        return planEmika;
    }

    public void setPlanEmika(Boolean planEmika) {
        this.planEmika = planEmika;
    }

    public String getPlanPeriod() {
        return planPeriod;
    }

    public void setPlanPeriod(String planPeriod) {
        this.planPeriod = planPeriod;
    }

    public Object getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Object planTime) {
        this.planTime = planTime;
    }

    public Integer getPlanOrder() {
        return planOrder;
    }

    public void setPlanOrder(Integer planOrder) {
        this.planOrder = planOrder;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Boolean getDeadlineEmika() {
        return deadlineEmika;
    }

    public void setDeadlineEmika(Boolean deadlineEmika) {
        this.deadlineEmika = deadlineEmika;
    }

    public String getDeadlinePeriod() {
        return deadlinePeriod;
    }

    public void setDeadlinePeriod(String deadlinePeriod) {
        this.deadlinePeriod = deadlinePeriod;
    }

    public Object getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(Object deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDurationActual() {
        return durationActual;
    }

    public void setDurationActual(Integer durationActual) {
        this.durationActual = durationActual;
    }

    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }

    public List<Object> getEpicLinks() {
        return epicLinks;
    }

    public void setEpicLinks(List<Object> epicLinks) {
        this.epicLinks = epicLinks;
    }

    public Boolean getEpicLinksEmika() {
        return epicLinksEmika;
    }

    public void setEpicLinksEmika(Boolean epicLinksEmika) {
        this.epicLinksEmika = epicLinksEmika;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public Object getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Object parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Object getSectionId() {
        return sectionId;
    }

    public void setSectionId(Object sectionId) {
        this.sectionId = sectionId;
    }

    public Object getDurationLogged() {
        return durationLogged;
    }

    public void setDurationLogged(Object durationLogged) {
        this.durationLogged = durationLogged;
    }
}
