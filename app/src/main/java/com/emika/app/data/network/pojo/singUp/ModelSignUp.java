package com.emika.app.data.network.pojo.singUp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelSignUp {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private PayloadSignUp payload;

    public PayloadSignUp getPayload() {
        return payload;
    }

    public void setPayload(PayloadSignUp payload) {
        this.payload = payload;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }
}
