package com.emika.app.presentation.viewmodel;

import android.mtp.MtpConstants;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.DurationActualCallback;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.CompanyDi;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.profile.UserRepository;
import com.emika.app.presentation.utils.Converter;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import javax.inject.Inject;

public class StartActivityViewModel extends ViewModel implements ShortMemberCallback, ProjectsCallback, EpicLinksCallback, MemberDbCallback, TaskDbCallback,
        ProjectDbCallback, EpicLinksDbCallback, UserInfoCallback, CompanyInfoCallback, SectionDbCallback, DurationActualCallback, ActualDurationDbCallback
        , TaskListCallback {
        private static final String TAG = "StartActivityViewModel";
        @Inject
        Project projectDi;
        @Inject
        EpicLinks epicLinksDi;
        @Inject
        CompanyDi companyDi;
        @Inject
        ProjectsDi projectsDagger;
        @Inject
        Assignee assignee;
        private String token;
        private CalendarRepository repository;
        private UserRepository userRepository;
        private Converter converter;
        private MutableLiveData<Boolean> hasCompanyId;
        private Boolean hasCompany = false;
        private UserDbManager userDbManager;

        public StartActivityViewModel(String token) {
            EmikaApplication.getInstance().getComponent().inject(this);
            this.token = token;
            userDbManager = new UserDbManager();
            repository = new CalendarRepository(token);
            userRepository = new UserRepository(token);
            converter = new Converter();
            hasCompanyId = new MutableLiveData<>();
        }

        public void fetchAllData() {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String key = task.getResult().getToken();
                            repository.sendRegistrationKey(key);
                        }
                    });
            userDbManager.dropAllTable();
            repository.downloadAllMembers(this);
            repository.downloadSections(this);
            repository.downloadAllProject(this);
            repository.downloadEpicLinks(this);
            repository.downloadCompanyInfo(this);
            repository.downloadDurationActualLog(this);
            repository.downloadTasks(this);
        }

        @Override
        public void getProjects(List<PayloadProject> projects) {
            repository.insertDbProject(converter.fromPayloadProjectToProjectEntityList(projects), this);
            projectsDagger.setProjects(projects);
            projectDi.setProjectId(projects.get(0).getId());
            projectDi.setProjectName(projects.get(0).getName());
            projectDi.setProjectSectionId(projects.get(0).getDefaultSectionId());
        }

        @Override
        public void getSections(List<PayloadSection> sections) {
            projectsDagger.setSections(sections);
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).getId().equals(projectDi.getProjectSectionId())) {
                    projectDi.setProjectSectionName(sections.get(i).getName());
                    Log.d(TAG, "getSections: " + projectDi.getProjectSectionName());
                }
            }
            repository.insertDbSections(converter.fromListPayloadSectionToSectionEntity(sections), this);
        }

        @Override
        public void allMembers(List<PayloadShortMember> shortMembers) {
            repository.insertDbMembers(shortMembers, this);

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
            if (epicLinksEntities == null || epicLinksEntities.size() == 0)
                repository.downloadEpicLinks(this);
        }

        @Override
        public void updateInfo(UpdateUserModel model) {

        }

        @Override
        public void getUserInfo(Payload userModel) {
            if (userModel.getCompany_id() != null && userModel.getCompany_id().length() != 0) {
                hasCompanyId.postValue(true);
                assignee.setId(userModel.getId());
                assignee.setFirstName(userModel.getFirstName());
                assignee.setLastName(userModel.getLastName());
                assignee.setPictureUrl(userModel.getPictureUrl());
                assignee.setJobTitle(userModel.getJobTitle());
            }
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

        public void setToken(String token) {
            this.token = token;
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
        }

        @Override
        public void onSectionLoaded(List<SectionEntity> sections) {
            repository.insertDbSections(sections, this);
        }

        @Override
        public void onDurationLogDownloaded(List<PayloadDurationActual> durationActualList) {
            if (durationActualList != null)
            repository.insertAllDbDurations(converter.fromPayloadListDurationToListDurationEntity(durationActualList), this);
            else repository.downloadDurationActualLog(this);
        }

        @Override
        public void onActualDurationLoaded(List<ActualDurationEntity> actualDurationEntities) {
        }


        @Override
        public void setTaskList(List<PayloadTask> taskList) {
            repository.sedDbData(taskList);
        }

    @Override
    public void onTasksLoaded(List<TaskEntity> taskList) {

    }

    @Override
    public void onFilteredTasksLoaded(List<TaskEntity> taskList) {

    }

    @Override
    public void onOneTaskLoaded(TaskEntity taskEntity) {

    }
}

