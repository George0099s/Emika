package com.emika.app.presentation.ui.calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.di.Assignee;
import com.emika.app.presentation.adapter.calendar.SelectCurrentUserAdapter;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BottomSheetCalendarSelectUser extends BottomSheetDialogFragment {

    private RecyclerView memberRecycler;
    private SelectCurrentUserAdapter adapter;
    private List<PayloadShortMember> memberList = new ArrayList<>();
    private String token;
    private String from = null;
    private static final String TAG = "BottomSheetSelectUser";
    private AddTaskListViewModel addTaskListViewModel;
    public static BottomSheetCalendarSelectUser newInstance() {
        return new BottomSheetCalendarSelectUser();
    }
    private EmikaApplication app = EmikaApplication.getInstance();
    @Inject
    Assignee assignee;
    private CalendarViewModel calendarViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_fragment, container, false);
        setStyle(BottomSheetCalendarSelectUser.STYLE_NORMAL, R.style.BottomSheetStyleDialogTheme);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView(view);
        return view;
    }

    private void initView(View view) {
        app.getComponent().inject(this);
        memberList = getArguments().getParcelableArrayList("members");
        from = getArguments().getString("from");
        calendarViewModel = getArguments().getParcelable("viewModel");
        addTaskListViewModel = getArguments().getParcelable("addTaskViewModel");
        memberRecycler = view.findViewById(R.id.bottom_sheet_recycler_select_user);
        memberRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        memberRecycler.setHasFixedSize(true);
        adapter = new SelectCurrentUserAdapter(memberList, getContext(), calendarViewModel, addTaskListViewModel, this);
        memberRecycler.setAdapter(adapter);
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_dialog_fragment, null);
        setStyle(BottomSheetCalendarSelectUser.STYLE_NO_INPUT, R.style.BottomSheetStyleDialogTheme);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setContentView(contentView);
    }


}
