package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.callback.calendar.ProjectsCallback
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadProjectCreation
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.data.network.pojo.project.PayloadSectionCreation
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Project
import com.emika.app.di.ProjectsDi
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.adapter.calendar.ProjectAdapter
import com.emika.app.presentation.adapter.calendar.SectionAdapter
import com.emika.app.presentation.ui.profile.AddContactDialogFragment
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import javax.inject.Inject

class BottomSheetAddTaskSelectProject : BottomSheetDialogFragment(), ProjectsCallback {

    @Inject
    lateinit var projectDi: Project
    private var mViewModel: BottomSheetAddTaskSelectProjectViewModel? = null
    private var projectRecycler: RecyclerView? = null
    private var sectionRecycler: RecyclerView? = null
    private var projectAdapter: ProjectAdapter? = null
    private var sectionAdapter: SectionAdapter? = null
    private var token: String? = null
    private var addTaskListViewModel: AddTaskListViewModel? = null
    private var taskInfoViewModel: TaskInfoViewModel? = null
    private var addProjectViewModel: AddProjectViewModel? = null
    private var selectProject: Button? = null
    private val app = EmikaApplication.instance
    private var converter: Converter? = null
    private var task: PayloadTask? = null
    private var project: PayloadProject? = null
    private var repository: CalendarRepository? = null
    private lateinit var addProject: TextView
    @Inject
    lateinit var projectsDagger: ProjectsDi

    private val setProjects = Observer { projects: List<PayloadProject?> ->
        Log.d(TAG, ":  projects bootm sheet" + projects.size)
        projectAdapter = ProjectAdapter(projects, mViewModel, task, null, context)
        projectRecycler!!.adapter = projectAdapter
    }
    private val setSection = Observer { sections: List<PayloadSection> ->
        val sectionList: MutableList<PayloadSection> = ArrayList()
        if (sections.isEmpty()) {
            projectDi.projectSectionId = null
            projectDi.projectSectionName = null
        }
        for (i in sections.indices) {
            if (projectDi.projectId != null)
                if (projectDi.projectId == sections[i].projectId)
                    sectionList.add(sections[i])
        }
        mViewModel!!.projectListMutableLiveData
        sectionAdapter = SectionAdapter(sectionList, null, context!!)
        sectionRecycler!!.adapter = sectionAdapter
    }

    private val setSectionLiveData = Observer { sections: List<PayloadSection> ->
        val sectionList: MutableList<PayloadSection> = ArrayList()
        if (sections.isEmpty()) {
            projectDi.projectSectionId = null
            projectDi.projectSectionName = null
        }
        for (i in sections.indices) {
            if (projectDi.projectId != null)
                if (projectDi.projectId == sections[i].projectId)
                    sectionList.add(sections[i])
        }
        mViewModel!!.projectListMutableLiveData
        sectionAdapter = SectionAdapter(sectionList, null, context!!)
        sectionRecycler!!.adapter = sectionAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_task_select_project_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        EmikaApplication.instance.component?.inject(this)
        addTaskListViewModel = arguments!!.getParcelable("addTaskViewModel")
        taskInfoViewModel = arguments!!.getParcelable("taskInfoViewModel")
        task = arguments!!.getParcelable("task")
        token = EmikaApplication.instance.sharedPreferences?.getString("token", "")
        mViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(BottomSheetAddTaskSelectProjectViewModel::class.java)
        mViewModel!!.projectListMutableLiveData.observe(viewLifecycleOwner, setProjects)
        mViewModel!!.sectionListMutableLiveData.observe(viewLifecycleOwner, setSection)
        repository = CalendarRepository(token)
        addProject = view.findViewById(R.id.add_project)
        addProject.setOnClickListener{createProject()}
        for (proj in projectsDagger.projects){
            if (proj.id == projectDi.projectId)
                project = proj
        }
        mViewModel?.project = project
        addProjectViewModel?.project = project
        view.findViewById<TextView>(R.id.edit_sections).setOnClickListener{
            editSection()
        }
        //        repository.downloadSections(this);
//        repository.downloadAllProject(this);
        projectRecycler = view.findViewById(R.id.recycler_add_task_project)
        projectRecycler!!.setHasFixedSize(true)
        projectRecycler!!.layoutManager = LinearLayoutManager(context)
        sectionRecycler = view.findViewById(R.id.recycler_add_task_section)
        sectionRecycler!!.setHasFixedSize(true)
        sectionRecycler!!.layoutManager = LinearLayoutManager(context)
        selectProject = view.findViewById(R.id.add_task_add_project_btn)
        selectProject!!.setOnClickListener { view: View -> addProject() }
        converter = Converter()
    }

    private fun createProject(){
        val addContact = AddProjectDialogFragment()
        addContact.isCancelable = true
        addContact.show(parentFragmentManager, "createProjectDialog")
    }

    private fun addProject() {
        if (addTaskListViewModel != null) {
            addTaskListViewModel!!.epicLinksList = ArrayList()
            addTaskListViewModel!!.epicLinksMutableLiveData
            addTaskListViewModel!!.projectMutableLiveData
        }
        if (taskInfoViewModel != null) {
            task!!.projectId = projectDi!!.projectId
            task!!.sectionId = projectDi!!.projectSectionId
            task!!.epicLinks = ArrayList()
            taskInfoViewModel!!.task = task
            taskInfoViewModel!!.epicLinksMutableLiveData.value = ArrayList()
            taskInfoViewModel!!.taskMutableLiveData
            taskInfoViewModel!!.projectMutableLiveData
            taskInfoViewModel!!.updateTask(task)
        }
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        addProject()
        super.onDismiss(dialog)
    }

    private fun editSection() {
        val bundle = Bundle()
        val mySheetDialog = BottomSheetEditSections()
        bundle.putParcelable("project", mViewModel!!.project)
        bundle.putParcelable("viewModel", addProjectViewModel)
        mySheetDialog.arguments = bundle
        mySheetDialog.show(fragmentManager!!, "modalSheetDialog")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        mViewModel!!.getSectionEntityLiveData.observe(viewLifecycleOwner, setSection)



        addProjectViewModel = ViewModelProviders.of(this).get(AddProjectViewModel::class.java)
    }

    override fun getProjects(projects: List<PayloadProject>) {
        projectAdapter = ProjectAdapter(projects, mViewModel, task, null, context)
        projectRecycler!!.adapter = projectAdapter
    }

    override fun getCreatedProject(payload: PayloadProjectCreation?) {
        TODO("Not yet implemented")
    }

    override fun getSections(sections: List<PayloadSection>) {
        val sectionList: MutableList<PayloadSection> = ArrayList()
        if (sections.isEmpty()) {
            projectDi!!.projectSectionId = null
            projectDi!!.projectSectionName = null
        }

        for (i in sections.indices) {
            if (projectDi!!.projectId != null)
                if (projectDi!!.projectId == sections[i].projectId)
                    sectionList.add(sections[i])
        }

        mViewModel!!.projectListMutableLiveData
        sectionAdapter = SectionAdapter(sectionList, null, context!!)
        sectionRecycler!!.adapter = sectionAdapter
    }

    override fun onSectionCreated(payload: PayloadSection?) {
    }

    companion object {
        private const val TAG = "BottomSheetAddTaskSelec"
        fun newInstance(): BottomSheetAddTaskSelectProject {
            return BottomSheetAddTaskSelectProject()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)

}