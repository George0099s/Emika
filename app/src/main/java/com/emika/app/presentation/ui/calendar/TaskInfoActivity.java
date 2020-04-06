package com.emika.app.presentation.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TaskInfo;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.User;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TaskInfoActivity extends AppCompatActivity {
    private static final String TAG = "TaskInfoActivity";
    @Inject
    User user;
    @Inject
    Assignee assignee;
    @Inject
    EpicLinks epicLinksDi;

    Calendar dateAndTime = Calendar.getInstance();
    private EmikaApplication app = EmikaApplication.getInstance();
    private PayloadTask task;
    private EditText taskName, taskDescription;
    private ImageView userImg;
    private TextView spentTime, estimatedTime, planDate, deadlineDate, userName, priority, project, section, epicLink;
    private TaskInfoViewModel taskInfoViewModel;
    private String token, deadlineDateString;
    private LinearLayout selectProject;
    private CalendarViewModel calendarViewModel;

    DatePickerDialog.OnDateSetListener deadlineDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            deadlineDateString = DateHelper.getDatePicker(year + "-" + (month + 1) + "-" + dayOfMonth);
            deadlineDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline_date_active), null, null, null );
            deadlineDate.setTextColor(getResources().getColor(R.color.black));
            task.setDeadlineDate(deadlineDate.getText().toString());
            calendarViewModel.updateTask(task);
        }
    };
    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
        task.setDuration(hourOfDay * 60);
        calendarViewModel.updateTask(task);
    };
    TimePickerDialog.OnTimeSetListener spentTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        spentTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
        task.setDurationActual(hourOfDay * 60);
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
    private ImageView menu;
    private CheckBox taskDone, taskCanceled;
    private Button back;
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
    private Observer<Project> getTaskProject = project -> {
        this.project.setText(project.getProjectName());
        section.setText(project.getProjectSectionName());
        task.setProjectId(project.getProjectId());
        task.setSectionId(project.getProjectSectionId());
    };
    private Observer<List<EpicLinksEntity>> setTaskEpicLinks = epicLinksEntities -> {
        if (epicLinksEntities.size() != 0)
        for (int i = 0; i < epicLinksEntities.size(); i++) {
                    if (task.getEpicLinks().size() > 1)
                        epicLink.setText(String.format("%s +%s", epicLinksEntities.get(i).getName(), String.valueOf(task.getEpicLinks().size() - 1)));
                    else
                        epicLink.setText(epicLinksEntities.get(i).getName());
        }
        else epicLink.setText("Epic link");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        setSupportActionBar(findViewById(R.id.task_info_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initViews();
    }

    private void initViews() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        app.getComponent().inject(this);
        task = getIntent().getParcelableExtra("task");
        token = getIntent().getStringExtra("token");
        taskInfoViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(TaskInfoViewModel.class);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        taskInfoViewModel.setTask(task);
        taskInfoViewModel.getEpicLinksMutableLiveData().observe(this, setTaskEpicLinks);
        taskDescription = findViewById(R.id.task_info_description);
        taskDescription.addTextChangedListener(taskDescriptionTextWatcher);
        taskName = findViewById(R.id.task_info_name);
        taskName.addTextChangedListener(taskNameTextWatcher);
        priority = findViewById(R.id.task_info_priority);
        priority.setOnClickListener(this::showPopupMenu);
        spentTime = findViewById(R.id.task_info_spent_time);
        spentTime.setOnClickListener(this::setSpentTime);
        estimatedTime = findViewById(R.id.task_info_estimated_time);
        estimatedTime.setOnClickListener(this::setTime);
        planDate = findViewById(R.id.task_info_plan_date);
        planDate.setOnClickListener(this::setPlanDate);
        deadlineDate = findViewById(R.id.task_info_deadline_date);
        deadlineDate.setOnClickListener(this::setDeadlineDate);
        userName = findViewById(R.id.task_info_user_name);
        userImg = findViewById(R.id.task_info_user_img);
        taskDone = findViewById(R.id.task_info_done);
        taskDone.setOnClickListener(this::taskDone);
        taskCanceled = findViewById(R.id.task_info_refresh);
        taskCanceled.setOnClickListener(this::refreshTask);
        userImg.setOnClickListener(this::selectCurrentAssignee);
        userName.setOnClickListener(this::selectCurrentAssignee);
        taskInfoViewModel.getAssigneeMutableLiveData().observe(this, setAssignee);
        back = findViewById(R.id.task_info_back);
        back.setOnClickListener(this::onBackPressed);
        selectProject = findViewById(R.id.tak_info_select_project);
        selectProject.setOnClickListener(this::selectProject);
        taskInfoViewModel.getProjectMutableLiveData().observe(this, getTaskProject);
        project = findViewById(R.id.task_info_project);
        section = findViewById(R.id.task_info_project_section);
        epicLink = findViewById(R.id.task_info_epic_links);
        epicLink.setOnClickListener(this::selectEpicLinks);
        setTaskInfo(task);
    }

    private void refreshTask(View view) {
        task.setStatus("wip");
        calendarViewModel.updateTask(task);
        taskDone.setVisibility(View.VISIBLE);
        taskDone.setChecked(false);
        taskCanceled.setVisibility(View.GONE);
        taskName.setTextColor(getResources().getColor(R.color.black));
        taskName.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
    }

    private void selectProject(View view) {
        Bundle bundle = new Bundle();
        BottomSheetAddTaskSelectProject mySheetDialog = new BottomSheetAddTaskSelectProject();
        bundle.putParcelable("taskInfoViewModel", taskInfoViewModel);
        bundle.putParcelable("task", task);
        mySheetDialog.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }

    private void selectEpicLinks(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        bundle.putParcelable("taskInfoViewModel", taskInfoViewModel);
        BottomSheetSelectEpicLinks mySheetDialog = new BottomSheetSelectEpicLinks();
        mySheetDialog.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }

    private void taskDone(View view) {
        if (task.getStatus().equals("done")) {
            task.setStatus("wip");
            taskInfoViewModel.updateTask(task);
        } else {
            task.setStatus("done");
            taskInfoViewModel.updateTask(task);
        }
    }

    private void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_cancel_task), getResources().getString(R.string.cancel_task)));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.drawable.ic_rename_task), getResources().getString(R.string.rename_task)));
        menu.add(0, 3, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_move_task), getResources().getString(R.string.move_task)));
//        menu.add(0, 4, 4, menuIconWithText(getResources().getDrawable(R.drawable.ic_duplicate_task), getResources().getString(R.string.duplicate_task)));
        menu.add(0, 5, 5, menuIconWithText(getResources().getDrawable(R.drawable.ic_duplicate_task), getResources().getString(R.string.archieve_task)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                task.setStatus("canceled");
                taskCanceled.setVisibility(View.VISIBLE);
                taskCanceled.setChecked(true);
                taskDone.setVisibility(View.GONE);
                calendarViewModel.updateTask(task);
                taskName.setTextColor(getResources().getColor(R.color.task_name_done));
                taskName.setPaintFlags(taskName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case 2:
                taskName.requestFocus();
                break;
            case 3:
                planDate.callOnClick();
                break;
            case 4:
                task.setStatus("archived");
                calendarViewModel.updateTask(task);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private void setTaskInfo(PayloadTask task) {
        if (task != null) {
            taskName.setText(task.getName());
            planDate.setText(DateHelper.getDate(task.getPlanDate()));
            if (task.getDeadlineDate() != null) {
                deadlineDate.setText(task.getDeadlineDate());
                deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline_date_active), null, null, null );
                deadlineDate.setTextColor(getResources().getColor(R.color.black));
            }
//            project.setText(task.get);
            if (task.getStatus().equals("canceled")){
                taskCanceled.setVisibility(View.VISIBLE);
                taskCanceled.setChecked(true);
                taskDone.setVisibility(View.GONE);
                calendarViewModel.updateTask(task);
                taskName.setTextColor(getResources().getColor(R.color.task_name_done));
                taskName.setPaintFlags(taskName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
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
            switch (task.getStatus()) {
                case "done":
                    taskDone.setChecked(true);
                    break;
                default:
                    taskDone.setChecked(false);
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
                            task.setPriority("low");
                            calendarViewModel.updateTask(task);
                            return true;
                        case R.id.normal:
                            priority.setText("Normal");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_normal), null, null, null);
                            task.setPriority("normal");
                            calendarViewModel.updateTask(task);
                            return true;
                        case R.id.high:
                            priority.setText("High");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_priority_high), null, null, null);
                            task.setPriority("high");
                            calendarViewModel.updateTask(task);
                            return true;
                        case R.id.urgent:
                            priority.setText("Urgent");
                            priority.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_task_urgent), null, null, null);
                            task.setPriority("urgent");
                            calendarViewModel.updateTask(task);
                            return true;
                        default:
                            return false;
                    }
                });
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
        int hourOfDay = task.getDuration() / 60;
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, estimatedTimeListener, hourOfDay, 0, true);
        timePickerDialog.setIcon(R.drawable.ic_estimated_time);
        timePickerDialog.show();
    }

    public void setSpentTime(View v) {
        int hourOfDay = task.getDurationActual() / 60;
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, spentTimeListener, hourOfDay, 0, true);
        timePickerDialog.setIcon(R.drawable.ic_estimated_time);
        timePickerDialog.show();
    }

    private void selectCurrentAssignee(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("viewModel", calendarViewModel);
        bundle.putParcelable("taskInfoViewModel", taskInfoViewModel);
        bundle.putParcelable("task", task);
        bundle.putString("from", "task info");
        BottomSheetCalendarSelectUser mySheetDialog = new BottomSheetCalendarSelectUser();
        mySheetDialog.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }
}

