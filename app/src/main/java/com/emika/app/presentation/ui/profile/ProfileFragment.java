package com.emika.app.presentation.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    @Inject
     User user;
    private static final String TAG = "ProfileFragment";
    private TextView userName, jobTitle, editProfile;
    private ProfileViewModel viewModel;
    private String token;
    private ImageView userImg;
    private EmikaApplication app = EmikaApplication.getInstance();
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        token = getActivity().getIntent().getStringExtra("token");
        userName = view.findViewById(R.id.profile_user_name);
        jobTitle = view.findViewById(R.id.profile_user_job_title);
        editProfile = view.findViewById(R.id.profile_edit);
        userImg = view.findViewById(R.id.profile_user_img);
        editProfile.setOnClickListener(this::editProfile);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        viewModel.setContext(getContext());
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), getUserInfo);
        app.getComponent().inject(this);
    }
    private Observer<Payload> getUserInfo = user -> {
        this.user.setId(user.getId());
        this.user.setFirstName(user.getFirstName());
        this.user.setLastName(user.getLastName());
        this.user.setBio(user.getBio());
        this.user.setPictureUrl(user.getPictureUrl());
//        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
        userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        jobTitle.setText(user.getJobTitle());
        Glide.with(this).load(user.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
    };

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getUserMutableLiveData();
    }

    private void editProfile(View view) {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}
