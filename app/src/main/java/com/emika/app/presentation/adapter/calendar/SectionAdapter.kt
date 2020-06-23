package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.data.network.pojo.subTask.SubTask
import com.emika.app.di.Project
import com.emika.app.di.ProjectsDi
import com.emika.app.presentation.adapter.ItemTouchHelper.ItemTouchHelperAdapter
import com.emika.app.presentation.adapter.calendar.SectionAdapter.SectionViewHolder
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SectionAdapter(sections: List<PayloadSection?>, addProjectViewModel: AddProjectViewModel?, context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    @JvmField
    @Inject
    var projectDi: Project? = null

    var sections: MutableList<PayloadSection?>
    private val emikaApplication = EmikaApplication.instance
    private val addProjectViewModel: AddProjectViewModel?
    private val context: Context
    private lateinit var newList: MutableList<String>
    @JvmField
    @Inject
    var projectsDagger: ProjectsDi? = null
    private val defaultSection: String? = null

    fun setList(newList: MutableList<PayloadSection?>){
        sections = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layout = -1
        when (viewType) {
            1 -> {
                layout = R.layout.item_edit_section
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                return SectionEditViewHolder(view)
            }
            2 -> {
                layout = R.layout.item_section
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                return SectionViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                return SectionViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = sections[position]
        when (holder.itemViewType){
            1 -> {
                val holder = holder as SectionEditViewHolder
                holder.sectionName.text = section!!.name
                holder.options.setOnClickListener { v: View -> showPopupMenu(v, position) }
                holder.options.visibility = View.VISIBLE
                holder.drag.visibility = View.VISIBLE
                if (section.id == addProjectViewModel?.project?.defaultSectionId)
                    holder.default.visibility = View.VISIBLE
                else
                    holder.default.visibility = View.GONE

                holder.options.setOnClickListener { showPopupMenu(it, position) }

            }

            2-> {
                val holder = holder as SectionViewHolder
                holder.sectionName.text = section!!.name
                if (projectDi!!.projectSectionId != null && addProjectViewModel == null) {
                    if (projectDi!!.projectSectionId == section.id) {
                        holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"))
                        projectDi!!.projectSectionName = section.name
                    } else holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    holder.item.setOnClickListener { v: View? ->
                        holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"))
                        projectDi!!.projectSectionId = section.id
                        projectDi!!.projectSectionName = section.name
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (addProjectViewModel != null){
            1
        } else {
            2
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        newList = arrayListOf()
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(sections, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(sections, i, i - 1)
            }
        }

        this.sections.sortWith(Comparator { lhs: PayloadSection?, rhs: PayloadSection? ->
            if (lhs!!.order === rhs!!.order) {
                return@Comparator 0
            } else if (lhs!!.order < rhs!!.order) {
                return@Comparator -1
            } else {
                return@Comparator 1
            }
        })
        for (section in sections)
            newList.add(section!!.id)

        addProjectViewModel?.updateSectionsOrder(newList as ArrayList<String>)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        deleteItem(position)
    }

    private fun deleteItem(position: Int){
        projectsDagger!!.sections.remove(sections[position])
        sections[position]?.status = "deleted"
        addProjectViewModel?.updateSection(sections[position]!!)
        sections.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sectionName: TextView = itemView.findViewById(R.id.item_section_name)
        var item: ConstraintLayout = itemView.findViewById(R.id.section)

    }

    inner class SectionEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sectionName: TextView = itemView.findViewById(R.id.item_section_name)
        var item: CardView = itemView.findViewById(R.id.section)
        var options: ImageButton = itemView.findViewById(R.id.item_section_options)
        var drag: ImageButton = itemView.findViewById(R.id.item_section_drag)
        var default: TextView = itemView.findViewById(R.id.item_section_default)
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.edit_section_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_section_default -> {
                    addProjectViewModel?.project?.defaultSectionId = sections[position]?.id
                    notifyDataSetChanged()
                }

                R.id.edit_section_edit -> {

                }
                R.id.edit_section_delete -> {
                    deleteItem(position)

                }
            }
            false
        }
        popupMenu.show()
    }

    companion object {
        private const val TAG = "SectionAdapter"
    }

    init {
        emikaApplication.component.inject(this)
        this.sections = sections as MutableList<PayloadSection?>
        this.addProjectViewModel = addProjectViewModel
        this.context = context


        this.sections.sortWith(Comparator { lhs: PayloadSection?, rhs: PayloadSection? ->
            if (lhs!!.order === rhs!!.order) {
                return@Comparator 0
            } else if (lhs!!.order < rhs!!.order) {
                return@Comparator -1
            } else {
                return@Comparator 1
            }
        })
    }
}