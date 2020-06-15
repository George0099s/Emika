package com.emika.app.presentation.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.callback.CreateCompanyCallback;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.networkManager.auth.CompanyNetworkManager;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.auth.CreateAccountViewModel;

public class CreateCompany extends Fragment implements CreateCompanyCallback, TokenCallback {
    private static final String TAG = "CreateCompany";
    private RadioGroup workspaceRadioGroup, teamSizeRadioGroup;
    private TextView teamSizeTitle, joinCompany;
    private Button createCompany;
    private EditText companyName;
    private CreateCompanyCallback callback;
    private CompanyNetworkManager networkManager;
    private String token;
    private FragmentManager fm;
    private TextView logout;
    private CreateAccountViewModel viewModel;
    private TokenDbManager tokenDbManager;
    private SharedPreferences sharedPreferences;
    private EmikaApplication emikaApplication;
    private StartActivityViewModel startActivityViewModel;

    public static CreateCompany newInstance() {
        return new CreateCompany();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_company_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        emikaApplication = EmikaApplication.instance;
        sharedPreferences = emikaApplication.getSharedPreferences();
        token =sharedPreferences.getString("token", "");
        networkManager = new CompanyNetworkManager(token);
        networkManager.setToken(token);
        startActivityViewModel = new ViewModelProvider(getActivity().getViewModelStore(), new TokenViewModelFactory(token)).get(StartActivityViewModel.class);
        fm = getParentFragmentManager();
        logout = view.findViewById(R.id.create_company_log_out);
        logout.setOnClickListener(this::logout);
        companyName = view.findViewById(R.id.company_name);
        teamSizeTitle = view.findViewById(R.id.team_size_title);
        joinCompany = view.findViewById(R.id.create_company_waiting_to_join);
        joinCompany.setOnClickListener(this::onClick);
        workspaceRadioGroup = view.findViewById(R.id.workspace_radio_group);
        workspaceRadioGroup.setOnCheckedChangeListener(workspaceRadioGroupListener);
        teamSizeRadioGroup = view.findViewById(R.id.team_size_radio_group);
        teamSizeRadioGroup.setOnCheckedChangeListener(teamSizeRadioGroupListener);
        createCompany = view.findViewById(R.id.create_company_next_btn);
        createCompany.setOnClickListener(this::onClick);
    }

    private void logout(View view) {
        AuthNetworkManager authNetworkManager = new AuthNetworkManager();
        authNetworkManager.logOut();
        authNetworkManager.createToken(this);
        fm.beginTransaction().replace(R.id.auth_container, new AuthFragment()).commit();
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_company_next_btn:
            networkManager.setName(companyName.getText().toString());
            networkManager.createCompany(this);
                break;
            case R.id.create_company_waiting_to_join:
            fm.beginTransaction().replace(R.id.auth_container, new JoinCompanyFragment()).commit();
        }
    }


    private RadioGroup.OnCheckedChangeListener workspaceRadioGroupListener = (group, checkedId) -> {
        switch (checkedId){
            case R.id.personal_workspace:
                teamSizeRadioGroup.setVisibility(View.GONE);
                teamSizeTitle.setVisibility(View.GONE);
                networkManager.setSize("");
                break;
            case R.id.team_workspace:
                teamSizeRadioGroup.setVisibility(View.VISIBLE);
                teamSizeTitle.setVisibility(View.VISIBLE);
                break;
        }
    };

    private RadioGroup.OnCheckedChangeListener teamSizeRadioGroupListener = (group, checkedId) -> {
        switch (checkedId){
            case R.id.under_20:
            networkManager.setSize(getString(R.string.under_20));
                break;
            case R.id.under_100:
            networkManager.setSize(getString(R.string.under_100));
                break;
            case R.id.under_500:
            networkManager.setSize(getString(R.string.under_500));
                break;
            case R.id.over_500:
            networkManager.setSize(getString(R.string.over_500));
                break;
        }
    };

    @Override
    public void callbackModelAuth(ModelAuth modelAuth) {
        if (modelAuth.getOk() &&  modelAuth.getPayload()) {
            Toast.makeText(getContext(), "Company created", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("token", token);
            sharedPreferences.edit().putBoolean("logged in", true).apply();
            startActivityViewModel.fetchAllData();
            startActivity(intent);
        } else
            Toast.makeText(getContext(), modelAuth.getError(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getToken(String token) {
       this.token = token;
    }
}
