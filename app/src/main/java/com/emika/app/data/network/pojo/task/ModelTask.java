package com.emika.app.data.network.pojo.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelTask {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private PayloadTask payloadTask;
    @SerializedName("error")
    @Expose
    private String error;


    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public PayloadTask getPayloadTask() {
        return payloadTask;
    }

    public void setPayloadTask(PayloadTask payloadTask) {
        this.payloadTask = payloadTask;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
