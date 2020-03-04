package com.emika.app.data.network.pojo.company;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("is_activation_email_sent")
    @Expose
    private Boolean isActivationEmailSent;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("bio")
    @Expose
    private String bio;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getIsActivationEmailSent() {
        return isActivationEmailSent;
    }

    public void setIsActivationEmailSent(Boolean isActivationEmailSent) {
        this.isActivationEmailSent = isActivationEmailSent;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}