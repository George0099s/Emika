package com.emika.app.presentation.adapter.calendar;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.presentation.adapter.profile.ItemTouchHelper.ItemTouchHelperAdapter;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "SubTaskAdapter";

    private List<SubTask> taskList;
    private CalendarViewModel calendarViewModel;
    private TaskInfoViewModel taskInfoViewModel;
    public SubTaskAdapter(List<SubTask> taskList, CalendarViewModel calendarViewModel, TaskInfoViewModel taskInfoViewModel) {
        this.taskList = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            if (!taskList.get(i).getStatus().equals("deleted"))
                this.taskList.add(taskList.get(i));
        }
        this.calendarViewModel = calendarViewModel;
        this.taskInfoViewModel = taskInfoViewModel;
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

        holder.body.setImeOptions(EditorInfo.IME_ACTION_DONE);
        holder.body.setRawInputType(InputType.TYPE_CLASS_TEXT);
        if (subTask.getNewTask()) {
            holder.body.requestFocus();
            subTask.setNewTask(false);
        } else holder.body.clearFocus();
        if (subTask.getName() != null)
            holder.body.setText(subTask.getName());

        if (subTask.getStatus().equals("done"))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (taskInfoViewModel != null)
                if (isChecked) {
                    subTask.setStatus("done");
                    taskInfoViewModel.updateSubTask(subTask);
                } else {
                    subTask.setStatus("wip");
                    taskInfoViewModel.updateSubTask(subTask);
                }
            else if (calendarViewModel != null) {
                if (isChecked) {
                    subTask.setStatus("done");
                } else {
                    subTask.setStatus("wip");
                }
            }
        });

        holder.body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                subTask.setName(holder.body.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (taskInfoViewModel != null) {
                    subTask.setName(holder.body.getText().toString());
                    taskInfoViewModel.updateSubTask(subTask);
                }
                if (calendarViewModel != null) {
                    subTask.setName(holder.body.getText().toString());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addSubTask(SubTask subTask) {
        subTask.setNewTask(true);
        taskList.add(getItemCount(), subTask);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout item;
        TextView body;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_sub_task);
            body = itemView.findViewById(R.id.sub_task_item_body);
            checkBox = itemView.findViewById(R.id.sub_task_item_checkbox);
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(taskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(taskList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        if (taskInfoViewModel != null) {
            taskList.get(position).setStatus("deleted");
            taskInfoViewModel.updateSubTask(taskList.get(position));
        }
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
}
