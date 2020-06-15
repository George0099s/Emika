package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.Project
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel
import com.emika.app.presentation.viewmodel.calendar.BottomSheetSelectEpicLinksViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class BottomSheetSelectEpicLinks : BottomSheetDialogFragment() {
    @JvmField
    @Inject
    var projectDi: Project? = null
    private var mViewModel: BottomSheetSelectEpicLinksViewModel? = null
    private var epicLinksRecycler: RecyclerView? = null
    private var adapter: EpicLinksAdapter? = null
    private var addTaskListViewModel: AddTaskListViewModel? = null
    private var taskInfoViewModel: TaskInfoViewModel? = null
    private var token: String? = null
    private var addEpicLinks: Button? = null
    private var task: PayloadTask? = null
    private val getEpicLinks = Observer { epicLinks: List<PayloadEpicLinks> ->
        adapter = EpicLinksAdapter(epicLinks, context!!, addTaskListViewModel, taskInfoViewModel)
        epicLinksRecycler!!.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_epic_links_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        EmikaApplication.instance.component?.inject(this)
        token = activity!!.intent.getStringExtra("token")
        addEpicLinks = view.findViewById(R.id.bottom_sheet_add_epic_links)
        addEpicLinks!!.setOnClickListener { view: View -> addEpicLinks(view) }
        addTaskListViewModel = arguments!!.getParcelable("addTaskViewModel")
        taskInfoViewModel = arguments!!.getParcelable("taskInfoViewModel")
        task = arguments!!.getParcelable("task")
        epicLinksRecycler = view.findViewById(R.id.bottom_sheet_recycler_select_epic_links)
        epicLinksRecycler!!.setHasFixedSize(true)
        epicLinksRecycler!!.layoutManager = LinearLayoutManager(context)
        mViewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(BottomSheetSelectEpicLinksViewModel::class.java)
        mViewModel!!.epicLinksMutableLiveData.observe(viewLifecycleOwner, getEpicLinks)
    }

    private fun addEpicLinks(view: View) {
        if (addTaskListViewModel != null)
            addTaskListViewModel!!.epicLinksMutableLiveData
        if (taskInfoViewModel != null) {
            taskInfoViewModel!!.updateTask(task)
        }
        dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
        // TODO: Use the ViewModel
    }

    companion object {
        private const val TAG = "BottomSheetSelectEpicLi"
        fun newInstance(): BottomSheetSelectEpicLinks {
            return BottomSheetSelectEpicLinks()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)

}