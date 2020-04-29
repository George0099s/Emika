package com.emika.app.presentation.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.Project;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;

import com.emika.app.presentation.adapter.calendar.SubTaskAdapter;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "AddTaskActivity";
    @Inject
    Assignee assignee;
    Calendar dateAndTime = Calendar.getInstance();
    @Inject
    Project projectDi;
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
        }
    };
    private DecimalFormat df;
    private String currentDate;
    private EditText taskName, taskDescription;
    private ImageView addTask, userImg;
    private AddTaskListViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private Button back, inbox;
    private Context toastContext;
    private SubTaskAdapter subTaskAdapter;
    private RecyclerView subTaskRecycler;
    private CalendarViewModel calendarViewModel, boardViewModel;
    private List<PayloadShortMember> memberList;
    private List<String> epicLinksId;
    private TextView addSubTask;
    private String oldAssignee;
    private EmikaApplication app = EmikaApplication.getInstance();
    private String token, deadlineDateString;
    private TextView planDate, priority, deadlineDate, estimatedTime, userName, project, section, epicLinks;
    private BottomSheetSelectEpicLinks mySheetDialog;
    private String DATE;
    private PayloadTask task;
    private Calendar c;
    private LinearLayout backLayout;
    DatePickerDialog.OnDateSetListener planDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDate = DateHelper.getDatePicker(year + "-" + (month + 1) + "-" + dayOfMonth);
            planDate.setText(DateHelper.getDate(String.format("%s-%s-%s", String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth))));
        }
    };

    private LinearLayout selectProject;

    TimePickerDialog.OnTimeSetListener estimatedTimeListener = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        int dayMinutes = hourOfDay * 60 + minute;
        if (dayMinutes % 60 == 0)
            estimatedTime.setText(String.format("%sh", String.valueOf(dayMinutes / 60)));
        else
            estimatedTime.setText(String.format("%sh", df.format(dayMinutes / 60.0f)));
        task.setDuration(dayMinutes);
    };

    private Observer<Payload> userInfo = userInfo -> {

    };

    private Observer<PayloadTask> taskObserver = task -> {
        calendarViewModel.addDbTask(task);
//        boardViewModel.downloadTasksByAssignee(assignee.getId());
        Intent intent = new Intent();
        intent.putExtra("task", task);
        setResult(42, intent);
        finish();
    };

    private Observer<List<PayloadEpicLinks>> getEpicLinks = epicLinks1 -> {
        if (epicLinks1.size() != 0) {
            epicLinks.setTextColor(getResources().getColor(R.color.black));
            epicLinksId = new ArrayList<>();
            if (epicLinks1.size() > 1)
            epicLinks.setText(String.format("%s +%s", epicLinks1.get(0).getName(), String.valueOf(epicLinks1.size() - 1)));
            else epicLinks.setText(epicLinks1.get(0).getName());
            for (int i = 0; i <epicLinks1.size() ; i++) {
                epicLinksId.add(epicLinks1.get(i).getId());
            }
        } else {
            epicLinks.setText("Epic links");
            epicLinks.setTextColor(getResources().getColor(R.color.unselected_text));

        }
    };

    private Observer<Assignee> setAssignee = assignee1 -> {
        userName.setText(String.format("%s %s", assignee1.getFirstName(), assignee1.getLastName()));
        if (assignee1.getPictureUrl() != null)
            Glide.with(this).load(assignee1.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(userImg);
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(userImg);
    };


    private Observer<Project> setProjectData = project1 -> {
        project.setText(project1.getProjectName());
        section.setText(project1.getProjectSectionName());
    };

    private long mLastClickTime = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);
        initView();
    }

    private void initView() {
        c = Calendar.getInstance();
        c.add(Calendar.DATE, +24);
        currentDate = getIntent().getStringExtra("date");
        taskDescription = findViewById(R.id.add_task_description);
        mySheetDialog = new BottomSheetSelectEpicLinks();
        List<SubTask> tasks = new ArrayList<>();
        df = new DecimalFormat("#.#");
        task = getIntent().getParcelableExtra("task");
        addSubTask = findViewById(R.id.add_task_add_sub_task);
        addSubTask.setOnClickListener(this::addSubTask);
        app.getComponent().inject(this);
        token = EmikaApplication.getInstance().getSharedPreferences().getString("token", "");
        subTaskRecycler = findViewById(R.id.add_task_subtask_recycler);
        inbox = findViewById(R.id.add_task_inbox);
        inbox.setOnClickListener(this::goToInBox);
        userImg = findViewById(R.id.add_task_user_img);
        userImg.setOnClickListener(this::selectCurrentAssignee);
        userName = findViewById(R.id.add_task_user_name);
        userName.setOnClickListener(this::selectCurrentAssignee);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AddTaskListViewModel.class);
        viewModel.getAssignee().observe(this, setAssignee);
        viewModel.getProjectMutableLiveData().observe(this, setProjectData);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        boardViewModel = getIntent().getParcelableExtra("calendarViewModel");
        profileViewModel.getUserMutableLiveData().observe(this, userInfo);
        taskName = findViewById(R.id.add_task_name);
        taskName.requestFocus();
        taskName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName.setRawInputType(InputType.TYPE_CLASS_TEXT);
        taskName.addTextChangedListener(textWatcher);
        addTask = findViewById(R.id.add_task_img);
        addTask.setOnClickListener(this::addTask);
        back = findViewById(R.id.add_task_back);
        backLayout = findViewById(R.id.add_task_back_back_layout);
        back.setOnClickListener(this::onBackPressed);
        backLayout.setOnClickListener(this::onBackPressed);
        planDate = findViewById(R.id.add_task_plan_date);
        estimatedTime = findViewById(R.id.add_task_estimated_time);
        estimatedTime.setOnClickListener(this::setTime);
        deadlineDate = findViewById(R.id.add_task_deadline_date);
        deadlineDate.setOnClickListener(this::setDeadlineDate);
        DATE = getIntent().getStringExtra("date");
        priority = findViewById(R.id.add_task_priority);
        priority.setOnClickListener(this::showPopupMenu);

        planDate.setOnClickListener(this::setPlanDate);
        memberList = getIntent().getParcelableArrayListExtra("members");
        project = findViewById(R.id.add_task_project);
        section = findViewById(R.id.add_task_project_section);
        project.setText(projectDi.getProjectName());
        section.setText(projectDi.getProjectSectionName());
        selectProject = findViewById(R.id.add_task_select_project);
        selectProject.setOnClickListener(this::selectProject);

        epicLinks = findViewById(R.id.add_task_epic_links);
        epicLinks.setOnClickListener(this::selectEpicLinks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        viewModel.getEpicLinksMutableLiveData().observe(this, getEpicLinks);
        subTaskRecycler.setHasFixedSize(true);
        subTaskRecycler.setLayoutManager(layoutManager);
        subTaskAdapter = new SubTaskAdapter(tasks, calendarViewModel, null);
        subTaskRecycler.setAdapter(subTaskAdapter);
        if (task != null)
            setTaskInfo(task);
        else task = new PayloadTask();
        oldAssignee = assignee.getId();
    }

    private void goToInBox(View view) {
        Intent intent = new Intent(this, Inbox.class);
        intent.putExtra("date", currentDate);
        startActivity(intent);
    }

    private void addSubTask(View view) {
        List<SubTask> subTasks = new ArrayList<>();
        SubTask subTask = new SubTask();
        subTask.setStatus("wip");
        subTask.setName("");
        if (subTaskAdapter.getTaskList().size() == 0) {
            subTaskAdapter.addSubTask(subTask);
            subTaskAdapter.notifyItemInserted(subTaskAdapter.getItemCount());
            subTaskRecycler.scrollToPosition(subTaskAdapter.getItemCount());
            subTaskRecycler.requestFocus(subTaskAdapter.getItemCount() - 1);
        } else if (!subTaskAdapter.getTaskList().get(subTaskAdapter.getItemCount()-1).getName().isEmpty()) {
            subTaskAdapter.addSubTask(subTask);
            subTaskAdapter.notifyItemInserted(subTaskAdapter.getItemCount());
            subTaskRecycler.scrollToPosition(subTaskAdapter.getItemCount());
            subTaskRecycler.requestFocus(subTaskAdapter.getItemCount() - 1);
        } else {
            Snackbar myAwesomeSnackbar = Snackbar.make(
                    view,
                    "Sub-task name is missing",
                    Snackbar.LENGTH_SHORT
            );
            myAwesomeSnackbar.show();
        }
    }

    private void onBackPressed(View view) {
        onBackPressed();
    }

    private void selectProject(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            BottomSheetAddTaskSelectProject mySheetDialog = new BottomSheetAddTaskSelectProject();
            bundle.putParcelable("addTaskViewModel", viewModel);
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    private void selectCurrentAssignee(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("members", (ArrayList<? extends Parcelable>) memberList);
            bundle.putParcelable("calendarViewModel", calendarViewModel);
            bundle.putParcelable("addTaskViewModel", viewModel);
            bundle.putParcelable("boardViewModel", boardViewModel);
            bundle.putString("from", "add task");
            BottomSheetCalendarSelectUser mySheetDialog = new BottomSheetCalendarSelectUser();
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
            bundle.putParcelable("addTaskViewModel", viewModel);
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    private void addTask(View view) {
        if (taskName.getText().toString().isEmpty()) {
            taskName.requestFocus();
            taskName.setError("Task name is missing");
        } else {
            List<String> subTasks = new ArrayList<>();
            task.setName(taskName.getText().toString());
            task.setProjectId(projectDi.getProjectId());
            task.setPlanDate(currentDate);
            task.setDeadlineDate(deadlineDateString);
            task.setAssignee(assignee.getId());
            task.setDescription(taskDescription.getText().toString());
            task.setPriority(priority.getText().toString().toLowerCase());
            task.setSectionId(projectDi.getProjectId());
            task.setPlanOrder("1");
            if (task.getDuration() == null)
            task.setDuration(60);
            task.setEpicLinks(epicLinksId);
            for (int i = 0; i < subTaskAdapter.getTaskList().size(); i++) {
                subTasks.add(subTaskAdapter.getTaskList().get(i).getName());
            }
            task.setSubTaskList(subTasks);
            if (task.getPlanDate() == null) {
                Toast.makeText(this, "Task added to inbox", Toast.LENGTH_LONG).show();
            }
            viewModel.getMutableLiveData(task).observe(this, taskObserver);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (taskName.getText().toString().length() > 0)
                inbox.setVisibility(View.GONE);
            if (taskName.getText().toString().length() == 0)
                inbox.setVisibility(View.VISIBLE);

        }
    };

    private void showPopupMenu(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            BottomSheetAddTaskSelectProject mySheetDialog = new BottomSheetAddTaskSelectProject();
            bundle.putParcelable("addTaskViewModel", viewModel);
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
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
            popupMenu.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public void setPlanDate(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, planDateListener,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("Set plan date");
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date", (dialog, which) -> {
                    planDate.setText(getResources().getString(R.string.inbox));
                    currentDate = null;
            });
            datePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public void setDeadlineDate(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, deadlineDateListener,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("Set deadline date");
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "No date", (dialog, which) -> {
                deadlineDate.setText(getResources().getString(R.string.no_deadline));
                deadlineDateString = null;
            });
            datePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public void setTime(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, estimatedTimeListener, 1, 0, true);
            timePickerDialog.setIcon(R.drawable.ic_estimated_time);
            timePickerDialog.show();
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    private void setTaskInfo(PayloadTask task) {
        if (task != null) {
            task.setId(null);
            currentDate = task.getPlanDate();
            taskName.setText(task.getName());
//            if (task.getPlanDate() != null || !task.getPlanDate().equals("null"))
            planDate.setText(DateHelper.getDate(task.getPlanDate()));
            if (task.getDeadlineDate() != null && !task.getDeadlineDate().equals("null")) {
                deadlineDate.setText(task.getDeadlineDate());
                deadlineDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_deadline_date_active), null, null, null);
                deadlineDate.setTextColor(getResources().getColor(R.color.black));
            }

            projectDi.setProjectId(task.getProjectId());
            projectDi.setProjectSectionId(task.getSectionId());

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
            if (task.getDuration() % 60 == 0)
                estimatedTime.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
            else
                estimatedTime.setText(String.format("%sh", df.format(task.getDuration() / 60.0f)));
        }
    }
}
