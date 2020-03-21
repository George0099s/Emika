package com.emika.app.presentation.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.User;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import javax.inject.Inject;

public class TaskInfoActivity extends AppCompatActivity {
    private static final String TAG = "TaskInfoActivity";
    @Inject
    User user;
    private EmikaApplication app = EmikaApplication.getInstance();
    private PayloadTask task;
    private EditText taskName, taskDescription;
    private ImageView userImg;
    private TextView spentTimeHour, estimatedTimeHour, planDate, deadlineDate, userName;
    private Button plusSpentTime, minusSpentTime, plusEstimatedTime, minusEstimatedTime;
    private TaskInfoViewModel taskInfoViewModel;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        initViews();
    }
    private void initViews() {
        app.getComponent().inject(this);
        token = getIntent().getStringExtra("token");
        taskInfoViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(TaskInfoViewModel.class);
        task = getIntent().getParcelableExtra("task");
        taskDescription = findViewById(R.id.task_info_description);
        taskDescription.addTextChangedListener(taskDescriptionTextWatcher);
        taskName = findViewById(R.id.task_info_name);
        taskName.addTextChangedListener(taskNameTextWatcher);
        spentTimeHour = findViewById(R.id.task_info_spent_time);
        estimatedTimeHour = findViewById(R.id.task_info_estimated_time);
        planDate = findViewById(R.id.task_info_plan_date);
        deadlineDate = findViewById(R.id.task_info_deadline_date);
        userName = findViewById(R.id.task_info_user_name);
        userImg = findViewById(R.id.task_info_user_img);
        setTaskInfo(task);
        setUserInfo();
    }
    private void setUserInfo() {
//        userName.setText(user.get);
    }

    private TextWatcher taskNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            task.setName(taskName.getText().toString());
            taskInfoViewModel.updateTask(task);
            Log.d(TAG, "afterTextChanged: " + task.getName());
        }
    };
    private TextWatcher taskDescriptionTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            task.setDescription(taskDescription.getText().toString());
            taskInfoViewModel.updateTask(task);
        }
    };

    private void setTaskInfo(PayloadTask task) {
        if (task != null){
            taskName.setText(task.getName());
            planDate.setText(task.getPlanDate());
            deadlineDate.setText(task.getDeadlineDate());
            spentTimeHour.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
            estimatedTimeHour.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
        }
    }
}

