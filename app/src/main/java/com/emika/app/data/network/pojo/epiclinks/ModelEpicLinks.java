package com.emika.app.data.network.pojo.epiclinks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelEpicLinks {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private List<PayloadEpicLinks> payload = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<PayloadEpicLinks> getPayload() {
        return payload;
    }

    public void setPayload(List<PayloadEpicLinks> payload) {
        this.payload = payload;
    }

}
