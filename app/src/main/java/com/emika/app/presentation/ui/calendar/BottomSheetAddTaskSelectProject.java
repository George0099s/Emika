package com.emika.app.presentation.ui.calendar;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Project;
import com.emika.app.presentation.adapter.calendar.ProjectAdapter;
import com.emika.app.presentation.adapter.calendar.SectionAdapter;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.inject.Inject;

public class BottomSheetAddTaskSelectProject extends BottomSheetDialogFragment{

    private static final String TAG = "BottomSheetAddTaskSelec";
    private BottomSheetAddTaskSelectProjectViewModel mViewModel;
    private RecyclerView projectRecycler, sectionRecycler;
    private ProjectAdapter projectAdapter;
    private SectionAdapter sectionAdapter;
    private String token;
    private AddTaskListViewModel addTaskListViewModel;
    private TaskInfoViewModel taskInfoViewModel;
    private Button selectProject;
    private EmikaApplication app = EmikaApplication.getInstance();
    private Converter converter;
    private PayloadTask task;
    @Inject
    Project projectDi;
    public static BottomSheetAddTaskSelectProject newInstance() {
        return new BottomSheetAddTaskSelectProject();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_task_select_project_fragment, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {
        app.getComponent().inject(this);
        addTaskListViewModel = getArguments().getParcelable("addTaskViewModel");
        taskInfoViewModel = getArguments().getParcelable("taskInfoViewModel");
        task = getArguments().getParcelable("task");
        token = getActivity().getIntent().getStringExtra("token");
        projectRecycler = view.findViewById(R.id.recycler_add_task_project);
        projectRecycler.setHasFixedSize(true);
        projectRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionRecycler = view.findViewById(R.id.recycler_add_task_section);
        sectionRecycler.setHasFixedSize(true);
        sectionRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        selectProject = view.findViewById(R.id.add_task_add_project_btn);
        selectProject.setOnClickListener(this::addProject);
        converter = new Converter();
    }

    private void addProject(View view) {
        if (addTaskListViewModel != null)
        addTaskListViewModel.getProjectMutableLiveData();
        if (taskInfoViewModel != null) {
            taskInfoViewModel.getProjectMutableLiveData();
            taskInfoViewModel.updateTask(task);
        }
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, new TokenViewModelFactory(token)).get(BottomSheetAddTaskSelectProjectViewModel.class);
        mViewModel.getProjectListMutableLiveData().observe(getViewLifecycleOwner(), setProjects);
        mViewModel.getSectionListMutableLiveData().observe(getViewLifecycleOwner(), setSection);
    }

    private Observer<List<PayloadProject>> setProjects = projects -> {
        projectAdapter = new ProjectAdapter(projects, mViewModel);
        projectRecycler.setAdapter(projectAdapter);
    };

    private Observer<List<PayloadSection>> setSection = sections -> {
        if (sections.size() == 0) {
            projectDi.setProjectSectionId(null);
            projectDi.setProjectSectionName(null);
        }
        sectionAdapter = new SectionAdapter(sections, mViewModel);
        sectionRecycler.setAdapter(sectionAdapter);
    };
}
