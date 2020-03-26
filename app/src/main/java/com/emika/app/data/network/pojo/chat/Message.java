package com.emika.app.data.network.pojo.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("is_emika")
    @Expose
    private Boolean isEmika;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("chain_position")
    @Expose
    private Integer chainPosition;
    @SerializedName("delay")
    @Expose
    private Integer delay;
    @SerializedName("is_password")
    @Expose
    private Boolean isPassword;
    @SerializedName("is_seen")
    @Expose
    private Boolean isSeen;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    public static final int USER_MSG = 0;
    public static final int OTHER_MSG = 1;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsEmika() {
        return isEmika;
    }

    public void setIsEmika(Boolean isEmika) {
        this.isEmika = isEmika;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getChainPosition() {
        return chainPosition;
    }

    public void setChainPosition(Integer chainPosition) {
        this.chainPosition = chainPosition;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getIsPassword() {
        return isPassword;
    }

    public void setIsPassword(Boolean isPassword) {
        this.isPassword = isPassword;
    }

    public Boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(Boolean isSeen) {
        this.isSeen = isSeen;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
