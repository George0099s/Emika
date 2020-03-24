package com.emika.app.di;

public class Project {
   private String projectName;
    private String projectId;
    private String projectSectionName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectSectionName() {
        return projectSectionName;
    }

    public void setProjectSectionName(String projectSectionName) {
        this.projectSectionName = projectSectionName;
    }

    private String projectSectionId;

    public String getProjectSectionId() {
        return projectSectionId;
    }

    public void setProjectSectionId(String projectSectionId) {
        this.projectSectionId = projectSectionId;
    }
}
