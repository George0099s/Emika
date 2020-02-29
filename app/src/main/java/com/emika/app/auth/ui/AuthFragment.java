package com.emika.app.auth.ui;

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
import com.emika.app.auth.data.AuthRepository;
import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.PayloadEmail;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;
import com.emika.app.auth.utils.AuthViewModelFactory;
import com.emika.app.auth.viewmodel.AuthViewModel;
import com.emika.app.main.MainActivity;

public class AuthFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";
    private AuthViewModel mViewModel;
    private TextView title;
    private ImageView logo;
    private EditText email, password;
    private String token;
    private AuthRepository authRepository;
    private Button next;
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
        next.setOnClickListener(this::checkEmail);
        token = getActivity().getIntent().getStringExtra("token");
    }

    private void checkEmail(View view) {
        authRepository = new AuthRepository(token, password.getText().toString(), email.getText().toString() );
        mViewModel = new ViewModelProvider(this, new AuthViewModelFactory(authRepository)).get(AuthViewModel.class);
//        authRepository.getAuthPayloadEmailMutableLiveData();
        mViewModel.init();
//        mViewModel.init();
//        mViewModel.getTokenPayloadMutableLiveData();
//        mViewModel.getTokenPayloadMutableLiveData().observe();
//        authRepository.registerCallBack(mViewModel);
        mViewModel.getTokenPayloadMutableLiveData().observe(getViewLifecycleOwner(), checkEmail);
    }

    private void signIn(View view) {
        mViewModel.setPassword(password.getText().toString());
        mViewModel.signIn();
        mViewModel.getAuthModelAuthMutableLiveData().observe(getViewLifecycleOwner(), logIn);
    }

    private void signUp(View view) {
        mViewModel.setPassword(password.getText().toString());
        authRepository = new AuthRepository(token, password.getText().toString(), email.getText().toString() );
        mViewModel = new ViewModelProvider(this, new AuthViewModelFactory(authRepository)).get(AuthViewModel.class);
        mViewModel.signUp();
        mViewModel.getSignUpMutableLiveData().observe(getViewLifecycleOwner(), signUp);
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
        } else {
            email.setEnabled(false);
            password.setVisibility(View.VISIBLE);
            password.requestFocus();
            title.setText(getResources().getText(R.string.create_new_account));
            next.setOnClickListener(this::signUp);
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
