package com.emika.app.presentation.viewmodel;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.CompanyDi;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.profile.UserRepository;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import javax.inject.Inject;

public class StartActivityViewModel extends ViewModel implements ShortMemberCallback, ProjectsCallback, EpicLinksCallback, MemberDbCallback,
        ProjectDbCallback, EpicLinksDbCallback, UserInfoCallback, CompanyInfoCallback {
    private static final String TAG = "StartActivityViewModel";
    private String token;
    private CalendarRepository repository;
    private UserRepository userRepository;
    private Converter converter;
    private MutableLiveData<Boolean> hasCompanyId;
    private Boolean hasCompany = false;
    private UserDbManager userDbManager;
    @Inject
    Project projectDi;
    @Inject
    EpicLinks epicLinksDi;
    @Inject
    CompanyDi companyDi;
    public StartActivityViewModel(String token) {
        EmikaApplication.getInstance().getComponent().inject(this);
        this.token = token;
        userDbManager = new UserDbManager();
        repository = new CalendarRepository(token);
        userRepository = new UserRepository(token);
        converter = new Converter();
        hasCompanyId = new MutableLiveData<>();
    }
    public void fetchAllData(){
       FirebaseInstanceId.getInstance().getInstanceId()
               .addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                      String key = task.getResult().getToken();
                       repository.sendRegistrationKey(key);
                   }
               });
        userDbManager.dropAllTable();
        repository.downloadAllMembers(this);
        repository.getAllSections(this);
        repository.downloadAllProject(this);
        repository.downloadEpicLinks(this);
        repository.downloadCompanyInfo(this);
    }

    @Override
    public void getProjects(List<PayloadProject> projects) {
        Log.d(TAG, "getProjects: " + projects.size());
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
        if (epicLinks.size() > 0) {
            for (int i = 0; i < epicLinks.size(); i++) {
                epicLinksDi.getEpicLinksList().add(epicLinks.get(i));
            }
            repository.insertDbEpicLinks(converter.fromPayloadEpicLinksToEpicLinksEntity(epicLinks), this);
        }
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
        if (epicLinksEntities==null || epicLinksEntities.size() == 0)
            repository.downloadEpicLinks(this);
    }

    @Override
    public void updateInfo(UpdateUserModel model) {

    }

    @Override
    public void getUserInfo(Payload userModel) {
        if (userModel.getCompany_id() != null && userModel.getCompany_id().length() != 0)
            hasCompanyId.postValue(true);
        else
            hasCompanyId.postValue(false);

    }

    public MutableLiveData<Boolean> getHasCompanyId() {
        userRepository.downloadUserInfo(this);
        return hasCompanyId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public void onCompanyInfoDownloaded(PayloadCompanyInfo companyInfo) {
        companyDi.setBalance(companyInfo.getBalance());
        companyDi.setManagers(companyInfo.getManagers());
        companyDi.setCreatedAt(companyInfo.getCreatedAt());
        companyDi.setCreatedBy(companyInfo.getCreatedBy());
        companyDi.setId(companyInfo.getId());
        companyDi.setName(companyInfo.getName());
        companyDi.setPictureUrl(companyInfo.getPictureUrl());
        companyDi.setStatus(companyInfo.getStatus());
        companyDi.setSize(companyInfo.getSize());
        Log.d("123", "onCompanyInfoDownloaded: " +companyDi.getName());
    }
}
