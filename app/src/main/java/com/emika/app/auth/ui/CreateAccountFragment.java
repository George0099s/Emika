package com.emika.app.auth.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.emika.app.R;
import com.emika.app.auth.data.RegistrationRepository;
import com.emika.app.auth.viewmodel.CreateAccountViewModel;

public class CreateAccountFragment extends Fragment {

    private CreateAccountViewModel mViewModel;
    private EditText firstName, lastName;
    private Button next;
    private RegistrationRepository registrationRepository;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        firstName = view.findViewById(R.id.create_first_name);
        lastName = view.findViewById(R.id.create_last_name);
        next = view.findViewById(R.id.create_next_btn);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProvider(this).get(CreateAccountViewModel.class);

    }

}
