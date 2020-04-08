package com.emika.app.presentation.adapter.calendar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.subTask.SubTask;

import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private static final String TAG = "SubTaskAdapter";

    List<SubTask> taskList;

    public SubTaskAdapter(List<SubTask> taskList) {
        this.taskList = taskList;
    }

    public List<SubTask> getTaskList() {
        return taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SubTask subTask = taskList.get(position);
        if (subTask.getName() != null)
            holder.body.setText(subTask.getName());
//        holder.body.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addSubTask(SubTask subTask) {
            taskList.add(0, new SubTask());
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout item;
        EditText body;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_sub_task);
            body = itemView.findViewById(R.id.sub_task_item_body);
            checkBox = itemView.findViewById(R.id.sub_task_item_checkbox);
        }
    }
}
