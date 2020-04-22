package com.emika.app.presentation.viewmodel.calendar;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.dbmanager.SectionDbManager;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.di.Project;
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
    private EmikaApplication app = EmikaApplication.getInstance();
    @Inject
    Project projectDi;
    public BottomSheetAddTaskSelectProjectViewModel(String token) {
        this.token = token;
        projectListMutableLiveData = new MutableLiveData<>();
        sectionListMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
        app.getComponent().inject(this);
    }

    public MutableLiveData<List<PayloadProject>> getProjectListMutableLiveData() {
        repository.getAllProjects(this);
        return projectListMutableLiveData;
    }

    @Override
    public void getProjects(List<PayloadProject> projects) {
        projectListMutableLiveData.postValue(projects);
    }

    @Override
    public void getSections(List<PayloadSection> sections) {
            List<PayloadSection> sectionList = new ArrayList<>();
                for (PayloadSection section : sections) {
                    if (section.getProjectId().equals(projectDi.getProjectId())) {
                        sectionList.add(section);
                    }
                }
                sectionListMutableLiveData.postValue(sectionList);
    }

    public MutableLiveData<List<PayloadSection>> getSectionListMutableLiveData() {
        repository.getAllSections(this);
        return sectionListMutableLiveData;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public void onProjectLoaded(List<ProjectEntity> projectEntities) {
        projectListMutableLiveData.postValue(converter.fromProjectEntityToPayloadProjectList(projectEntities));
    }

    @Override
    public void onSectionLoaded(List<SectionEntity> sections) {
        sectionListMutableLiveData.postValue(converter.fromListEntitySectionToPayloadSection(sections));
    }

    public String getProjectId() {
        return projectId;
    }
}
