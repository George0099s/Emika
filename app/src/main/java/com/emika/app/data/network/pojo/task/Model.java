package com.emika.app.data.network.pojo.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private List<PayloadTask> payloadTask = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<PayloadTask> getPayloadTask() {
        return payloadTask;
    }

    public void setPayloadTask(List<PayloadTask> payloadTask) {
        this.payloadTask = payloadTask;
    }
}
