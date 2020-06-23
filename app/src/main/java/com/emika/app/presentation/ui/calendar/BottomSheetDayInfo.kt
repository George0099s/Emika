package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.ActualDurationEntity
import com.emika.app.data.db.entity.TaskEntity
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.di.User
import com.emika.app.features.calendar.MemberItemDecoration
import com.emika.app.presentation.adapter.calendar.DayInfoTaskAdapter
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.DayInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_day_info.*
import java.text.DecimalFormat
import javax.inject.Inject

class BottomSheetDayInfo : BottomSheetDialogFragment() {
    @Inject
    lateinit var assigneeDi: Assignee
    @Inject
    lateinit var userDi: User
    private val token: String? = EmikaApplication.instance.sharedPreferences?.getString("token", "")
    private val viewModel: DayInfoViewModel = DayInfoViewModel(token!!)
    private val decor: MemberItemDecoration = MemberItemDecoration()
    private var date: String? = null
    private var spentTime: Int = 0
    private var taskList: MutableList<PayloadTask> = arrayListOf()
    private var durations: List<PayloadDurationActual> = arrayListOf()
    private val df = DecimalFormat("#.#")
    fun newInstance(): BottomSheetSelectEpicLinks? {
        return BottomSheetSelectEpicLinks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EmikaApplication.instance.component?.inject(this)
        date = arguments!!.getString("date")
        viewModel.getAllDbDurationByAssignee(assigneeDi.id, date)
        viewModel.getAllDbTaskByAssignee(assigneeDi.id) 
        return inflater.inflate(R.layout.bottom_sheet_day_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        date = arguments!!.getString("date")
        durations = arguments!!.getParcelableArrayList("actualDurationList")!!
        val estimatedTime = arguments!!.getString("estimatedTime")
        if (estimatedTime?.toDouble()!! > 0.0f)
            dayInfoEstimatedTime.progress = estimatedTime.toString()
        else
            dayInfoEstimatedTime.progress = "0"

        viewModel.taskListMutableLiveData.observe(viewLifecycleOwner, getTask)
        viewModel.durationMutableLiveData.observe(viewLifecycleOwner, getDuration)
        dayInfoRecycler.setHasFixedSize(true)
        dayInfoRecycler.addItemDecoration(decor)
        dayInfoRecycler.layoutManager = LinearLayoutManager(context)
        dayInfoDate.text = DateHelper.getDate(date)
        dayInfoUser.text = "for " + assigneeDi.firstName + " " + assigneeDi.lastName
    }

    private val getTask = Observer { taskList: List<PayloadTask> ->
        this.taskList = taskList as MutableList<PayloadTask>
        val adapter = DayInfoTaskAdapter(taskList, context!!, assigneeDi, durations)
        dayInfoRecycler.adapter = adapter
//        if (taskList.isNotEmpty())
//        for (task in taskList) {
//            if (task.duration % 60 == 0)
//                dayInfoEstimatedTime.progress = (task.duration / 60).toString()
//            else {
//                var s = df.format(task.duration / 60.0f.toDouble())
//                s = s.replace(',', '.')
//                dayInfoEstimatedTime.progress = s
//            }
//        }
//        else { dayInfoEstimatedTime.progress = "0" }
    }

    private val getDuration = Observer { durations: List<PayloadDurationActual> ->
        for (duration in durations) {
            spentTime += duration.value
        }

        if (spentTime % 60 == 0)
            dayInfoSpentTime.progress = (spentTime / 60).toString()
        else {
            var s = df.format(spentTime / 60.0f.toDouble())
            s = s.replace(',', '.')
            dayInfoSpentTime.progress = s
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)
}
