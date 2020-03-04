package com.emika.app.data.network.pojo.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payload {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("invite_code")
    @Expose
    private String inviteCode;
    @SerializedName("activation_email_requested_at")
    @Expose
    private String activationEmailRequestedAt;
    @SerializedName("activation_code")
    @Expose
    private String activationCode;
    @SerializedName("email_confirmed")
    @Expose
    private Boolean emailConfirmed;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("is_leader")
    @Expose
    private Boolean isLeader;
    @SerializedName("is_remote")
    @Expose
    private Boolean isRemote;
    @SerializedName("extra_coworkers")
    @Expose
    private List<Object> extraCoworkers = null;
    @SerializedName("extra_leaders")
    @Expose
    private List<Object> extraLeaders = null;
    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

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

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getActivationEmailRequestedAt() {
        return activationEmailRequestedAt;
    }

    public void setActivationEmailRequestedAt(String activationEmailRequestedAt) {
        this.activationEmailRequestedAt = activationEmailRequestedAt;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Boolean getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }

    public Boolean getIsRemote() {
        return isRemote;
    }

    public void setIsRemote(Boolean isRemote) {
        this.isRemote = isRemote;
    }

    public List<Object> getExtraCoworkers() {
        return extraCoworkers;
    }

    public void setExtraCoworkers(List<Object> extraCoworkers) {
        this.extraCoworkers = extraCoworkers;
    }

    public List<Object> getExtraLeaders() {
        return extraLeaders;
    }

    public void setExtraLeaders(List<Object> extraLeaders) {
        this.extraLeaders = extraLeaders;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
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