package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;

import java.util.List;

public interface ProjectsCallback {
    void getProjects(List<PayloadProject> projects);
    void getSections(List<PayloadSection> sections);
}
