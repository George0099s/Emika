package com.emika.app.presentation.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.member.PayloadMember;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.di.CompanyDi;
import com.emika.app.presentation.adapter.profile.AllMembersAdapter;
import com.emika.app.presentation.adapter.profile.ProfileContactAdapter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.profile.MemberViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MemberActivity extends AppCompatActivity {
    private static final String TAG = "MemberActivity";
    private String token;
    private ImageView memberImg, companyImg;
    private TextView back;
    private TextView memberName, memberJob, allMembers, companyName, companyMembers;
    private PayloadMember member = new PayloadMember();
    private List<PayloadShortMember> memberList = new ArrayList<>();
    private RecyclerView contactRecycler, leadRecycler, coWorkerRecycler;
    private AllMembersAdapter leadAdapter, coWorkersAdapter;
    private ProfileContactAdapter contactAdapter;
    private MemberViewModel viewModel;
    private String memberId;
    @Inject
    CompanyDi companyDi;

    private Observer<List<PayloadShortMember>> getMembers = members -> {
        memberList = members;
        companyMembers.setText(String.format("%d %s", memberList.size(), getResources().getString(R.string.members_string)));
        setLeaders();
        setCoworkers();
    };
    private Observer<PayloadMember> getMemberInfo = member -> {
        this.member = member;

        viewModel.getMembersMutableLiveData();
        memberName.setText(String.format("%s %s", member.getFirstName(), member.getLastName()));
        memberJob.setText(member.getJobTitle());
        contactAdapter = new ProfileContactAdapter(member.getContacts(), this);
        contactRecycler.setAdapter(contactAdapter);
        if (member.getPictureUrl() != null)
            Glide.with(this).load(member.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(memberImg);
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(memberImg);

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();
    }

    private void initView() {
        EmikaApplication.getInstance().getComponent().inject(this);
        token = EmikaApplication.getInstance().getSharedPreferences().getString("token", "");
        memberId = getIntent().getStringExtra("memberId");
        memberImg = findViewById(R.id.member_profile_user_img);
        memberName = findViewById(R.id.member_profile_user_name);
        memberJob = findViewById(R.id.member_profile_user_job_title);
        back = findViewById(R.id.member_back);
        back.setOnClickListener(this::back);
        allMembers = findViewById(R.id.member_profile_see_all_members);
        allMembers.setOnClickListener(this::seeAllMembers);
        contactRecycler = findViewById(R.id.member_profile_contacts_recycler);
        contactRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactRecycler.setHasFixedSize(true);
        leadRecycler = findViewById(R.id.member_profile_lead_recycler);
        leadRecycler.setLayoutManager(new LinearLayoutManager(this));
        leadRecycler.setHasFixedSize(true);
        coWorkerRecycler = findViewById(R.id.member_profile_coworker_recycler);
        coWorkerRecycler.setLayoutManager(new LinearLayoutManager(this));
        coWorkerRecycler.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(MemberViewModel.class);
        viewModel.getMemberMutableLiveData(memberId).observe(this, getMemberInfo);
        viewModel.getMembersMutableLiveData().observe(this, getMembers);

        companyName = findViewById(R.id.member_company_name);
        companyMembers = findViewById(R.id.member_company_members);
        companyImg = findViewById(R.id.member_company_img);

        companyName.setText(companyDi.getName());
        Glide.with(this).load(companyDi.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(companyImg);

    }

    private void back(View view) {
        super.onBackPressed();
    }

    private void seeAllMembers(View view) {
        Intent intent = new Intent(this, AllMembersActivity.class);
        intent.putExtra("memberId", memberId);
        startActivity(intent);
    }

    private void setLeaders() {
        List<PayloadShortMember> leaders = new ArrayList<>();
        for (PayloadShortMember leader : memberList) {
            if (member.getExtraLeaders().contains(leader.getId())) {
                leaders.add(leader);
            }
        }
        leadAdapter = new AllMembersAdapter(leaders, this, memberId);
        leadRecycler.setAdapter(leadAdapter);
    }

    private void setCoworkers() {
        List<PayloadShortMember> coWorkers = new ArrayList<>();
        for (PayloadShortMember coWorker : memberList) {
            if (member.getExtraCoworkers().contains(coWorker.getId()))
                coWorkers.add(coWorker);
        }

        coWorkersAdapter = new AllMembersAdapter(coWorkers, this, memberId);
        coWorkerRecycler.setAdapter(coWorkersAdapter);
    }


}
