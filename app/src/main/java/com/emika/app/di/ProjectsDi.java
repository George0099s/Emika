package com.emika.app.di;

import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProjectsDi {
   private List<PayloadProject> projects = new ArrayList<>();

    public List<PayloadSection> getSections() {
        return sections;
    }

    public void setSections(List<PayloadSection> sections) {
        this.sections = sections;
    }

    private List<PayloadSection> sections = new ArrayList<>();
    public List<PayloadProject> getProjects() {
        return projects;
    }

    public void setProjects(List<PayloadProject> projects) {
        this.projects = projects;
    }
}
