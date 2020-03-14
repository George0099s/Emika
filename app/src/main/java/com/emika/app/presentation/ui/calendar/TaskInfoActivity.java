package com.emika.app.presentation.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.task.PayloadTask;

public class TaskInfoActivity extends AppCompatActivity {
    private static final String TAG = "TaskInfoActivity";
    private PayloadTask task;
    private EditText taskName;
    private TextView spentTimeHour, estimatedTimeHour,spentTimeMinute, estimatedTimeMinute, planDate, deadlineDate;
    private Button plusSpentTime, minusSpentTime, plusEstimatedTime, minusEstimatedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        initViews();
    }

    private void initViews() {
        task = getIntent().getParcelableExtra("task");
        taskName = findViewById(R.id.task_name);
        spentTimeHour = findViewById(R.id.task_spent_time_hour);
        spentTimeMinute = findViewById(R.id.task_spent_time_minute);
        estimatedTimeHour = findViewById(R.id.task_estimated_time_hour);
        estimatedTimeMinute = findViewById(R.id.task_estimated_time_minute);
        planDate = findViewById(R.id.task_plan_date);
        deadlineDate = findViewById(R.id.task_dead_line_date);
        setTaskInfo(task);

    }

    private void setTaskInfo(PayloadTask task) {
        if (task != null){
            taskName.setText(task.getName());
            planDate.setText(task.getPlanDate());
            deadlineDate.setText(task.getDeadlineDate());
            spentTimeHour.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
            estimatedTimeHour.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
            estimatedTimeMinute.setText(String.format("%sm", String.valueOf(task.getDuration() % 60)));
            spentTimeMinute.setText(String.format("%sm", String.valueOf(task.getDuration() % 60)));
        }
    }
}

