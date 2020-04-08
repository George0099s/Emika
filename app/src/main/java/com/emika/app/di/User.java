package com.emika.app.di;

import com.emika.app.data.network.pojo.user.Contact;

import java.util.List;


public class User {
    private String id;
    private String status;
    private Boolean isAdmin;
    private String inviteCode;
    private String activationEmailRequestedAt;
    private String activationCode;
    private Boolean emailConfirmed;
    private String email;
    private String firstName;
    private String lastName;
    private String lang;
    private String gender;
    private String bio;
    private String jobTitle;
    private Boolean isLeader;
    private Boolean isRemote;
    private List<Object> extraCoworkers = null;
    private List<Object> extraLeaders = null;
    private List<Contact> contacts = null;
    private String context;
    private String updatedAt;
    private String pictureUrl;

    public User(String id, String status, Boolean isAdmin, String inviteCode, String activationEmailRequestedAt, String activationCode,
                Boolean emailConfirmed, String email, String firstName, String lastName, String lang, String gender, String bio, String jobTitle,
                Boolean isLeader, Boolean isRemote,
                List<Object> extraCoworkers, List<Object> extraLeaders, String context, String updatedAt, String createdAt) {
        this.id = id;
        this.status = status;
        this.isAdmin = isAdmin;
        this.inviteCode = inviteCode;
        this.activationEmailRequestedAt = activationEmailRequestedAt;
        this.activationCode = activationCode;
        this.emailConfirmed = emailConfirmed;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lang = lang;
        this.gender = gender;
        this.bio = bio;
        this.jobTitle = jobTitle;
        this.isLeader = isLeader;
        this.isRemote = isRemote;
        this.extraCoworkers = extraCoworkers;
        this.extraLeaders = extraLeaders;
        this.context = context;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public User() {

    }

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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
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

    public Boolean getLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }

    public Boolean getRemote() {
        return isRemote;
    }

    public void setRemote(Boolean remote) {
        isRemote = remote;
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

    private String createdAt;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
