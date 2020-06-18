package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.member.PayloadShortMember
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.presentation.adapter.calendar.SelectCurrentUserAdapter
import com.emika.app.presentation.ui.profile.ManageInvites
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import javax.inject.Inject

class BottomSheetProjectAddMember() : BottomSheetDialogFragment() {
    private var memberRecycler: RecyclerView? = null
    private var adapter: SelectCurrentUserAdapter? = null
    private val token: String? = null
    private lateinit var project: PayloadProject
    private lateinit var addProjectViewModel: AddProjectViewModel
    private val app = EmikaApplication.instance

    @JvmField
    @Inject
    var assignee: Assignee? = null
    private var calendarViewModel: CalendarViewModel? = null
    private var boardViewModel: CalendarViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_project_add_member, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyleDialogTheme)
        initView(view)
        return view
    }

    private val members = Observer { members1: List<PayloadShortMember?>? ->
        adapter = SelectCurrentUserAdapter(members1, context, null, null, null, null, addProjectViewModel)
        memberRecycler!!.adapter = adapter
    }


    private fun initView(view: View) {
        app.component?.inject(this)
        project = arguments!!.getParcelable("project")!!
        addProjectViewModel = arguments!!.getParcelable("viewModel")!!
        calendarViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        calendarViewModel!!.membersMutableLiveData.observe(viewLifecycleOwner, members)
        memberRecycler = view.findViewById(R.id.bottom_sheet_recycler_add_member)
        memberRecycler!!.layoutManager = LinearLayoutManager(context)
        memberRecycler!!.setHasFixedSize(true)
    }


    override fun onDismiss(dialog: DialogInterface) {

        super.onDismiss(dialog)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottom_sheet_select_assignee, null)
        setStyle(DialogFragment.STYLE_NO_INPUT, R.style.BottomSheetStyleDialogTheme)
        dialog.setContentView(contentView)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)




}