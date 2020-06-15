package com.emika.app.presentation.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.presentation.adapter.profile.AllMembersAdapter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.profile.AllMembersViewModel;

import java.util.List;

public class AllMembersActivity extends AppCompatActivity {

    private RecyclerView allMembersRecycler;
    private TextView back;
    private AllMembersAdapter adapter;
    private AllMembersViewModel viewModel;
    private String token;
    private String memberId;
    private PackageManager packageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        initView();
    }

    private void initView() {
        packageManager = getPackageManager();
        token = EmikaApplication.instance.getSharedPreferences().getString("token", "");
        memberId = getIntent().getStringExtra("memberId");
        back = findViewById(R.id.all_members_back);
        back.setOnClickListener(this::onBackPressed);
        allMembersRecycler = findViewById(R.id.all_members_recycler);
        allMembersRecycler.setHasFixedSize(true);
        allMembersRecycler.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AllMembersViewModel.class);
        viewModel.getMemberMutableLiveData().observe(this, getMembers);
    }

    private void onBackPressed(View view) {
        super.onBackPressed();
    }

    private Observer<List<PayloadShortMember>> getMembers = members -> {
        adapter = new AllMembersAdapter(members, this, memberId, packageManager);
        allMembersRecycler.setAdapter(adapter);
    };
}
