package com.emika.app.presentation.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.SectionEntity
import com.emika.app.data.network.pojo.project.PayloadProject
import com.emika.app.data.network.pojo.project.PayloadSection
import com.emika.app.di.ProjectsDi
import com.emika.app.presentation.adapter.ItemTouchHelper.SectionTouchHelperCallback
import com.emika.app.presentation.adapter.calendar.SectionAdapter
import com.emika.app.presentation.utils.Converter
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class BottomSheetEditSections : DialogFragment() {
    @Inject
    lateinit var sectionDi: ProjectsDi
    private lateinit var adapter: SectionAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var project: PayloadProject
    private lateinit var addSection: ImageButton
    private lateinit var addSectionText: EditText
    private val converter = Converter()
    private var sections: MutableList<PayloadSection> = arrayListOf()

    init {
        EmikaApplication.instance.component?.inject(this)
    }

    private lateinit var viewModel: AddProjectViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bottom_sheet_edit_sections, container, false)
        dialog!!.window!!.statusBarColor = resources.getColor(R.color.green)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        viewModel = arguments!!.getParcelable("viewModel")!!
        project = arguments!!.getParcelable("project")!!
        viewModel.getSectionsByProjectId(project.id).observe(viewLifecycleOwner, getSections())
        recycler = view.findViewById<RecyclerView>(R.id.bottom_sheet_recycler_edit_section).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        adapter = SectionAdapter(ArrayList(), viewModel, context!!)
        recycler.adapter = adapter
        val callback: ItemTouchHelper.Callback = SectionTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recycler)

        addSection = view.findViewById(R.id.add_section_btn)
        addSection.setOnClickListener {
            createSection()
        }
        addSectionText = view.findViewById(R.id.bottom_sheet_add_section)
        addSectionText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                createSection()
                true
            } else false
        }


        addSectionText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(ed: Editable?) {
                addSection.isEnabled = ed!!.isNotEmpty()
            }

            override fun beforeTextChanged(ed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addSection.isEnabled = ed!!.isNotEmpty()
            }

            override fun onTextChanged(ed: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun getSections() = Observer<List<SectionEntity>> {
        adapter.setList(converter.fromListEntitySectionToPayloadSection(it))
        adapter.notifyDataSetChanged()
    }

    private fun createSection() {
        if (addSectionText.text.isNotEmpty()) {
            viewModel.createSection(addSectionText.text.toString(), "active", "0", project.id)
            addSectionText.setText("")
        } else {
            addSectionText.requestFocus()
            addSectionText.error = "missing section name"
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.window!!.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

}
