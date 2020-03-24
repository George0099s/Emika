package com.emika.app.data.network.pojo.project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelSection {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private List<PayloadSection> payload = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<PayloadSection> getPayload() {
        return payload;
    }

    public void setPayload(List<PayloadSection> payload) {
        this.payload = payload;
    }
}
