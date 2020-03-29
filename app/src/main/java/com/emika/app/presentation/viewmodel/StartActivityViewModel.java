package com.emika.app.presentation.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.di.Project;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;

import java.util.List;

import javax.inject.Inject;

public class StartActivityViewModel extends ViewModel implements ShortMemberCallback, ProjectsCallback, EpicLinksCallback, MemberDbCallback,
        ProjectDbCallback, EpicLinksDbCallback  {
    private static final String TAG = "StartActivityViewModel";
    private String token;
    private CalendarRepository repository;
    private Converter converter;
    @Inject
    Project projectDi;
    public StartActivityViewModel(String token) {
        EmikaApplication.getInstance().getComponent().inject(this);
        this.token = token;
        repository = new CalendarRepository(token);
        converter = new Converter();
    }
    public void fetchAllData(){
        repository.downloadAllMembers(this);
        repository.getAllSections(this);
        repository.downloadAllProject(this);
        repository.downloadEpicLinks(this);
    }

    @Override
    public void getProjects(List<PayloadProject> projects) {

        repository.insertDbProject(converter.fromPayloadProjectToProjectEntityList(projects), this);
        projectDi.setProjectId(projects.get(0).getId());
        projectDi.setProjectName(projects.get(0).getName());
        projectDi.setProjectSectionId(projects.get(0).getDefaultSectionId());
    }

    @Override
    public void getSections(List<PayloadSection> sections) {
//        repository.insertDbSections(sections);
    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        repository.insertDbMembers(shortMembers, this);
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void onEpicLinksDownloaded(List<PayloadEpicLinks> epicLinks) {
        repository.insertDbEpicLinks(converter.fromPayloadEpicLinksToEpicLinksEntity(epicLinks), this);
    }

    @Override
    public void onMembersLoaded(List<MemberEntity> memberEntityList) {
        if (memberEntityList == null)
            repository.downloadAllMembers(this);
    }

    @Override
    public void onProjectLoaded(List<ProjectEntity> projectEntities) {
        if (projectEntities == null) {
            repository.downloadAllProject(this);
        }


    }

    @Override
    public void onEpicLinksLoaded(List<EpicLinksEntity> epicLinksEntities) {
        if (epicLinksEntities==null)
            repository.downloadEpicLinks(this);
    }
}
