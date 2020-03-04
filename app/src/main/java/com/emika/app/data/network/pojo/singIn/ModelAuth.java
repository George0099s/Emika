package com.emika.app.data.network.pojo.singIn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelAuth {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private Boolean payload;
    @SerializedName("error")
    @Expose
    private String error;
    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Boolean getPayload() {
        return payload;
    }

    public void setPayload(Boolean payload) {
        this.payload = payload;
    }

    public String getError() {
        return error;
    }
}
