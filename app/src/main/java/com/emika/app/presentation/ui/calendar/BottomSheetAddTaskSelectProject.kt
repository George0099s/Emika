package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.callback.calendar.ProjectsCallback
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Project
import com.emika.app.di.ProjectsDi
import com.emika.app.domain.repository.calendar.CalendarRepository
import com.emika.app.presentation.adapter.calendar.ProjectAdapter
import com.emika.app.presentation.adapter.calendar.SectionAdapter
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
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
    private var selectProject: Button? = null
    private val app = EmikaApplication.instance
    private var converter: Converter? = null
    private var task: PayloadTask? = null
    private var repository: CalendarRepository? = null

    @Inject
    lateinit var projectsDagger: ProjectsDi
    private val setProjects = Observer { projects: List<PayloadProject?> ->
        Log.d(TAG, ":  projects bootm sheet" + projects.size)
        projectAdapter = ProjectAdapter(projects, mViewModel, task)
        projectRecycler!!.adapter = projectAdapter
    }
    private val setSection = Observer { sections: List<PayloadSection> ->
        val sectionList: MutableList<PayloadSection> = ArrayList()
        if (sections.isEmpty()) {
            projectDi.projectSectionId = null
            projectDi.projectSectionName = null
        }
        for (i in sections.indices) {
            if (projectDi.projectId != null) if (projectDi.projectId == sections[i].projectId) sectionList.add(sections[i])
        }
        mViewModel!!.projectListMutableLiveData
        sectionAdapter = SectionAdapter(sectionList, mViewModel, task)
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
        repository = CalendarRepository(token)
        //        repository.downloadSections(this);
//        repository.downloadAllProject(this);
        projectRecycler = view.findViewById(R.id.recycler_add_task_project)
        projectRecycler!!.setHasFixedSize(true)
        projectRecycler!!.layoutManager = LinearLayoutManager(context)
        sectionRecycler = view.findViewById(R.id.recycler_add_task_section)
        sectionRecycler!!.setHasFixedSize(true)
        sectionRecycler!!.layoutManager = LinearLayoutManager(context)
        selectProject = view.findViewById(R.id.add_task_add_project_btn)
        selectProject!!.setOnClickListener { view: View -> addProject(view) }
        converter = Converter()
        //        projectAdapter = new ProjectAdapter(projectsDagger.getProjects(), mViewModel, task);
//        projectRecycler.setAdapter(projectAdapter);
//
//        List<PayloadSection> sectionList = new ArrayList<>();
//        if (projectsDagger.getSections().size() == 0) {
//            projectDi.setProjectSectionId(null);
//            projectDi.setProjectSectionName(null);
//        }
//
//        for (int i = 0; i < projectsDagger.getSections().size(); i++) {
//            if (projectDi.getProjectId().equals(projectsDagger.getSections().get(i).getProjectId()))
//                sectionList.add(projectsDagger.getSections().get(i));
//        }
//        sectionAdapter = new SectionAdapter(sectionList, mViewModel,  task);
//        sectionRecycler.setAdapter(sectionAdapter);
    }

    private fun addProject(view: View) {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(BottomSheetAddTaskSelectProjectViewModel::class.java)
        mViewModel!!.projectListMutableLiveData.observe(viewLifecycleOwner, setProjects)
        mViewModel!!.sectionListMutableLiveData.observe(viewLifecycleOwner, setSection)
    }

    override fun getProjects(projects: List<PayloadProject>) {
        projectAdapter = ProjectAdapter(projects, mViewModel, task)
        projectRecycler!!.adapter = projectAdapter
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
        sectionAdapter = SectionAdapter(sectionList, mViewModel, task)
        sectionRecycler!!.adapter = sectionAdapter
    }

    companion object {
        private const val TAG = "BottomSheetAddTaskSelec"
        fun newInstance(): BottomSheetAddTaskSelectProject {
            return BottomSheetAddTaskSelectProject()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)

}