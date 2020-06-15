package com.emika.app.presentation.ui.calendar

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.db.entity.ProjectEntity
import com.emika.app.data.network.networkManager.profile.UserNetworkManager
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.member.PayloadShortMember
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.Assignee
import com.emika.app.di.EpicLinks
import com.emika.app.di.User
import com.emika.app.features.calendar.BoardView
import com.emika.app.features.calendar.ColumnProperties
import com.emika.app.features.calendar.DragItem
import com.emika.app.features.hourcounter.HourCounterView
import com.emika.app.presentation.adapter.calendar.ItemAdapter
import com.emika.app.presentation.ui.chat.ChatActivity
import com.emika.app.presentation.ui.profile.ProfileActivity
import com.emika.app.presentation.utils.Constants
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.StartActivityViewModel
import com.emika.app.presentation.viewmodel.calendar.BottomSheetDialogViewModel
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

class CalendarActivity : AppCompatActivity() {

    @Inject
    lateinit var assignee: Assignee

    @Inject
    lateinit var epicLinks: EpicLinks

    @Inject
    lateinit var user: User
    private var mLastClickTime: Long = 0
    private lateinit var mBoardView: BoardView
    private lateinit var selectCurrentUser: ConstraintLayout
    private var token: String? = null
    private val mColumns = 0
    private var epicLinksEntities: List<EpicLinksEntity> = ArrayList()
    private lateinit var rightScroll: ImageButton
    private lateinit var leftScroll: ImageButton
    private val app = EmikaApplication.instance
    private lateinit var addTask: FloatingActionButton
    private val payloadTaskList: List<PayloadTask> = ArrayList()
    private var projectEntities: List<ProjectEntity> = ArrayList()
    private var profileViewModel: ProfileViewModel? = null
    private var bottomSheetDialogViewModel: BottomSheetDialogViewModel? = null
    private var userNetworkManager: UserNetworkManager? = null
    private var fabImg: ImageView? = null
    private var viewModel: CalendarViewModel? = null
    private var fabUserName: TextView? = null
    private var fabJobTitle: TextView? = null
    private var memberList: List<PayloadShortMember?>? = null
    private var firstRun = true
    private var durationActualList: MutableList<PayloadDurationActual?> = ArrayList()
    private val tokenJson = JSONObject()
    private lateinit var socket: Socket
    private var df: DecimalFormat? = null
    private var animRight: Animation? = null
    private var animLeft: Animation? = null
    private var animOutLeft: Animation? = null
    private var animOutRight: Animation? = null
    private var oldAssignee: String? = null
    private var vibrator: Vibrator? = null
    private var startActivityViewModel: StartActivityViewModel? = null
    private var converter: Converter? = null
    private lateinit var day: TextView
    private lateinit var date: TextView
    private lateinit var anim: Animation

    var set = AnimationSet(true)
    var fadeIn: Animation = AlphaAnimation(0.0f, 1.0f)
    var controller: LayoutAnimationController? = null
    private val getEpicLinks = androidx.lifecycle.Observer { epicLinksEntities: List<EpicLinksEntity> -> this.epicLinksEntities = epicLinksEntities }
    private val getDuration = androidx.lifecycle.Observer { durationActual: MutableList<PayloadDurationActual?> -> durationActualList = durationActual }
    private val getProjectEntity = androidx.lifecycle.Observer { projectEntities1: List<ProjectEntity> -> projectEntities = projectEntities1 }
    private val shortMembers = androidx.lifecycle.Observer { members: List<PayloadShortMember?>? -> memberList = members }
    private val getAssignee = androidx.lifecycle.Observer { currentAssignee: Assignee ->
        oldAssignee = currentAssignee.id
        fabJobTitle!!.text = currentAssignee.jobTitle
        fabUserName!!.text = String.format("%s %s", currentAssignee.firstName, currentAssignee.lastName)
        if (currentAssignee.pictureUrl != null) Glide.with(this).load(currentAssignee.pictureUrl).apply(RequestOptions.circleCropTransform()).into(fabImg!!) else Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(fabImg!!)
    }
    private val userInfo = androidx.lifecycle.Observer { userInfo: Payload ->
        assignee.firstName = userInfo.firstName
        assignee.lastName = userInfo.lastName
        assignee.id = userInfo.id
        assignee.pictureUrl = userInfo.pictureUrl
        assignee.jobTitle = userInfo.jobTitle
        fabUserName!!.text = String.format("%s %s", assignee.firstName, assignee.lastName)
        fabJobTitle!!.text = assignee.jobTitle
        Glide.with(this).load(assignee.pictureUrl).apply(RequestOptions.circleCropTransform()).into(fabImg!!)
        Glide.with(this).load(assignee.pictureUrl).apply(RequestOptions.circleCropTransform()).into(profile)
        viewModel!!.assigneeMutableLiveData.observe(this, getAssignee)
        viewModel!!.insertDbUser(userInfo)
    }
    private val onCreateActualDuration = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            val id: String
            val status: String
            val taskId: String
            val projectId: String
            val companyId: String
            val date: String
            val person: String
            val createdAt: String
            val createdBy: String
            val value: Int
            var estimatedTime = 0.0
            var jsonArray: JSONArray? = null
            var jsonObject = JSONObject()
            val durationActual = PayloadDurationActual()
            try {
                jsonArray = JSONArray(Arrays.toString(args))
                jsonObject = jsonArray.getJSONObject(0)
                id = jsonObject.getString("_id")
                value = jsonObject.getInt("value")
                status = jsonObject.getString("status")
                taskId = jsonObject.getString("task_id")
                projectId = jsonObject.getString("project_id")
                companyId = jsonObject.getString("company_id")
                date = jsonObject.getString("date")
                createdAt = jsonObject.getString("created_at")
                createdBy = jsonObject.getString("created_by")
                person = jsonObject.getString("person")
                durationActual.value = value
                durationActual.companyId = companyId
                durationActual.createdAt = createdAt
                durationActual.createdBy = createdBy
                durationActual.date = date
                durationActual.id = id
                durationActual.person = person
                durationActual.projectId = projectId
                durationActual.taskId = taskId
                durationActualList.add(durationActual)
                for (i in 0 until mBoardView!!.columnCount) {
                    if (Constants.dateColumnMap[i] == date && assignee!!.id == person) {
                        val spentHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_spent)
                        val estimatedTimeHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_estimated)
                        estimatedTime = estimatedTimeHourCounterView.progress.toDouble()
                        if (estimatedTime % 60 == 0.0) {
                            if (estimatedTime - (value / 60).toString().toInt() < 0) estimatedTimeHourCounterView.progress = 0.toString() else estimatedTimeHourCounterView.progress = (estimatedTime - value / 60).toString()
                        } else {
                            if (estimatedTime - value / 60.0f < 0) estimatedTimeHourCounterView.progress = "0" else {
                                var s = df!!.format(estimatedTime - value / 60.0f)
                                s = s.replace(',', '.')
                                estimatedTimeHourCounterView.progress = s
                            }
                        }
                        if (value % 60 == 0) spentHourCounterView.progress = (spentHourCounterView.progress.toInt() + value / 60).toString() else {
                            var s = df!!.format(spentHourCounterView.progress.toDouble() + value / 60.0f)
                            s = s.replace(',', '.')
                            spentHourCounterView.progress = s
                        }
                    }
                }
                viewModel!!.insertDbDuration(durationActual)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private val onDeleteActualDuration = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            val id: String
            var value: String
            var status: String
            var taskId: String
            var projectId: String
            var companyId: String
            var date: String?
            var person: String
            var createdAt: String
            var createdBy: String
            date = null
            var jsonArray: JSONArray? = null
            var jsonObject = JSONObject()
            val durationActual = PayloadDurationActual()
            try {
                jsonArray = JSONArray(Arrays.toString(args))
                jsonObject = jsonArray.getJSONObject(0)
                id = jsonObject.getString("_id")
                durationActual.id = id
                var spentTime = 0
                val taskSpentTime = 0
                var estimatedTime = 0.0
                for (i in durationActualList.indices) {
                    if (durationActualList[i]!!.id == id) {
                        date = durationActualList[i]!!.date
                        durationActualList.removeAt(i)
                    }
                }
                for (i in 0 until mBoardView!!.columnCount) {
                    for (j in durationActualList.indices) {
                        if (Constants.dateColumnMap[i] == date && durationActualList[j]!!.person == assignee!!.id) {
                            if (Constants.dateColumnMap[i] == durationActualList[j]!!.date && durationActualList[j]!!.person == assignee!!.id) {
                                spentTime += durationActualList[j]!!.value
                            }
                            val spentHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_spent)
                            if (spentTime % 60 == 0) spentHourCounterView.progress = (spentTime / 60).toString() else {
                                var s = df!!.format(spentTime / 60.0f.toDouble())
                                s = s.replace(',', '.')
                                spentHourCounterView.progress = s
                            }
                        }
                    }
                    if (Constants.dateColumnMap[i] == date) {
                        val estimatedTimeHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_estimated)
                        for (q in mBoardView!!.getAdapter(i).itemList.indices) {
                            val task = mBoardView!!.getAdapter(i).itemList[q] as Pair<Long?, PayloadTask>
                            estimatedTime += task.second!!.duration.toDouble()
                        }
                        estimatedTime = estimatedTime / 60
                        if (estimatedTime % 60 == 0.0) {
                            estimatedTimeHourCounterView.progress = (estimatedTime - spentTime / 60).toString()
                        } else {
                            var s = df!!.format(estimatedTime - spentTime / 60.0f)
                            s = s.replace(',', '.')
                            estimatedTimeHourCounterView.progress = s
                        }
                    }
                }
                viewModel!!.deleteDuration(durationActual)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private val onUpdateActualDuration = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            val id: String
            val value: String
            val status: String
            val taskId: String
            val projectId: String
            val companyId: String
            val date: String
            val person: String
            val createdAt: String
            val createdBy: String
            var jsonArray: JSONArray? = null
            var jsonObject = JSONObject()
            var estimatedTime = 0.0
            val durationActual = PayloadDurationActual()
            try {
                jsonArray = JSONArray(Arrays.toString(args))
                jsonObject = jsonArray.getJSONObject(0)
                id = jsonObject.getString("_id")
                value = jsonObject.getString("value")
                status = jsonObject.getString("status")
                taskId = jsonObject.getString("task_id")
                projectId = jsonObject.getString("project_id")
                companyId = jsonObject.getString("company_id")
                date = jsonObject.getString("date")
                createdAt = jsonObject.getString("created_at")
                createdBy = jsonObject.getString("created_by")
                person = jsonObject.getString("person")
                durationActual.id = id
                durationActual.date = date
                durationActual.value = Integer.valueOf(value)
                for (i in durationActualList.indices) {
                    if (durationActualList[i]!!.id == id) {
                        durationActualList[i]!!.value = Integer.valueOf(value)
                    }
                }
                var spentTime = 0
                for (i in 0 until mBoardView.columnCount) {
                    for (j in durationActualList.indices) {
                        if (Constants.dateColumnMap[i] == date && durationActualList[j]!!.person == assignee!!.id) {
                            if (durationActualList[j]!!.id == id) {
                                durationActualList[j]!!.value = Integer.valueOf(value)
                            }
                            if (Constants.dateColumnMap[i] == durationActualList[j]!!.date && durationActualList[j]!!.person == assignee!!.id) {
                                spentTime += durationActualList[j]!!.value
                            }
                            val spentHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_spent)
                            if (spentTime % 60 == 0) spentHourCounterView.progress = (spentTime / 60).toString() else {
                                var s = df!!.format(spentTime / 60.0f.toDouble())
                                s = s.replace(',', '.')
                                spentHourCounterView.progress = s
                            }
                        }
                    }
                    if (Constants.dateColumnMap[i] == date) {
                        val estimatedTimeHourCounterView: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_estimated)
                        for (q in mBoardView!!.getAdapter(i).itemList.indices) {
                            val task = mBoardView!!.getAdapter(i).itemList[q] as Pair<Long?, PayloadTask>
                            estimatedTime += task.second!!.duration.toDouble()
                        }
                        if (estimatedTime % 60 < 0) {
                            if (estimatedTime / 60 - spentTime == 0.0) estimatedTimeHourCounterView.progress = 0.toString() else estimatedTimeHourCounterView.progress = (estimatedTime / 60 - spentTime).toString()
                        } else {
                            if (estimatedTime / 60.0f - spentTime / 60.0f < 0) estimatedTimeHourCounterView.progress = "0" else {
                                var s = df!!.format(estimatedTime / 60.0f - spentTime / 60.0f)
                                s = s.replace(',', '.')
                                estimatedTimeHourCounterView.progress = s
                            }
                        }
                    }
                }
                viewModel!!.updateDbDuration(durationActual)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private val getTask = androidx.lifecycle.Observer { taskList: List<PayloadTask> ->
        viewModel!!.projectMutableLiveData
        for (i in 0 until mBoardView.columnCount) {
            var estimatedTime = 0
            var taskSpentTime = 0
            var spentTime = 0
            val spentTimeCounter: HourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent)
            val estimatedTimeCounter: HourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated)


            spentTimeCounter.progress = "0"
            val mItemArray = ArrayList<Pair<Long, PayloadTask>>()
            mBoardView.getAdapter(i).itemList = mItemArray
            for (j in taskList.indices) {
                if (taskList[j].planDate != null && taskList[j].status != "archived" && taskList[j].status != "deleted") if (taskList[j].planDate == Constants.dateColumnMap[i]) {
                    val id = sCreatedItems++.toLong()
                    mItemArray.add(Pair(id, taskList[j]))
                    Collections.sort(mItemArray, Comparator<Pair<Long, PayloadTask>> { lhs: Pair<Long, PayloadTask>, rhs: Pair<Long, PayloadTask> ->
                        when {
                            lhs.second!!.planOrder.toInt() == rhs.second!!.planOrder.toInt() -> {
                                return@Comparator 0
                            }
                            lhs.second!!.planOrder.toInt() < rhs.second!!.planOrder.toInt() -> {
                                return@Comparator -1
                            }
                            else -> {
                                return@Comparator 1
                            }
                        }
                    })
                    mBoardView.getAdapter(i).itemList = mItemArray
                    mBoardView.getAdapter(i).notifyDataSetChanged()
                }
            }
            for (s in durationActualList.indices) {
                if (Constants.dateColumnMap[i] == durationActualList[s]!!.date && durationActualList[s]!!.person == assignee!!.id) {
                    spentTime += durationActualList[s]!!.value
                    if (spentTime % 60 == 0) spentTimeCounter.progress = (spentTime / 60).toString() else {
                        var s2 = df!!.format(spentTime / 60.0f.toDouble())
                        s2 = s2.replace(',', '.')
                        spentTimeCounter.progress = s2
                    }
                }
            }
            for (s in mBoardView!!.getAdapter(i).itemList.indices) {
                val taskNewPos = mBoardView!!.getAdapter(i).itemList[s] as Pair<Long, PayloadTask>
                estimatedTime += taskNewPos.second!!.duration
                taskSpentTime += taskNewPos.second!!.durationActual
            }
            estimatedTime -= taskSpentTime
            if (estimatedTime > 0) {
                if (estimatedTime % 60 == 0) estimatedTimeCounter.progress = (estimatedTime / 60).toString() else {
                    var s = df!!.format(estimatedTime / 60.0f.toDouble())
                    s = s.replace(',', '.')
                    estimatedTimeCounter.progress = s
                }
            } else estimatedTimeCounter.progress = "0"
        }
        if (firstRun) {
            viewModel!!.getAllDbTaskByAssignee(assignee!!.id)
            firstRun = false
        }
    }
    private val getFilteredTask = androidx.lifecycle.Observer { taskList: List<PayloadTask?> ->
        val t = mBoardView!!.columnCount - 1
        mBoardView!!.clearBoard()
        for (i in taskList.indices) {
            addTask(taskList[i], t)
        }
    }
    private val onTaskUpdate = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            var hasTask = false
            val row: Int
            val jsonObject: JSONObject
            val date: String
            val name: String?
            val assignee: String
            val id: String
            val priority: String?
            val planDate: String
            val deadlineDate: String?
            val estimatedTime: String?
            val spentTime: String?
            val status: String
            val description: String?
            var parentId: String
            val projectId: String?
            val sectionId: String?
            var epicLinks = JSONArray()
            val epicLinkList: MutableList<String?> = ArrayList()
            try {
                val jsonArray = JSONArray(Arrays.toString(args))
                jsonObject = jsonArray.getJSONObject(0)
                id = jsonObject.getString("_id")
                status = jsonObject.getString("status")
                name = jsonObject.getString("name")
                priority = jsonObject.getString("priority")
                planDate = jsonObject.getString("plan_date")
                epicLinks = jsonObject.getJSONArray("epic_links")
                projectId = jsonObject.getString("project_id")
                sectionId = jsonObject.getString("section_id")
                description = jsonObject.getString("description")
                date = planDate
                assignee = jsonObject.getString("assignee")
                row = jsonObject.getInt("plan_order")
                deadlineDate = jsonObject.getString("deadline_date")
                estimatedTime = jsonObject.getString("duration")
                spentTime = jsonObject.getString("duration_actual")
                if (epicLinks.length() != 0) for (i in 0 until epicLinks.length()) {
                    epicLinkList.add(epicLinks[i] as String)
                }
                val task2 = PayloadTask()
                task2.id = id
                task2.name = name
                task2.durationActual = Integer.valueOf(spentTime)
                task2.duration = Integer.valueOf(estimatedTime)
                task2.deadlineDate = deadlineDate
                task2.planDate = planDate
                task2.projectId = projectId
                task2.sectionId = sectionId
                task2.epicLinks = epicLinkList
                task2.assignee = assignee
                task2.priority = priority
                task2.planOrder = row.toString()
                task2.description = description
                task2.status = status
                for (i in 0 until mBoardView.columnCount) {
                    for ((j, taskNewPos) in mBoardView.getAdapter(i).itemList.withIndex()) {
//                        val taskNewPos = mBoardView.getAdapter(i).itemList[j]
                        Log.d(TAG, taskNewPos.second?.id + " " +mBoardView.getAdapter(i).itemList.size )
                        if (taskNewPos.second?.id == id && taskNewPos.second?.id != null) {
                            hasTask = true
                            taskNewPos.second!!.name = name
                            taskNewPos.second!!.durationActual = Integer.valueOf(spentTime)
                            taskNewPos.second!!.duration = Integer.valueOf(estimatedTime)
                            taskNewPos.second!!.deadlineDate = deadlineDate
                            taskNewPos.second!!.planOrder = row.toString()
                            taskNewPos.second!!.assignee = assignee
                            taskNewPos.second!!.epicLinks = epicLinkList
                            taskNewPos.second!!.projectId = projectId
                            taskNewPos.second!!.sectionId = sectionId
                            taskNewPos.second!!.priority = priority
                            taskNewPos.second!!.status = status
                            mBoardView.replaceItem(i, j, taskNewPos, false)
                            mBoardView.getAdapter(i).notifyItemInserted(j)
                            var estimatedTimeOld = 0
                            var taskSpentTimeOld = 0
                            if (date == "null") {
                                taskNewPos.second!!.planDate = null
                                viewModel!!.updateDbTask(taskNewPos.second)
                                mBoardView.removeItem(i, j)
                                mBoardView.getAdapter(i).notifyItemRemoved(j)
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j, mBoardView.getAdapter(i).itemCount)
                            } else if (assignee != oldAssignee) {
                                taskNewPos.second!!.assignee = assignee
                                viewModel!!.updateDbTask(taskNewPos.second)
                                mBoardView.removeItem(i, j)
                                mBoardView.getAdapter(i).notifyItemRemoved(j)
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j, mBoardView.getAdapter(i).itemCount)
                            } else if (taskNewPos.second!!.status == "deleted") {
                                taskNewPos.second!!.planDate = planDate
                                viewModel!!.updateDbTask(taskNewPos.second)
                                mBoardView.removeItem(i, j)
                                mBoardView.getAdapter(i).notifyItemRemoved(j)
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j + 1, mBoardView.getAdapter(i).itemCount)
                            } else  //                            if (taskNewPos.second.getPlanDate().equals(planDate))
                            {
//                            Log.d("date 2 ", ": " + taskNewPos.second.getPlanDate());
//                            if (!taskNewPos.second.getPlanOrder().equals(row)) {
//                                    taskNewPos.second.setPlanOrder(String.valueOf(row));
//                                    Collections.sort(mBoardView.getAdapter(i).getItemList(), (Comparator<Pair<Long, PayloadTask>>) (lhs, rhs) -> {
//                                        if ((Integer.parseInt(lhs.second.getPlanOrder()) == Integer.parseInt(rhs.second.getPlanOrder()) )) {
//                                            return 0;
//                                        } else if ((Integer.parseInt(lhs.second.getPlanOrder()) < Integer.parseInt(rhs.second.getPlanOrder()) )) {
//                                            return -1;
//                                        } else {
//                                            return 1;
//                                        }
//                                    });
//                                    mBoardView.getAdapter(i).notifyDataSetChanged();
//                                }
//                                mBoardView.invalidate();
//                                viewModel.updateDbTask(taskNewPos.second);
//                        } else if (!taskNewPos.second.getPlanDate().equals(planDate) || !taskNewPos.second.getPlanDate().equals(Constants.dateColumnMap.get(i))) {
                                taskNewPos.second!!.planOrder = row.toString()
                                for (k in 0 until mBoardView!!.columnCount) {
                                    if (!Constants.dateColumnMap.containsValue(planDate)) {
                                        mBoardView!!.getAdapter(i).itemList.remove(taskNewPos)
                                    }
                                    if (planDate == Constants.dateColumnMap[k]) {
                                        taskNewPos.second!!.planDate = planDate
                                        mBoardView!!.moveItem(i, j, k, 0, false)
                                        Collections.sort(mBoardView!!.getAdapter(k).itemList, label@ Comparator { lhs: Pair<Long?, PayloadTask>, rhs: Pair<Long?, PayloadTask> ->
                                            if (lhs.second!!.planOrder.toInt() == rhs.second!!.planOrder.toInt()) {
                                                return@Comparator 0
                                            } else if (lhs.second!!.planOrder.toInt() < rhs.second!!.planOrder.toInt()) {
                                                return@Comparator -1
                                            } else {
                                                return@Comparator 1
                                            }
                                        } as java.util.Comparator<Pair<Long?, PayloadTask>>)
                                        mBoardView!!.getAdapter(i).notifyDataSetChanged()
                                        mBoardView!!.getAdapter(k).notifyDataSetChanged()
                                        var estimatedTimeOldNewDate = 0
                                        var taskSpentTimeOldNewDate = 0
                                        val hourEstimatedOldNewDate: HourCounterView = mBoardView!!.getHeaderView(k).findViewById(R.id.hour_counter_estimated)
                                        val oldTasksNewDate = mBoardView!!.getAdapter(k).itemList
                                        for (m in oldTasksNewDate.indices) {
                                            val pair = oldTasksNewDate[m]
                                            val task = pair.second
                                            estimatedTimeOldNewDate += task!!.duration
                                            taskSpentTimeOldNewDate += task.durationActual
                                        }
                                        estimatedTimeOldNewDate -= taskSpentTimeOldNewDate
                                        if (estimatedTimeOldNewDate > 0) {
                                            if (estimatedTimeOldNewDate % 60 == 0) hourEstimatedOldNewDate.progress = (estimatedTimeOld / 60).toString() else {
                                                var s = df!!.format(estimatedTimeOldNewDate / 60.0f.toDouble())
                                                s = s.replace(',', '.')
                                                hourEstimatedOldNewDate.progress = s
                                            }
                                        } else hourEstimatedOldNewDate.progress = "0"
                                        var estimatedTimeNew = 0
                                        val hourEstimatedNew: HourCounterView = mBoardView!!.getHeaderView(k).findViewById(R.id.hour_counter_estimated)
                                        val newTasks = mBoardView!!.getAdapter(k).itemList
                                        for (p in newTasks.indices) {
                                            val pair = newTasks[p]
                                            val task = pair.second
                                            estimatedTimeNew += task!!.duration
                                        }
                                        if (estimatedTimeNew % 60 == 0) hourEstimatedNew.progress = (estimatedTimeNew / 60).toString() else hourEstimatedNew.progress = df!!.format(estimatedTimeNew / 60.0f.toDouble())
                                        taskNewPos.second!!.planDate = planDate
                                    }
                                }
                            }
                            val hourEstimatedOld: HourCounterView = mBoardView!!.getHeaderView(i).findViewById(R.id.hour_counter_estimated)
                            val oldTasks = mBoardView!!.getAdapter(i).itemList
                            for (m in oldTasks.indices) {
                                val pair = oldTasks[m]
                                val task = pair.second
                                estimatedTimeOld += task!!.duration
                                taskSpentTimeOld += task.durationActual
                            }
                            estimatedTimeOld -= taskSpentTimeOld
                            if (estimatedTimeOld > 0) {
                                if (estimatedTimeOld % 60 == 0) hourEstimatedOld.progress = (estimatedTimeOld / 60).toString() else {
                                    var s = df!!.format(estimatedTimeOld / 60.0f.toDouble())
                                    s = s.replace(',', '.')
                                    hourEstimatedOld.progress = s
                                }
                            } else hourEstimatedOld.progress = "0"
                        }
                    }
                }
                if (date != "null") if (assignee == this.assignee!!.id && !hasTask) {
                    val task = PayloadTask()
                    task.name = name
                    task.id = id
                    task.durationActual = Integer.valueOf(spentTime)
                    task.duration = Integer.valueOf(estimatedTime)
                    task.deadlineDate = deadlineDate
                    task.planDate = planDate
                    task.projectId = projectId
                    task.sectionId = sectionId
                    task.epicLinks = epicLinkList
                    task.assignee = assignee
                    task.priority = priority
                    task.planOrder = row.toString()
                    task.status = status
                    if (status != "deleted" && status != "archived") {
                        addTask(task)
                    }
                    viewModel!!.updateDbTask(task)
                    viewModel!!.addDbTask(task)
                }
                viewModel!!.updateDbTask(task2)
                viewModel!!.addDbTask(task2)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_layout)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener{ onBackPressed() }
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(findViewById(R.id.chat))
        chat.setOnClickListener{
            socket.emit("server_read_messages", tokenJson)
            startActivity(Intent(this, ChatActivity::class.java))
        }
        profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        initViews()
    }


    private fun initViews() {
        anim = AnimationUtils.loadAnimation(this,R.anim.alpha_anim)
        val header = View.inflate(this, R.layout.column_header, null)
        val estimatedTimeCounter = header.findViewById<View>(R.id.hour_counter_estimated) as HourCounterView
        day_info.setOnClickListener { v: View? ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            } else {
                val bundle = Bundle()
                bundle.putString("date", Constants.dateColumnMap[mBoardView.focusedColumn])
                bundle.putString("estimatedTime", estimatedTimeCounter.progress.toString())
                bundle.putParcelableArrayList("actualDurationList", durationActualList as ArrayList<out Parcelable?>)
                bundle.putString("id", assignee.id)
                val mySheetDialog = BottomSheetDayInfo()
                mySheetDialog.arguments = bundle
                val fm = supportFragmentManager
                mySheetDialog.show(fm, "dayInfo")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        day = findViewById(R.id.main_day)
        date = findViewById(R.id.main_date)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        converter = Converter()
        animRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right_anim)
        animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left_anim)
        animOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left_anim)
        animOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right_anim)
        df = DecimalFormat("#.#")
        app.component.inject(this)
        token = EmikaApplication.instance.sharedPreferences.getString("token", "")
        startActivityViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(StartActivityViewModel::class.java)
        startActivityViewModel!!.token = token!!
        startActivityViewModel!!.fetchAllData()
        viewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        profileViewModel = ViewModelProvider(this, TokenViewModelFactory(token)).get(ProfileViewModel::class.java)
        bottomSheetDialogViewModel = ViewModelProvider(this).get(BottomSheetDialogViewModel::class.java)
        userNetworkManager = UserNetworkManager(token)
        socket = app.socket
        try {
            tokenJson.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket.emit("server_create_connection", tokenJson)
        socket.on("update_task", onTaskUpdate)
        socket.on("create_duration_actual_log", onCreateActualDuration)
        socket.on("delete_duration_actual_log", onDeleteActualDuration)
        socket.on("update_duration_actual_log", onUpdateActualDuration)
        profileViewModel!!.setContext(this)
        profileViewModel!!.downloadUserData()
        profileViewModel!!.userMutableLiveData.observe(this, userInfo)
        viewModel!!.downloadDurationActualLog()
        viewModel!!.downloadTasksByAssignee(user!!.id)
        viewModel!!.projectMutableLiveData
        fabUserName = findViewById(R.id.fab_user_name)
        fabImg = findViewById(R.id.fab_img)
        fabJobTitle = findViewById(R.id.fab_job_title)
        selectCurrentUser = findViewById(R.id.select_current_user)
        selectCurrentUser.setOnClickListener { view: View -> selectCurrentAssignee(view) }
        rightScroll = findViewById(R.id.right_scroll_to_current_date)
        rightScroll = findViewById(R.id.right_scroll_to_current_date)
        leftScroll = findViewById(R.id.left_scroll_to_current_date)
        rightScroll.setOnClickListener(View.OnClickListener { view: View -> scrollToCurrentDate(view) })
        leftScroll.setOnClickListener(View.OnClickListener { view: View -> scrollToCurrentDate(view) })
        addTask = findViewById(R.id.add_task)
        addTask.setTransitionName("shared_element")
        addTask.setOnClickListener(View.OnClickListener { v: View -> goToAddTask(v) })
        mBoardView = findViewById(R.id.board_view)
        mBoardView.setLastColumn(15)

//        mBoardView.setSnapToColumnsWhenScrolling(true);
//        mBoardView.setSnapToColumnWhenDragging(true);
//        mBoardView.setSnapDragItemToTouch(true);
//        mBoardView.setSnapToColumnInLandscape(false);
//        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        for (i in 0..364) {
            Constants.dateColumnMap[i] = DateHelper.compareDate(i)
            addColumn2(DateHelper.getDate(DateHelper.compareDate(i)), DateHelper.getDatOfWeek(i), Constants.dateColumnMap[i])
            if (i == 15) mBoardView.getHeaderView(i).findViewById<View>(R.id.item_layout_task).background = resources.getDrawable(R.drawable.shape_foreground_stroke)
        }
        mBoardView.setBoardListener(boardListener)
        viewModel!!.setContext(this)
        viewModel!!.epicLinksMutableLiveData.observe(this, getEpicLinks)
        viewModel!!.projectMutableLiveData.observe(this, getProjectEntity)
        viewModel!!.durationMutableLiveData.observe(this, getDuration)
        viewModel!!.membersMutableLiveData.observe(this, shortMembers)
        viewModel!!.getAllDbTaskByAssignee(assignee!!.id)
        viewModel!!.listMutableLiveData.observe(this, getTask)
        oldAssignee = assignee!!.id
    }

    private val boardListener: BoardView.BoardListener = object : BoardView.BoardListener {
        override fun onItemDragStarted(column: Int, row: Int) {}
        override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
            val taskNewPos = mBoardView!!.getAdapter(toColumn).itemList[toRow] as Pair<Long, PayloadTask>
            taskNewPos.second!!.planDate = Constants.dateColumnMap[toColumn]
            taskNewPos.second!!.planOrder = toRow.toString()
            viewModel!!.updateTask(taskNewPos.second)
            for (i in mBoardView!!.getAdapter(toColumn).itemList.indices) {
                val task = mBoardView!!.getAdapter(toColumn).itemList[i] as Pair<Long, PayloadTask>
                task.second!!.planOrder = mBoardView!!.getAdapter(toColumn).getPositionForItem(task).toString()
                viewModel!!.updateTask(task.second)
            }
            var estimatedTimeNew = 0
            var estimatedTimeOld = 0
            var spentTimeNew = 0
            var spentTimeOld = 0
            val hourEstimatedOld: HourCounterView = mBoardView!!.getHeaderView(fromColumn).findViewById(R.id.hour_counter_estimated)
            val hourEstimatedNew: HourCounterView = mBoardView!!.getHeaderView(toColumn).findViewById(R.id.hour_counter_estimated)
            val newTasks = mBoardView!!.getAdapter(toColumn).itemList
            val oldTasks = mBoardView!!.getAdapter(fromColumn).itemList
            for (i in oldTasks.indices) {
                val pair = oldTasks[i]
                val task = pair.second
                estimatedTimeOld += task!!.duration
                spentTimeOld += task.durationActual
            }
            if (estimatedTimeOld % 60 == 0) hourEstimatedOld.progress = (estimatedTimeOld / 60 - spentTimeOld / 60).toString() else hourEstimatedOld.progress = df!!.format(estimatedTimeOld / 60.0f - spentTimeOld / 60.0f.toDouble())
            for (i in newTasks.indices) {
                val pair = newTasks[i]
                val task = pair.second
                estimatedTimeNew += task!!.duration
                spentTimeNew += task.durationActual
            }
            if (estimatedTimeNew % 60 == 0) hourEstimatedNew.progress = (estimatedTimeNew / 60 - spentTimeNew / 60).toString() else hourEstimatedNew.progress = df!!.format(estimatedTimeNew / 60.0f - spentTimeNew / 60.0f.toDouble())
        }

        override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {}
        override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {}
        override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
            date.text = DateHelper.getDate(Constants.dateColumnMap[mBoardView.focusedColumn])
            day.text = DateHelper.getDatOfWeek(mBoardView.focusedColumn)
            date.startAnimation(anim)
            day.startAnimation(anim)
            mBoardView.setLastColumn(newColumn)
            if (!firstRun) if (mBoardView.focusedColumn == 15) {
                if (newColumn < oldColumn) leftScroll.startAnimation(animOutLeft)
                if (newColumn > oldColumn) rightScroll.startAnimation(animOutRight)
                leftScroll.visibility = View.GONE
                rightScroll.visibility = View.GONE
            } else if (mBoardView.focusedColumn < 15) {
                leftScroll.visibility = View.GONE
                rightScroll.visibility = View.VISIBLE
                if (mBoardView.focusedColumn == 14 && oldColumn > newColumn)
                    rightScroll.startAnimation(animRight)
            } else if (mBoardView.focusedColumn > 15) {
                rightScroll.visibility = View.GONE
                leftScroll.visibility = View.VISIBLE
                if (mBoardView.focusedColumn == 16 && oldColumn < newColumn)
                    leftScroll.startAnimation(animLeft)
            }
        }

        override fun onColumnDragStarted(position: Int) {
            //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
        }

        override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
//                Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
        }

        override fun onColumnDragEnded(position: Int) {
//                Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private fun selectCurrentAssignee(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val bundle = Bundle()
            bundle.putParcelableArrayList("members", memberList as ArrayList<out Parcelable?>?)
            bundle.putParcelable("calendarViewModel", viewModel)
            val mySheetDialog = BottomSheetCalendarSelectUser()
            mySheetDialog.arguments = bundle
            val fm = supportFragmentManager
            mySheetDialog.show(fm, "modalSheetDialog")
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun scrollToCurrentDate(view: View) {
        mBoardView.scrollToColumn(15, true)
    }


    private fun addColumn2(month: String, day: String, date: String?) {
        val mItemArray = ArrayList<Pair<Long, PayloadTask>>()
        val listAdapter = ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout,
                true, this, token, epicLinksEntities, projectEntities, viewModel, supportFragmentManager,this)
        listAdapter.setContext(this)
        listAdapter.setmDragOnLongPress(true)
        listAdapter.setmLayoutId(R.layout.column_item)
        listAdapter.setmGrabHandleId(R.id.item_layout)
        val header = View.inflate(this, R.layout.column_header, null)
        val estimatedTimeCounter = header.findViewById<View>(R.id.hour_counter_estimated) as HourCounterView

//        header.setOnClickListener { v: View? ->
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                return@setOnClickListener
//            } else {
//                val bundle = Bundle()
//                bundle.putString("date", date)
//                bundle.putString("estimatedTime", estimatedTimeProgressBar.progress.toString())
//                bundle.putParcelableArrayList("actualDurationList", durationActualList as ArrayList<out Parcelable?>)
//                bundle.putString("id", assignee.id)
//                val mySheetDialog = BottomSheetDayInfo()
//                mySheetDialog.arguments = bundle
//                val fm = supportFragmentManager
//                mySheetDialog.show(fm, "dayInfo")
//            }
//            mLastClickTime = SystemClock.elapsedRealtime()
//        }
        //
        (header.findViewById<View>(R.id.header_date) as TextView).text = month
        (header.findViewById<View>(R.id.header_day) as TextView).text = day
        val layoutManager = LinearLayoutManager(this)
        val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header)
                .build()
        mBoardView!!.addColumn(columnProperties)
    }

    private fun goToAddTask(v: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        } else {
            val dialog = AddTask()
            val bundle = Bundle()
            bundle.putString("token", token)
            bundle.putParcelableArrayList("members", memberList as ArrayList<out Parcelable?>?)
            bundle.putParcelable("calendarViewModel", viewModel)
            bundle.putString("date", Constants.dateColumnMap[mBoardView!!.focusedColumn])
            dialog.arguments = bundle
            vibrator!!.vibrate(100)
            val ft = supportFragmentManager
            dialog.show(ft, "123")
            //            Intent intent = new Intent(getContext(), AddTaskActivity.class);
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "shared_element");
//            intent.putExtra("token", token);
//            intent.putParcelableArrayListExtra("members", (ArrayList<? extends Parcelable>) memberList);
//            intent.putExtra("calendarViewModel", viewModel);
//            intent.putExtra("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
//            startActivity(intent, options.toBundle());
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addTask(task: PayloadTask?, column: Int) {
        val id = sCreatedItems++.toLong()
        val item: Pair<*, *> = Pair(id, task)
        mBoardView!!.addItem(column, 0, item, false)
    }

    private fun addTask(task: PayloadTask) {
        val id = sCreatedItems++.toLong()
        var column = 0
        val item: Pair<*, *> = Pair(id, task)
        for ((key, value) in Constants.dateColumnMap) {
            if (value == task.planDate) {
                column = key
            }
        }
        if (assignee!!.id == task.assignee) {
            val hourEstimatedNew: HourCounterView = mBoardView!!.getHeaderView(column).findViewById(R.id.hour_counter_estimated)
            if (task.duration % 60 == 0) hourEstimatedNew.progress = (hourEstimatedNew.progress.toDouble() + task.duration / 60).toString() else hourEstimatedNew.progress = df!!.format(hourEstimatedNew.progress.toDouble() + task.duration / 60.0f)
            mBoardView!!.addItem(column, mBoardView!!.getAdapter(column).itemCount, item, false)
            mBoardView!!.getAdapter(column).notifyDataSetChanged()
        } else {
        }
    }

    private class MyDragItem internal constructor(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            (dragView.findViewById<View>(R.id.text) as TextView).text = text
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            //            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight -
                    clickedCard.paddingRight
            val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom -
                    clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }
    }

    companion object {
        private const val TAG = "BoardFragment"
        private var sCreatedItems = 0
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}
