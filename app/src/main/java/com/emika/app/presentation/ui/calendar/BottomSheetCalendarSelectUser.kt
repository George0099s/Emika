package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.presentation.adapter.calendar.SelectCurrentUserAdapter
import com.emika.app.presentation.ui.profile.ManageInvites
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import javax.inject.Inject

class BottomSheetCalendarSelectUser : BottomSheetDialogFragment() {
    private var memberRecycler: RecyclerView? = null
    private var adapter: SelectCurrentUserAdapter? = null
    private var memberList: List<PayloadShortMember>? = ArrayList()
    private val token: String? = null
    private var from: String? = null
    private var addTaskListViewModel: AddTaskListViewModel? = null
    private var taskInfoViewModel: TaskInfoViewModel? = null
    private var task: PayloadTask? = null
    private var invite: Button? = null
    private val app = EmikaApplication.instance

    @JvmField
    @Inject
    var assignee: Assignee? = null
    private var calendarViewModel: CalendarViewModel? = null
    private var boardViewModel: CalendarViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_assignee, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyleDialogTheme)
        initView(view)
        return view
    }

    private val members = Observer { members1: List<PayloadShortMember?>? ->
        adapter = SelectCurrentUserAdapter(members1, context, boardViewModel, addTaskListViewModel, taskInfoViewModel, this)
        memberRecycler!!.adapter = adapter
        invite!!.visibility = View.VISIBLE
    }

    private fun initView(view: View) {
        app.component?.inject(this)
        memberList = arguments!!.getParcelableArrayList("members")
        from = arguments!!.getString("from")
        task = arguments!!.getParcelable("task")
        addTaskListViewModel = arguments!!.getParcelable("addTaskViewModel")
        taskInfoViewModel = arguments!!.getParcelable("taskInfoViewModel")
        boardViewModel = arguments!!.getParcelable("calendarViewModel")
        calendarViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(CalendarViewModel::class.java)
        calendarViewModel!!.membersMutableLiveData.observe(viewLifecycleOwner, members)
        memberRecycler = view.findViewById(R.id.bottom_sheet_recycler_select_user)
        memberRecycler!!.layoutManager = LinearLayoutManager(context)
        memberRecycler!!.setHasFixedSize(true)
        invite = view.findViewById(R.id.invite_person)
        invite!!.setOnClickListener { view: View -> invitePerson(view) }
    }

    private fun invitePerson(view: View) {
        val intent = Intent(context, ManageInvites::class.java)
        startActivity(intent)
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (taskInfoViewModel != null) {
            task!!.assignee = assignee!!.id
            calendarViewModel!!.updateTask(task)
        }
        super.onDismiss(dialog)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottom_sheet_select_assignee, null)
        setStyle(DialogFragment.STYLE_NO_INPUT, R.style.BottomSheetStyleDialogTheme)
        //        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setContentView(contentView)
    }

    companion object {
        private const val TAG = "BottomSheetSelectUser"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)

}