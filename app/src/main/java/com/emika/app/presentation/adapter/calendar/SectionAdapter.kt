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

class SectionAdapter(sections: List<PayloadSection?>, addProjectViewModel: AddProjectViewModel?, context: Context) : RecyclerView.Adapter<SectionViewHolder>(), ItemTouchHelperAdapter {
    @JvmField
    @Inject
    var projectDi: Project? = null

    val sections: MutableList<PayloadSection?>
    private val emikaApplication = EmikaApplication.instance
    private val addProjectViewModel: AddProjectViewModel?
    private val context: Context
    private lateinit var newList: MutableList<String>
    @JvmField
    @Inject
    var projectsDagger: ProjectsDi? = null
    private val defaultSection: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
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

        if (addProjectViewModel != null) {
            holder.options.setOnClickListener { v: View -> showPopupMenu(v, section, position) }
            holder.options.visibility = View.VISIBLE
            holder.drag.visibility = View.VISIBLE
            if (section.id == addProjectViewModel.project?.defaultSectionId)
                holder.default.visibility = View.VISIBLE
            else
                holder.default.visibility = View.GONE


        } else {
            holder.options.visibility = View.GONE
            holder.drag.visibility = View.GONE
            holder.default.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return sections.size
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
        sections[position]?.status = "deleted"
        addProjectViewModel?.updateSection(sections[position]!!)
        sections.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sectionName: TextView
        var item: ConstraintLayout
        var options: ImageButton
        var drag: ImageButton
        var default: TextView

        init {
            item = itemView.findViewById(R.id.section)
            sectionName = itemView.findViewById(R.id.item_section_name)
            options = itemView.findViewById(R.id.item_section_options)
            drag = itemView.findViewById(R.id.item_section_drag)
            default = itemView.findViewById(R.id.item_section_default)
        }
    }

    private fun showPopupMenu(view: View, section: PayloadSection?, position: Int) {
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