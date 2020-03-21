package com.emika.app.presentation.adapter.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.network.pojo.member.PayloadShortMember;

import java.util.List;

public class SelectCurrentUserAdapter extends RecyclerView.Adapter<SelectCurrentUserAdapter.MemberViewHolder> {
    private List<PayloadShortMember> memberList;
    private Context context;


    public SelectCurrentUserAdapter(List<PayloadShortMember> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }


    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new SelectCurrentUserAdapter.MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        if (memberList.size() != 0) {

            PayloadShortMember member = memberList.get(position);
            if (!member.getId().equals("emika")) {
                holder.memberName.setText(String.format("%s %s", member.getFirstName(), member.getLastName()));
                holder.memberJobTitle.setText(member.getJobTitle());
                if (member.getPictureUrl() != null)
                    Glide.with(context).load(member.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(holder.memberImg);
                else
                    Glide.with(context).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(holder.memberImg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView memberImg;
        private TextView memberName, memberJobTitle;


        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberImg = itemView.findViewById(R.id.member_img);
            memberJobTitle = itemView.findViewById(R.id.member_job_title);
            memberName = itemView.findViewById(R.id.member_name);
        }
    }
}
