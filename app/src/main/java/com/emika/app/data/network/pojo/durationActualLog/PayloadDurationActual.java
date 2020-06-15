package com.emika.app.data.network.pojo.durationActualLog;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadDurationActual implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("task_id")
    @Expose
    private String taskId;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("person")
    @Expose
    private String person;
    @SerializedName("value")
    @Expose
    private Integer value;

    public PayloadDurationActual(String id, String status, String taskId, String projectId, String companyId, String date, String person, Integer value, String createdAt, String createdBy) {
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

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by")
    @Expose
    private String createdBy;

    public PayloadDurationActual() {

    }

    protected PayloadDurationActual(Parcel in) {
        id = in.readString();
        status = in.readString();
        taskId = in.readString();
        projectId = in.readString();
        companyId = in.readString();
        date = in.readString();
        person = in.readString();
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readInt();
        }
        createdAt = in.readString();
        createdBy = in.readString();
    }

    public static final Creator<PayloadDurationActual> CREATOR = new Creator<PayloadDurationActual>() {
        @Override
        public PayloadDurationActual createFromParcel(Parcel in) {
            return new PayloadDurationActual(in);
        }

        @Override
        public PayloadDurationActual[] newArray(int size) {
            return new PayloadDurationActual[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(taskId);
        dest.writeString(projectId);
        dest.writeString(companyId);
        dest.writeString(date);
        dest.writeString(person);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(value);
        }
        dest.writeString(createdAt);
        dest.writeString(createdBy);
    }
}
