package com.emika.app.presentation.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.emika.app.R;
import com.emika.app.presentation.utils.Constants;

public class AddTaskActivity extends AppCompatActivity {
    private TextView planDate;
    private String currentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);
        initView();
    }

    private void initView() {
        planDate = findViewById(R.id.add_task_plan_date);
        currentDate = getIntent().getStringExtra("date");

    }
}
