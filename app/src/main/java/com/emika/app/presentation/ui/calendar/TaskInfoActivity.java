package com.emika.app.presentation.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.di.User;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;
import com.emika.app.presentation.adapter.calendar.SubTaskAdapter;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    @Inject
    Project projectDi;
    Calendar dateAndTime = Calendar.getInstance();
    private EmikaApplication app = EmikaApplication.getInstance();
    private PayloadTask task;
    private EditText taskName, taskDescription, subTaskName;
    private RecyclerView subTaskRecycler;
    private SubTaskAdapter adapter;
    private ImageView userImg;
    private TextView spentTime, estimatedTime, planDate, deadlineDate, userName, priority, project, section, epicLink, addSubTask;
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
            deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline_date_active), null, null, null);
            deadlineDate.setTextColor(getResources().getColor(R.color.black));
            task.setDeadlineDate(DateHelper.getDatePicker(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            calendarViewModel.updateTask(task);
        }
    };
    DatePickerDialog.OnDateSetListener planDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            planDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
            task.setPlanDate(DateHelper.getDatePicker(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));

            calendarViewModel.updateTask(task);
        }

    };
    private DecimalFormat df;
    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
        int dayMinutes = hourOfDay * 60 + minute;
        if (dayMinutes % 60 == 0)
            estimatedTime.setText(String.format("%sh", String.valueOf(dayMinutes / 60)));
        else
            estimatedTime.setText(String.format("%sh", df.format(dayMinutes / 60.0f)));
        task.setDuration(dayMinutes);
        calendarViewModel.updateTask(task);
    };
    TimePickerDialog.OnTimeSetListener spentTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        int dayMinutes = hourOfDay * 60 + minute;
        if (dayMinutes % 60 == 0)
            spentTime.setText(String.format("%sh", String.valueOf(dayMinutes / 60)));
        else
            spentTime.setText(String.format("%sh", df.format(dayMinutes / 60.0f)));
        task.setDurationActual(hourOfDay * 60 + minute);

        calendarViewModel.updateTask(task);
    };
    private List<PayloadSection> sectionList = new ArrayList<>();
    private List<ProjectEntity> projectList = new ArrayList<>();
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
    private long mLastClickTime = 0;
    private Observer<List<SubTask>> getSubTask = subTask -> {
        adapter = new SubTaskAdapter(subTask, null, taskInfoViewModel);
        subTaskRecycler.setAdapter(adapter);
    };
    private Observer<List<ProjectEntity>> getProjects = projectEntities -> {
        projectList = projectEntities;
        calendarViewModel.getSectionListMutableLiveData();
    };
    private Observer<List<PayloadSection>> getSections = payloadSections -> {
        sectionList = payloadSections;
        taskInfoViewModel.getProjectMutableLiveData();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        initViews();
        setSupportActionBar(findViewById(R.id.task_info_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initViews() {
        df = new DecimalFormat("#.#");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        app.getComponent().inject(this);
        if (getIntent() != null)
            task = getIntent().getParcelableExtra("task");
        token = EmikaApplication.getInstance().getSharedPreferences().getString("token", null);
        subTaskRecycler = findViewById(R.id.task_info_subtask_recycler);
        subTaskRecycler.setHasFixedSize(true);
        subTaskRecycler.setLayoutManager(new LinearLayoutManager(this));
        addSubTask = findViewById(R.id.task_info_add_sub_task);
        addSubTask.setOnClickListener(this::addSubTask);

        taskDescription = findViewById(R.id.task_info_description);
        taskDescription.addTextChangedListener(taskDescriptionTextWatcher);
        taskName = findViewById(R.id.task_info_name);
        taskName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName.setRawInputType(InputType.TYPE_CLASS_TEXT);
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
        back = findViewById(R.id.task_info_back);
        back.setOnClickListener(this::onBackPressed);
        selectProject = findViewById(R.id.tak_info_select_project);
        selectProject.setOnClickListener(this::selectProject);
        project = findViewById(R.id.task_info_project);
        section = findViewById(R.id.task_info_project_section);
        epicLink = findViewById(R.id.task_info_epic_links);
        epicLink.setOnClickListener(this::selectEpicLinks);

        taskInfoViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(TaskInfoViewModel.class);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        taskInfoViewModel.setTask(task);
        taskInfoViewModel.getTaskMutableLiveData().observe(this, getTask);
        taskInfoViewModel.getEpicLinksMutableLiveData().observe(this, setTaskEpicLinks);
        taskInfoViewModel.getSubTaskMutableLiveData(task.getId()).observe(this, getSubTask);
        taskInfoViewModel.getAssigneeMutableLiveData().observe(this, setAssignee);
        calendarViewModel.getProjectMutableLiveData().observe(this, getProjects);
        calendarViewModel.getSectionListMutableLiveData().observe(this, getSections);
        taskInfoViewModel.getProjectMutableLiveData().observe(this, getProjectsMutable);

    }


    private Observer<Project> getProjectsMutable = project1 -> {
        if (project1 != null && project1.getProjectSectionId() != null && project1.getProjectId() != null ) {
            for (ProjectEntity proj : projectList) {
                if (project1.getProjectId().equals(proj.getId())) {
                    this.project.setText(proj.getName());
                }
            }

            for (PayloadSection section : sectionList) {
                if (project1.getProjectSectionId().equals(section.getId())) {
                    this.section.setText(section.getName());
                }
            }
        }
    };

    private void addSubTask(View view) {
        List<String> subTasks = new ArrayList<>();
        SubTask subTask = new SubTask();
        subTask.setStatus("wip");
        subTask.setParentTaskId(task.getId());
        subTask.setAssignee(user.getId());
        subTask.setPlanDate(task.getPlanDate());
        subTask.setCompanyId(task.getCompanyId());
        adapter.addSubTask(subTask);
        adapter.notifyItemInserted(0);
        subTaskRecycler.scrollToPosition(0);
        for (int i = 0; i < adapter.getTaskList().size(); i++) {
            subTasks.add(adapter.getTaskList().get(i).getName());
        }
        task.setSubTaskList(subTasks);
        taskInfoViewModel.updateTask(task);
        taskInfoViewModel.addSubTask(subTask);
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

    private Observer<PayloadTask> getTask = task ->{
        if (task != null) {
            taskName.setText(task.getName());
            planDate.setText(DateHelper.getDate(task.getPlanDate()));
            if (task.getDeadlineDate() != null && !task.getDeadlineDate().equals("null")) {
                deadlineDate.setText(task.getDeadlineDate());
                deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline_date_active), null, null, null);
                deadlineDate.setTextColor(getResources().getColor(R.color.black));
            }
            if (task.getDescription() != null)
            taskDescription.setText(Html.fromHtml(task.getDescription()));
            if (task.getStatus().equals("canceled")) {
                taskCanceled.setVisibility(View.VISIBLE);
                taskCanceled.setChecked(true);
                taskDone.setVisibility(View.GONE);
                calendarViewModel.updateTask(task);
                taskName.setTextColor(getResources().getColor(R.color.task_name_done));
                taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
            if (task.getDuration() % 60 == 0)
                estimatedTime.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
            else
                estimatedTime.setText(String.format("%sh", df.format(task.getDuration() / 60.0f)));
            if (task.getDurationActual() % 60 == 0)
                spentTime.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
            else
                spentTime.setText(String.format("%sh", df.format(task.getDurationActual() / 60.0f)));
        }
    };

    private void selectProject(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            BottomSheetAddTaskSelectProject mySheetDialog = new BottomSheetAddTaskSelectProject();
            bundle.putParcelable("taskInfoViewModel", taskInfoViewModel);
            bundle.putParcelable("task", task);
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    private void selectEpicLinks(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", task);
            bundle.putParcelable("taskInfoViewModel", taskInfoViewModel);
            BottomSheetSelectEpicLinks mySheetDialog = new BottomSheetSelectEpicLinks();
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        }
        mLastClickTime = SystemClock.elapsedRealtime();
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
        menu.add(0, 4, 4, menuIconWithText(getResources().getDrawable(R.drawable.ic_duplicate_task), getResources().getString(R.string.duplicate_task)));
        if (task.getStatus().equals("canceled"))
            menu.add(0, 5, 5, menuIconWithText(getResources().getDrawable(R.drawable.ic_archive), getResources().getString(R.string.archieve_task)));
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
                taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case 2:
                taskName.requestFocus();
                break;
            case 3:
                planDate.callOnClick();
                break;
            case 4:
                Intent intent = new Intent(this, AddTaskActivity.class);
                finish();
                intent.putExtra("task", task);
                startActivity(intent);
                break;
            case 5:
                task.setStatus("deleted");
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
    
    private void showPopupMenu(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            mLastClickTime = SystemClock.elapsedRealtime();
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
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public void setPlanDate(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, planDateListener,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("Set plan date");
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date", (dialog, which) -> {
                planDate.setText(getResources().getString(R.string.inbox));
                task.setPlanDate(null);
                if (task.getPlanDate() == null)
                    Toast.makeText(this, "Task added to inbox", Toast.LENGTH_SHORT).show();
                calendarViewModel.updateTask(task);
            });
            datePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    public void setDeadlineDate(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, deadlineDateListener,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("Set deadline date");
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date", (dialog, which) -> {
                deadlineDate.setText("No deadline");
                deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline), null, null, null);
                deadlineDate.setTextColor(getResources().getColor(R.color.grey));
                task.setDeadlineDate(null);
                calendarViewModel.updateTask(task);
            });
            datePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    public void setTime(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            int hourOfDay = task.getDuration() / 60;
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, estimatedTimeListener, hourOfDay, 0, true);
            timePickerDialog.setIcon(R.drawable.ic_estimated_time);
            timePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    public void setSpentTime(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            int hourOfDay = task.getDurationActual() / 60;
            int minute = task.getDurationActual() % 60;
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, spentTimeListener, hourOfDay, minute, true);
            timePickerDialog.setIcon(R.drawable.ic_estimated_time);
            timePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    private void selectCurrentAssignee(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
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
        mLastClickTime = SystemClock.elapsedRealtime();

    }
}

