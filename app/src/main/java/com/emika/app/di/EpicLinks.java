package com.emika.app.di;

import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;

import java.util.ArrayList;
import java.util.List;

public class EpicLinks {
    List<PayloadEpicLinks> epicLinksList = new ArrayList<>();
    private String id;
    private String name;
    private String status;
    private Integer order;
    private String emoji;
    private String projectId;
    private String updatedAt;
    private String createdAt;

    public List<PayloadEpicLinks> getEpicLinksList() {
        return epicLinksList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
