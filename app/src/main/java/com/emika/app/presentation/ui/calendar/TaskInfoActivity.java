package com.emika.app.presentation.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TaskInfo;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.di.User;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class TaskInfoActivity extends AppCompatActivity {
    private static final String TAG = "TaskInfoActivity";
    @Inject
    User user;
    @Inject
    Assignee assignee;
    private EmikaApplication app = EmikaApplication.getInstance();
    private PayloadTask task;
    private EditText taskName, taskDescription;
    private ImageView userImg;
    private TextView spentTime, estimatedTime, planDate, deadlineDate, userName, priority;
    private Button plusSpentTime, minusSpentTime, plusEstimatedTime, minusEstimatedTime;
    private TaskInfoViewModel taskInfoViewModel;
    private String token ,deadlineDateString;
    private CalendarViewModel calendarViewModel;
    private ImageView menu;
    private Button back;
    private AddTaskListViewModel addTaskListViewModel;


    Calendar dateAndTime = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        setSupportActionBar(findViewById(R.id.task_info_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initViews();
    }

    private void initViews() {
        app.getComponent().inject(this);
        token = getIntent().getStringExtra("token");
        taskInfoViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(TaskInfoViewModel.class);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        addTaskListViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AddTaskListViewModel.class);
        task = getIntent().getParcelableExtra("task");
        taskDescription = findViewById(R.id.task_info_description);
        taskDescription.addTextChangedListener(taskDescriptionTextWatcher);
        taskName = findViewById(R.id.task_info_name);
        taskName.addTextChangedListener(taskNameTextWatcher);
        priority = findViewById(R.id.task_info_priority);
        priority.setOnClickListener(this::showPopupMenu);
        spentTime = findViewById(R.id.task_info_spent_time);
        estimatedTime = findViewById(R.id.task_info_estimated_time);
        estimatedTime.setOnClickListener(this::setTime);
        planDate = findViewById(R.id.task_info_plan_date);
        planDate.setOnClickListener(this::setPlanDate);
        deadlineDate = findViewById(R.id.task_info_deadline_date);
        deadlineDate.setOnClickListener(this::setDeadlineDate);
        userName = findViewById(R.id.task_info_user_name);
        userImg = findViewById(R.id.task_info_user_img);
        userImg.setOnClickListener(this::selectCurrentAssignee);
        userName.setOnClickListener(this::selectCurrentAssignee);
        taskInfoViewModel.getAssigneeMutableLiveData().observe(this, setAssignee);
        back = findViewById(R.id.task_info_back);
        back.setOnClickListener(this::onBackPressed);
        setTaskInfo(task);
    }

    private void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private Observer<Assignee> setAssignee = assignee1 -> {
        userName.setText(String.format("%s %s", assignee1.getFirstName(), assignee1.getLastName()));
        if (assignee1.getPictureUrl() != null)
            Glide.with(this).load(assignee1.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg);
    };

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
        if (task != null) {
            taskName.setText(task.getName());
            planDate.setText(DateHelper.getDate(task.getPlanDate()));
            deadlineDate.setText(task.getDeadlineDate());
            switch (task.getPriority()) {
                case "low":
                    priority.setText("Low");
                    priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_low), null, null, null);
                    break;
                case "normal":
                    priority.setText("Normal");
                    priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_normal), null, null, null);
                    break;
                case "high":
                    priority.setText("High");
                    priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_high), null, null, null);
                    break;
                case "urgent":
                    priority.setText("Urgent");
                    priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_task_urgent), null, null, null);
                    break;
            }
            estimatedTime.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
            spentTime.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.priority_popup_menu);
        popupMenu
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.low:
                            priority.setText("Low");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_low), null, null, null);
                            return true;
                        case R.id.normal:
                            priority.setText("Normal");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_normal), null, null, null);
                            return true;
                        case R.id.high:
                            priority.setText("High");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_high), null, null, null);
                            return true;
                        case R.id.urgent:
                            priority.setText("Urgent");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_task_urgent), null, null, null);
                            return true;
                        default:
                            return false;
                    }
                });

        popupMenu.setOnDismissListener(menu -> Toast.makeText(getApplicationContext(), "onDismiss",
                Toast.LENGTH_SHORT).show());
        popupMenu.show();
    }

    public void setPlanDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, planDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Set plan date");
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void setDeadlineDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, deadlineDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Set deadline date");
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void setTime(View v) {
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, estimatedTimeListener, dateAndTime.get(Calendar.HOUR_OF_DAY), dateAndTime.get(Calendar.MINUTE), true);
        timePickerDialog.setIcon(R.drawable.ic_estimated_time);
        timePickerDialog.show();
    }
    DatePickerDialog.OnDateSetListener deadlineDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            deadlineDateString = DateHelper.getDatePicker(year + "-" + (month + 1) + "-" + dayOfMonth);
            deadlineDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            task.setDeadlineDate(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            calendarViewModel.updateTask(task);
        }
    };
    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
        task.setDuration(hourOfDay);
        calendarViewModel.updateTask(task);
    };
    DatePickerDialog.OnDateSetListener planDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            planDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            task.setPlanDate(planDate.getText().toString());
            calendarViewModel.updateTask(task);
        }
    };

    private void selectCurrentAssignee(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("viewModel", calendarViewModel);
        bundle.putParcelable("addTaskViewModel", addTaskListViewModel);
        bundle.putString("from", "task info");
        BottomSheetCalendarSelectUser mySheetDialog = new BottomSheetCalendarSelectUser();
        mySheetDialog.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }
}

