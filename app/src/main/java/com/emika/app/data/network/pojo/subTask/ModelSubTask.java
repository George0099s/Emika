package com.emika.app.data.network.pojo.subTask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelSubTask {

    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private PayloadSubTask payload;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public PayloadSubTask getPayload() {
        return payload;
    }

    public void setPayload(PayloadSubTask payload) {
        this.payload = payload;
    }

}
