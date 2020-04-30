package com.emika.app.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Assignee
import com.emika.app.di.User
import com.emika.app.features.calendar.MemberItemDecoration
import com.emika.app.presentation.adapter.calendar.DayInfoTaskAdapter
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.emika.app.presentation.viewmodel.calendar.DayInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_day_info.*
import java.text.DecimalFormat
import javax.inject.Inject

class BottomSheetDayInfo : BottomSheetDialogFragment() {
    @Inject
    lateinit var assigneeDi: Assignee
    @Inject
    lateinit var userDi: User
    private val token: String? = EmikaApplication.instance.sharedPreferences.getString("token", "")
    private val viewModel: DayInfoViewModel = DayInfoViewModel(token!!)
    private val decor: MemberItemDecoration = MemberItemDecoration()
    private var date: String? = null
    private var estimatedTime: Int = 0
    private var spentTime: Int = 0
    private var taskList: MutableList<PayloadTask> = arrayListOf()
    private var durations: List<PayloadDurationActual> = arrayListOf()
    private val df = DecimalFormat("#.#")
    fun newInstance(): BottomSheetSelectEpicLinks? {
        return BottomSheetSelectEpicLinks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_day_info, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        EmikaApplication.instance.component.inject(this)
        date = arguments!!.getString("date")
        estimatedTime = arguments!!.getInt("estimatedTime")
        spentTime = arguments!!.getInt("spentTime")
//        viewModel.setFirstRun(false)
        viewModel.getAllDbTaskByAssignee(assigneeDi.id)
        viewModel.getAllDbDurationByAssignee(assigneeDi.id, date)
//        viewModel.getDurations()
        viewModel.taskListMutableLiveData.observe(viewLifecycleOwner, getTask)
        viewModel.durationMutableLiveData.observe(viewLifecycleOwner, getDuration)

        dayInfoRecycler.setHasFixedSize(true)
        dayInfoRecycler.addItemDecoration(decor)
        dayInfoRecycler.layoutManager = LinearLayoutManager(context)
        dayInfoDate.text = DateHelper.getDate(date)
        dayInfoUser.text = "for " + assigneeDi.firstName + " " + assigneeDi.lastName

    }

    private val getTask = Observer { taskList: List<PayloadTask> ->
        val tasks: MutableList<PayloadTask> = arrayListOf()
        this.taskList = taskList as MutableList<PayloadTask>
        for (duration in durations) {
            for (task in taskList)
                if (task.id == duration.taskId)
                    tasks.add(task)
        }
        viewModel.durationMutableLiveData
        val adapter = DayInfoTaskAdapter(tasks, context!!, assigneeDi, durations)
        dayInfoRecycler.adapter = adapter
    }

    private val getDuration = Observer { durations: List<PayloadDurationActual> ->
        this.durations = durations

//        for (duration in durations) {
//            spentTime += duration.value
//        }
        viewModel.taskListMutableLiveData

        if (estimatedTime % 60 == 0)
            dayInfoEstimatedTime.progress = (estimatedTime / 60).toString()
        else {
            var s = df.format(estimatedTime / 60.0f.toDouble())
            s = s.replace(',', '.')
            dayInfoEstimatedTime.progress = s
        }
        if (spentTime % 60 == 0)
            dayInfoSpentTime.progress = (spentTime / 60).toString()
        else {
            var s = df.format(spentTime / 60.0f.toDouble())
            s = s.replace(',', '.')
            dayInfoSpentTime.progress = s
        }
    }
}
