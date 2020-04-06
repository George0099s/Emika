package com.emika.app.data.network.pojo.durationActualLog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelDurationActual {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private List<PayloadDurationActual> payload = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<PayloadDurationActual> getPayload() {
        return payload;
    }

    public void setPayload(List<PayloadDurationActual> payload) {
        this.payload = payload;
    }
}
