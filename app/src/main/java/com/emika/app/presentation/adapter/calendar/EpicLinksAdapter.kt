package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.chat.Message
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.di.Project
import com.emika.app.presentation.adapter.ItemTouchHelper.ItemTouchHelperAdapter
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter.EpicLInksViewHolder
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EpicLinksAdapter(epicLinks: List<PayloadEpicLinks>, context: Context, addTaskListViewModel: AddTaskListViewModel?,
                       taskInfoViewModel: TaskInfoViewModel?, addProjectViewModel: AddProjectViewModel?) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private var epicLinks: MutableList<PayloadEpicLinks>
    private val context: Context
    private val addTaskListViewModel: AddTaskListViewModel?
    private val taskInfoViewModel: TaskInfoViewModel?
    private val addProjectViewModel: AddProjectViewModel?
    private val epicLinksId: List<String>
    private var newList: ArrayList<String>? = null

    fun setList(newList: MutableList<PayloadEpicLinks>){
        epicLinks = newList
    }

    @JvmField
    @Inject
    var projectDi: Project? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layout = -1
        when (viewType) {
            1 -> {
                layout = R.layout.item_edit_epic_link
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                return EpicLInksEditViewHolder(view)
            }
            2 -> {
                layout = R.layout.item_epic_link
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                return EpicLInksViewHolder(view)
            }
            else ->{
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return EpicLInksEditViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val epicLink = epicLinks[position]
        when (holder.itemViewType){
            2-> {
                val holder = holder as EpicLInksViewHolder
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
                    holder.item?.setOnClickListener {
                        holder.checkBox.isChecked = !holder.checkBox.isChecked
                    }

            }
            1->{
                val holder = holder as EpicLInksEditViewHolder
                holder.options.setOnClickListener {  }
                holder.epicLinkName.text = epicLink.name
            }
        }

    }

    override fun getItemCount(): Int {
        return epicLinks.size
    }

    inner class EpicLInksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val epicLinkName: TextView = itemView.findViewById(R.id.item_epic_link_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.item_epic_link_check)
        var item: LinearLayout = itemView.findViewById(R.id.epic_link_item)

    }

    inner class EpicLInksEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val epicLinkName: TextView = itemView.findViewById(R.id.item_epic_link_name)
        var item: CardView = itemView.findViewById(R.id.epic_link_item)
        val options: ImageButton = itemView.findViewById(R.id.item_epic_link_options)
        val drag: ImageView = itemView.findViewById(R.id.item_epic_link_drag)

    }

    companion object {
        private const val TAG = "EpicLinksAdapter"
    }


    override fun getItemViewType(position: Int): Int {
        return if (addProjectViewModel != null){
            1
        } else {
            2
        }
    }

    init {
        this.epicLinks = ArrayList()
        this.context = context
        this.addTaskListViewModel = addTaskListViewModel
        this.taskInfoViewModel = taskInfoViewModel
        this.addProjectViewModel = addProjectViewModel
        EmikaApplication.instance.component?.inject(this)
        epicLinksId = ArrayList()
        for (i in epicLinks.indices) {
            if (epicLinks[i].projectId == projectDi!!.projectId)
                this.epicLinks.add(epicLinks[i])
        }
        if (addProjectViewModel != null)
            this.epicLinks = epicLinks as MutableList<PayloadEpicLinks>

    }

    private fun showPopupMenu(view: View, epicLink: PayloadEpicLinks?, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.edit_epic_link_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.edit_epic_link_edit -> {

                }
                R.id.edit_epic_link_delete -> {
                    deleteItem(position)
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun deleteItem(position: Int){
        epicLinks[position].status = "deleted"
        addProjectViewModel?.updateEpicLink(epicLinks[position])
        epicLinks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun onItemDismiss(position: Int) {
        deleteItem(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        newList = ArrayList()
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(epicLinks, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(epicLinks, i, i - 1)
            }
        }

//        this.epicLinks.sortWith(Comparator { lhs: PayloadEpicLinks?, rhs: PayloadEpicLinks? ->
//            when {
//                lhs!!.order === rhs!!.order -> {
//                    return@Comparator 0
//                }
//                lhs!!.order < rhs!!.order -> {
//                    return@Comparator -1
//                }
//                else -> {
//                    return@Comparator 1
//                }
//            }
//        })
        for (epicLink in epicLinks)
            newList?.add(epicLink.id)

        addProjectViewModel?.updateEpicLinksOrder(newList!!)
        notifyItemMoved(fromPosition, toPosition)

    }
}