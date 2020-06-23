/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emika.app.presentation.adapter.calendar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.SystemClock
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.db.entity.ProjectEntity
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.EpicLinks
import com.emika.app.di.ProjectsDi
import com.emika.app.features.calendar.DragItemAdapter
import com.emika.app.presentation.ui.calendar.TaskInfoActivity
import com.emika.app.presentation.utils.Constants
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class ItemAdapter : DragItemAdapter<Pair<Long?, String?>?, ItemAdapter.ViewHolder?> {
    @Inject
    lateinit var epicLinksDi: EpicLinks
    @Inject
    lateinit var projects: ProjectsDi
    private var mLayoutId = 0
    private var mGrabHandleId = 0
    private var mDragOnLongPress = false
    private var context: Context? = null
    private var token: String? = null
    private var calendarNetworkManager: CalendarNetworkManager? = null
    private var epicLinksEntities: List<EpicLinksEntity>? = null
    private var projectEntities: List<ProjectEntity>? = null
    private var mLastClickTime: Long = 0
    private var df: DecimalFormat? = null
    private var hasStub: Boolean = false
    private val sortedTask = ArrayList<Pair<Long, PayloadTask>>()
    private var calendarViewModel: CalendarViewModel? = null
    private lateinit var fragmentManager: FragmentManager
    private var vibrator: Vibrator? = null
    private lateinit var activity: Activity
    constructor(list: ArrayList<Pair<Long, PayloadTask>>?, layoutId: Int, grabHandleId: Int, dragOnLongPress: Boolean,
                context: Context?, token: String?, epicLinksEntities: List<EpicLinksEntity>?, projectEntities: List<ProjectEntity>?,
                calendarViewModel: CalendarViewModel?, fragmentManager: FragmentManager, activity: Activity) {

        Collections.sort(list,  Comparator { lhs: Pair<Long, PayloadTask>, rhs: Pair<Long, PayloadTask> ->
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
        } as Comparator<Pair<Long, PayloadTask>>)
        EmikaApplication.instance.component?.inject(this)
        mGrabHandleId = grabHandleId
        mDragOnLongPress = dragOnLongPress
        this.context = context
        this.token = token
        this.projectEntities = projectEntities
        this.epicLinksEntities = epicLinksEntities
        this.calendarViewModel = calendarViewModel
        this.activity = activity
        vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        calendarNetworkManager = CalendarNetworkManager(token)
//        if (list?.size != 0){
//            hasStub = false
//        } else {
//            hasStub = true
//        }
//        hasStub =/ list!!.size > 0
        if (hasStub) {
            list?.add(Pair(1, PayloadTask()))
            mItemList = list
            Log.d(TAG, "hasnostub ${list?.size}")
        } else
            mItemList = list

        df = DecimalFormat("#.#")
        this.fragmentManager = fragmentManager
    }

    init{
        EmikaApplication.instance.component?.inject(this)
    }
    fun setmLayoutId(mLayoutId: Int) {
        this.mLayoutId = mLayoutId
    }

    fun setmGrabHandleId(mGrabHandleId: Int) {
        this.mGrabHandleId = mGrabHandleId
    }

    fun setmDragOnLongPress(mDragOnLongPress: Boolean) {
        this.mDragOnLongPress = mDragOnLongPress
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setList(list: ArrayList<Pair<Long?, PayloadTask?>?>?) {

        itemList = list

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = -1
        val viewHolder: ViewHolder? = null
        val v: View? = null
        when (viewType) {
            1 -> {
                val v = LayoutInflater
                        .from(parent.context)
                        .inflate(mLayoutId, parent, false)
                return ViewHolder(v!!)
            }
            2 -> {
                val v = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_stub_calendar, parent, false)
                return ViewHolder(v!!)
            }
            else -> {
                val v = LayoutInflater
                        .from(parent.context)
                        .inflate(mLayoutId, parent, false)
                return ViewHolder(v!!)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (hasStub)
            2
        else
            1
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
            val task = mItemList[position].second
       if (!hasStub){
                if (task!!.status != "deleted") {
                    task.planOrder = position.toString()
                    holder.mText?.text = task.name
                    holder.itemView.tag = mItemList[position]

                    if (task.subTaskCount != "0" && task.subTaskCount != null) {
                        holder.subTaskCount?.text = "${task.doneSubTaskCount}/${task.subTaskCount}"
                        holder.subTaskLayout?.visibility = View.VISIBLE
                    } else {
                        holder.subTaskLayout?.visibility = View.GONE
                    }

                    if (task.duration % 60 == 0)
                        holder.estimatedTime?.text = String.format("%sh", (task.duration / 60).toString())
                    else {
                        var s = String.format("%sh", df!!.format(task.duration / 60.0f.toDouble()))
                        s = s.replace(',', '.')
                        holder.estimatedTime?.text = s
                    }
                    if (task.durationActual % 60 == 0)
                        holder.spentTime?.text = String.format("%sh", (task.durationActual / 60).toString())
                    else {
                        var s = String.format("%sh", df!!.format(task.durationActual / 60.0f.toDouble()))
                        s = s.replace(',', '.')
                        holder.spentTime?.text = s
                    }
                    holder.isDone!!.setOnClickListener { v: View? ->
                        if (holder.isDone!!.isChecked) {
                            holder.mText!!.setTextColor(context!!.resources.getColor(R.color.task_name_done))
                            task.status = "done"
                            calendarNetworkManager!!.updateTask(task)
                        } else {
                            holder.mText!!.setTextColor(context!!.resources.getColor(R.color.mainTextColor))
                            task.status = "wip"
                            calendarNetworkManager!!.updateTask(task)
                        }
                    }
                    holder.refresh!!.setOnClickListener { v: View? ->
                        holder.mText!!.paintFlags = Paint.ANTI_ALIAS_FLAG
                        task.status = "wip"
                        holder.taskTextCanceled!!.visibility = View.GONE
                        holder.priority!!.visibility = View.VISIBLE
                        holder.refresh!!.visibility = View.GONE
                        holder.isDone!!.visibility = View.VISIBLE
                        holder.isDone!!.isChecked = false
                        calendarNetworkManager!!.updateTask(task)
                    }
                    when (task.status) {
                        "done" -> {
                            holder.mText!!.paintFlags = Paint.ANTI_ALIAS_FLAG
                            holder.mText!!.setTextColor(context!!.resources.getColor(R.color.task_name_done))
                            holder.isDone!!.visibility = View.VISIBLE
                            holder.isDone!!.isChecked = true
                            holder.taskTextCanceled!!.visibility = View.GONE
                            holder.priority!!.visibility = View.VISIBLE
                            holder.refresh!!.visibility = View.GONE
                        }
                        "canceled" -> {
                            holder.mText!!.setTextColor(context!!.resources.getColor(R.color.task_name_done))
                            holder.mText!!.paintFlags = holder.mText!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            holder.taskTextCanceled!!.visibility = View.VISIBLE
                            holder.isDone!!.visibility = View.GONE
                            holder.priority!!.visibility = View.GONE
                            holder.refresh!!.visibility = View.VISIBLE
                            holder.refresh!!.isChecked = true
                        }
                        else -> {
                            holder.mText!!.paintFlags = Paint.ANTI_ALIAS_FLAG
                            holder.taskTextCanceled!!.visibility = View.GONE
                            holder.priority!!.visibility = View.VISIBLE
                            holder.refresh!!.visibility = View.GONE
                            holder.isDone!!.visibility = View.VISIBLE
                            holder.isDone!!.isChecked = false
                            holder.mText!!.setTextColor(context!!.resources.getColor(R.color.mainTextColor))
                            holder.isDone!!.isChecked = false
                        }
                    }
                    if (task.priority != null) when (task.priority) {
                        "low" -> {
                            //                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_low));
                            holder.priority!!.setTextColor(context!!.resources.getColor(R.color.low_priority))
                            holder.priority!!.setCompoundDrawablesWithIntrinsicBounds(context!!.resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
                        }
                        "normal" -> holder.priority!!.visibility = View.GONE
                        "high" -> {
                            //                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_high));
                            holder.priority!!.setTextColor(context!!.resources.getColor(R.color.yellow))
                            holder.priority!!.setCompoundDrawablesWithIntrinsicBounds(context!!.resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
                        }
                        "urgent" -> {
                            //                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_urgent));
                            holder.priority!!.setTextColor(context!!.resources.getColor(R.color.red))
                            holder.priority!!.setCompoundDrawablesWithIntrinsicBounds(context!!.resources.getDrawable(R.drawable.ic_task_urgent), null, null, null)
                        }
                    }
                    if (task.epicLinks != null && task.epicLinks.size != 0) {
                        for (i in task.epicLinks.indices) {
                            for (j in epicLinksDi!!.epicLinksList.indices) {
                                if (task.epicLinks[i] == epicLinksDi!!.epicLinksList[j].id) {
                                    holder.epicLink!!.visibility = View.VISIBLE
                                    if (task.epicLinks.size > 1) {
                                        holder.epicLink!!.text = String.format("%s+%d", epicLinksDi!!.epicLinksList[j].name, task.epicLinks.size - 1)
                                    } else holder.epicLink!!.text = epicLinksDi!!.epicLinksList[j].name
                                }
                            }
                        }
                    } else holder.epicLink!!.visibility = View.GONE

                    for (i in projects.projects.indices) {
                        if (task.projectId != null) if (task.projectId == projects.projects[i].id) {
                            holder.project!!.text = projects.projects[i].name
                        }
                    }
                    holder.priority!!.text = Constants.priority[task.priority]
                    if (task.deadlineDate != null && task.deadlineDate != "null") holder.deadLine!!.text = DateHelper.getDate(task.deadlineDate) else holder.deadLine!!.visibility = View.GONE
                }
            }
             else {
                holder.stubText!!.visibility = View.VISIBLE
            }
    }

    override fun changeItemPosition(fromPos: Int, toPos: Int) {
        super.changeItemPosition(fromPos, toPos)
    }

    override fun swapItems(pos1: Int, pos2: Int) {
        super.swapItems(pos1, pos2)
    }

    override fun getPositionForItemId(id: Long): Int {
        return super.getPositionForItemId(id)
    }

    override fun getDropTargetId(): Long {
        return super.getDropTargetId()
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first!!
    }


//    inner class StubViewHolder(itemView: View, itemStubCalendar: Int) : DragItemAdapter.ViewHolder(itemView, R.id.item_layout, mDragOnLongPress){
//        var stubText: TextView = itemView.findViewById(R.id.calendar_stub_text)
//
//
//        override fun onItemClicked(view: View?) {
//            return
//        }
//
//        override fun onItemLongClicked(view: View?): Boolean {
//            return false
//        }
//
//        override fun onItemTouch(view: View?, event: MotionEvent?): Boolean {
//            return true
//        }
//    }

    inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView? = null
        var spentTime: TextView? = null
        var estimatedTime: TextView? = null
        var deadLine: TextView? = null
        var project: TextView? = null
        var priority: TextView? = null
        var epicLink: TextView? = null
        var taskTextCanceled: TextView? = null
        var cardView: CardView? = null
        var layout: FrameLayout? = null
        var isDone: CheckBox? = null
        var refresh: CheckBox? = null
        var subTaskLayout: LinearLayout? = null
        var subTaskCount: TextView? = null
        var stubText: TextView? = null

        override fun onItemClicked(view: View) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return
            } else {
                try {

//            AddTask dialog = new AddTask();
//            Bundle bundle = new Bundle();
//            bundle.putString("token", token);
//            bundle.putParcelableArrayList("members", (ArrayList<? extends Parcelable>) memberList);
//            bundle.putParcelable("calendarViewModel", viewModel);
//            bundle.putString("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
//            dialog.setArguments(bundle);
//            vibrator.vibrate(50);
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            dialog.show(ft, "123");

//                    val taskInfo = com.emika.app.presentation.ui.calendar.TaskInfo()
//                    val bundle = Bundle().apply {
//                        putString("token", token)
//                        putParcelable("task", mItemList[adapterPosition].second)
//                        putParcelable("calendarViewModel", calendarViewModel)
//                    }
//                    taskInfo.arguments = bundle
//                    val ft: FragmentTransaction = fragmentManager.beginTransaction()
////                    ft.setCustomAnimations(R.anim.zoom_anim, R.anim.zoom_anim)
////                    taskInfo.show(ft, "taskInfoDialog")
//                    ft.add(R.id.main_container, taskInfo).addToBackStack("boardFragment").commit()
                    mLastClickTime = SystemClock.elapsedRealtime()
                    val intent = Intent(context, TaskInfoActivity::class.java)
                        intent.putExtra("task", mItemList[adapterPosition].second)
                        intent.putExtra("calendarViewModel", calendarViewModel)
//                    context!!.startActivity(intent)
                    vibrator!!.vibrate(100)
                    startActivityForResult(activity, intent, 1, null)

                } catch (e: Exception) {
//                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }



            }

        }

        override fun onItemLongClicked(view: View): Boolean {
//            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true
        }

        init {
            if (!hasStub) {
                epicLink = itemView.findViewById(R.id.calendar_task_epic_links)
                mText = itemView.findViewById<View>(R.id.text) as TextView
                layout = itemView.findViewById(R.id.item_layout)
                cardView = itemView.findViewById(R.id.card)
                spentTime = itemView.findViewById(R.id.item_task_spent_time)
                estimatedTime = itemView.findViewById(R.id.item_task_estimated_time)
                priority = itemView.findViewById(R.id.calendar_task_priority)
                deadLine = itemView.findViewById(R.id.calendar_task_deadline)
                project = itemView.findViewById(R.id.calendar_task_project)
                isDone = itemView.findViewById(R.id.task_done)
                refresh = itemView.findViewById(R.id.task_refresh)
                taskTextCanceled = itemView.findViewById(R.id.calendar_task_canceled)
                subTaskCount = itemView.findViewById(R.id.calendar_task_sub_tasks_count)
                subTaskLayout = itemView.findViewById(R.id.calendar_task_sub_tasks)
            } else
             stubText = itemView.findViewById(R.id.calendar_stub_text)

        }
    }

    companion object {
        private const val TAG = "ItemAdapter"
    }
}