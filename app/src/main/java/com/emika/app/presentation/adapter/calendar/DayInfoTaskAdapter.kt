package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.User
import com.emika.app.presentation.utils.DateHelper
import java.text.DecimalFormat
import java.util.*

class DayInfoTaskAdapter(private val taskList: List<PayloadTask>, private val context: Context, private val user: User) : RecyclerView.Adapter<DayInfoTaskAdapter.ViewHolder>() {
    private var df: DecimalFormat? = null
    init {

    }
    private val addedTask: MutableList<PayloadTask>

    init {
        df = DecimalFormat("#.#")
    }
    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView
        val estimatedTime: TextView
        val spentTime: TextView
        val project: TextView
        val priority: TextView
        val deadline: TextView
        val status: CheckBox
        val userName: TextView

        init {
            taskName = itemView.findViewById(R.id.day_info_task_text)
            estimatedTime = itemView.findViewById(R.id.day_info_task_estimated_time)
            spentTime = itemView.findViewById(R.id.day_info_task_spent_time)
            project = itemView.findViewById(R.id.day_info_task_project)
            priority = itemView.findViewById(R.id.day_info_task_priority);
            deadline = itemView.findViewById(R.id.day_info_task_deadline)
            status = itemView.findViewById(R.id.day_info_task_done)
            userName = itemView.findViewById(R.id.day_info_task_completed_by)
        }
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

        if (task.duration % 60 == 0)
            holder.estimatedTime.text = String.format("%sh", (task.duration / 60).toString())
        else
            holder.estimatedTime.text = String.format("%sh", df!!.format(task.duration / 60.0f.toDouble()))

        if (task.durationActual % 60 == 0)
            holder.spentTime.text = String.format("%sh", (task.durationActual / 60).toString())
        else
            holder.spentTime.text = String.format("%sh", df!!.format(task.durationActual / 60.0f.toDouble()))
        holder.priority.text = task.priority
        holder.userName.text = user.firstName + " " + user.lastName
        if (task.status == "done")
            holder.status.isChecked = true

        if (task.deadlineDate != null && task.deadlineDate != "null")
            holder.deadline.text = DateHelper.getDate(task.deadlineDate)
        else
            holder.deadline.visibility = View.GONE
        if (task.priority != null) when (task.getPriority()) {
            "low" -> {
                holder.priority.background = context.resources.getDrawable(R.drawable.shape_priority_low)
                holder.priority.setTextColor(context.resources.getColor(R.color.low_priority))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
            }
            "normal" -> holder.priority.setVisibility(View.GONE)
            "high" -> {
                holder.priority.background = context.resources.getDrawable(R.drawable.shape_priority_high)
                holder.priority.setTextColor(context.resources.getColor(R.color.yellow))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_priority_high), null, null, null)
            }
            "urgent" -> {
                holder.priority.background = context.resources.getDrawable(R.drawable.shape_priority_urgent)
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