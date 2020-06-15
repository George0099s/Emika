package com.emika.app.presentation.ui.calendar

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.data.network.pojo.member.PayloadShortMember
import com.emika.app.data.network.pojo.subTask.SubTask
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.Assignee
import com.emika.app.di.Project
import com.emika.app.di.ProjectsDi
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog
import com.emika.app.presentation.adapter.calendar.SubTaskAdapter
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_task.view.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class AddTask : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_add_task, container, false)
        rootView.add_task_name.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog?.window?.setWindowAnimations(R.style.SlideUpDialogAnim)
        initView(rootView)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return super.onCreateDialog(savedInstanceState)
    }

    @Inject
    lateinit var assignee: Assignee
    var dateAndTime = Calendar.getInstance()

    @Inject
    lateinit var projectDi: Project

    @Inject
    lateinit var projectsDagger: ProjectsDi
    var deadlineDateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = month
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        deadlineDateString = DateHelper.getDatePicker(year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
        deadlineDate!!.text = DateHelper.getDate(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
        deadlineDate!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_deadline_date_active), null, null, null)
        deadlineDate!!.setTextColor(resources.getColor(R.color.black))
    }
    private var df: DecimalFormat? = null
    private var currentDate: String? = null
    private var taskName: EditText? = null
    private var taskDescription: EditText? = null
    private var addTask: ImageView? = null
    private var userImg: ImageView? = null
    private var viewModel: AddTaskListViewModel? = null
    private var profileViewModel: ProfileViewModel? = null
    private var back: Button? = null
    private var inbox: Button? = null
    private var subTaskAdapter: SubTaskAdapter? = null
    private var subTaskRecycler: RecyclerView? = null
    private var calendarViewModel: CalendarViewModel? = null
    private var boardViewModel: CalendarViewModel? = null
    private var memberList: List<PayloadShortMember?>? = null
    private var epicLinksId: MutableList<String>? = null
    private var addSubTask: TextView? = null
    private var oldAssignee: String? = null
    private val app = EmikaApplication.instance
    private var token: String? = null
    private var deadlineDateString: String? = null
    private var planDate: TextView? = null
    private var priority: TextView? = null
    private var deadlineDate: TextView? = null
    private var estimatedTime: TextView? = null
    private var userName: TextView? = null
    private var project: TextView? = null
    private var section: TextView? = null
    private var epicLinks: TextView? = null
    private var mySheetDialog: BottomSheetSelectEpicLinks? = null
    private var DATE: String? = null
    private var task: PayloadTask? = null
    private var c: Calendar? = null
    private var backLayout: LinearLayout? = null

    var planDateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = month
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        currentDate = DateHelper.getDatePicker(year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
        planDate!!.text = DateHelper.getDate(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
    }
    private var selectProject: LinearLayout? = null
    var estimatedTimeListener = TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
        dateAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
        dateAndTime[Calendar.MINUTE] = minute
        val dayMinutes = hourOfDay * 60 + minute
        if (dayMinutes % 60 == 0) estimatedTime!!.text = String.format("%sh", (dayMinutes / 60).toString()) else estimatedTime!!.text = String.format("%sh", df!!.format(dayMinutes / 60.0f.toDouble()))
        task!!.duration = dayMinutes
    }
    private val userInfo = androidx.lifecycle.Observer { userInfo: Payload? -> }

    private val taskObserver = androidx.lifecycle.Observer {
        task: PayloadTask? ->
        calendarViewModel!!.addDbTask(task)
        //        boardViewModel.downloadTasksByAssignee(assignee.getId());
//        val intent = Intent()
//        intent.putExtra("task", task)
//        setResult(42, intent)
//        finish()
        dismiss()
    }


    private val getEpicLinks = androidx.lifecycle.Observer { epicLinks1: List<PayloadEpicLinks> ->
        if (epicLinks1.size != 0) {
            epicLinks!!.setTextColor(resources.getColor(R.color.black))
            epicLinksId = ArrayList()
            if (epicLinks1.size > 1) epicLinks!!.text = String.format("%s +%s", epicLinks1[0].name, (epicLinks1.size - 1).toString()) else epicLinks!!.text = epicLinks1[0].name
            for (i in epicLinks1.indices) {
                epicLinksId!!.add(epicLinks1[i].id)
            }
        } else {
            epicLinks!!.text = "Epic links"
            epicLinks!!.setTextColor(resources.getColor(R.color.unselected_text))
        }
    }
    private val setAssignee = androidx.lifecycle.Observer { assignee1: Assignee ->
        userName!!.text = String.format("%s %s", assignee1.firstName, assignee1.lastName)
        if (assignee1.pictureUrl != null) Glide.with(this).load(assignee1.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
    }
    private val setProjectData = androidx.lifecycle.Observer { project1: Project ->
        project!!.text = project1.projectName
        section!!.text = project1.projectSectionName
    }
    private var mLastClickTime: Long = 0

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private fun initView(rootView: View) {
        c = Calendar.getInstance()
        c!!.add(Calendar.DATE, +24)
        currentDate = arguments!!.getString("date")
        taskDescription = rootView.findViewById(R.id.add_task_description)
        mySheetDialog = BottomSheetSelectEpicLinks()
        val tasks: List<SubTask> = ArrayList()
        df = DecimalFormat("#.#")
        task = arguments!!.getParcelable("task")
        addSubTask = rootView.findViewById(R.id.add_task_add_sub_task)
        addSubTask!!.setOnClickListener(View.OnClickListener { view: View -> addSubTask(view) })
        app.component?.inject(this)
        token = EmikaApplication.instance.sharedPreferences?.getString("token", "")
        subTaskRecycler = rootView.findViewById(R.id.add_task_subtask_recycler)
        inbox = rootView.findViewById(R.id.add_task_inbox)
        inbox!!.setOnClickListener(View.OnClickListener { view: View -> goToInBox(view) })
        userImg = rootView.findViewById(R.id.add_task_user_img)
        userImg!!.setOnClickListener(View.OnClickListener { view: View -> selectCurrentAssignee(view) })
        userName = rootView.findViewById(R.id.add_task_user_name)
        userName!!.setOnClickListener(View.OnClickListener { view: View -> selectCurrentAssignee(view) })
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(AddTaskListViewModel::class.java).apply {
            assignee.observe(viewLifecycleOwner, setAssignee)
            projectMutableLiveData.observe(viewLifecycleOwner, setProjectData)
        }
        calendarViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        profileViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ProfileViewModel::class.java)
        boardViewModel = arguments!!.getParcelable("calendarViewModel")
        profileViewModel!!.userMutableLiveData.observe(viewLifecycleOwner, userInfo)
        taskName = rootView.findViewById(R.id.add_task_name)
        taskName!!.requestFocus()
        taskName!!.imeOptions = EditorInfo.IME_ACTION_DONE
        taskName!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
        taskName!!.addTextChangedListener(textWatcher)
        addTask = rootView.findViewById(R.id.add_task_img)
        addTask!!.setOnClickListener { view: View -> addTask(view) }
        back = rootView.findViewById(R.id.add_task_back)
        backLayout = rootView.findViewById(R.id.add_task_back_back_layout)
        back!!.setOnClickListener { view: View -> this.onBackPressed(view) }
        backLayout!!.setOnClickListener { view: View -> this.onBackPressed(view) }
        planDate = rootView.findViewById(R.id.add_task_plan_date)
        estimatedTime = rootView.findViewById(R.id.add_task_estimated_time)
        estimatedTime!!.setOnClickListener { v: View? -> setTime(v) }
        deadlineDate = rootView.findViewById(R.id.add_task_deadline_date)
        deadlineDate!!.setOnClickListener { v: View? -> setDeadlineDate(v) }
        DATE = arguments!!.getString("date")
        priority = rootView.findViewById(R.id.add_task_priority)
        priority!!.setOnClickListener { v: View -> showPopupMenu(v) }
        planDate!!.setOnClickListener { v: View? -> setPlanDate(v) }
        memberList = arguments!!.getParcelableArrayList("members")
        project = rootView.findViewById(R.id.add_task_project)
        section = rootView.findViewById(R.id.add_task_project_section)
        selectProject = rootView.findViewById(R.id.add_task_select_project)
        selectProject!!.setOnClickListener(View.OnClickListener { view: View -> selectProject(view) })
        epicLinks = rootView.findViewById(R.id.add_task_epic_links)
        epicLinks!!.setOnClickListener(View.OnClickListener { view: View -> selectEpicLinks(view) })
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
        viewModel!!.epicLinksMutableLiveData.observe(viewLifecycleOwner, getEpicLinks)
        subTaskRecycler!!.setHasFixedSize(true)
        subTaskRecycler!!.layoutManager = layoutManager
        subTaskAdapter = SubTaskAdapter(tasks, calendarViewModel, null)
        subTaskRecycler!!.adapter = subTaskAdapter
        if (task != null)
            setTaskInfo(task)
        else {
            task = PayloadTask()
            project?.text = projectsDagger.projects[0]?.name
            for (section in projectsDagger.sections)
                if (section.id == projectsDagger.projects[0]?.defaultSectionId)
                    this.section?.text = section.name
        }
            oldAssignee = assignee.id
    }

    private fun goToInBox(view: View) {
        val inbox = Inbox()
        val bundle = Bundle()
        bundle.putString("date", currentDate)
        inbox.arguments = bundle
        val ft = parentFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_right_anim, R.anim.slide_out_right_anim)
        inbox.show(ft, "inboxDialog")
        dismiss()
    }

    private fun addSubTask(view: View) {
        val subTasks: List<SubTask> = ArrayList()
        val subTask = SubTask()
        subTask.status = "wip"
        subTask.name = ""
        if (subTaskAdapter!!.taskList.size == 0) {
            subTaskAdapter!!.addSubTask(subTask)
            subTaskAdapter!!.notifyItemInserted(subTaskAdapter!!.itemCount)
            subTaskRecycler!!.scrollToPosition(subTaskAdapter!!.itemCount)
            subTaskRecycler!!.requestFocus(subTaskAdapter!!.itemCount - 1)
        } else if (!subTaskAdapter!!.taskList[subTaskAdapter!!.itemCount - 1].name.isEmpty()) {
            subTaskAdapter!!.addSubTask(subTask)
            subTaskAdapter!!.notifyItemInserted(subTaskAdapter!!.itemCount)
            subTaskRecycler!!.scrollToPosition(subTaskAdapter!!.itemCount)
            subTaskRecycler!!.requestFocus(subTaskAdapter!!.itemCount - 1)
        } else {
            val myAwesomeSnackbar = Snackbar.make(
                    view,
                    "Sub-task name is missing",
                    Snackbar.LENGTH_SHORT
            )
            myAwesomeSnackbar.show()
        }
    }

    private fun onBackPressed(view: View) {
        dismiss()
    }

    private fun selectProject(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val bundle = Bundle()
            val mySheetDialog = BottomSheetAddTaskSelectProject()
            bundle.putParcelable("addTaskViewModel", viewModel)
            mySheetDialog.arguments = bundle
            val fm = parentFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun selectCurrentAssignee(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val bundle = Bundle()
            bundle.putParcelableArrayList("members", memberList as ArrayList<out Parcelable?>?)
            bundle.putParcelable("calendarViewModel", boardViewModel)
            bundle.putParcelable("addTaskViewModel", viewModel)
            bundle.putString("from", "add task")
            val mySheetDialog = BottomSheetCalendarSelectUser()
            mySheetDialog.arguments = bundle
            val fm = parentFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun selectEpicLinks(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val bundle = Bundle()
            bundle.putParcelable("addTaskViewModel", viewModel)
            mySheetDialog!!.arguments = bundle
            val fm = parentFragmentManager
            mySheetDialog!!.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun addTask(view: View) {
        if (taskName!!.text.toString().isEmpty()) {
            taskName!!.requestFocus()
            taskName!!.error = "Task name is missing"
        } else {
            val subTasks: MutableList<String> = ArrayList()
            task!!.name = taskName!!.text.toString()
            task!!.projectId = projectDi!!.projectId
            task!!.planDate = currentDate
            task!!.deadlineDate = deadlineDateString
            task!!.assignee = assignee!!.id
            task!!.description = taskDescription!!.text.toString()
            task!!.priority = priority!!.text.toString().toLowerCase()
            task!!.sectionId = projectDi!!.projectId
            task!!.planOrder = "1"
            if (task!!.duration == null) task!!.duration = 60
            task!!.epicLinks = epicLinksId
            for (i in subTaskAdapter!!.taskList.indices) {
                subTasks.add(subTaskAdapter!!.taskList[i].name)
            }
            task!!.subTaskList = subTasks
            if (task!!.planDate == null) {
                Toast.makeText(context, "Task added to inbox", Toast.LENGTH_LONG).show()
            }
            viewModel!!.getMutableLiveData(task).observe(viewLifecycleOwner, taskObserver)
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (taskName!!.text.toString().length > 0) inbox!!.visibility = View.GONE
            if (taskName!!.text.toString().length == 0) inbox!!.visibility = View.VISIBLE
        }
    }

    private fun showPopupMenu(v: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val popupMenu = PopupMenu(context, v)
            popupMenu.inflate(R.menu.priority_popup_menu)
            popupMenu
                    .setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.low -> {
                                priority!!.text = "Low"
                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.normal -> {
                                priority!!.text = "Normal"
                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_normal), null, null, null)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.high -> {
                                priority!!.text = "High"
                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.urgent -> {
                                priority!!.text = "Urgent"
                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_task_urgent), null, null, null)
                                return@setOnMenuItemClickListener true
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
            popupMenu.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setPlanDate(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val datePickerDialog = DatePickerDialog(context!!, planDateListener,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH])
            datePickerDialog.setTitle("Set plan date")
            datePickerDialog.datePicker.minDate = Date().time
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date") { dialog: DialogInterface?, which: Int ->
                planDate!!.text = resources.getString(R.string.inbox)
                currentDate = null
            }
            datePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setDeadlineDate(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val datePickerDialog = DatePickerDialog(context!!, deadlineDateListener,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH])
            datePickerDialog.setTitle("Set deadline date")
            datePickerDialog.datePicker.minDate = Date().time
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date") { dialog: DialogInterface?, which: Int ->
                deadlineDate!!.text = resources.getString(R.string.no_deadline)
                deadlineDateString = null
            }
            datePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setTime(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val timePickerDialog = CustomTimePickerDialog(context!!, estimatedTimeListener, 1, 0, true)
            timePickerDialog.setIcon(R.drawable.ic_estimated_time)
            timePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun setTaskInfo(task: PayloadTask?) {
        if (task != null) {
            task.id = ""
            currentDate = task.planDate
            taskName!!.setText(task.name)
            planDate!!.text = DateHelper.getDate(task.planDate)
            if (task.deadlineDate != null && task.deadlineDate != "null") {
                deadlineDate!!.text = task.deadlineDate
                deadlineDate!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_deadline_date_active), null, null, null)
                deadlineDate!!.setTextColor(resources.getColor(R.color.black))
            }
            projectDi!!.projectId = task.projectId
            projectDi!!.projectSectionId = task.sectionId
            for (project in projectsDagger.projects)
                if (task.projectId == project.id)
                    this.project?.text = project.name
            for (section in  projectsDagger.sections)
                if (task.sectionId == section.id)
                    this.section?.text = section.name

            when (task.priority) {
                "low" -> {
                    priority!!.text = "Low"
                    priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
                }
                "normal" -> {
                    priority!!.text = "Normal"
                    priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_normal), null, null, null)
                }
                "high" -> {
                    priority!!.text = "High"
                    priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
                }
                "urgent" -> {
                    priority!!.text = "Urgent"
                    priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_task_urgent), null, null, null)
                }
            }
            if (task.duration % 60 == 0) estimatedTime!!.text = String.format("%sh", (task.duration / 60).toString()) else estimatedTime!!.text = String.format("%sh", df!!.format(task.duration / 60.0f.toDouble()))
        }
    }

    companion object {
        private const val TAG = "AddTaskActivity"
    }

}

