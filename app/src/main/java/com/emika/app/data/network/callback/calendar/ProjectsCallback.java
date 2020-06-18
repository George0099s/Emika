package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadProjectCreation;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.project.PayloadSectionCreation;

import java.util.List;

public interface ProjectsCallback {
    void getProjects(List<PayloadProject> projects);
    void getSections(List<PayloadSection> sections);
    void getCreatedProject(PayloadProjectCreation payload);
    void onSectionCreated(PayloadSection payload);
}
