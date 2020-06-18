package com.emika.app.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.ProjectEntity
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.User
import com.emika.app.features.calendar.MemberItemDecoration
import com.emika.app.presentation.adapter.calendar.InboxTaskAdapter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.InboxViewModel
import kotlinx.android.synthetic.main.activity_inbox.*
import kotlinx.android.synthetic.main.activity_inbox.view.*
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class Inbox : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_inbox, container, false)
        initView(view)
        return view
    }
    private var viewModel: CalendarViewModel? = null
    private var inboxViewModel: InboxViewModel? = null
    private var inboxRecycler: RecyclerView? = null
    private var adapter: InboxTaskAdapter? = null
    private var token: String? = null
    private var inboxTaskList: MutableList<PayloadTask>? = null
    private var date: String = ""
    private val decor: MemberItemDecoration = MemberItemDecoration()
    private var projects: List<ProjectEntity> = arrayListOf()
    @Inject
    lateinit var userDi: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    private fun initView(view: View) {
        EmikaApplication.instance.component?.inject(this)
        view.inboxText.text = "Unplanned tasks assigned to "+ userDi!!.firstName +" and tasks created by " +userDi!!.firstName + " with no assignee"
        token = EmikaApplication.instance.sharedPreferences?.getString("token", "")
        inboxTaskList = ArrayList()
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        inboxViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(InboxViewModel::class.java)
        viewModel!!.projectMutableLiveData.observe(viewLifecycleOwner, getProjectLiveData)
        view.inbox_recycler.setHasFixedSize(false)
        view.inbox_recycler.addItemDecoration(decor)
        view.inbox_recycler.layoutManager = LinearLayoutManager(context)
        date = arguments!!.getString("date", null)
        viewModel!!.setContext(context)
        viewModel!!.getAllDbTask()
        viewModel!!.listMutableLiveData.observe(viewLifecycleOwner, getTask)
        inboxViewModel!!.taskListMutableLiveData!!.observe(viewLifecycleOwner, addedTaskCount)
//        inboxViewModel!!.addedTaskList.observe(this, getAddedTask)
        view.inbox_add.setOnClickListener(this::addTasks)
    }



    private val getProjectLiveData = Observer<List<ProjectEntity>> { projects ->
        this.projects = projects
    }
    private val addedTaskCount = Observer<List<PayloadTask>> { tasks ->
        val str = resources.getString(R.string.add)
        val size = tasks.size
            if(size > 0)
                view!!.inbox_add.text = "$str($size)"
            else
                view!!.inbox_add.text = "$str"

    }

    private val getAddedTask = Observer<List<PayloadTask>> { tasks ->
        if (tasks.size > 0)
            inbox_add.text = "add(" + tasks.size+ ")"
        else
            inbox_add.text = "add"
    }

    private fun addTasks(v: View) {
        inboxViewModel!!.date = this.date
//        inboxViewModel!!.addTaskFromBacklog(adapter!!.getAddedTask())
        inboxViewModel!!.addTaskFromBacklog(inboxViewModel!!.addedTaskList)
        dismiss()
    }



    private val getTask = Observer { taskList: List<PayloadTask> ->

        for (inboxTask in taskList) {
            if (inboxTask.planDate == null && inboxTask.assignee == userDi.id)
                inboxTaskList!!.add(inboxTask)
        }
        adapter = InboxTaskAdapter(inboxTaskList!!, context!!, inboxViewModel!!, projects)
        inbox_recycler!!.adapter = adapter
    }


    companion object {
        private const val TAG = "Inbox"
    }
}
