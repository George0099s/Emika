package com.emika.app.presentation.adapter.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.di.Assignee;
import com.emika.app.presentation.ui.calendar.BottomSheetCalendarSelectUser;
import com.emika.app.presentation.viewmodel.calendar.AddProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SelectCurrentUserAdapter extends RecyclerView.Adapter<SelectCurrentUserAdapter.MemberViewHolder> {
    @Inject
    Assignee assignee;

    private List<PayloadShortMember> memberList = new ArrayList<>();
    private List<String> newmemberList = new ArrayList<>();
    private Context context;
    private EmikaApplication emikaApplication = EmikaApplication.instance;
    private CalendarViewModel calendarViewModel;
    private BottomSheetCalendarSelectUser bottomSheetCalendarSelectUser;
    private TaskInfoViewModel taskInfoViewModel;
    private AddTaskListViewModel addTaskListViewModel;
    private AddProjectViewModel addProjectViewModel;


    public SelectCurrentUserAdapter(List<PayloadShortMember> memberList, Context context, CalendarViewModel calendarViewModel,
                                    AddTaskListViewModel addTaskListViewModel, TaskInfoViewModel taskInfoViewModel, BottomSheetCalendarSelectUser bottomSheetCalendarSelectUser, AddProjectViewModel addProjectViewModel) {
        for (int i = 0; i < memberList.size(); i++) {
            if (!memberList.get(i).getId().equals("emika"))
                this.memberList.add(memberList.get(i));
        }
        this.context = context;
        this.calendarViewModel = calendarViewModel;
        this.bottomSheetCalendarSelectUser = bottomSheetCalendarSelectUser;
        this.addTaskListViewModel = addTaskListViewModel;
        this.taskInfoViewModel = taskInfoViewModel;
        this.addProjectViewModel = addProjectViewModel;
        if (addProjectViewModel != null)
        for (String member: addProjectViewModel.getProject().getMembers()){
            newmemberList.add(member);
        }

        emikaApplication.getComponent().inject(this);
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


                if (bottomSheetCalendarSelectUser != null) {
                    if (assignee.getId().equals(member.getId())) {
                        holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    } else {
                        holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }

                    holder.item.setOnClickListener(v -> {
                        assignee.setFirstName(member.getFirstName());
                        assignee.setLastName(member.getLastName());
                        assignee.setId(member.getId());
                        assignee.setJobTitle(member.getJobTitle());
                        assignee.setPictureUrl(member.getPictureUrl());
                        if (addTaskListViewModel == null && taskInfoViewModel == null) {
                            calendarViewModel.getAssigneeMutableLiveData();
                            calendarViewModel.getAllDbTaskByAssignee(member.getId());
                        } else if (addTaskListViewModel != null) {
                            addTaskListViewModel.getAssignee();
                        } else {
                            taskInfoViewModel.getAssigneeMutableLiveData();
                        }
                        bottomSheetCalendarSelectUser.dismiss();
                    });
                } else {
                    if (newmemberList.contains(member.getId())) {
                        holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    } else {
                        holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }

                    holder.item.setOnClickListener(v -> {
                        if (!newmemberList.contains(member.getId())) {
//                            addProjectViewModel.getMembers().add(member.getId());
                            newmemberList.add(member.getId());
                            holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
                        }else {
//                            addProjectViewModel.getMembers().remove(member.getId());
                            newmemberList.remove(member.getId());
                            holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        addProjectViewModel.getMembersMutableLiveData().setValue(newmemberList);
                        addProjectViewModel.getProject().setMembers(newmemberList);
                        Log.d("TAG", "onBindViewHolder: " + addProjectViewModel.getMembersMutableLiveData().getValue().size());
//                        addProjectViewModel.getMembersMutableLiveData().setValue(addProjectViewModel.getMembers());
                    });
                }
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
        private ConstraintLayout item;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.member);
            memberImg = itemView.findViewById(R.id.member_img);
            memberJobTitle = itemView.findViewById(R.id.member_job_title);
            memberName = itemView.findViewById(R.id.member_name);
        }
    }
}
