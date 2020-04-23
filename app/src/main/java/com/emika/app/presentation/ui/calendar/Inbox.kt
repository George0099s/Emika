package com.emika.app.presentation.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import javax.inject.Inject

class Inbox : AppCompatActivity() {
    private var viewModel: CalendarViewModel? = null
    private var inboxViewModel: InboxViewModel? = null
    private var inboxRecycler: RecyclerView? = null
    private var adapter: InboxTaskAdapter? = null
    private var token: String? = null
    private var inboxTaskList: MutableList<PayloadTask>? = null
    private var date: String = ""
    private val decor: MemberItemDecoration = MemberItemDecoration()
    private var projects: List<ProjectEntity> = arrayListOf()
    @JvmField
    @Inject
    var userDi: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)
        initView()
    }

    private fun initView() {
        EmikaApplication.getInstance().component.inject(this)
        inboxText.text = "Unplanned tasks assigned to "+ userDi!!.firstName +" and tasks created by " +userDi!!.firstName + " with no assignee"
        token = EmikaApplication.getInstance().sharedPreferences.getString("token", "")
        inboxTaskList = ArrayList()
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        inboxViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(InboxViewModel::class.java)
        viewModel!!.projectMutableLiveData.observe(this, getProjectLiveData)
        inbox_recycler.setHasFixedSize(true)
        inbox_recycler.addItemDecoration(decor)
        inbox_recycler.setLayoutManager(LinearLayoutManager(this))
        add_task_back.setOnClickListener(this::onBackPressed)
        date = intent.getStringExtra("date")
        viewModel!!.setContext(this)
        viewModel!!.getAllDbTask()
        viewModel!!.listMutableLiveData.observe(this, getTask)
//        inboxViewModel!!.addedTaskList.observe(this, getAddedTask)
        inbox_add.setOnClickListener(this::addTasks)
    }

    fun onBackPressed(v: View) {
        super.onBackPressed()
    }

    private val getProjectLiveData = Observer<List<ProjectEntity>> { projects ->
        this.projects = projects
    }

    private val getAddedTask = Observer<List<PayloadTask>> { tasks ->
        if (tasks.size > 0)
        inbox_add.text = "add(" + tasks.size+ ")"
        else
            inbox_add.text = "add"
    }
    private fun addTasks(v: View) {
        inboxViewModel!!.date = this.date
        inboxViewModel!!.addTaskFromBacklog(adapter!!.getAddedTask())
        finish()
    }
    private fun goToAddTask(v: View) {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra("date", date)
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }

    private val getTask = Observer { taskList: List<PayloadTask> ->

        for (inboxTask in taskList) {
            if (inboxTask.planDate == null && inboxTask.assignee == userDi!!.id)
                inboxTaskList!!.add(inboxTask)
        }
        adapter = InboxTaskAdapter(inboxTaskList!!, this, inboxViewModel!!, projects)
        inbox_recycler!!.adapter = adapter
    }

    companion object {
        private const val TAG = "Inbox"
    }
}