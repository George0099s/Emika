package com.emika.app.auth.data.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadEmail {
    @SerializedName("exists")
    @Expose
    private Boolean exists;

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
}
