package com.emika.app.presentation.ui.calendar;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.emika.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetDialogViewModel mViewModel;

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.bottom_sheet_dialog_fragment, container, false);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BottomSheetDialogViewModel.class);
        // TODO: Use the ViewModel
    }

}
