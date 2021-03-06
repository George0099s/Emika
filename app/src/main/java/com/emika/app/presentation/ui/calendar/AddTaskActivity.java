package com.emika.app.presentation.ui.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.Project;
import com.emika.app.features.customtimepickerdialog.CustomTimePickerDialog;
import com.emika.app.presentation.adapter.calendar.SubTaskAdapter;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

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

    private String currentDate;
    private EditText taskName, taskDescription;
    private ImageView addTask, userImg;
    private AddTaskListViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private Button back;
    private SubTaskAdapter subTaskAdapter;
    private RecyclerView subTaskRecycler;
    private CalendarViewModel calendarViewModel;
    private List<PayloadShortMember> memberList;
    private List<String> epicLinksId;
    private TextView addSubTask;
    private EmikaApplication app = EmikaApplication.getInstance();
    private String token, deadlineDateString;
    private TextView planDate, priority, deadlineDate, estimatedTime, userName, project, section, epicLinks;
    private BottomSheetSelectEpicLinks mySheetDialog;
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
        estimatedTime.setText(String.format("%sh", String.valueOf(hourOfDay)));
    };
    private Observer<Payload> userInfo = userInfo -> {

    };
    private Observer<PayloadTask> taskObserver = task -> {
//        Intent intent = new Intent();
//        intent.putExtra("task", task);
//        setResult(42, intent);
        finish();
    };
    private Observer<List<PayloadEpicLinks>> getEpicLinks = epicLinks1 -> {
        if (epicLinks1.size() != 0) {
            epicLinksId = new ArrayList<>();
            if (epicLinks1.size() > 1)
            epicLinks.setText(String.format("%s +%s", epicLinks1.get(0).getName(), String.valueOf(epicLinks1.size() - 1)));
            else epicLinks.setText(epicLinks1.get(0).getName());
            for (int i = 0; i <epicLinks1.size() ; i++) {
                epicLinksId.add(epicLinks1.get(i).getId());
            }
        } else {
            epicLinks.setText("Epic links");
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
        taskDescription = findViewById(R.id.add_task_description);
        mySheetDialog = new BottomSheetSelectEpicLinks();
        List<SubTask> tasks = new ArrayList<>();

        addSubTask = findViewById(R.id.add_task_add_sub_task);
        addSubTask.setOnClickListener(this::addSubTask);
        app.getComponent().inject(this);
        token = getIntent().getStringExtra("token");
        subTaskRecycler = findViewById(R.id.add_task_subtask_recycler);

        userImg = findViewById(R.id.add_task_user_img);
        userImg.setOnClickListener(this::selectCurrentAssignee);
        userName = findViewById(R.id.add_task_user_name);
        userName.setOnClickListener(this::selectCurrentAssignee);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(AddTaskListViewModel.class);
        viewModel.getAssignee().observe(this, setAssignee);
        viewModel.getProjectMutableLiveData().observe(this, setProjectData);
        calendarViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        profileViewModel.getUserMutableLiveData().observe(this, userInfo);
        taskName = findViewById(R.id.add_task_name);
        taskName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName.setRawInputType(InputType.TYPE_CLASS_TEXT);
        addTask = findViewById(R.id.add_task_img);
        addTask.setOnClickListener(this::addTask);
        back = findViewById(R.id.add_task_back);
        back.setOnClickListener(this::onBackPressed);
        planDate = findViewById(R.id.add_task_plan_date);
        estimatedTime = findViewById(R.id.add_task_estimated_time);
        estimatedTime.setOnClickListener(this::setTime);
        deadlineDate = findViewById(R.id.add_task_deadline_date);
        deadlineDate.setOnClickListener(this::setDeadlineDate);
        currentDate = getIntent().getStringExtra("date");
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
        viewModel.getEpicLinksMutableLiveData().observe(this, getEpicLinks);
        subTaskRecycler.setHasFixedSize(true);
        subTaskRecycler.setLayoutManager(new LinearLayoutManager(this));
        subTaskAdapter = new SubTaskAdapter(tasks, calendarViewModel, null);
        subTaskRecycler.setAdapter(subTaskAdapter);

    }

    private void addSubTask(View view) {
        SubTask subTask = new SubTask();
        subTask.setStatus("wip");
        subTaskAdapter.addSubTask(subTask);
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
            bundle.putParcelable("viewModel", calendarViewModel);
            bundle.putParcelable("addTaskViewModel", viewModel);
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
            PayloadTask newTask = new PayloadTask();
            newTask.setName(taskName.getText().toString());
            newTask.setProjectId(projectDi.getProjectId());
            newTask.setPlanDate(currentDate);
            newTask.setDeadlineDate(deadlineDateString);
            newTask.setAssignee(assignee.getId());
            newTask.setDuration(Integer.parseInt(estimatedTime.getText().toString().substring(0, estimatedTime.length() - 1)) * 60);
            newTask.setDescription(taskDescription.getText().toString());
            newTask.setPriority(priority.getText().toString().toLowerCase());
            newTask.setSectionId(projectDi.getProjectId());
            newTask.setPlanOrder("1");
            newTask.setEpicLinks(epicLinksId);
            for (int i = 0; i < subTaskAdapter.getTaskList().size(); i++) {
                subTasks.add(subTaskAdapter.getTaskList().get(i).getName());
            }
            newTask.setSubTaskList(subTasks);
            viewModel.getMutableLiveData(newTask).observe(this, taskObserver);
        }
    }

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
}
