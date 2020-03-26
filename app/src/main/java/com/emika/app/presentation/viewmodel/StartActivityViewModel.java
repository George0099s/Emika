package com.emika.app.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.List;

public class StartActivityViewModel extends ViewModel implements ShortMemberCallback, ProjectsCallback, EpicLinksCallback {
    private static final String TAG = "StartActivityViewModel";
    private String token;
    private CalendarRepository repository;
    private Converter converter;
    public StartActivityViewModel(String token) {
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
        repository.insertDbProject(converter.fromPayloadProjectToProjectEntityList(projects));
    }

    @Override
    public void getSections(List<PayloadSection> sections) {

    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        repository.insertDbMembers(shortMembers);
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void onEpicLinksLoaded(List<PayloadEpicLinks> epicLinks) {
        repository.insertDbEpicLinks(converter.fromPayloadEpicLinksToEpicLinksEntity(epicLinks));
    }
}
