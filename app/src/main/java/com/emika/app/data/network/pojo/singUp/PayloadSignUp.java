package com.emika.app.data.network.pojo.singUp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadSignUp {
    @SerializedName("email_sent")
    @Expose
    private Boolean isEmailSent;

    public Boolean getEmailSent() {
        return isEmailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        isEmailSent = emailSent;
    }
}
