package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.di.ProjectsDi
import com.emika.app.presentation.utils.DateHelper
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject


class DayInfoTaskAdapter(private val taskList: List<PayloadTask>, private val context: Context, private val assignee: Assignee, private val durations: List<PayloadDurationActual>) : RecyclerView.Adapter<DayInfoTaskAdapter.ViewHolder>() {
    private var df: DecimalFormat? = null

    private val addedTask: MutableList<PayloadTask>
    @Inject
    lateinit var project: ProjectsDi
    init {
        df = DecimalFormat("#.#")
        EmikaApplication.instance.component?.inject(this)
        Log.d(TAG, taskList.size.toString())
    }
    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.day_info_task_text)
        val estimatedTime: TextView = itemView.findViewById(R.id.day_info_task_estimated_time)
        val spentTime: TextView = itemView.findViewById(R.id.day_info_task_spent_time)
        val project: TextView = itemView.findViewById(R.id.day_info_task_project)
        val priority: TextView = itemView.findViewById(R.id.day_info_task_priority)
        val deadline: TextView = itemView.findViewById(R.id.day_info_task_deadline)
        val taskTextCanceled: TextView = itemView.findViewById(R.id.day_info_task_canceled)
        val status: CheckBox = itemView.findViewById(R.id.day_info_task_done)
        val refresh: CheckBox = itemView.findViewById(R.id.day_info_task_refresh)
        val userName: TextView = itemView.findViewById(R.id.day_info_task_completed_by)
        val time: TextView = itemView.findViewById(R.id.day_info_task_time)
        val loggedTime: TextView = itemView.findViewById(R.id.day_info_task_logged_time)
    }

    companion object {
        private const val TAG = "InboxTaskAdapter"
    }

    init {
        addedTask = ArrayList()
    }

    override fun onBindViewHolder(holder: DayInfoTaskAdapter.ViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.name
        for (duration in durations)
            if (duration.taskId == task.id) {
                holder.time.text = DateHelper.getLoggedTimDayInfo(duration.createdAt)
//                holder.loggedTime.text = (duration.value / 60).toString()
                val loggedTime = duration.value
                if (loggedTime % 60 == 0) {
                    val spannable = SpannableString(context.resources.getString(R.string.logged_time) + " " + String.format("%sh", (loggedTime / 60).toString()))
                    spannable.setSpan(
                            ForegroundColorSpan(context.resources.getColor(R.color.green)),
                            context.resources.getString(R.string.logged_time).length, spannable.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.loggedTime.text = spannable
                }
                else {
                    var s =context.resources.getString(R.string.logged_time) + " " +  String.format("%sh", df!!.format(loggedTime / 60.0f.toDouble()))
                    s = s.replace(',', '.')
                    val spannable = SpannableString(s)
                    spannable.setSpan(
                            ForegroundColorSpan(context.resources.getColor(R.color.green)),
                            context.resources.getString(R.string.logged_time).length, spannable.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.loggedTime.text = spannable
                }
            }
        if (task.duration % 60 == 0)
            holder.estimatedTime.text = String.format("%sh", (task.duration / 60).toString())
        else
            holder.estimatedTime.text = String.format("%sh", df!!.format(task.duration / 60.0f.toDouble()))

        if (task.durationActual % 60 == 0) {
            holder.spentTime.text = String.format("%sh", (task.durationActual / 60).toString())
        } else {
            holder.spentTime.text = String.format("%sh", df!!.format(task.durationActual / 60.0f.toDouble()))
        }

        holder.priority.text = task.priority
        holder.userName.text = assignee.firstName + " " + assignee.lastName

        when (task.status) {
            "done" -> {
                holder.taskName.paintFlags = Paint.ANTI_ALIAS_FLAG
                holder.taskName.setTextColor(context!!.resources.getColor(R.color.task_name_done))
                holder.status.visibility = View.VISIBLE
                holder.status.isChecked = true
                holder.taskTextCanceled.visibility = View.GONE
                holder.priority.visibility = View.VISIBLE
                holder.refresh.visibility = View.GONE
            }
            "canceled" -> {
                holder.taskName.setTextColor(context.resources.getColor(R.color.task_name_done))
                holder.taskName.paintFlags = holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.taskTextCanceled.visibility = View.VISIBLE
                holder.status.visibility = View.GONE
                holder.priority.visibility = View.GONE
                holder.refresh.visibility = View.VISIBLE
                holder.refresh.isChecked = true
            }
            else -> {
                holder.taskName.paintFlags = Paint.ANTI_ALIAS_FLAG
                holder.taskTextCanceled.visibility = View.GONE
                holder.priority.visibility = View.VISIBLE
                holder.refresh.visibility = View.GONE
                holder.status.visibility = View.VISIBLE
                holder.taskName.setTextColor(context.resources.getColor(R.color.mainTextColor))
                holder.status.isChecked = false
            }
        }

        for (i in project.projects.indices) {
            if (task.projectId != null) if (task.projectId == project.projects[i].id) {
                holder.project.text = project.projects[i].name
            }
        }

        if (task.deadlineDate != null && task.deadlineDate != "null")
            holder.deadline.text = DateHelper.getDate(task.deadlineDate)
        else
            holder.deadline.visibility = View.GONE
        if (task.priority != null) when (task.priority) {
            "low" -> {
                holder.priority.setTextColor(context.resources.getColor(R.color.low_priority))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
            }
            "normal" -> holder.priority.visibility = View.GONE
            "high" -> {
                holder.priority.setTextColor(context.resources.getColor(R.color.yellow))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
            }
            "urgent" -> {
                holder.priority.setTextColor(context.resources.getColor(R.color.red))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_task_urgent), null, null, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_info_task, parent, false)
        return ViewHolder(view)
    }
}