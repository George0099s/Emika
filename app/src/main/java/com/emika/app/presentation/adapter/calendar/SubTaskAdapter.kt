package com.emika.app.presentation.adapter.calendar

import android.R.attr.password
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.subTask.SubTask
import com.emika.app.presentation.adapter.ItemTouchHelper.ItemTouchHelperAdapter
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import java.util.*


class SubTaskAdapter(taskList: List<SubTask>, calendarViewModel: CalendarViewModel?, taskInfoViewModel: TaskInfoViewModel?) : RecyclerView.Adapter<SubTaskAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    var taskList: MutableList<SubTask?> = mutableListOf()
    private val calendarViewModel: CalendarViewModel?
    private val taskInfoViewModel: TaskInfoViewModel?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subtask, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subTask = taskList[position]
        subTask!!.planOrder = position
        holder.body.imeOptions = EditorInfo.IME_ACTION_DONE
        holder.body.setRawInputType(InputType.TYPE_CLASS_TEXT)
        if (subTask.newTask) {
            holder.body.requestFocus()
            subTask.newTask = false
        } else holder.body.clearFocus()
        if (subTask.name != null) holder.body.text = subTask.name
        if (subTask.status == "done")
            holder.checkBox.isChecked = true
        else
            holder.checkBox.isChecked = false

//        holder.body.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (position == taskList.size-1 )
//                    addSubTask(SubTask())
//                true
//            } else false
//        }
        holder.checkBox.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (taskInfoViewModel != null) if (isChecked) {
                subTask.status = "done"
                taskInfoViewModel.updateSubTask(subTask)
            } else {
                subTask.status = "wip"
                taskInfoViewModel.updateSubTask(subTask)
            } else if (calendarViewModel != null) {
                if (isChecked) {
                    subTask.status = "done"
                } else {
                    subTask.status = "wip"
                }
            }
        }
//        holder.body.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                return@setOnEditorActionListener true
//            }
//            false
//        }
        holder.body.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                subTask.setName(holder.body.getText().toString());
            }

            override fun afterTextChanged(s: Editable) {
                if (taskInfoViewModel != null) {
                    subTask.name = holder.body.text.toString()
                    taskInfoViewModel.updateSubTask(subTask)
                }
                if (calendarViewModel != null) {
                    subTask.name = holder.body.text.toString()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun addSubTask(subTask: SubTask) {
        subTask.newTask = true
        taskList.add(itemCount, subTask)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item: ConstraintLayout
        var body: TextView
        var checkBox: CheckBox

        init {
            item = itemView.findViewById(R.id.item_sub_task)
            body = itemView.findViewById(R.id.sub_task_item_body)
            checkBox = itemView.findViewById(R.id.sub_task_item_checkbox)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val list: MutableList<String> = ArrayList()
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                taskList[i]?.order = i + 1
                Collections.swap(taskList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                taskList[i]?.order = i - 1
                Collections.swap(taskList, i, i - 1)

            }
        }
        this.taskList.sortWith(Comparator { lhs: SubTask?, rhs: SubTask? ->
            if (lhs!!.order === rhs!!.order) {
                return@Comparator 0
            } else if (lhs!!.order < rhs!!.order) {
                return@Comparator -1
            } else {
                return@Comparator 1
            }
        })
        for (subTask in taskList)
            list.add(subTask?.id!!)

        taskInfoViewModel?.updateTaskOrder(list)
            notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        if (taskInfoViewModel != null) {
            taskList[position]!!.status = "deleted"
            taskInfoViewModel.updateSubTask(taskList[position])
        }
        taskList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    companion object {
        private const val TAG = "SubTaskAdapter"
    }

    init {
        this.taskList = ArrayList()
        for (i in taskList.indices) {
            if (taskList[i].status != "deleted") this.taskList.add(taskList[i])
        }
        this.taskList.sortWith(Comparator { lhs: SubTask?, rhs: SubTask? ->
            if (lhs!!.order === rhs!!.order) {
                return@Comparator 0
            } else if (lhs!!.order < rhs!!.order) {
                return@Comparator -1
            } else {
                return@Comparator 1
            }
        })
        this.calendarViewModel = calendarViewModel
        this.taskInfoViewModel = taskInfoViewModel
    }
}