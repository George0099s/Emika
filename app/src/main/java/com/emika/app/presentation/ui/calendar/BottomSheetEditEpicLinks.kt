package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.emika.app.R
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.db.entity.SectionEntity
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.di.ProjectsDi
import com.emika.app.presentation.adapter.ItemTouchHelper.EpicLinkTouchHelperCallback
import com.emika.app.presentation.adapter.ItemTouchHelper.SectionTouchHelperCallback
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter
import com.emika.app.presentation.adapter.calendar.SectionAdapter
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_project.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class BottomSheetEditEpicLinks : DialogFragment() {

    private lateinit var adapter: EpicLinksAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var project: PayloadProject
    private lateinit var addEpicLink: ImageButton
    private lateinit var addEpicLinkText: EditText
    private val converter = Converter()
    private lateinit var viewModel: AddProjectViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_edit_epic_links, container, false)
        dialog!!.window!!.statusBarColor = resources.getColor(R.color.green)
        initView(view)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.window!!.setWindowAnimations(R.style.DialogAnimation)
    }

    private fun initView(view: View) {
        viewModel = arguments!!.getParcelable("viewModel")!!
        project = arguments!!.getParcelable("project")!!
        viewModel.getEpicLinksByProjectId(project.id).observe(viewLifecycleOwner, getEpicLinks())
        recycler = view.findViewById<RecyclerView>(R.id.bottom_sheet_recycler_edit_epic_links).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        addEpicLink = view.findViewById(R.id.add_epic_link_btn)
        addEpicLink.setOnClickListener {
                createEpicLink()
        }
        addEpicLinkText = view.findViewById(R.id.bottom_sheet_add_epic_link)
        addEpicLinkText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                createEpicLink()
                true
            } else false
        }
        adapter = EpicLinksAdapter(ArrayList() ,context!!, null, null, viewModel)
        recycler.adapter = adapter
        val callback: ItemTouchHelper.Callback = EpicLinkTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recycler)
        addEpicLinkText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(ed: Editable?) {
                addEpicLink.isEnabled = ed!!.isNotEmpty()
            }

            override fun beforeTextChanged(ed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addEpicLink.isEnabled = ed!!.isNotEmpty()
            }

            override fun onTextChanged(ed: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun getEpicLinks() = Observer<List<EpicLinksEntity>>{
        adapter.setList(converter.fromEpicLinksEntityToPayloadEpicLinks(it))
        adapter.notifyDataSetChanged()

    }

    private fun createEpicLink() {
        if (addEpicLinkText.text.isNotEmpty()) {
            viewModel.createEpicLink(addEpicLinkText.text.toString(), "active", "0", project.id)
            addEpicLinkText.setText("")
        } else {
            addEpicLinkText.requestFocus()
            addEpicLinkText.error = "missing section name"
        }

    }

    //    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleDialogTheme)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)

    }
}
