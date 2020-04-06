package com.emika.app.presentation.adapter.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import java.util.ArrayList;
import java.util.List;

public class EpicLinksAdapter extends RecyclerView.Adapter<EpicLinksAdapter.EpicLInksViewHolder> {
    private static final String TAG = "EpicLinksAdapter";

    private List<PayloadEpicLinks> epicLinks;
    private Context context;
    private AddTaskListViewModel addTaskListViewModel;
    private TaskInfoViewModel taskInfoViewModel;
    private List<String> epicLinksId;

    public EpicLinksAdapter(List<PayloadEpicLinks> epicLinks, Context context, AddTaskListViewModel addTaskListViewModel, TaskInfoViewModel taskInfoViewModel) {
        this.epicLinks = epicLinks;
        this.context = context;
        this.addTaskListViewModel = addTaskListViewModel;
        this.taskInfoViewModel = taskInfoViewModel;
        epicLinksId = new ArrayList<>();
        EmikaApplication.getInstance().getComponent().inject(this);
    }


    @NonNull
    @Override
    public EpicLInksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_epic_link, parent, false);
        return new EpicLInksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpicLInksViewHolder holder, int position) {
        PayloadEpicLinks epicLink = epicLinks.get(position);
        holder.epicLinkName.setText(epicLink.getName());
        if (addTaskListViewModel != null)
            for (int i = 0; i < addTaskListViewModel.getEpicLinksList().size(); i++) {
                if (addTaskListViewModel.getEpicLinksList().get(i).getId().equals(epicLink.getId()))
                    holder.checkBox.setChecked(true);
            }
        if (taskInfoViewModel != null)
            for (int i = 0; i < taskInfoViewModel.getTask().getEpicLinks().size(); i++) {
                if (taskInfoViewModel.getTask().getEpicLinks().get(i).equals(epicLink.getId()))
                    holder.checkBox.setChecked(true);
            }

        holder.item.setOnClickListener(v -> {
            if (addTaskListViewModel != null) {
                if (!addTaskListViewModel.getEpicLinksList().contains(epicLink) && !holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                        addTaskListViewModel.getEpicLinksList().add(epicLink);
                        addTaskListViewModel.getEpicLinksMutableLiveData();

                } else {
                    holder.checkBox.setChecked(false);
                        for (int i = 0; i < addTaskListViewModel.getEpicLinksList().size(); i++) {
                            if (addTaskListViewModel.getEpicLinksList().get(i).getId().equals(epicLink.getId()))
                                addTaskListViewModel.getEpicLinksList().remove(i);
                        }
                        addTaskListViewModel.getEpicLinksMutableLiveData();
                }
            }
            if (taskInfoViewModel != null) {
                if (!taskInfoViewModel.getTask().getEpicLinks().contains(epicLink.getId()) && !holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    taskInfoViewModel.getTask().getEpicLinks().add(epicLink.getId());
                    taskInfoViewModel.getEpicLinksMutableLiveData();
                    taskInfoViewModel.updateTask(taskInfoViewModel.getTask());
                } else {
                    holder.checkBox.setChecked(false);
                        for (int i = 0; i < taskInfoViewModel.getTask().getEpicLinks().size(); i++) {
                            if (taskInfoViewModel.getTask().getEpicLinks().get(i).equals(epicLink.getId()))
                                taskInfoViewModel.getTask().getEpicLinks().remove(i);
                        }
                    taskInfoViewModel.getEpicLinksMutableLiveData();
                    taskInfoViewModel.updateTask(taskInfoViewModel.getTask());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return epicLinks.size();
    }

    public class EpicLInksViewHolder extends RecyclerView.ViewHolder {
        private TextView epicLinkName;
        private CheckBox checkBox;
        private ConstraintLayout item;

        public EpicLInksViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.epic_link_item);
            epicLinkName = itemView.findViewById(R.id.item_epic_link_name);
            checkBox = itemView.findViewById(R.id.item_epic_link_check);
        }
    }
}
