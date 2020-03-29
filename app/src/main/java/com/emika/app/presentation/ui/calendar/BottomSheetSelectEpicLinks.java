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
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetSelectEpicLinksViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BottomSheetSelectEpicLinks extends BottomSheetDialogFragment {

    private BottomSheetSelectEpicLinksViewModel mViewModel;
    private RecyclerView epicLinksRecycler;
    private EpicLinksAdapter adapter;
    private AddTaskListViewModel addTaskListViewModel;
    private static final String TAG = "BottomSheetSelectEpicLi";
    private String token;
    private Button addEpicLinks;
    @Inject
    Project projectDi;

    public static BottomSheetSelectEpicLinks newInstance() {
        return new BottomSheetSelectEpicLinks();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_epic_links_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        EmikaApplication.getInstance().getComponent().inject(this);
        token = getActivity().getIntent().getStringExtra("token");
        addEpicLinks = view.findViewById(R.id.bottom_sheet_add_epic_links);
        addEpicLinks.setOnClickListener(this::addEpicLinks);
        addTaskListViewModel = getArguments().getParcelable("addTaskViewModel");
        epicLinksRecycler = view.findViewById(R.id.bottom_sheet_recycler_select_epic_links);
        epicLinksRecycler.setHasFixedSize(true);
        epicLinksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mViewModel = ViewModelProviders.of(this, new TokenViewModelFactory(token)).get(BottomSheetSelectEpicLinksViewModel.class);
        mViewModel.getEpicLinksMutableLiveData().observe(getViewLifecycleOwner(), getEpicLinks);
    }

    private void addEpicLinks(View view) {
        addTaskListViewModel.getEpicLinksMutableLiveData();
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }


    private Observer<List<PayloadEpicLinks>> getEpicLinks = epicLinks -> {
        adapter = new EpicLinksAdapter(epicLinks, getContext(), addTaskListViewModel);
        epicLinksRecycler.setAdapter(adapter);
    };
}
