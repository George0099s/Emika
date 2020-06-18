package com.emika.app.data.network.pojo.project;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PayloadProject implements Parcelable{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("members")
    @Expose
    private List<String> members = new ArrayList<>();
    @SerializedName("active_fields")
    @Expose
    private List<String> activeFields = new ArrayList<>();
    @SerializedName("is_company_wide")
    @Expose
    private Boolean isCompanyWide;
    @SerializedName("is_personal")
    @Expose
    private Boolean isPersonal = false;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("default_section_id")
    @Expose
    private String defaultSectionId;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("description")
    @Expose
    private String description;

    public PayloadProject(){}

    protected PayloadProject(Parcel in) {
        id = in.readString();
        name = in.readString();
        status = in.readString();
        createdBy = in.readString();
        members = in.createStringArrayList();
        activeFields = in.createStringArrayList();
        byte tmpIsCompanyWide = in.readByte();
        isCompanyWide = tmpIsCompanyWide == 0 ? null : tmpIsCompanyWide == 1;
        byte tmpIsPersonal = in.readByte();
        isPersonal = tmpIsPersonal == 0 ? null : tmpIsPersonal == 1;
        updatedAt = in.readString();
        createdAt = in.readString();
        companyId = in.readString();
        defaultSectionId = in.readString();
        color = in.readString();
        description = in.readString();
    }

    public static final Creator<PayloadProject> CREATOR = new Creator<PayloadProject>() {
        @Override
        public PayloadProject createFromParcel(Parcel in) {
            return new PayloadProject(in);
        }

        @Override
        public PayloadProject[] newArray(int size) {
            return new PayloadProject[size];
        }
    };

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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Boolean getIsCompanyWide() {
        return isCompanyWide;
    }

    public void setIsCompanyWide(Boolean isCompanyWide) {
        this.isCompanyWide = isCompanyWide;
    }

    public Boolean getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(Boolean isPersonal) {
        this.isPersonal = isPersonal;
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



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getActiveFields() {
        return activeFields;
    }

    public void setActiveFields(List<String> activeFields) {
        this.activeFields = activeFields;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(createdBy);
        dest.writeStringList(members);
        dest.writeStringList(activeFields);
        dest.writeByte((byte) (isCompanyWide == null ? 0 : isCompanyWide ? 1 : 2));
        dest.writeByte((byte) (isPersonal == null ? 0 : isPersonal ? 1 : 2));
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeString(companyId);
        dest.writeString(defaultSectionId);
        dest.writeString(color);
        dest.writeString(description);
    }
}
