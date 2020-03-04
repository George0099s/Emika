package com.emika.app.presentation.ui;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.emika.app.R;
import com.emika.app.domain.repository.AuthRepository;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;
import com.emika.app.presentation.utils.viewModelFactory.AuthViewModelFactory;
import com.emika.app.presentation.viewmodel.AuthViewModel;

public class AuthFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";
    private AuthViewModel mViewModel;
    private TextView title;
    private ImageView logo;
    private EditText email, password;
    private String token;
    private AuthRepository authRepository;
    private Button next, back;
    private FragmentManager fm;

    public static AuthFragment newInstance() {

        return new AuthFragment();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        fm = getParentFragmentManager();
        email = view.findViewById(R.id.auth_email);
        password = view.findViewById(R.id.auth_pass);
        title = view.findViewById(R.id.auth_title);
        next = view.findViewById(R.id.auth_next_btn);
        back = view.findViewById(R.id.back_btn);
        back.setOnClickListener(this::back);
        next.setOnClickListener(this::checkEmail);
        token = getActivity().getIntent().getStringExtra("token");
    }

    private void back(View view) {
        title.setText(R.string.to_access_emika_please_login_or_sign_up);
        password.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        password.setText("");
        email.setEnabled(true);
        next.setOnClickListener(this::checkEmail);
    }

    private void checkEmail(View view) {
        mViewModel = new ViewModelProvider(this, new AuthViewModelFactory(token)).get(AuthViewModel.class);
        mViewModel.setEmail(email.getText().toString());
        mViewModel.getTokenPayloadMutableLiveData().observe(getViewLifecycleOwner(), checkEmail);
    }

    private void signIn(View view) {
        mViewModel.setPassword(password.getText().toString());
        mViewModel.getAuthModelAuthMutableLiveData().observe(getViewLifecycleOwner(), logIn);
    }

    private void signUp(View view) {
        if (password.getText().length() >= 6){
            mViewModel.setPassword(password.getText().toString());
            mViewModel.getSignUpMutableLiveData().observe(getViewLifecycleOwner(), signUp);
        } else {
            password.requestFocus();
            password.setError(getString(R.string.short_password));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private Observer<PayloadEmail> checkEmail = tokenPayload -> {
        if (tokenPayload.getExists()){
            email.setEnabled(false);
            password.setVisibility(View.VISIBLE);
            password.requestFocus();
            title.setText(getResources().getText(R.string.login_string));
            next.setOnClickListener(this::signIn);
            back.setVisibility(View.VISIBLE);
        } else {
            email.setEnabled(false);
            password.setVisibility(View.VISIBLE);
            password.requestFocus();
            title.setText(getResources().getText(R.string.create_new_account));
            next.setOnClickListener(this::signUp);
            back.setVisibility(View.VISIBLE);
        }
    };  

    private Observer<ModelAuth> logIn = auth -> {
        if (auth.getOk() && auth.getPayload()){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else {
            password.requestFocus();
            password.setError("Password is not correct");
        }
    };

    private Observer<ModelSignUp> signUp = signUpModel -> {
        if (signUpModel.getOk() && signUpModel.getPayload().getEmailSent()){
            CreateAccountFragment createAccountFragment = new CreateAccountFragment();
            fm.beginTransaction().replace(R.id.auth_container, createAccountFragment).commit();
        }
    };
}
