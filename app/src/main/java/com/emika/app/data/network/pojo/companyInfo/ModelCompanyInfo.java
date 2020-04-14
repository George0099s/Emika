package com.emika.app.data.network.pojo.companyInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelCompanyInfo {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("payload")
    @Expose
    private PayloadCompanyInfo payload;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public PayloadCompanyInfo getPayload() {
        return payload;
    }

    public void setPayload(PayloadCompanyInfo payload) {
        this.payload = payload;
    }
}
