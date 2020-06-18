package com.emika.app.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emika.app.R
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadProjectCreation
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel

class AddProjectDialogFragment : DialogFragment() {
    private lateinit var createProject: Button
    private lateinit var projectName: EditText


    companion object {
        fun newInstance() = AddProjectDialogFragment()
    }

    private lateinit var viewModel: AddProjectViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_project_dialog_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View){
        projectName = view.findViewById(R.id.project_name)
        createProject = view.findViewById(R.id.create_project_btn)
        createProject.setOnClickListener {
            createProject()
        }
    }


    private fun createProject() {
        if (projectName.text.isNotEmpty()) {
            viewModel.createProject(projectName.text.toString())
        } else {
            projectName.requestFocus()
            projectName.error = "Missing project name"
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddProjectViewModel::class.java)
        viewModel.projectMutableLiveData.observe(viewLifecycleOwner, getCreatedProject())
        // TODO: Use the ViewModel
    }


    private fun getCreatedProject() = Observer<PayloadProjectCreation> {
        if (it != null){
            dismiss()
            val project = PayloadProject()
            project.id = it.id
            project.name = it.name
            project.createdBy = it.createdBy
            project.color = it.color
            project.isPersonal = it.isPersonal
            project.status = it.status
            val bundle = Bundle()
            val mySheetDialog = BottomSheetEditProjectFragment()
            bundle.putParcelable("project", project)
            mySheetDialog.arguments = bundle
            mySheetDialog.show(fragmentManager!!, "modalSheetDialog")
        } else {
            Toast.makeText(context, "Something went wrong, try later", Toast.LENGTH_SHORT).show()
        }
    }

}
