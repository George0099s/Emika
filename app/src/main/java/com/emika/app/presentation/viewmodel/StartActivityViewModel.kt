package com.emika.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.callback.calendar.*
import com.emika.app.data.db.dbmanager.UserDbManager
import com.emika.app.data.db.entity.*
import com.emika.app.data.network.callback.CompanyInfoCallback
import com.emika.app.data.network.callback.calendar.*
import com.emika.app.data.network.callback.user.UserInfoCallback
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.data.network.pojo.member.PayloadShortMember
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadProjectCreation
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.data.network.pojo.project.PayloadSectionCreation
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.*
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.domain.repository.profile.UserRepository
import com.emika.app.presentation.utils.Converter
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import javax.inject.Inject

class StartActivityViewModel(token: String) : ViewModel(), ShortMemberCallback, ProjectsCallback, EpicLinksCallback, MemberDbCallback, TaskDbCallback,
        ProjectDbCallback, EpicLinksDbCallback, UserInfoCallback, CompanyInfoCallback, SectionDbCallback, DurationActualCallback, ActualDurationDbCallback, TaskListCallback {
    @Inject
    lateinit var projectDi: Project

    @Inject
    lateinit var epicLinksDi: EpicLinks

    @Inject
    lateinit var companyDi: CompanyDi

    @Inject
    lateinit var projectsDagger: ProjectsDi

    @Inject
    lateinit var assignee: Assignee

    var token: String
        get() {
            return field
        }
    private val repository: CalendarRepository
    private val userRepository: UserRepository
    private val converter: Converter
    private val hasCompanyId: MutableLiveData<Boolean>
    private val hasCompany = false
    private val userDbManager: UserDbManager

    fun fetchAllData() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task: Task<InstanceIdResult> ->
                    if (task.isSuccessful) {
                        val key = task.result!!.token
                        repository.sendRegistrationKey(key)
                    }
                }
//        userDbManager.dropAllTable()
        repository.downloadAllMembers(this)
        repository.downloadSections(this)
        repository.downloadAllProject(this)
        repository.downloadEpicLinks(this)
        repository.downloadCompanyInfo(this)
        repository.downloadDurationActualLog(this)
        repository.downloadTasks(this)
    }

    override fun getProjects(projects: List<PayloadProject>) {
        Log.d(TAG, projects.size.toString())
        projectDi.projectId = projects[0].id
        projectDi.projectName = projects[0].name
        projectDi.projectSectionId = projects[0].defaultSectionId
        Log.d(TAG,         projects[0].activeFields.size.toString())
        repository.insertDbProject(converter.fromPayloadProjectToProjectEntityList(projects), this)
        projectsDagger.projects = projects

    }

    override fun getCreatedProject(payload: PayloadProjectCreation?) {
        TODO("Not yet implemented")
    }

    override fun getSections(sections: List<PayloadSection>) {
        projectsDagger.sections = sections
        for (i in sections.indices) {
            if (sections[i].id == projectDi.projectSectionId) {
                projectDi.projectSectionName = sections[i].name
            }
        }
        repository.insertDbSections(converter.fromListPayloadSectionToSectionEntity(sections), this)
    }

    override fun onSectionCreated(payload: PayloadSection?) {
    }

    override fun allMembers(shortMembers: List<PayloadShortMember>) {
        repository.insertDbMembers(shortMembers, this)
    }

    override fun onEpicLinksDownloaded(epicLinks: List<PayloadEpicLinks>) {
        if (epicLinks.size > 0) {
            for (i in epicLinks.indices) {
                epicLinksDi.epicLinksList.add(epicLinks[i])
            }
            repository.insertDbEpicLinks(converter.fromPayloadEpicLinksToEpicLinksEntity(epicLinks), this)
        }
    }

    override fun onMembersLoaded(memberEntityList: List<MemberEntity>) {
        if (memberEntityList == null)
            repository.downloadAllMembers(this)
    }

    override fun onProjectLoaded(projectEntities: List<ProjectEntity>) {
        if (projectEntities == null) {
            repository.downloadAllProject(this)
        }
    }

    override fun onEpicLinksLoaded(epicLinksEntities: List<EpicLinksEntity>) {
        if (epicLinksEntities == null || epicLinksEntities.isEmpty()) repository.downloadEpicLinks(this)
    }

    override fun updateInfo(model: UpdateUserModel) {}
    override fun getUserInfo(userModel: Payload) {
        if (userModel.company_id != null && userModel.company_id.isNotEmpty()) {
            hasCompanyId.postValue(true)
            assignee.id = userModel.id
            assignee.firstName = userModel.firstName
            assignee.lastName = userModel.lastName
            assignee.pictureUrl = userModel.pictureUrl
            assignee.jobTitle = userModel.jobTitle
        } else hasCompanyId.postValue(false)
    }

    fun getHasCompanyId(): MutableLiveData<Boolean> {
        userRepository.downloadUserInfo(this)
        return hasCompanyId
    }

    override fun onCompanyInfoDownloaded(companyInfo: PayloadCompanyInfo) {
        companyDi.balance = companyInfo.balance
        companyDi.managers = companyInfo.managers
        companyDi.createdAt = companyInfo.createdAt
        companyDi.createdBy = companyInfo.createdBy
        companyDi.id = companyInfo.id
        companyDi.name = companyInfo.name
        companyDi.pictureUrl = companyInfo.pictureUrl
        companyDi.status = companyInfo.status
        companyDi.size = companyInfo.size
    }

    override fun onSectionLoaded(sections: List<SectionEntity>) {
        repository.insertDbSections(sections, this)
    }

    override fun onDurationLogDownloaded(durationActualList: List<PayloadDurationActual>) {
        repository.insertAllDbDurations(converter.fromPayloadListDurationToListDurationEntity(durationActualList), this)
    }

    override fun onActualDurationLoaded(actualDurationEntities: List<ActualDurationEntity>) {}
    override fun setTaskList(taskList: List<PayloadTask>) {
        repository.sedDbData(taskList)
    }

    override fun onTasksLoaded(taskList: List<TaskEntity>) {}
    override fun onFilteredTasksLoaded(taskList: List<TaskEntity>) {}
    override fun onOneTaskLoaded(taskEntity: TaskEntity) {}

    companion object {
        private const val TAG = "StartActivityViewModel"
    }

    init {
        EmikaApplication.instance.component?.inject(this)
        this.token = token
        userDbManager = UserDbManager()
        repository = CalendarRepository(token)
        userRepository = UserRepository(token)
        converter = Converter()
        hasCompanyId = MutableLiveData()
    }
}