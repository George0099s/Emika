package com.emika.app.presentation.ui.auth;

import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;

public class JoinOrCreateCompany extends Fragment {


    public static JoinOrCreateCompany newInstance() {
        return new JoinOrCreateCompany();
    }

    private LinearLayout joinExisting, createNewWorkSpace;
    private String token;
    private FragmentManager fm;
    private TextView logout;
    private ImageView emika;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_or_create_company_fragment, container, false);
        initViews(view);
        return view;

    }

    private void initViews(View view) {
        token = getActivity().getIntent().getStringExtra("token");
        fm = getParentFragmentManager();
        joinExisting = view.findViewById(R.id.join_existing_company);
        joinExisting.setOnClickListener(this::onClick);
        createNewWorkSpace = view.findViewById(R.id.create_new_workspace);
        createNewWorkSpace.setOnClickListener(this::onClick);
        emika = view.findViewById(R.id.emika_gif);
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(emika);
    }

    private void onClick(View view) {
        switch (view.getId()){
            case R.id.create_new_workspace:
                fm.beginTransaction().replace(R.id.auth_container, new CreateCompany()).commit();
                break;
            case R.id.join_existing_company:
                fm.beginTransaction().replace(R.id.auth_container, new JoinCompanyFragment()).commit();
                break;
        }
    }
}
