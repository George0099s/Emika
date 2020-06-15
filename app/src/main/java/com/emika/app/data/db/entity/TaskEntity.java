package com.emika.app.data.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.emika.app.data.db.utils.EpicLinksTypeConverter;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "Task")
public class TaskEntity implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private Integer order;
    private String description;
    private String type;
    private String status;
    private String assignee;
    private String priority;
    private String createdBy;
    private String planDate;
    private Boolean planEmika;
    private String planPeriod;
    private String planTime;
    private Integer planOrder;
    private String deadlineDate;
    private Boolean deadlineEmika;
    private String deadlinePeriod;
    private String deadlineTime;
    private Integer duration;
    private Integer durationActual;
    private Boolean epicLinksEmika;
    private String companyId;
    private String projectId;
    private String updatedAt;
    private String createdAt;
    private String parentTaskId;
    private String sectionId;
    @TypeConverters({EpicLinksTypeConverter.class})
    private List<String> epicLinks = new ArrayList<>();

    protected TaskEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        if (in.readByte() == 0) {
            order = null;
        } else {
            order = in.readInt();
        }
        description = in.readString();
        type = in.readString();
        status = in.readString();
        assignee = in.readString();
        priority = in.readString();
        createdBy = in.readString();
        planDate = in.readString();
        byte tmpPlanEmika = in.readByte();
        planEmika = tmpPlanEmika == 0 ? null : tmpPlanEmika == 1;
        planPeriod = in.readString();
        planTime = in.readString();
        if (in.readByte() == 0) {
            planOrder = null;
        } else {
            planOrder = in.readInt();
        }
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
        byte tmpEpicLinksEmika = in.readByte();
        epicLinksEmika = tmpEpicLinksEmika == 0 ? null : tmpEpicLinksEmika == 1;
        companyId = in.readString();
        projectId = in.readString();
        updatedAt = in.readString();
        createdAt = in.readString();
        parentTaskId = in.readString();
        sectionId = in.readString();
        epicLinks = in.createStringArrayList();
        durationLogged = in.readString();
    }

    public static final Creator<TaskEntity> CREATOR = new Creator<TaskEntity>() {
        @Override
        public TaskEntity createFromParcel(Parcel in) {
            return new TaskEntity(in);
        }

        @Override
        public TaskEntity[] newArray(int size) {
            return new TaskEntity[size];
        }
    };

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

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
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
        return durationActual;
    }

    public void setDurationActual(Integer durationActual) {
        this.durationActual = durationActual;
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

    private String durationLogged;

    public TaskEntity() {
    }

    public List<String> getEpicLinks() {
        return epicLinks;
    }

    public void setEpicLinks(List<String> epicLinks) {
        this.epicLinks = epicLinks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        if (order == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(order);
        }
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(assignee);
        dest.writeString(priority);
        dest.writeString(createdBy);
        dest.writeString(planDate);
        dest.writeByte((byte) (planEmika == null ? 0 : planEmika ? 1 : 2));
        dest.writeString(planPeriod);
        dest.writeString(planTime);
        if (planOrder == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(planOrder);
        }
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
        dest.writeByte((byte) (epicLinksEmika == null ? 0 : epicLinksEmika ? 1 : 2));
        dest.writeString(companyId);
        dest.writeString(projectId);
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeString(parentTaskId);
        dest.writeString(sectionId);
        dest.writeStringList(epicLinks);
        dest.writeString(durationLogged);
    }
}
