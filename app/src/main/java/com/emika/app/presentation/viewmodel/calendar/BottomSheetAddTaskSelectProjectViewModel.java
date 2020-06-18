package com.emika.app.presentation.viewmodel.calendar;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadProjectCreation;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.project.PayloadSectionCreation;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BottomSheetAddTaskSelectProjectViewModel extends ViewModel implements ProjectsCallback, ProjectDbCallback, SectionDbCallback {
    private static final String TAG = "BottomSheetAddTaskSelec";
    private String token;
    private MutableLiveData<List<PayloadProject>> projectListMutableLiveData;
    private MutableLiveData<List<PayloadSection>> sectionListMutableLiveData;
    private CalendarRepository repository;
    private String projectId;
    private Converter converter;
    private EmikaApplication app = EmikaApplication.instance;
    @Inject
    Project projectDi;
    @Inject
    ProjectsDi projectsDagger;
    public BottomSheetAddTaskSelectProjectViewModel(String token) {
        this.token = token;
        projectListMutableLiveData = new MutableLiveData<>();
        sectionListMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        repository.getAllProjects(this);
        repository.getAllSections(this);
        converter = new Converter();
        app.getComponent().inject(this);
    }

//    public MutableLiveData<List<PayloadProject>> getProjectListMutableLiveData() {
//        repository.getAllProjects(this);
//        return projectListMutableLiveData;
//    }

    public MutableLiveData<List<PayloadProject>> getProjectListMutableLiveData() {
//        projectListMutableLiveData.setValue(projectsDagger.getProjects());
//        repository.downloadAllProject(this);
        return projectListMutableLiveData;
    }

    public void getProjects(){
        repository.getAllProjects(this);
    }

    @Override
    public void getProjects(List<PayloadProject> projects) {
        projectsDagger.setProjects(projects);
        projectListMutableLiveData.postValue(projects);
    }

    @Override
    public void getSections(List<PayloadSection> sections) {
//            List<PayloadSection> sectionList = new ArrayList<>();
//                for (PayloadSection section : sections) {
//                    if (section.getProjectId().equals(projectDi.getProjectId())) {
//                        sectionList.add(section);
//                    }
//                }
        Log.d(TAG, "getSections: " + sections.size());
        projectsDagger.setSections(sections);
                sectionListMutableLiveData.postValue(sections);
    }

    @Override
    public void getCreatedProject(PayloadProjectCreation payload) {

    }

    @Override
    public void onSectionCreated(PayloadSection payload) {

    }


    public void getSections(){
        repository.getAllSections(this);
    }
    public MutableLiveData<List<PayloadSection>> getSectionListMutableLiveData() {
//        repository.downloadSections(this);
        List<PayloadSection> sectionList = new ArrayList<>();
        for (PayloadSection section : projectsDagger.getSections()) {
            if (section.getProjectId().equals(projectDi.getProjectId())) {
                sectionList.add(section);
            }
        }
        sectionListMutableLiveData.setValue(sectionList);
        return sectionListMutableLiveData;
    }

    public void setProjectId() {
        this.projectId = projectDi.getProjectId();
    }

    @Override
    public void onProjectLoaded(List<ProjectEntity> projectEntities) {
        projectsDagger.setProjects(converter.fromProjectEntityToPayloadProjectList(projectEntities));
        projectListMutableLiveData.postValue(converter.fromProjectEntityToPayloadProjectList(projectEntities));
    }

    @Override
    public void onSectionLoaded(List<SectionEntity> sections) {
        projectsDagger.setSections(converter.fromListEntitySectionToPayloadSection(sections));
        sectionListMutableLiveData.postValue(converter.fromListEntitySectionToPayloadSection(sections));    }

    public String getProjectId() {
        return projectId;
    }
}
