package com.emika.app.presentation.adapter.calendar;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;

import java.util.List;

import javax.inject.Inject;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private static final String TAG = "ProjectAdapter";
    @Inject
    Project projectDi;

    private List<PayloadProject> projects;
    private BottomSheetAddTaskSelectProjectViewModel viewModel;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private PayloadTask task;
    public ProjectAdapter(List<PayloadProject> projects, BottomSheetAddTaskSelectProjectViewModel viewModel, PayloadTask task) {
        emikaApplication.getComponent().inject(this);
        this.projects = projects;
        this.viewModel = viewModel;
        this.task = task;
    }


    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        PayloadProject project = projects.get(position);
        for (int i = 0; i < projects.size(); i++) {
            if (projectDi.getProjectId().equals(project.getId()))
                holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
            else
                holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        if (project.getIsPersonal())
            holder.memberCount.setText("Private");
        else
            holder.memberCount.setText(String.format("%d members", project.getMembers().size()));
        holder.projectName.setText(project.getName());

        holder.item.setOnClickListener(v -> {
            holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
            projectDi.setProjectId(project.getId());
            projectDi.setProjectName(project.getName());
//            viewModel.setProjectId(project.getId());
//            viewModel.setProjectId();
            viewModel.getSectionListMutableLiveData();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView memberCount, projectName;
        ConstraintLayout item;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.project);
            memberCount = itemView.findViewById(R.id.item_project_members);
            projectName = itemView.findViewById(R.id.item_project_name);
        }
    }
}
