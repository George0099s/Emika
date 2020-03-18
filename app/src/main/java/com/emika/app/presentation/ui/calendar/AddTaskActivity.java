package com.emika.app.presentation.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.di.User;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class AddTaskActivity extends AppCompatActivity {
    private TextView planDate, priority, deadlineDate, estimatedTime;
    private String currentDate;
    private EditText taskName, taskDescription;
    private ListPopupWindow mListPopupWindow;
    private ImageView addTask;
    private AddTaskViewModel viewModel;
    @Inject User user;
    private EmikaApplication app = EmikaApplication.getInstance();
    Calendar dateAndTime= Calendar.getInstance();
    private String token;
    private static final String TAG = "AddTaskActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);
        initView();
    }

    private void initView() {
        app.getComponent().inject(this);
        token = getIntent().getStringExtra("token");
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AddTaskViewModel.class);
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
        priority.setOnClickListener(v -> showPopupMenu(v));
        planDate.setOnClickListener(this::setPlanDate);
    }

    private void addTask(View view) {
        JSONObject task = new JSONObject();
        try {
            task.put("name", taskName.getText().toString());
            task.put("priority", priority.getText().toString());
            task.put("plan_date", planDate.getText());
            task.put("assignee", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewModel.addTask(task);
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
       DatePickerDialog datePickerDialog =  new DatePickerDialog(AddTaskActivity.this, planDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
       datePickerDialog.setTitle("Set plan date");
       datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
       datePickerDialog.show();
    }
    DatePickerDialog.OnDateSetListener planDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d(TAG, "onDateSet: " + year+month+dayOfMonth);
            planDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month+1), String.valueOf(dayOfMonth))));
        }
    };

    public void setDeadlineDate(View v) {
        DatePickerDialog datePickerDialog =  new DatePickerDialog(AddTaskActivity.this, deadlineDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Set deadline date");
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }
    DatePickerDialog.OnDateSetListener deadlineDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            deadlineDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month+1), String.valueOf(dayOfMonth))));
        }
    };

    public void setTime(View v) {
        TimePickerDialog timePicker = new TimePickerDialog(AddTaskActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, estimatedTimeListener,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true);
                    timePicker.setIcon(R.drawable.ic_estimated_time);
                    timePicker.show();
    }
    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
    };
}
