package com.emika.app.di;

import android.os.Parcel;
import android.os.Parcelable;

public class Assignee implements Parcelable {
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String pictureUrl;
    private String id;

    protected Assignee(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        jobTitle = in.readString();
        pictureUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<Assignee> CREATOR = new Creator<Assignee>() {
        @Override
        public Assignee createFromParcel(Parcel in) {
            return new Assignee(in);
        }

        @Override
        public Assignee[] newArray(int size) {
            return new Assignee[size];
        }
    };

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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Assignee() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(jobTitle);
        dest.writeString(pictureUrl);
        dest.writeString(id);
    }
}
