package com.emika.app.data.network.pojo.member;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadShortMember implements Parcelable {
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("picture_url")
    @Expose
    private String pictureUrl;
    @SerializedName("_id")
    @Expose
    private String id;

    protected PayloadShortMember(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        jobTitle = in.readString();
        pictureUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<PayloadShortMember> CREATOR = new Creator<PayloadShortMember>() {
        @Override
        public PayloadShortMember createFromParcel(Parcel in) {
            return new PayloadShortMember(in);
        }

        @Override
        public PayloadShortMember[] newArray(int size) {
            return new PayloadShortMember[size];
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
