package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emika.app.R
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 * A simple [Fragment] subclass.
 */
class BottomSheetEditProjectFragment : BottomSheetDialogFragment() {
    private lateinit var project: PayloadProject
    private lateinit var projectName: TextView
    private lateinit var projectDescription: TextView
    private lateinit var switchEpicLinks: Switch
    private lateinit var switchEstimated: Switch
    private lateinit var switchPriority: Switch
    private lateinit var switchDeadline: Switch
    private lateinit var switchActualSpent: Switch
    private lateinit var members: TextView
    private lateinit var sections: TextView
    private lateinit var epicLinks: TextView
    private lateinit var viewModel: AddProjectViewModel
    private lateinit var save: TextView

    private var activeFieldList: MutableList<String> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_edit_project, container, false)

        initView(view)
        dialog!!.setOnShowListener { dialog ->
//            val d = dialog as BottomSheetDialog
//            val bottomSheetInternal = d.findViewById<View>(android.support.R.id.design_bottom_sheet)
//            BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun initView(view: View) {
        viewModel = ViewModelProviders.of(this).get(AddProjectViewModel::class.java)
        viewModel.membersMutableLiveData.observe(viewLifecycleOwner, getMemberLiveData())
        project = arguments!!.getParcelable("project")!!
        projectName = view.findViewById(R.id.edit_project_name)
        save = view.findViewById(R.id.save_proj)
        save.setOnClickListener {
            updateProject()
            dismiss()
        }
        projectDescription = view.findViewById(R.id.edit_project_description)
        switchEpicLinks = view.findViewById(R.id.edit_project_epic_links)
        switchEpicLinks.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                activeFieldList.add("epic_links")
            else
                activeFieldList.remove("epic_links")

        }
        switchEstimated = view.findViewById(R.id.edit_project_estimated)
        switchEstimated.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                activeFieldList.add("duration")
            else
                activeFieldList.remove("duration")

        }
        switchPriority = view.findViewById(R.id.edit_project_priority)
        switchPriority.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                activeFieldList.add("priority")
            else
                activeFieldList.remove("priority")

        }
        switchDeadline = view.findViewById(R.id.edit_project_deadline)
        switchDeadline.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                activeFieldList.add("deadline")
            else
                activeFieldList.remove("deadline")
        }
        switchActualSpent = view.findViewById(R.id.edit_project_spent_time)
        switchActualSpent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                activeFieldList.add("actual_duration")
            else
                activeFieldList.remove("actual_duration")

        }
        members = view.findViewById(R.id.edit_project_members)
        members.setOnClickListener {
            addMembers()
        }
        sections = view.findViewById(R.id.edit_project_sections_tv)
        sections.setOnClickListener {
            editSection()
        }
        epicLinks = view.findViewById(R.id.edit_project_epic_links_tv)
        setProjectInfo(project)
    }

    private fun editSection() {
        val bundle = Bundle()
        val mySheetDialog = BottomSheetEditSections()
        bundle.putParcelable("project", project)
        bundle.putParcelable("viewModel", viewModel)
        mySheetDialog.arguments = bundle
        mySheetDialog.show(fragmentManager!!, "modalSheetDialog")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)

    private fun getMemberLiveData() = Observer<List<String>>{
        if (it.isNotEmpty())
        members.text = "${it.size} members"
        else
            members.text = "Members"
    }

    private fun addMembers(){
        val bundle = Bundle()
        val mySheetDialog = BottomSheetProjectAddMember()
        bundle.putParcelable("project", project)
        bundle.putParcelable("viewModel", viewModel)
        mySheetDialog.arguments = bundle
        mySheetDialog.show(fragmentManager!!, "modalSheetDialog")
    }

    private fun setProjectInfo(project: PayloadProject){
        viewModel.project = project
        viewModel.membersMutableLiveData.value = project.members
        projectName.text = project.name

        Log.d("123", project.id)
        projectDescription.text = project.description
        for (field in project.activeFields){
            if (field == "priority")
                switchPriority.isChecked = true
            if (field == "epic_links")
                switchEpicLinks.isChecked = true
            if (field == "duration")
                    switchEstimated.isChecked = true
            if (field == "actual_duration")
                    switchActualSpent.isChecked = true
            if (field == "deadline")
                    switchDeadline.isChecked = true
        }
        projectDescription.text = project.description

//        members.text = resources.getQuantityText(R.plurals.numberOfMembers, project.members.size)
//        sections.text = "${addPro}"
//           epicLinks =
    }

    private fun updateProject(){
        project.activeFields = activeFieldList
        project.name = projectName.text.toString()
        project.description = projectDescription.text.toString()
        project.activeFields = activeFieldList
        project.members = viewModel.membersMutableLiveData.value
        viewModel.update(project)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}
