package com.emika.app.presentation.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.domain.repository.auth.CreateUserRepository;
import com.emika.app.presentation.utils.viewModelFactory.auth.CreateAccountViewModelFactory;
import com.emika.app.presentation.viewmodel.auth.CreateAccountViewModel;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;

public class CreateAccountFragment extends Fragment{
    private static final String TAG = "CreateAccountFragment";


    private CreateAccountViewModel mViewModel;
    private EditText firstName, lastName;
    private Button next, back;
    private String token, jobTitle, bio;
    private CreateUserRepository createUserRepository;
    private CreateAccountViewModel viewModel;
    private EmikaApplication emikaApplication;
    private AppDatabase db;
    private FragmentManager fm;
    private TextView logout;
    private SharedPreferences sharedPreferences;
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
        emikaApplication = EmikaApplication.getInstance();
        sharedPreferences = EmikaApplication.getInstance().getSharedPreferences();
        token = sharedPreferences.getString("token", "");
        db = emikaApplication.getDatabase();
        fm = getParentFragmentManager();
        logout = view.findViewById(R.id.create_account_log_out);
        logout.setOnClickListener(this::logOut);
        firstName = view.findViewById(R.id.create_first_name);
        lastName = view.findViewById(R.id.create_last_name);
        back = view.findViewById(R.id.create_back_btn);
        back.setOnClickListener(this::onclick);
        next = view.findViewById(R.id.create_next_btn);
        next.setOnClickListener(this::onclick);
        viewModel = new ViewModelProvider(this, new CreateAccountViewModelFactory(token, firstName.getText().toString(), lastName.getText().toString(), jobTitle, bio)).get(CreateAccountViewModel.class);
    }

    private void logOut(View view) {
        viewModel.logOut();
        fm.beginTransaction().replace(R.id.auth_container, new AuthFragment()).commit();
    }

    private void onclick(View view) {
        switch (view.getId()){
            case R.id.create_back_btn:
                break;
            case R.id.create_next_btn:
                if (firstName.getText().toString().isEmpty()){
                    firstName.requestFocus();
                    firstName.setError("Missing first name");
                }
                if (lastName.getText().toString().isEmpty()){
                    lastName.requestFocus();
                    lastName.setError("Missing last name");
                }
                if (!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()) {
                    viewModel = new ViewModelProvider(this, new CreateAccountViewModelFactory(token, firstName.getText().toString(), lastName.getText().toString(), jobTitle, bio)).get(CreateAccountViewModel.class);
                    viewModel.setFirstName(firstName.getText().toString());
                    viewModel.setLastName(lastName.getText().toString());
                    viewModel.getUpdatedUserLiveData().observe(this, updateUserInfo);
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private Observer<Payload> observeUserLiveData = user -> {
        Toast.makeText(getContext(), user.getFirstName(), Toast.LENGTH_SHORT).show();
    };

    private Observer<UpdateUserModel> updateUserInfo = updateUserModel -> {
        if (updateUserModel.getOk()){
            viewModel.getUserLiveData().observe(this, observeUserLiveData);
            fm.beginTransaction().replace(R.id.auth_container, new JoinOrCreateCompany()).commit();
        } else {
            Toast.makeText(emikaApplication, updateUserModel.getError(), Toast.LENGTH_SHORT).show();
        }
    };
}
