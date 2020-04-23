package com.emika.app.presentation.adapter.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.company.Payload;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Project;
import com.emika.app.di.ProjectsDi;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;

import java.util.List;

import javax.inject.Inject;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private static final String TAG = "SectionAdapter";
    @Inject
    Project projectDi;
    private List<PayloadSection> sections;
    private BottomSheetAddTaskSelectProjectViewModel viewModel;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    @Inject
    ProjectsDi projectsDagger;
    public SectionAdapter(List<PayloadSection> sections, BottomSheetAddTaskSelectProjectViewModel viewModel, PayloadTask task) {
        emikaApplication.getComponent().inject(this);
        this.viewModel = viewModel;
        this.sections = sections;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        PayloadSection section = sections.get(position);
        holder.sectionName.setText(section.getName());
        if (projectDi.getProjectSectionId() != null)
        if (projectDi.getProjectSectionId().equals(section.getId()))
            holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
        else
            holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));

        holder.item.setOnClickListener(v -> {
            holder.item.setBackgroundColor(Color.parseColor("#F5F5F5"));
            projectDi.setProjectSectionId(section.getId());
            projectDi.setProjectSectionName(section.getName());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;
        ConstraintLayout item;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.section);
            sectionName = itemView.findViewById(R.id.item_section_name);
        }
    }
}
