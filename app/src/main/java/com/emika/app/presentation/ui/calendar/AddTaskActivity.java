package com.emika.app.presentation.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.model.Member;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "AddTaskActivity";
    private String currentDate;
    private EditText taskName, taskDescription;
    private ListPopupWindow mListPopupWindow;
    private ImageView addTask, userImg;
    private AddTaskListViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private CalendarViewModel calendarViewModel;
    private Member member;
    private EmikaApplication app = EmikaApplication.getInstance();
    private String token, planDateString, deadlineDateString;
    private TextView planDate, priority, deadlineDate, estimatedTime, userName;
    @Inject
    User user;
    Calendar dateAndTime = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener deadlineDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            deadlineDateString = DateHelper.getDatePicker(year + "-" + (month + 1) + "-" + dayOfMonth);
            deadlineDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
        }
    };
    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
    };
    DatePickerDialog.OnDateSetListener planDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDate = DateHelper.getDatePicker(year + "-" + (month + 1)  + "-" + dayOfMonth);
            planDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
        }
    };
    private Observer<Payload> userInfo = userInfo -> {

    };
    private Observer<PayloadTask> taskObserver = task -> {
        Intent intent = new Intent();
        intent.putExtra("task", task);
        setResult(42, intent);
        finish();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);
        initView();
    }

    private void initView() {
        taskDescription = findViewById(R.id.add_task_description);
        app.getComponent().inject(this);
        userImg = findViewById(R.id.add_task_user_img);
        token = getIntent().getStringExtra("token");
        userName = findViewById(R.id.add_task_user_name);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AddTaskListViewModel.class);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        profileViewModel.getUserMutableLiveData().observe(this, userInfo);
        taskName = findViewById(R.id.add_task_name);
        addTask = findViewById(R.id.add_task_img);
        addTask.setOnClickListener(this::addTask);
        planDate = findViewById(R.id.add_task_plan_date);
        estimatedTime = findViewById(R.id.add_task_estimated_time);
        estimatedTime.setOnClickListener(this::setTime);
        deadlineDate = findViewById(R.id.add_task_deadline_date);
        deadlineDate.setOnClickListener(this::setDeadlineDate);
        currentDate = getIntent().getStringExtra("date");
        priority = findViewById(R.id.add_task_priority);
        userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        priority.setOnClickListener(this::showPopupMenu);
        planDate.setOnClickListener(this::setPlanDate);
        Glide.with(this).load(user.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
    }

    private void addTask(View view) {
        if (taskName.getText().toString().isEmpty()) {
            taskName.requestFocus();
            taskName.setError("Task name is missing");
        } else {
            Log.d(TAG, "addTask: " + Constants.priority.get(priority.getText().toString()));
            viewModel.getMutableLiveData(taskName.getText().toString(), "5e51008be5fa7f153486fe38",
                    currentDate, deadlineDateString,
                    user.getId(), String.valueOf(Integer.parseInt(estimatedTime.getText().toString().substring(0, estimatedTime.length() - 1)) * 60), taskDescription.getText().toString(),
                    priority.getText().toString().toLowerCase()).observe(this, taskObserver);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, planDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Set plan date");
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void setDeadlineDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, deadlineDateListener,
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


}
