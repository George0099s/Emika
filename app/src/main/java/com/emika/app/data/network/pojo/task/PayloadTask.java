package com.emika.app.data.network.pojo.task;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
@Entity
public class PayloadTask implements Parcelable {
    @PrimaryKey
    @NonNull
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
    private String order;
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
    private String planTime;
    @SerializedName("plan_order")
    @Expose
    private String planOrder;
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
    private String deadlineTime;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("duration_actual")
    @Expose
    private Integer durationActual;
    @SerializedName("attachments")
    @Expose
    private List<String> attachments = null;
    @SerializedName("epic_links")
    @Expose
    private List<String> epicLinks = null;
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
    private String parentTaskId;
    @SerializedName("section_id")
    @Expose
    private String sectionId;
    @SerializedName("duration_logged")
    @Expose
    private String durationLogged;

    public PayloadTask(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        type = in.readString();
        status = in.readString();
        order = in.readString();
        assignee = in.readString();
        priority = in.readString();
        createdBy = in.readString();
        planDate = in.readString();
        byte tmpPlanEmika = in.readByte();
        planEmika = tmpPlanEmika == 0 ? null : tmpPlanEmika == 1;
        planPeriod = in.readString();
        planTime = in.readString();
        planOrder = in.readString();
        deadlineDate = in.readString();
        byte tmpDeadlineEmika = in.readByte();
        deadlineEmika = tmpDeadlineEmika == 0 ? null : tmpDeadlineEmika == 1;
        deadlinePeriod = in.readString();
        deadlineTime = in.readString();
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
        attachments = in.createStringArrayList();
        epicLinks = in.createStringArrayList();
        byte tmpEpicLinksEmika = in.readByte();
        epicLinksEmika = tmpEpicLinksEmika == 0 ? null : tmpEpicLinksEmika == 1;
        companyId = in.readString();
        projectId = in.readString();
        updatedAt = in.readString();
        createdAt = in.readString();
        parentTaskId = in.readString();
        sectionId = in.readString();
        durationLogged = in.readString();
    }

    public static final Creator<PayloadTask> CREATOR = new Creator<PayloadTask>() {
        @Override
        public PayloadTask createFromParcel(Parcel in) {
            return new PayloadTask(in);
        }

        @Override
        public PayloadTask[] newArray(int size) {
            return new PayloadTask[size];
        }
    };

    public PayloadTask() {
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
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

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    public String getPlanOrder() {
        return planOrder;
    }

    public void setPlanOrder(String planOrder) {
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

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDurationActual() {
        if (durationActual!=null)
        return durationActual;
        else
            return 0;
    }

    public void setDurationActual(Integer durationActual) {
        this.durationActual = durationActual;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<String> getEpicLinks() {
        return epicLinks;
    }

    public void setEpicLinks(List<String> epicLinks) {
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

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getDurationLogged() {
        return durationLogged;
    }

    public void setDurationLogged(String durationLogged) {
        this.durationLogged = durationLogged;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(order);
        dest.writeString(assignee);
        dest.writeString(priority);
        dest.writeString(createdBy);
        dest.writeString(planDate);
        dest.writeByte((byte) (planEmika == null ? 0 : planEmika ? 1 : 2));
        dest.writeString(planPeriod);
        dest.writeString(planTime);
        dest.writeString(planOrder);
        dest.writeString(deadlineDate);
        dest.writeByte((byte) (deadlineEmika == null ? 0 : deadlineEmika ? 1 : 2));
        dest.writeString(deadlinePeriod);
        dest.writeString(deadlineTime);
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
        dest.writeStringList(attachments);
        dest.writeStringList(epicLinks);
        dest.writeByte((byte) (epicLinksEmika == null ? 0 : epicLinksEmika ? 1 : 2));
        dest.writeString(companyId);
        dest.writeString(projectId);
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeString(parentTaskId);
        dest.writeString(sectionId);
        dest.writeString(durationLogged);
    }
}
