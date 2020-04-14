package com.emika.app.presentation.adapter.profile;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.user.Contact;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.ui.profile.MemberActivity;

import java.util.ArrayList;
import java.util.List;

public class AllMembersAdapter extends RecyclerView.Adapter<AllMembersAdapter.ViewHolder> {


    List<PayloadShortMember> members;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private MembersContactAdapter adapter;
    private long mLastClickTime = 0;

    public AllMembersAdapter(List<PayloadShortMember> members, Context context) {
        this.members = members;
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).getId().equals("emika"))
                this.members.remove(i);
        }
        this.context = context;
    }

    @NonNull
    @Override
    public AllMembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_all_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMembersAdapter.ViewHolder holder, int position) {
        PayloadShortMember member = members.get(position);

        if (!member.getId().equals("emika")) {
                List<Contact> memberContacts = new ArrayList<>();
            if (member.getContacts().size() > 3)
                for (int i = 0; i < 3 ; i++) {
                memberContacts.add(member.getContacts().get(i));
                    adapter = new MembersContactAdapter(memberContacts, context);
                }
            else adapter = new MembersContactAdapter(member.getContacts(), context);

            holder.name.setText(String.format("%s %s", member.getFirstName(), member.getLastName()));
                holder.job.setText(member.getJobTitle());
                linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.contactRecycler.setHasFixedSize(true);
                holder.contactRecycler.setLayoutManager(linearLayoutManager);

                holder.contactRecycler.setAdapter(adapter);
                if (member.getPictureUrl() != null)
                    Glide.with(context).load(member.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(holder.memberImg);
                else
                    Glide.with(context).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(holder.memberImg);
                holder.item.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    } else {
                        Intent intent = new Intent(context, MemberActivity.class);
                        intent.putExtra("memberId", member.getId());
                        context.startActivity(intent);
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                });
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView memberImg;
        private TextView name, job;
        private RecyclerView contactRecycler;
        private ConstraintLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberImg = itemView.findViewById(R.id.item_member_img);
            job = itemView.findViewById(R.id.item_member_job);
            name = itemView.findViewById(R.id.item_member_name);
            contactRecycler = itemView.findViewById(R.id.item_member_contact_recycler);
            item = itemView.findViewById(R.id.item_member_profile);
        }

    }
}
