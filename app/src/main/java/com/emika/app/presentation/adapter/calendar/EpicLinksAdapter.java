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
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.di.EpicLinks;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;

import java.util.List;

import javax.inject.Inject;

public class EpicLinksAdapter extends RecyclerView.Adapter<EpicLinksAdapter.EpicLInksViewHolder>{
    @Inject
    EpicLinks epicLinksDi;

    private List<PayloadEpicLinks> epicLinks;
    private Context context;
    private static final String TAG = "EpicLinksAdapter";
    private AddTaskListViewModel addTaskListViewModel;
    public EpicLinksAdapter(List<PayloadEpicLinks> epicLinks, Context context, AddTaskListViewModel addTaskListViewModel) {
        this.epicLinks = epicLinks;
        this.context = context;
        this.addTaskListViewModel = addTaskListViewModel;
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
        for (int i = 0; i < epicLinksDi.getEpicLinksList().size(); i++) {
            if (epicLinksDi.getEpicLinksList().get(i).getName().equals(epicLink.getName()))
                holder.checkBox.setChecked(true);

        }
//        holder.checkBox.setOnClickListener(v -> {
//            if (!epicLinksDi.getEpicLinksList().contains(epicLink) && !holder.checkBox.isChecked()) {
//                epicLinksDi.getEpicLinksList().add(epicLink);
//                holder.checkBox.setChecked(true);
//                addTaskListViewModel.setEpicLinksMutableLiveData(epicLinksDi);
//            } else if (epicLinksDi.getEpicLinksList().contains(epicLink) && holder.checkBox.isChecked()){
//                epicLinksDi.getEpicLinksList().remove(epicLink);
//                holder.checkBox.setChecked(false);
//                addTaskListViewModel.setEpicLinksMutableLiveData(epicLinksDi);
//            }
//        });
            holder.item.setOnClickListener(v -> {
                if (!epicLinksDi.getEpicLinksList().contains(epicLink) && !holder.checkBox.isChecked()) {
                    epicLinksDi.getEpicLinksList().add(epicLink);
                    holder.checkBox.setChecked(true);
                    addTaskListViewModel.setEpicLinksMutableLiveData(epicLinksDi);
                } else if (epicLinksDi.getEpicLinksList().contains(epicLink) && holder.checkBox.isChecked()){
                    epicLinksDi.getEpicLinksList().remove(epicLink);
                    holder.checkBox.setChecked(false);
                    addTaskListViewModel.setEpicLinksMutableLiveData(epicLinksDi);
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
