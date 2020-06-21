package com.emika.app.data.network.pojo.epiclinks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelEpicLinkCreation {
    @SerializedName("ok")
    @Expose
    private Boolean ok;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public PayloadEpicLinks getPayload() {
        return payload;
    }

    public void setPayload(PayloadEpicLinks payload) {
        this.payload = payload;
    }

    @SerializedName("payload")
    @Expose
    private PayloadEpicLinks payload = null;
}
