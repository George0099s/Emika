package com.emika.app.data.network.pojo.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
    @SerializedName("company_id")
    @Expose
    private String company_id;
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

    @SerializedName("coworkers")
    @Expose
    private List<String> coworkers = new ArrayList<>();
    @SerializedName("managers")
    @Expose
    private List<String> leaders = new ArrayList<>();
    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("picture_url")
    @Expose
    private String pictureUrl;
    @SerializedName("unread_count")
    @Expose
    private int unreadCount;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @SerializedName("contacts")
    @Expose
    private List<Contact> contacts = new ArrayList<>();
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<String> getCoworkers() {
        return coworkers;
    }

    public void setCoworkers(List<String> coworkers) {
        this.coworkers = coworkers;
    }

    public List<String> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<String> leaders) {
        this.leaders = leaders;
    }
}