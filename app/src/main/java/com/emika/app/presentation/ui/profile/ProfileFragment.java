package com.emika.app.presentation.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.presentation.adapter.profile.ProfileContactAdapter;
import com.emika.app.presentation.ui.auth.AuthActivity;
import com.emika.app.presentation.utils.NetworkState;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements TokenCallback {
    private static final String TAG = "ProfileFragment";
    @Inject
    User user;
    private UserDbManager userDbManager;
    private AuthNetworkManager networkManager;
    private SharedPreferences sharedPreferences;
    private TextView userName, jobTitle, editProfile, logOut;
    private ProfileViewModel viewModel;
    private String token;
    private ImageView userImg;
    private RecyclerView contactsRecycler;
    private ProfileContactAdapter contactAdapter;
    private EmikaApplication app = EmikaApplication.getInstance();
    private Observer<Payload> getUserInfo = user -> {
        this.user.setId(user.getId());
        this.user.setFirstName(user.getFirstName());
        this.user.setLastName(user.getLastName());
        this.user.setBio(user.getBio());
        this.user.setPictureUrl(user.getPictureUrl());
        this.user.setContacts(user.getContacts());
        //        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
//        this.user.setFirstName(user.getFirstName());
        userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        jobTitle.setText(user.getJobTitle());
        contactAdapter = new ProfileContactAdapter(user.getContacts(), getContext());
        contactsRecycler.setAdapter(contactAdapter);
        if (user.getPictureUrl() != null)
            Glide.with(this).load(user.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg);

    };


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//        getActivity().setActionBar(view.findViewById(R.id.profile_toolbar));
        initView(view);
        return view;
    }


    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_rename_task), getResources().getString(R.string.edit_account), false));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.drawable.ic_log_ou), getResources().getString(R.string.log_out), true));
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
              Intent intent = new Intent(getContext(), EditProfileActivity.class);
              startActivity(intent);
                break;
            case 2:
                logOut(getView());
                break;

        }
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title, Boolean red) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        if (red)
        sb.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.red)), 0, sb.length(), 0);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
    private void initView(View view) {
        token = getActivity().getIntent().getStringExtra("token");
        sharedPreferences = EmikaApplication.getInstance().getSharedPreferences();
        userDbManager = new UserDbManager();
        networkManager = new AuthNetworkManager(token);
        userName = view.findViewById(R.id.profile_user_name);
        jobTitle = view.findViewById(R.id.profile_user_job_title);
        editProfile = view.findViewById(R.id.profile_edit);
        userImg = view.findViewById(R.id.profile_user_img);
        logOut = view.findViewById(R.id.log_out);
        logOut.setOnClickListener(this::logOut);
        editProfile.setOnClickListener(this::editProfile);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        viewModel.setContext(getContext());
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), getUserInfo);
        app.getComponent().inject(this);
        contactsRecycler = view.findViewById(R.id.profile_contacts_recycler);
        contactsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRecycler.setHasFixedSize(true);
    }

    private void logOut(View view) {
        if (NetworkState.getInstance(getContext()).isOnline()) {
            userDbManager.dropAllTable();
            networkManager.logOut();
            sharedPreferences.edit().remove("token").apply();
            networkManager.createToken(this);

        } else {
            Toast.makeText(getContext(), "Lost internet connection", Toast.LENGTH_SHORT).show();
        }
    }

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

    @Override
    public void getToken(String token) {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra("token", token);
        SharedPreferences sharedPreferences = EmikaApplication.getInstance().getSharedPreferences();
        sharedPreferences.edit().putBoolean("logged in", false).apply();
        sharedPreferences.edit().putString("token", token).apply();
        startActivity(intent);
    }
}
