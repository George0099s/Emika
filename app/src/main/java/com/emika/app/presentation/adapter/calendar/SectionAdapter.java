package com.emika.app.presentation.adapter.calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.di.Project;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;

import java.util.List;

import javax.inject.Inject;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private static final String TAG = "SectionAdapter";
    private List<PayloadSection> sections;
    private BottomSheetAddTaskSelectProjectViewModel viewModel;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    @Inject
    Project projectDi;
    public SectionAdapter(List<PayloadSection> sections, BottomSheetAddTaskSelectProjectViewModel viewModel) {
        this.sections = sections;
        this.viewModel = viewModel;
        emikaApplication.getComponent().inject(this);
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
        holder.item.setOnClickListener(v -> {
            projectDi.setProjectSectionId(section.getId());
            projectDi.setProjectSectionName(section.getName());
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
