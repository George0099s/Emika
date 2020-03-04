package com.emika.app.data.network.pojo.updateUserInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserModel {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("error")
    @Expose
    private String error;

    public Boolean getOk() {
        return ok;
    }

    public String getError() {
        return error;
    }
}
