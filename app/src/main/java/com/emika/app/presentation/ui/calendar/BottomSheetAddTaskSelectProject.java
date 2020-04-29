package com.emika.app.presentation.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.adapter.calendar.ProjectAdapter;
import com.emika.app.presentation.adapter.calendar.SectionAdapter;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BottomSheetAddTaskSelectProject extends BottomSheetDialogFragment implements ProjectsCallback {

    private static final String TAG = "BottomSheetAddTaskSelec";
    @Inject
    Project projectDi;
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
    private CalendarRepository repository;
    @Inject
    ProjectsDi projectsDagger;
    private Observer<List<PayloadProject>> setProjects = projects -> {
        Log.d(TAG, ":  projects bootm sheet" + projects.size());
        projectAdapter = new ProjectAdapter(projects, mViewModel, task);
        projectRecycler.setAdapter(projectAdapter);
    };

    private Observer<List<PayloadSection>> setSection = sections -> {
        Log.d(TAG, ": section bottom sheet " + sections.size());
        List<PayloadSection> sectionList = new ArrayList<>();
        if (sections.size() == 0) {
            projectDi.setProjectSectionId(null);
            projectDi.setProjectSectionName(null);
        }
        for (int i = 0; i < sections.size(); i++) {
            if (projectDi.getProjectId() != null)
            if (projectDi.getProjectId().equals(sections.get(i).getProjectId()))
                sectionList.add(sections.get(i));
        }
        mViewModel.getProjectListMutableLiveData();
        sectionAdapter = new SectionAdapter(sectionList, mViewModel,  task);
        sectionRecycler.setAdapter(sectionAdapter);
    };
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
        EmikaApplication.getInstance().getComponent().inject(this);
        addTaskListViewModel = getArguments().getParcelable("addTaskViewModel");
        taskInfoViewModel = getArguments().getParcelable("taskInfoViewModel");
        task = getArguments().getParcelable("task");
        token = EmikaApplication.getInstance().getSharedPreferences().getString("token", "");
        repository = new CalendarRepository(token);
//        repository.downloadSections(this);
//        repository.downloadAllProject(this);

        projectRecycler = view.findViewById(R.id.recycler_add_task_project);
        projectRecycler.setHasFixedSize(true);
        projectRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionRecycler = view.findViewById(R.id.recycler_add_task_section);
        sectionRecycler.setHasFixedSize(true);
        sectionRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        selectProject = view.findViewById(R.id.add_task_add_project_btn);
        selectProject.setOnClickListener(this::addProject);
        converter = new Converter();
//        projectAdapter = new ProjectAdapter(projectsDagger.getProjects(), mViewModel, task);
//        projectRecycler.setAdapter(projectAdapter);
//
//        List<PayloadSection> sectionList = new ArrayList<>();
//        if (projectsDagger.getSections().size() == 0) {
//            projectDi.setProjectSectionId(null);
//            projectDi.setProjectSectionName(null);
//        }
//
//        for (int i = 0; i < projectsDagger.getSections().size(); i++) {
//            if (projectDi.getProjectId().equals(projectsDagger.getSections().get(i).getProjectId()))
//                sectionList.add(projectsDagger.getSections().get(i));
//        }
//        sectionAdapter = new SectionAdapter(sectionList, mViewModel,  task);
//        sectionRecycler.setAdapter(sectionAdapter);
    }

    private void addProject(View view) {
        if (addTaskListViewModel != null) {
            addTaskListViewModel.setEpicLinksList(new ArrayList<>());
            addTaskListViewModel.getEpicLinksMutableLiveData();
            addTaskListViewModel.getProjectMutableLiveData();
        }
        if (taskInfoViewModel != null) {
            task.setProjectId(projectDi.getProjectId());
            task.setSectionId(projectDi.getProjectSectionId());
            taskInfoViewModel.setTask(task);
            taskInfoViewModel.getEpicLinksMutableLiveData().setValue(new ArrayList<>());
            taskInfoViewModel.getTaskMutableLiveData();
            taskInfoViewModel.getProjectMutableLiveData();
            taskInfoViewModel.updateTask(task);
        }
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, new TokenViewModelFactory(token)).get(BottomSheetAddTaskSelectProjectViewModel.class);
//        mViewModel.getProjects();
//        mViewModel.getSections();

        mViewModel.getProjectListMutableLiveData().observe(getViewLifecycleOwner(), setProjects);
        mViewModel.getSectionListMutableLiveData().observe(getViewLifecycleOwner(), setSection);
    }

    @Override
    public void getProjects(List<PayloadProject> projects) {
        projectAdapter = new ProjectAdapter(projects, mViewModel, task);
        projectRecycler.setAdapter(projectAdapter);
    }

    @Override
    public void getSections(List<PayloadSection> sections) {
        List<PayloadSection> sectionList = new ArrayList<>();
        if (sections.size() == 0) {
            projectDi.setProjectSectionId(null);
            projectDi.setProjectSectionName(null);
        }
        for (int i = 0; i < sections.size(); i++) {
            if (projectDi.getProjectId() != null)
                if (projectDi.getProjectId().equals(sections.get(i).getProjectId()))
                    sectionList.add(sections.get(i));
        }
        mViewModel.getProjectListMutableLiveData();
        sectionAdapter = new SectionAdapter(sectionList, mViewModel,  task);
        sectionRecycler.setAdapter(sectionAdapter);
    }
}
