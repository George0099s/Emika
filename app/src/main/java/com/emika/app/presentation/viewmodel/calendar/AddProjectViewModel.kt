package com.emika.app.presentation.viewmodel.calendar

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.db.entity.SectionEntity
import com.emika.app.data.network.callback.calendar.EpicLinksCallback
import com.emika.app.data.network.callback.calendar.ProjectsCallback
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadProjectCreation
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.data.network.pojo.project.PayloadSectionCreation
import com.emika.app.domain.repository.calendar.CalendarRepository

class AddProjectViewModel() : ViewModel(), Parcelable, ProjectsCallback, EpicLinksCallback {
    private val repository: CalendarRepository = CalendarRepository(EmikaApplication.instance.sharedPreferences.getString("token", null))
    var projectMutableLiveData: MutableLiveData<PayloadProjectCreation> = MutableLiveData()
    var members: MutableList<String> = arrayListOf()
    var membersMutableLiveData: MutableLiveData<MutableList<String>> = MutableLiveData()
    var project: PayloadProject? = null
    var sectionLiveData: LiveData<List<SectionEntity>> = MutableLiveData()

    fun  getSectionsByProjectId(projectId: String): LiveData<List<SectionEntity>>{
        return repository.getSectionsDbLiveData(projectId)
    }

    fun  getEpicLinksByProjectId(projectId: String): LiveData<List<EpicLinksEntity>>{
        return repository.getEpicLinksDbLiveData(projectId)
    }

    fun getMembersLiveData(): MutableLiveData<MutableList<String>>{
        membersMutableLiveData.value = members
        return membersMutableLiveData
    }

    fun createSection( name: String, status: String, order: String, projectId: String){
        repository.createSection( this, name, status, order, projectId)
    }



    constructor(parcel: Parcel) : this() {
       members = parcel.createStringArrayList()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(members)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddProjectViewModel> {
        override fun createFromParcel(parcel: Parcel): AddProjectViewModel {
            return AddProjectViewModel(parcel)
        }

        override fun newArray(size: Int): Array<AddProjectViewModel?> {
            return arrayOfNulls(size)
        }
    }

    fun createProject(projectName: String){
        repository.createProject(this, projectName)
    }

    fun update(project: PayloadProject){
        repository.updateProject(project, this)
    }

    override fun getCreatedProject(project: PayloadProjectCreation?) {
        repository.insertProject(project!!)
        projectMutableLiveData.postValue(project)

    }

    override fun getSections(sections: MutableList<PayloadSection>?) {
    }

    override fun onSectionCreated(section: PayloadSection) {
//        sectionLiveData.postValue(payload)
        repository.insertDbSection(section)
    }

    override fun getProjects(projects: MutableList<PayloadProject>?) {
    }

    fun updateSection(payloadSection: PayloadSection) {
        repository.updateSection(payloadSection)
    }

    fun updateSectionsOrder(newList: ArrayList<String>) {
        repository.updateSectionsOrder(newList)
    }

    fun updateEpicLinksOrder(newEpicLinkList: ArrayList<String>) {
        repository.updateEpicLinksOrder(newEpicLinkList)
    }

    fun updateEpicLink(payloadEpicLinks: PayloadEpicLinks) {
        repository.updateEpicLink(payloadEpicLinks)
    }

    fun createEpicLink(name: String, status: String, order: String, projectId: String) {
        repository.createEpicLink(this, name, status, order, projectId)
    }

    override fun onEpicLinkCreated(epicLink: PayloadEpicLinks?) {
        repository.insertEpicLink(epicLink)
    }

    override fun onEpicLinksDownloaded(epicLinks: MutableList<PayloadEpicLinks>?) {
    }


}

