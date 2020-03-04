package com.emika.app.presentation.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.network.callback.CreateCompanyCallback;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.networkManager.CompanyNetworkManager;

public class CreateCompany extends Fragment implements CreateCompanyCallback {
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
        fm = getParentFragmentManager();
        token = getActivity().getIntent().getStringExtra("token");
        networkManager = new CompanyNetworkManager(token);
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

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_company_next_btn:
            networkManager.setName(companyName.getText().toString());
            networkManager.updateUserInfo(this);
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
        if (modelAuth.getOk() &&  modelAuth.getPayload())
            Toast.makeText(getContext(), "Company created", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), modelAuth.getError(), Toast.LENGTH_SHORT).show();
    }
}
