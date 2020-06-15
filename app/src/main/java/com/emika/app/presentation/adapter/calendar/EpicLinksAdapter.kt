package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.di.Project
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter.EpicLInksViewHolder
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import java.util.*
import javax.inject.Inject

class EpicLinksAdapter(epicLinks: List<PayloadEpicLinks>, context: Context, addTaskListViewModel: AddTaskListViewModel?, taskInfoViewModel: TaskInfoViewModel?) : RecyclerView.Adapter<EpicLInksViewHolder>() {
    private val epicLinks: MutableList<PayloadEpicLinks>
    private val context: Context
    private val addTaskListViewModel: AddTaskListViewModel?
    private val taskInfoViewModel: TaskInfoViewModel?
    private val epicLinksId: List<String>

    @JvmField
    @Inject
    var projectDi: Project? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpicLInksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_epic_link, parent, false)
        return EpicLInksViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpicLInksViewHolder, position: Int) {
        val epicLink = epicLinks[position]
        holder.epicLinkName.text = epicLink.name
        if (addTaskListViewModel != null)
            for (i in addTaskListViewModel.epicLinksList.indices) {
            if (addTaskListViewModel.epicLinksList[i].id == epicLink.id)
                holder.checkBox.isChecked = true
        }
        if (taskInfoViewModel != null)
            for (i in taskInfoViewModel.task.epicLinks.indices) {
            if (taskInfoViewModel.task.epicLinks[i] == epicLink.id)
                holder.checkBox.isChecked = true
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (addTaskListViewModel != null) {
                if (!isChecked) {
                    addTaskListViewModel.epicLinksList.remove(epicLink)
                    addTaskListViewModel.epicLinksMutableLiveData
                }
                if (isChecked) {
                    addTaskListViewModel.epicLinksList.add(epicLink)
                    addTaskListViewModel.epicLinksMutableLiveData
                }
            }
            if (taskInfoViewModel != null) {
                if (!isChecked) {
                    taskInfoViewModel.task.epicLinks.remove(epicLink.id)
                    taskInfoViewModel.updateTask(taskInfoViewModel.task)
                    taskInfoViewModel.epicLinksMutableLiveData
                }
                if (isChecked) {
                    taskInfoViewModel.task.epicLinks.add(epicLink.id)
                    taskInfoViewModel.updateTask(taskInfoViewModel.task)
                    taskInfoViewModel.epicLinksMutableLiveData
                }
            }
        }
        holder.item.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return epicLinks.size
    }

    inner class EpicLInksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val epicLinkName: TextView
        val checkBox: CheckBox
        val item: ConstraintLayout

        init {
            item = itemView.findViewById(R.id.epic_link_item)
            epicLinkName = itemView.findViewById(R.id.item_epic_link_name)
            checkBox = itemView.findViewById(R.id.item_epic_link_check)
        }
    }

    companion object {
        private const val TAG = "EpicLinksAdapter"
    }

    init {
        this.epicLinks = ArrayList()
        this.context = context
        this.addTaskListViewModel = addTaskListViewModel
        this.taskInfoViewModel = taskInfoViewModel
        EmikaApplication.instance.component?.inject(this)
        epicLinksId = ArrayList()
        for (i in epicLinks.indices) {
            if (epicLinks[i].projectId == projectDi!!.projectId) this.epicLinks.add(epicLinks[i])
        }
    }
}