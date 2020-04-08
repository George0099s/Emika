package com.emika.app.data.network.pojo.subTask;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubTask implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
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
    private Object planDate;
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
    private Object deadlineDate;
    @SerializedName("deadline_time")
    @Expose
    private Object deadlineTime;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("duration_actual")
    @Expose
    private Integer durationActual;
    @SerializedName("duration_actual_date")
    @Expose
    private Object durationActualDate;
    @SerializedName("attachments")
    @Expose
    private List<Object> attachments = null;
    @SerializedName("epic_links")
    @Expose
    private List<Object> epicLinks = null;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("parent_task_id")
    @Expose
    private String parentTaskId;

    public SubTask(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        status = in.readString();
        if (in.readByte() == 0) {
            order = null;
        } else {
            order = in.readInt();
        }
        assignee = in.readString();
        priority = in.readString();
        createdBy = in.readString();
        planPeriod = in.readString();
        if (in.readByte() == 0) {
            planOrder = null;
        } else {
            planOrder = in.readInt();
        }
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
        if (in.readByte() == 0) {
            durationActual = null;
        } else {
            durationActual = in.readInt();
        }
        companyId = in.readString();
        projectId = in.readString();
        sectionId = in.readString();
        updatedAt = in.readString();
        createdAt = in.readString();
        parentTaskId = in.readString();
    }

    public static final Creator<SubTask> CREATOR = new Creator<SubTask>() {
        @Override
        public SubTask createFromParcel(Parcel in) {
            return new SubTask(in);
        }

        @Override
        public SubTask[] newArray(int size) {
            return new SubTask[size];
        }
    };

    public SubTask() {

    }

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

    public Object getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Object planDate) {
        this.planDate = planDate;
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

    public Object getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Object deadlineDate) {
        this.deadlineDate = deadlineDate;
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

    public Object getDurationActualDate() {
        return durationActualDate;
    }

    public void setDurationActualDate(Object durationActualDate) {
        this.durationActualDate = durationActualDate;
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

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
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

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(status);
        if (order == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(order);
        }
        dest.writeString(assignee);
        dest.writeString(priority);
        dest.writeString(createdBy);
        dest.writeString(planPeriod);
        if (planOrder == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(planOrder);
        }
        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(duration);
        }
        if (durationActual == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(durationActual);
        }
        dest.writeString(companyId);
        dest.writeString(projectId);
        dest.writeString(sectionId);
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeString(parentTaskId);
    }
}
