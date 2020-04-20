package com.emika.app.presentation.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.User
import com.emika.app.features.calendar.MemberItemDecoration
import com.emika.app.presentation.adapter.calendar.DayInfoTaskAdapter
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_day_info.*
import javax.inject.Inject

class BottomSheetDayInfo : BottomSheetDialogFragment(){
    @Inject
    lateinit var userDi: User
    private val token: String? = EmikaApplication.instance.sharedPreferences.getString("token", "")
    private val viewModel: CalendarViewModel = CalendarViewModel(token)
    private var taskList: MutableList<PayloadTask> = arrayListOf()
    private var durationList: MutableList<PayloadDurationActual> = arrayListOf()
    private var firstRun: Boolean = true
    private val decor: MemberItemDecoration = MemberItemDecoration()
    private var date: String? = null
    private var estimatedTime: Int = 0
    private var spentTime: Int = 0

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
        spentTime = arguments!!.getInt("spentTime")
        estimatedTime = arguments!!.getInt("estimatedTime")
        if (firstRun)
        viewModel.geTasks().observe(viewLifecycleOwner, getTask)
        viewModel.durationMutableLiveData.observe(viewLifecycleOwner, getDuration)

        dayInfoRecycler.setHasFixedSize(true)
        dayInfoRecycler.addItemDecoration(decor)
        dayInfoRecycler.layoutManager = LinearLayoutManager(context)
        dayInfoDate.text = DateHelper.getDate(date)
        dayInfoUser.text = "for " + userDi.firstName + " " + userDi.lastName

    }

    private val getTask = Observer { taskList: List<PayloadTask> ->
        firstRun = false
        for (duration in durationList) {
            for (task in taskList) {
                if (duration.taskId == task.id)
                    this.taskList.add(task)
            }
        }
        val adapter = DayInfoTaskAdapter(this.taskList, context!!, userDi)
        dayInfoRecycler.adapter = adapter
    }

    private val getDuration = Observer { durations: List<PayloadDurationActual> ->
        val userId = arguments!!.getString("id")
        for (duration in durations) {
            if (duration.date == date && duration.person == userId)
                durationList.add(duration)
        }
        if(!firstRun)
        viewModel.geTasks()
        dayInfoEstimatedTime.progress = estimatedTime
        dayInfoSpentTime.progress = spentTime
    }
}
