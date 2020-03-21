package com.emika.app.data.network.pojo.member;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelShortMember {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private List<PayloadShortMember> payload = null;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public List<PayloadShortMember> getPayload() {
        return payload;
    }

    public void setPayload(List<PayloadShortMember> payload) {
        this.payload = payload;
    }
}
