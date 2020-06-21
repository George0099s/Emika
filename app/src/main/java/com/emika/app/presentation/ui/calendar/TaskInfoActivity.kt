package com.emika.app.presentation.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.text.*
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.network.pojo.subTask.Comment
import com.emika.app.data.network.pojo.subTask.SubTask
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.di.EpicLinks
import com.emika.app.di.Project
import com.emika.app.di.ProjectsDi
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog
import com.emika.app.presentation.adapter.calendar.CommentAdapter
import com.emika.app.presentation.adapter.calendar.SubTaskAdapter
import com.emika.app.presentation.adapter.ItemTouchHelper.SubTaskTouchHelperCallback
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.task_info.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TaskInfoActivity : AppCompatActivity() {
    @Inject
    lateinit var epicLinksDi: EpicLinks

    @Inject
    lateinit var projectDi: Project
    @Inject
    lateinit var projectDagger: ProjectsDi
    @Inject
    lateinit var projectsDagger: ProjectsDi
    private var dateAndTime = Calendar.getInstance()
    private val app = EmikaApplication.instance
    private var taskName: EditText? = null
    private var taskDescription: EditText? = null
    private var subTaskRecycler: RecyclerView? = null
    private var adapter: SubTaskAdapter? = null
    private var userImg: ImageView? = null
    private var spentTime: TextView? = null
    private var estimatedTime: TextView? = null
    private var planDate: TextView? = null
    private var deadlineDate: TextView? = null
    private var userName: TextView? = null
    private var task: PayloadTask? = null
    private var priority: TextView? = null
    private var project: TextView? = null
    private var section: TextView? = null
    private var workFlow: TextView? = null
    private var customField: TextView? = null
    private var epicLinkLinearLayout: LinearLayout? = null
    private var addSubTask: TextView? = null
    private lateinit var taskInfoViewModel: TaskInfoViewModel
    private lateinit var token: String
    private var comments: ArrayList<Comment> = arrayListOf()
    private lateinit var deadlineDateString: String
    private lateinit var selectProject: LinearLayout
    private lateinit var taskAssignee: LinearLayout
    private lateinit var assignee: Assignee
    private var canceled = false
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var calendarViewModelFromBoard: CalendarViewModel
    private lateinit var df: DecimalFormat
    private lateinit var imm: InputMethodManager
    private lateinit var chipGroup: ChipGroup
    private lateinit var taskIsDone: TextView
    private var taskId: String? = null
    private lateinit var mainActivity :AppCompatActivity
    private var adapterComments: CommentAdapter? = null
    private var recyclerComments: RecyclerView? = null
    private var vibrator: Vibrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_info)
        val toolbar = findViewById<Toolbar>(R.id.task_toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.overflowIcon!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white)
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.title = "Calendar"
        toolbar.setNavigationOnClickListener{
            finish() }
        initViews()
    }

    private fun goToWebView(url: String){
        val intent = Intent(this, WebViewTask::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun initViews() {
        c = Calendar.getInstance()
        c!!.add(Calendar.DATE, +24)
        df = DecimalFormat("#.#")
        app.component?.inject(this)
        recyclerComments = findViewById<RecyclerView>(R.id.task_info_comms_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
        workFlow = findViewById(R.id.task_info_work_flow)
        customField = findViewById(R.id.task_info_custom_field)
        customField!!.setOnClickListener {
            goToWebView(task!!.customFieldsLink)
        }
        workFlow!!.setOnClickListener {
           goToWebView(task!!.workflowLink)
        }
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        taskIsDone = findViewById(R.id.task_info_task_done)
        chipGroup = findViewById(R.id.task_info_epic_links_chip_group)
        epicLinkLinearLayout = findViewById(R.id.task_info_epic_links)
        task = intent.getParcelableExtra("task")!!
        taskId = task!!.id
        calendarViewModelFromBoard = intent.getParcelableExtra("calendarViewModel")!!
        token = EmikaApplication.instance.sharedPreferences?.getString("token", null)!!
        subTaskRecycler = findViewById(R.id.task_info_subtask_recycler)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
        findViewById<LinearLayout>(R.id.task_info_comms).setOnClickListener{goToComms()}
        subTaskRecycler!!.setHasFixedSize(true)
        subTaskRecycler!!.layoutManager = layoutManager
        addSubTask = findViewById(R.id.task_info_add_sub_task)
        addSubTask!!.setOnClickListener { view: View -> addSubTask(view) }
        taskDescription = findViewById(R.id.task_info_description)
        taskDescription!!.addTextChangedListener(taskDescriptionTextWatcher)
        taskName = findViewById(R.id.task_info_name)
        taskName!!.addTextChangedListener(taskNameTextWatcher)
        priority = findViewById(R.id.task_info_priority)
        priority!!.setOnClickListener { v: View -> showPopupMenu(v) }
        spentTime = findViewById(R.id.task_info_spent_time)
        spentTime!!.setOnClickListener { v: View? -> setSpentTime() }
        estimatedTime = findViewById(R.id.task_info_estimated_time)
        estimatedTime!!.setOnClickListener { v: View? -> setTime() }
        planDate = findViewById(R.id.task_info_plan_date)
        planDate!!.setOnClickListener { v: View? -> setPlanDate() }
        task_info_press_estimated_time.setOnClickListener { v: View? -> setTime() }
        task_info_press_spent_time.setOnClickListener { v: View? -> setSpentTime() }
        deadlineDate = findViewById(R.id.task_info_deadline_date)
        deadlineDate!!.setOnClickListener { v: View? -> setDeadlineDate() }
        userName = findViewById(R.id.task_info_user_name)
        userImg = findViewById(R.id.task_info_user_img)
        userImg!!.setOnClickListener { view: View -> selectCurrentAssignee() }
        userName!!.setOnClickListener { view: View -> selectCurrentAssignee() }
        taskAssignee = findViewById(R.id.task_info_assign)
        taskAssignee.setOnClickListener { view: View -> selectCurrentAssignee() }
        selectProject = findViewById(R.id.task_info_select_project)
        selectProject.setOnClickListener { view: View -> selectProject() }
        project = findViewById(R.id.task_info_project)
        section = findViewById(R.id.task_info_project_section)
        epicLinkLinearLayout!!.setOnClickListener { view: View -> selectEpicLinks() }
        taskInfoViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(TaskInfoViewModel::class.java)
        calendarViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        taskInfoViewModel.assigneeMutableLiveData.observe(this, setAssignee)
        taskInfoViewModel.task = task
        taskInfoViewModel.taskMutableLiveData.observe(this, getTask)
        taskInfoViewModel.getDbEpicLinks()
        taskInfoViewModel.epicLinksMutableLiveData.observe(this, setTaskEpicLinks)
        taskInfoViewModel.getSubTaskMutableLiveData(task!!.id).observe(this, getSubTask)
        taskInfoViewModel.getCommentsMutableLiveData(task!!.id).observe(this, getComments)
//        calendarViewModel!!.projectMutableLiveData.observe(this, getProjects)
//        calendarViewModel!!.sectionListMutableLiveData.observe(this, getSections)
        taskInfoViewModel.projectMutableLiveData.observe(this, getProjectsMutable)
        taskInfoViewModel.subTaskIdMutableLiveData.observe(this, getAddedSubTaskId)
    }

    private fun goToComms() {
        val intent = Intent(this, Comments::class.java)
        intent.putExtra("taskId", taskId)
        startActivity(intent)
    }


    private var deadlineDateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = month
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        deadlineDateString = DateHelper.getDatePicker(year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
        deadlineDate!!.text = DateHelper.getDate(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
        deadlineDate!!.setTextColor(resources.getColor(R.color.white))
        deadlineDate!!.background = resources.getDrawable(R.drawable.shape_selected_date)
        deadlineDate!!.setPadding(12, 0, 12, 0)
        task!!.deadlineDate = DateHelper.getDatePicker(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
        calendarViewModel.updateSocketTask(task)
        calendarViewModelFromBoard.getAllDbTaskByAssignee(assignee.id)
        calendarViewModelFromBoard.listMutableLiveData
    }
    private var planDateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = month
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        planDate!!.text = DateHelper.getDate(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
        task!!.planDate = DateHelper.getDatePicker(String.format("%s-%s-%s", year.toString(), (month + 1).toString(), dayOfMonth.toString()))
        calendarViewModel.updateSocketTask(task)
    }
    private var estimatedTimeListener = TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
        dateAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
        dateAndTime[Calendar.MINUTE] = minute
        estimatedTime!!.text = String.format("%sh", hourOfDay.toString())
        val dayMinutes = hourOfDay * 60 + minute
        if (dayMinutes % 60 == 0)
            estimatedTime!!.text = String.format("%sh", (dayMinutes / 60).toString())
        else {
            var s: String = String.format("%sh", df.format(dayMinutes / 60.0f.toDouble()))
            s = s.replace(',', '.')
            estimatedTime!!.text = s
        }
            task!!.duration = dayMinutes
        calendarViewModel.updateTask(task)
        calendarViewModel.updateDbTask(task)
    }
   private var spentTimeListener = TimePickerDialog.OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
        dateAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
        dateAndTime[Calendar.MINUTE] = minute
        val dayMinutes = hourOfDay * 60 + minute
        if (dayMinutes % 60 == 0)
            spentTime!!.text = String.format("%sh", (dayMinutes / 60).toString())
        else {
            var s: String = String.format("%sh", df.format(dayMinutes / 60.0f.toDouble()))
            s = s.replace(',', '.')
            spentTime!!.text = s
        }
         task!!.durationActual = hourOfDay * 60 + minute
        calendarViewModel.updateDbTask(task)
        calendarViewModel.updateTask(task)
    }

    private var c: Calendar? = null

    private val setAssignee = androidx.lifecycle.Observer { assignee1: Assignee ->
        assignee = assignee1
        task!!.assignee = assignee.id
        userName!!.text = String.format("%s %s", assignee1.firstName, assignee1.lastName)
        if (assignee1.pictureUrl != null) Glide.with(this).load(assignee1.pictureUrl).apply(RequestOptions.circleCropTransform()).into(userImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg!!)
    }
    private val taskNameTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            task!!.name = taskName!!.text.toString()
            taskInfoViewModel.updateTask(task)
        }
    }
    private val taskDescriptionTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            task!!.description = taskDescription!!.text.toString()
            taskInfoViewModel.updateTask(task)
        }
    }

    private val setTaskEpicLinks = androidx.lifecycle.Observer { epicLinksEntities: List<EpicLinksEntity> ->
        if (epicLinksEntities.isNotEmpty()) {
//            epicLink!!.setTextColor(resources.getColor(R.color.black))
            var s: String? = null
            chipGroup.removeAllViews()
            chipGroup.isClickable = false
            for (epic in epicLinksEntities) {

                val chip = Chip(chipGroup.context)
                chip.text= epic.name

                chip.isClickable = true
                chip.isCheckable = false
                chip.chipCornerRadius = 8.0f
                chip.chipBackgroundColor = resources.getColorStateList(R.color.epic_links_chips)
                chip.chipStrokeWidth = 2.0f
                chip.chipStrokeColor = resources.getColorStateList(R.color.epic_links_chips_stroke)
                chip.setOnClickListener{selectEpicLinks()}
                chipGroup.addView(chip)

            }

        } else {
            chipGroup.removeAllViews()
        }
    }
    private var mLastClickTime: Long = 0
    private val getSubTask = androidx.lifecycle.Observer { subTask: List<SubTask>  ->
        adapter = SubTaskAdapter(subTask, null, taskInfoViewModel)
        subTaskRecycler!!.adapter = adapter
        subTaskRecycler!!.setItemViewCacheSize(subTask.size)
        val callback: ItemTouchHelper.Callback = SubTaskTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(subTaskRecycler)
    }
    private val getComments = androidx.lifecycle.Observer { comments: List<Comment> ->
        if (comments.isNotEmpty()) {
            val  newList: MutableList<Comment> = arrayListOf()
            newList.add(comments[comments.size-1])
            adapterComments = CommentAdapter(newList, this, taskInfoViewModel.memberEntities, null)
            recyclerComments!!.adapter = adapterComments
            task_info_no_comms.visibility = View.GONE
            recyclerComments!!.visibility = View.VISIBLE
            task_info_count_comms.text = "${comments.size} comments"
        } else {
            task_info_no_comms.visibility = View.VISIBLE
            recyclerComments!!.visibility = View.GONE
            task_info_count_comms.text = "Add comments"
        }
    }

    private val getAddedSubTaskId = androidx.lifecycle.Observer<String> { subTaskId: String? -> adapter!!.taskList[adapter!!.itemCount - 1]!!.id = subTaskId }

    private val getProjectsMutable = androidx.lifecycle.Observer { project1: Project? ->

        for (proj in projectDagger.projects) {
            if (project1!!.projectId == proj.id) {
                project!!.text = proj.name
            }
        }

        for (section in projectDagger.sections) {
            if (project1!!.projectSectionId == section.id) {
                this.section!!.text = section.name
            }
        }
    }

    private fun addSubTask(v: View) {
        val subTasks: MutableList<String> = ArrayList()
        val subTask = SubTask()
        subTask.status = "wip"
        subTask.name = ""
        subTask.parentTaskId = task!!.id
        subTask.assignee = assignee.id
        subTask.planDate = task!!.planDate
        subTask.deadlineDate = task!!.deadlineDate
        subTask.companyId = task!!.companyId
        subTask.projectId = task!!.projectId
        subTask.sectionId = task!!.sectionId
        subTask.duration = 60
        if (adapter!!.taskList.size == 0) {
            adapter!!.addSubTask(subTask)
            adapter!!.notifyItemInserted(adapter!!.itemCount)
            subTaskRecycler!!.scrollToPosition(adapter!!.itemCount)
            for (i in adapter!!.taskList.indices) {
                subTasks.add(adapter!!.taskList[i]!!.name)
            }
            task!!.subTaskList = subTasks
            taskInfoViewModel.updateTask(task)
            taskInfoViewModel.addSubTask(subTask)
            subTaskRecycler!!.requestFocus(adapter!!.itemCount - 1)
        } else if (adapter!!.taskList[adapter!!.itemCount - 1]!!.name.isNotEmpty()) {
            adapter!!.addSubTask(subTask)
            adapter!!.notifyItemInserted(adapter!!.itemCount)
            subTaskRecycler!!.scrollToPosition(adapter!!.itemCount)
            for (i in adapter!!.taskList.indices) {
                subTasks.add(adapter!!.taskList[i]!!.name)
            }
            task!!.subTaskList = subTasks
            taskInfoViewModel.updateTask(task)
            taskInfoViewModel.addSubTask(subTask)
            subTaskRecycler!!.requestFocus(adapter!!.itemCount - 1)
        } else {
            val myAwesomeSnackbar = Snackbar.make(
                    v,
                    "Sub-task name is missing",
                    Snackbar.LENGTH_SHORT
            )
            myAwesomeSnackbar.show()
        }
    }

    private fun refreshTask() {
        task!!.status = "wip"
        calendarViewModel.updateTask(task)
        canceled = false
        taskName!!.setTextColor(resources.getColor(R.color.black))
        taskName!!.paintFlags = Paint.ANTI_ALIAS_FLAG
    }

    private val getTask = androidx.lifecycle.Observer { task: PayloadTask ->
        taskName!!.setText(task.name)
        planDate!!.text = DateHelper.getDate(task.planDate)

        if (task.workFlowText != null) {
            workFlow!!.text = task.workFlowText
            workFlow!!.visibility = View.VISIBLE
        } else {
            workFlow!!.visibility = View.GONE
        }

        if (task.customFieldsText != null) {
            customField!!.text = task.customFieldsText
            customField!!.visibility = View.VISIBLE
        } else {
            customField!!.visibility = View.GONE
        }

        if (task.deadlineDate != null && task.deadlineDate != "null") {
            deadlineDate!!.text = DateHelper.getDate(task.deadlineDate)
            deadlineDate!!.setTextColor(resources.getColor(R.color.white))
            deadlineDate!!.background = resources.getDrawable(R.drawable.shape_selected_date)
            deadlineDate!!.setPadding(12,0,12,0)
        }
        if (task.description != null)
            taskDescription!!.setText(Html.fromHtml(task.description))
        if (task.status == "canceled") {
            calendarViewModel.updateTask(task)
            canceled = true
            taskName!!.setTextColor(resources.getColor(R.color.task_name_done))
            taskName!!.paintFlags = taskName!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        for (i in projectsDagger.projects.indices) {
            if (task.projectId == projectsDagger.projects[i].id) {
                this.project!!.text = projectsDagger.projects[i].name
                println(projectsDagger.projects[i].name)
            }
        }
        for (i in projectsDagger.sections.indices) {
            if (task.sectionId == projectsDagger.sections[i].id) {
                section!!.text = projectsDagger.sections[i].name
                println(projectsDagger.sections[i].name)
            }
        }
        when (task.priority) {
            "low" -> {
                priority!!.text = "Low"
                priority!!.setTextColor(resources.getColor(R.color.priority_low))
            }
            "normal" -> {
                priority!!.text = "Normal"
                priority!!.setTextColor(resources.getColor(R.color.priority_normal))
            }
            "high" -> {
                priority!!.text = "High"
                priority!!.setTextColor(resources.getColor(R.color.priority_high))
            }
            "urgent" -> {
                priority!!.text = "Urgent"
                priority!!.setTextColor(resources.getColor(R.color.priority_urgent))
            }
        }
        if (task.status == "done")
            taskIsDone.visibility = View.VISIBLE
        else
            taskIsDone.visibility = View.GONE



        if (task.duration % 60 == 0)
            estimatedTime!!.text = String.format("%sh", (task.duration / 60).toString())
        else
            estimatedTime!!.text = String.format("%sh", df.format(task.duration / 60.0f.toDouble()))
        if (task.durationActual % 60 == 0)
            spentTime!!.text = String.format("%sh", (task.durationActual / 60).toString())
        else
            spentTime!!.text = String.format("%sh", df.format(task.durationActual / 60.0f.toDouble()))
    }

    private fun selectProject() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val bundle = Bundle()
            val mySheetDialog = BottomSheetAddTaskSelectProject()
            bundle.putParcelable("taskInfoViewModel", taskInfoViewModel)
            bundle.putParcelable("task", task)
            mySheetDialog.arguments = bundle
            val fm = supportFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun selectEpicLinks() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val bundle = Bundle()
            bundle.putParcelable("task", task)
            bundle.putParcelable("taskInfoViewModel", taskInfoViewModel)
            val mySheetDialog = BottomSheetSelectEpicLinks()
            mySheetDialog.arguments = bundle
            val fm = supportFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun taskDone() {
        if (task!!.status == "done") {
            task!!.status = "wip"
            taskInfoViewModel.updateTask(task)
            canceled = false
        } else {
            task!!.status = "done"
            taskInfoViewModel.updateTask(task)
            canceled = false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_info_menu, menu)
        menu!!.findItem(R.id.menu_delete_task).isVisible = canceled && task!!.status != "done"
        menu.findItem(R.id.menu_recover_task).isVisible = task!!.status == "done" || canceled
        menu.findItem(R.id.menu_done_task).isVisible = task!!.status != "done" && !canceled
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_cancel_task -> {
                task!!.status = "canceled"
                canceled = true
                calendarViewModel.updateTask(task)
                taskName!!.setTextColor(resources.getColor(R.color.task_name_done))
                taskName!!.paintFlags = taskName!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                invalidateOptionsMenu(this)
            }
            R.id.menu_rename_task -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
                taskName!!.setSelection(taskName!!.text.length)
                taskName!!.requestFocus()

            }
            R.id.menu_move_task -> planDate!!.callOnClick()
            R.id.menu_duplicate_task -> {
                val dialog = AddTask()
                val bundle = Bundle()
                bundle.putString("token", token)
                bundle.putParcelable("task", task)
                dialog.arguments = bundle
                val ft = supportFragmentManager
                dialog.show(ft, "DuplicateTask")
                onBackPressed()

//                dismiss()
            }
            R.id.menu_delete_task -> {
                task!!.status = "deleted"
                calendarViewModel.updateTask(task)
                onBackPressed()
            }

               R.id.menu_recover_task -> {
                   refreshTask()
                   taskIsDone.visibility = View.GONE
                   invalidateOptionsMenu(this)
               }
               R.id.menu_done_task -> {
                   task!!.status = "done"
                   taskInfoViewModel.updateTask(task)
                   canceled = false
                   taskIsDone.visibility = View.VISIBLE
                   invalidateOptionsMenu(this)

               }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun menuIconWithText(r: Drawable, title: String): CharSequence {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("    $title")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun showMenu(){
        val menu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PopupMenu(this, view, Gravity.END, R.attr.actionOverflowMenuStyle, 0)
        } else {
            PopupMenu(this, view)
        }
//        val menu = PopupMenu(activity!!, view)
        if (!canceled) {
            menu.menu.add(0, 1, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_cancel_task), resources.getString(R.string.cancel_task)))
        }
        menu.menu.add(0, 2, 2, menuIconWithText(resources.getDrawable(R.drawable.ic_rename_task), resources.getString(R.string.rename_task)))
        menu.menu.add(0, 3, 3, menuIconWithText(resources.getDrawable(R.drawable.ic_move_task), resources.getString(R.string.move_task)))
        menu.menu.add(0, 4, 4, menuIconWithText(resources.getDrawable(R.drawable.ic_duplicate_task), resources.getString(R.string.duplicate_task)))
        if (canceled) menu.menu.add(0, 5, 5, menuIconWithText(resources.getDrawable(R.drawable.ic_archive), resources.getString(R.string.archieve_task)))
        menu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        1 -> {
                            task!!.status = "canceled"
                            canceled = true
                            calendarViewModel.updateTask(task)
                            taskName!!.setTextColor(resources.getColor(R.color.task_name_done))
                            taskName!!.paintFlags = taskName!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            return@setOnMenuItemClickListener true
                        }
                        2 -> {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
                            taskName!!.setSelection(taskName!!.text.length)
                            taskName!!.requestFocus()
                            return@setOnMenuItemClickListener true
                        }
                        3 -> {
                            planDate!!.callOnClick()
                            return@setOnMenuItemClickListener true
                        }
                        4 -> {
                            val dialog = AddTask()
                            val bundle = Bundle()
                            bundle.putString("token", token)
                            bundle.putParcelable("task", task)
                            dialog.arguments = bundle
                            val ft = supportFragmentManager
                            dialog.show(ft, "DuplicateTask")
                            finish()
                            return@setOnMenuItemClickListener true
                        }
                        5 -> {
                            task!!.status = "deleted"
                            calendarViewModel.updateTask(task)
//                            dismiss()
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
        menu.show()
    }

    private fun showPopupMenu(v: View) {
        vibrator!!.vibrate(100)
        val popupMenu = PopupMenu(this, v)
            popupMenu.inflate(R.menu.priority_popup_menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.low -> {
                                priority!!.text = "Low"
//                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
                                task!!.priority = "low"
                                priority!!.setTextColor(resources.getColor(R.color.priority_low))
                                calendarViewModel.updateTask(task)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.normal -> {
                                priority!!.text = "Normal"
//                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_normal), null, null, null)
                                priority!!.setTextColor(resources.getColor(R.color.priority_normal))
                                task!!.priority = "normal"
                                calendarViewModel.updateTask(task)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.high -> {
                                priority!!.text = "High"
//                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
                                priority!!.setTextColor(resources.getColor(R.color.priority_high))
                                task!!.priority = "high"
                                calendarViewModel.updateTask(task)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.urgent -> {
                                priority!!.text = "Urgent"
//                                priority!!.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_task_urgent), null, null, null)
                                priority!!.setTextColor(resources.getColor(R.color.priority_urgent))
                                task!!.priority = "urgent"
                                calendarViewModel.updateTask(task)
                                return@setOnMenuItemClickListener true
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
            popupMenu.show()
    }

    fun setPlanDate() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val datePickerDialog = DatePickerDialog(this, planDateListener,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH])
            datePickerDialog.setTitle("Set plan date")
            datePickerDialog.datePicker.minDate = Date().time
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date") { dialog: DialogInterface?, which: Int ->
                planDate!!.text = resources.getString(R.string.inbox)
                task!!.planDate = null
                if (task!!.planDate == null)
                    Toast.makeText(this, "Task added to inbox", Toast.LENGTH_SHORT).show()
                calendarViewModel.updateTask(task)
            }
            datePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setDeadlineDate() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val datePickerDialog = DatePickerDialog(this, deadlineDateListener,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH])
            datePickerDialog.setTitle("Set deadline date")
            datePickerDialog.datePicker.minDate = Date().time
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date") { dialog: DialogInterface?, which: Int ->
                deadlineDate!!.text =resources.getString(R.string.set_date)
                deadlineDate!!.setTextColor(resources.getColor(R.color.green))
                deadlineDate!!.background = null
                task!!.deadlineDate = null
                calendarViewModel.updateTask(task)
            }
            datePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setTime() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val hourOfDay = task!!.duration / 60
            val timePickerDialog = CustomTimePickerDialog(this, estimatedTimeListener, hourOfDay, 0, true)
            timePickerDialog.setIcon(R.drawable.ic_estimated_time)
            timePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    fun setSpentTime() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val hourOfDay = task!!.durationActual / 60
            val minute = task!!.durationActual % 60
            val timePickerDialog = CustomTimePickerDialog(this, spentTimeListener, hourOfDay, minute, true)
            timePickerDialog.setIcon(R.drawable.ic_estimated_time)
            timePickerDialog.show()
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun selectCurrentAssignee() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            vibrator!!.vibrate(50)
            val bundle = Bundle()
            bundle.putParcelable("calendarViewModel", calendarViewModelFromBoard)
            bundle.putParcelable("taskInfoViewModel", taskInfoViewModel)
            bundle.putParcelable("task", task)
            bundle.putString("from", "task info")
            val mySheetDialog = BottomSheetCalendarSelectUser()
            mySheetDialog.arguments = bundle
            val fm = supportFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    companion object {
        private const val TAG = "TaskInfoActivity"
    }

}
