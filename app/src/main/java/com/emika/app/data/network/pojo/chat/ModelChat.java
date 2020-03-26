package com.emika.app.data.network.pojo.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelChat {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private PayloadChat payload;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public PayloadChat getPayload() {
        return payload;
    }

    public void setPayload(PayloadChat payload) {
        this.payload = payload;
    }
}
