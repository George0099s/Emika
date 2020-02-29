package com.emika.app.auth.data.pojo.singIn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelAuth {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private Boolean payload;

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
}
