package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.db.entity.ProjectEntity
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.InboxViewModel
import java.text.DecimalFormat
import java.util.*

class InboxTaskAdapter(private val taskList: List<PayloadTask>, private val context: Context, private val viewModel: InboxViewModel?, private val projectEntities: List<ProjectEntity>) : RecyclerView.Adapter<InboxTaskAdapter.ViewHolder>() {

    fun getAddedTask(): List<PayloadTask> {
        return addedTask
    }

    private val addedTask: MutableList<PayloadTask>

    private var df: DecimalFormat? = null
    init {
        df = DecimalFormat("#.#")

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inbox_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inbox = taskList[position]
        holder.taskName.text = inbox.name
        if (inbox.duration % 60 == 0)
            holder.estimatedTime.text = String.format("%sh", (inbox.duration / 60).toString())
        else
            holder.estimatedTime.text = String.format("%sh", df!!.format(inbox.duration / 60.0f.toDouble()))

        if (inbox.durationActual % 60 == 0)
            holder.spentTime.text = String.format("%sh", (inbox.durationActual / 60).toString())
        else
            holder.spentTime.text = String.format("%sh", df!!.format(inbox.durationActual / 60.0f.toDouble()))


        holder.item.setOnClickListener { v: View? ->
            if (addedTask.contains(inbox)) {
                addedTask.remove(inbox)
//                viewModel?.addedTaskList.value!!.toMutableList().remove(inbox)
                holder.item.background = context.resources.getDrawable(R.drawable.shape_item_inbox_unchecked)
                holder.add.setImageDrawable(context.getDrawable(R.drawable.ic_add_inbox))
            } else if (!addedTask.contains(inbox)) {
                addedTask.add(inbox)
//                viewModel.addedTaskList.value!!.toMutableList().add(inbox)
                holder.item.background = context.resources.getDrawable(R.drawable.shape_item_inbox_checked)
                holder.add.setImageDrawable(context.getDrawable(R.drawable.ic_added_inbox))

            }
        }

        for (i in projectEntities.indices) {
            if (inbox.projectId != null)
                Log.d("1233", projectEntities.size.toString())
            if (inbox.projectId == projectEntities[i].id) {
                    holder.project.text = projectEntities[i].name
            }
        }

        holder.priority.text = inbox.priority
        if (inbox.deadlineDate != null && inbox.deadlineDate != "null")
            holder.deadline.text = DateHelper.getDate(inbox.deadlineDate)
        else
            holder.deadline.visibility = View.GONE
        if (inbox.priority != null) when (inbox.priority) {
            "low" -> {
                holder.priority.background = context.resources.getDrawable(R.drawable.shape_priority_low)
                holder.priority.setTextColor(context.resources.getColor(R.color.low_priority))
                holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.resources.getDrawable(R.drawable.ic_priority_low), null, null, null)
            }
            "normal" -> holder.priority.visibility = View.GONE
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

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: ConstraintLayout
        val taskName: TextView
        val estimatedTime: TextView
        val spentTime: TextView
        val project: TextView
        var add: ImageView
        val added: ImageView
        val priority: TextView
        val deadline: TextView

        init {
            item = itemView.findViewById(R.id.item_inbox_layout)
            taskName = itemView.findViewById(R.id.inbox_task_text)
            estimatedTime = itemView.findViewById(R.id.inbox_task_estimated_time)
            spentTime = itemView.findViewById(R.id.inbox_task_spent_time)
            project = itemView.findViewById(R.id.inbox_task_project)
            add = itemView.findViewById(R.id.inbox_task_add)
            added = itemView.findViewById(R.id.inbox_task_added)
            priority = itemView.findViewById(R.id.inbox_task_priority);
            deadline = itemView.findViewById(R.id.inbox_task_deadline)
        }
    }

    companion object {
        private const val TAG = "InboxTaskAdapter"
    }

    init {
        addedTask = ArrayList()
    }
}