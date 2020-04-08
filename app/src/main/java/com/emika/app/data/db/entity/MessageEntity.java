package com.emika.app.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Messages")
public class MessageEntity {
    public MessageEntity(@NonNull String id, String type, Boolean isEmika, String accountId, String text, String createdAt, Integer chainPosition, Integer delay, Boolean isPassword, Boolean isSeen, String updatedAt) {
        this.id = id;
        this.type = type;
        this.isEmika = isEmika;
        this.accountId = accountId;
        this.text = text;
        this.createdAt = createdAt;
        this.chainPosition = chainPosition;
        this.delay = delay;
        this.isPassword = isPassword;
        this.isSeen = isSeen;
        this.updatedAt = updatedAt;
    }

    @PrimaryKey
    @NonNull
    private String id;
    private String type;
    private Boolean isEmika;
    private String accountId;
    private String text;
    private String createdAt;
    private Integer chainPosition;
    private Integer delay;
    private Boolean isPassword;
    private Boolean isSeen;
    private String updatedAt;


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

    public Boolean getEmika() {
        return isEmika;
    }

    public void setEmika(Boolean emika) {
        isEmika = emika;
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

    public Boolean getPassword() {
        return isPassword;
    }

    public void setPassword(Boolean password) {
        isPassword = password;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
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
