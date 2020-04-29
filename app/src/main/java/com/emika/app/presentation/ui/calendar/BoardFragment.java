package com.emika.app.presentation.ui.calendar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
import com.emika.app.di.EpicLinks;
import com.emika.app.di.User;
import com.emika.app.features.calendar.BoardView;
import com.emika.app.features.calendar.ColumnProperties;
import com.emika.app.features.calendar.DragItem;
import com.emika.app.features.hourcounter.HourCounterView;
import com.emika.app.presentation.adapter.calendar.ItemAdapter;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetDialogViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class BoardFragment extends Fragment {
    private static final String TAG = "BoardFragment";
    private static int sCreatedItems = 0;
    @Inject
    Assignee assignee;
    @Inject
    EpicLinks epicLinks;
    @Inject
    User user;
    private BoardView mBoardView;
    private ConstraintLayout selectCurrentUser;
    private String token;
    private int mColumns;
    private List<EpicLinksEntity> epicLinksEntities = new ArrayList<>();
    private Button rightScroll, leftScroll;
    private EmikaApplication app = EmikaApplication.getInstance();
    private FloatingActionButton addTask;
    private List<PayloadTask> payloadTaskList = new ArrayList<>();
    private List<ProjectEntity> projectEntities = new ArrayList<>();
    private ProfileViewModel profileViewModel;
    private BottomSheetDialogViewModel bottomSheetDialogViewModel;
    private UserNetworkManager userNetworkManager;
    private ImageView fabImg;
    private CalendarViewModel viewModel;
    private TextView fabUserName, fabJobTitle;
    private List<PayloadShortMember> memberList;
    private boolean firstRun = false;
    private List<PayloadDurationActual> durationActualList = new ArrayList<>();
    private JSONObject tokenJson = new JSONObject();
    private Socket socket;
    private DecimalFormat df;
    private Animation animRight;
    private Animation animLeft;
    private Animation animOutLeft;
    private Animation animOutRight;
    private String oldAssignee;
    private ProgressBar progressBar;
    private Converter converter;
    private Observer<List<EpicLinksEntity>> getEpicLinks = epicLinksEntities -> {
        this.epicLinksEntities = epicLinksEntities;
    };

    private Observer<List<PayloadDurationActual>> getDuration = durationActual -> {
        durationActualList = durationActual;
    };

    private Observer<List<ProjectEntity>> getProjectEntity = projectEntities1 -> {
        projectEntities = projectEntities1;
    };

    private Observer<List<PayloadShortMember>> shortMembers = members -> {
        memberList = members;
    };

    private Observer<Assignee> getAssignee = currentAssignee -> {
        oldAssignee = currentAssignee.getId();
        fabJobTitle.setText(currentAssignee.getJobTitle());
        fabUserName.setText(String.format("%s %s", currentAssignee.getFirstName(), currentAssignee.getLastName()));
        if (currentAssignee.getPictureUrl() != null)
            Glide.with(getContext()).load(currentAssignee.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(fabImg);
        else
            Glide.with(getContext()).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(fabImg);
    };

    private Observer<Payload> userInfo = userInfo -> {
        assignee.setFirstName(userInfo.getFirstName());
        assignee.setLastName(userInfo.getLastName());
        assignee.setId(userInfo.getId());
        assignee.setPictureUrl(userInfo.getPictureUrl());
        assignee.setJobTitle(userInfo.getJobTitle());
        fabUserName.setText(String.format("%s %s", assignee.getFirstName(), assignee.getLastName()));
        fabJobTitle.setText(assignee.getJobTitle());
        Glide.with(this).load(assignee.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(fabImg);
        viewModel.getAssigneeMutableLiveData().observe(getViewLifecycleOwner(), getAssignee);
        viewModel.insertDbUser(userInfo);
    };

    private Emitter.Listener onCreateActualDuration = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        String id, status, taskId, projectId, companyId, date, person, createdAt, createdBy;
        int value;
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        PayloadDurationActual durationActual = new PayloadDurationActual();
        try {
            jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            value = jsonObject.getInt("value");
            status = jsonObject.getString("status");
            taskId = jsonObject.getString("task_id");
            projectId = jsonObject.getString("project_id");
            companyId = jsonObject.getString("company_id");
            date = jsonObject.getString("date");
            createdAt = jsonObject.getString("created_at");
            createdBy = jsonObject.getString("created_by");
            person = jsonObject.getString("person");
            durationActual.setValue(value);
            durationActual.setCompanyId(companyId);
            durationActual.setCreatedAt(createdAt);
            durationActual.setCreatedBy(createdBy);
            durationActual.setDate(date);
            durationActual.setId(id);
            durationActual.setPerson(person);
            durationActual.setProjectId(projectId);
            durationActual.setTaskId(taskId);
            durationActualList.add(durationActual);
            for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                if (Constants.dateColumnMap.get(i).equals(date) && assignee.getId().equals(person)) {
                    HourCounterView spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                    if (value % 60 == 0)
                        spentHourCounterView.setProgress(String.valueOf(Double.parseDouble(spentHourCounterView.getProgress()) + value / 60));
                    else {
                        String s = df.format(Double.parseDouble(spentHourCounterView.getProgress()) + value / 60.0f);
                        s = s.replace(',', '.');
                        spentHourCounterView.setProgress(s);
                    }
                }
            }
            viewModel.insertDbDuration(durationActual);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });
    private Emitter.Listener onDeleteActualDuration = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        String id, value, status, taskId, projectId, companyId, date, person, createdAt, createdBy;
        date = null;
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            int spentTime = 0;
            for (int i = 0; i < durationActualList.size(); i++) {
                if (durationActualList.get(i).getId().equals(id)) {
                    date = durationActualList.get(i).getDate();
                    durationActualList.remove(i);
                }
            }
            for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                for (int j = 0; j < durationActualList.size(); j++) {
                    if (Objects.equals(Constants.dateColumnMap.get(i), date) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                        if (Objects.equals(Constants.dateColumnMap.get(i), durationActualList.get(j).getDate()) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                            spentTime += (durationActualList.get(j).getValue());
                        }
                        HourCounterView spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                        if (spentTime % 60 == 0)
                            spentHourCounterView.setProgress(String.valueOf(spentTime / 60));
                        else {
                            String s = df.format(spentTime / 60.0f);
                            s = s.replace(',', '.');
                            spentHourCounterView.setProgress(s);
                        }
//                            spentHourCounterView.setProgress(df.format(spentTime / 60.0f));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    });
    private Emitter.Listener onUpdateActualDuration = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        String id, value, status, taskId, projectId, companyId, date, person, createdAt, createdBy;
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        PayloadDurationActual durationActual = new PayloadDurationActual();
        try {
            jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            value = jsonObject.getString("value");
            status = jsonObject.getString("status");
            taskId = jsonObject.getString("task_id");
            projectId = jsonObject.getString("project_id");
            companyId = jsonObject.getString("company_id");
            date = jsonObject.getString("date");
            createdAt = jsonObject.getString("created_at");
            createdBy = jsonObject.getString("created_by");
            person = jsonObject.getString("person");
            durationActual.setValue(Integer.valueOf(value));
            durationActual.setCompanyId(companyId);
            durationActual.setCreatedAt(createdAt);
            durationActual.setCreatedBy(createdBy);
            durationActual.setDate(date);
            durationActual.setId(id);
            durationActual.setPerson(person);
            durationActual.setProjectId(projectId);
            durationActual.setTaskId(taskId);
            for (int i = 0; i < durationActualList.size(); i++) {
                if (durationActualList.get(i).getId().equals(id)) {
                    durationActualList.get(i).setValue(Integer.valueOf(value));
                }
            }
            int spentTime = 0;
            for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                for (int j = 0; j < durationActualList.size(); j++) {
                    if (Objects.equals(Constants.dateColumnMap.get(i), date) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                        if (durationActualList.get(j).getId().equals(id)) {
                            durationActualList.get(j).setValue(Integer.valueOf(value));
                        }
                        if (Objects.equals(Constants.dateColumnMap.get(i), durationActualList.get(j).getDate()) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                            spentTime += (durationActualList.get(j).getValue());
                        }
                        HourCounterView spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                        if (spentTime % 60 == 0)
                            spentHourCounterView.setProgress(String.valueOf(spentTime / 60));
                        else {
                            String s = df.format(spentTime / 60.0f);
                            s = s.replace(',', '.');
                            spentHourCounterView.setProgress(s);
                        }
                    }
                }
            }
            viewModel.updateDbDuration(durationActual);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });
    private long mLastClickTime = 0;

    private Observer<List<PayloadTask>> getTask = taskList -> {
//       Toast.makeText(getContext(), String.valueOf(taskList.size()), Toast.LENGTH_SHORT).show();
//
//        for (int i = 0; i < taskList.size(); i++) {
//            Log.d(TAG, ": "  + taskList.get(i).getName() +  " " + taskList.get(i).getAssignee() + " " + taskList.get(i).getStatus() + " " + taskList.get(i).getPlanDate());
//        }
        progressBar.setVisibility(View.VISIBLE);
        mBoardView.setVisibility(View.GONE);
        payloadTaskList = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getAssignee().equals(assignee.getId()))
                payloadTaskList.add(taskList.get(i));
        }
        viewModel.getProjectMutableLiveData();

//        for (int i = 0; i < 40; i++) {
//            Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
//        }

        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute(payloadTaskList);
    };

    private Observer<List<PayloadTask>> getFilteredTask = taskList -> {
        int t = mBoardView.getColumnCount() - 1;
        mBoardView.clearBoard();
        for (int i = 0; i < taskList.size(); i++) {
            addTask(taskList.get(i), t);
        }
    };

    private Emitter.Listener onTaskUpdate = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        Boolean hasTask = false;
        int row;
        JSONObject jsonObject;
        String date, name, assignee, id, priority, planDate, deadlineDate, estimatedTime, spentTime, status, description, parentId, projectId, sectionId;
        JSONArray epicLinks = new JSONArray();
        List<String> epicLinkList = new ArrayList<>();
        if (getActivity() != null)
            try {
                JSONArray jsonArray = new JSONArray(Arrays.toString(args));
                jsonObject = jsonArray.getJSONObject(0);
                id = jsonObject.getString("_id");
                status = jsonObject.getString("status");
                name = jsonObject.getString("name");
                priority = jsonObject.getString("priority");
                planDate = jsonObject.getString("plan_date");
                epicLinks = jsonObject.getJSONArray("epic_links");
                projectId = jsonObject.getString("project_id");
                sectionId = jsonObject.getString("section_id");
                description = jsonObject.getString("description");
                date = planDate;
                assignee = jsonObject.getString("assignee");
                row = jsonObject.getInt("plan_order");
                if (epicLinks.length() != 0)
                    for (int i = 0; i < epicLinks.length(); i++) {
                        epicLinkList.add((String) epicLinks.get(i));
                    }
                deadlineDate = jsonObject.getString("deadline_date");
                estimatedTime = jsonObject.getString("duration");
                spentTime = jsonObject.getString("duration_actual");

                PayloadTask task2 = new PayloadTask();
                task2.setId(id);
                task2.setName(name);
                task2.setDurationActual(Integer.valueOf(spentTime));
                task2.setDuration(Integer.valueOf(estimatedTime));
                task2.setDeadlineDate(deadlineDate);
                task2.setPlanDate(planDate);
                task2.setProjectId(projectId);
                task2.setSectionId(sectionId);
                task2.setEpicLinks(epicLinkList);
                task2.setAssignee(assignee);
                task2.setPriority(priority);
                task2.setPlanOrder(String.valueOf(row));
                task2.setDescription(description);
                task2.setStatus(status);
                Log.d(TAG, ":  " + row);
                for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                    for (int j = 0; j < mBoardView.getAdapter(i).getItemCount(); j++) {
                        Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(j);
                        assert taskNewPos.second != null;
                        if (taskNewPos.second.getId().equals(id)) {
                            hasTask = true;
                            taskNewPos.second.setName(name);
                            taskNewPos.second.setDurationActual(Integer.valueOf(spentTime));
                            taskNewPos.second.setDuration(Integer.valueOf(estimatedTime));
                            taskNewPos.second.setDeadlineDate(deadlineDate);
                            taskNewPos.second.setPlanOrder(String.valueOf(row));
                            taskNewPos.second.setAssignee(assignee);
                            taskNewPos.second.setEpicLinks(epicLinkList);
                            taskNewPos.second.setProjectId(projectId);
                            taskNewPos.second.setSectionId(sectionId);
                            taskNewPos.second.setPriority(priority);
                            taskNewPos.second.setStatus(status);
                            mBoardView.replaceItem(i, j, taskNewPos, false);
                            if (date.equals("null")) {
                                taskNewPos.second.setPlanDate(null);
                                viewModel.updateDbTask(taskNewPos.second);
                                mBoardView.removeItem(i, j);
                            } else if (!assignee.equals(oldAssignee)) {
                                taskNewPos.second.setAssignee(assignee);
                                viewModel.updateDbTask(taskNewPos.second);
                                mBoardView.removeItem(i, j);
                            } else {
                                if (taskNewPos.second.getStatus().equals("deleted")) {
                                    taskNewPos.second.setPlanDate(planDate);
                                    viewModel.updateDbTask(taskNewPos.second);
                                    mBoardView.removeItem(i, j);
                                } else if (taskNewPos.second.getPlanDate().equals(planDate)) {
                                    if (row > 0)
                                    mBoardView.getAdapter(i).swapItems(j, row);
                                    else if (row == 0)
                                        mBoardView.getAdapter(i).swapItems(j, row);
                                    else if (row < 0)
                                        mBoardView.getAdapter(i).swapItems(j, 0);

                                    mBoardView.invalidate();
                                    viewModel.updateDbTask(taskNewPos.second);
                                } else if (!taskNewPos.second.getPlanDate().equals(planDate)) {
                                    for (int k = 0; k < mBoardView.getColumnCount(); k++) {
                                        if (!Constants.dateColumnMap.containsValue(planDate)){
                                            mBoardView.getAdapter(i).getItemList().remove(taskNewPos);
                                        }

                                            if (planDate.equals(Constants.dateColumnMap.get(k))) {
                                            taskNewPos.second.setPlanDate(planDate);
                                                if (row > 0)
                                            mBoardView.moveItem(i, j, k, row-1, false);
                                            else
                                                mBoardView.moveItem(i, j, k, 0, false);
                                            mBoardView.invalidate();
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                if (!date.equals("null"))
                    if (assignee.equals(this.assignee.getId()) && !hasTask)  {
                        PayloadTask task = new PayloadTask();
                        task.setName(name);
                        task.setId(id);
                        task.setDurationActual(Integer.valueOf(spentTime));
                        task.setDuration(Integer.valueOf(estimatedTime));
                        task.setDeadlineDate(deadlineDate);
                        task.setProjectId(projectId);
                        task.setSectionId(sectionId);
                        task.setPlanDate(planDate);
                        task.setAssignee(assignee);
                        task.setPriority(priority);
                        task.setPlanOrder(String.valueOf(row));
                        task.setStatus(status);
                        if (!status.equals("deleted") && !status.equals("archived"))
                        addTask(task);
                        viewModel.updateDbTask(task2);
                        viewModel.addDbTask(task2);
                    }
                viewModel.updateDbTask(task2);
                viewModel.addDbTask(task2);
            }
//                        if (taskNewPos.second.getId().equals(id)) {
//                            hasTask = true;
//                            taskNewPos.second.setName(name);
//                            taskNewPos.second.setDurationActual(Integer.valueOf(spentTime));
//                            taskNewPos.second.setDuration(Integer.valueOf(estimatedTime));
//                            taskNewPos.second.setDeadlineDate(deadlineDate);
//                            taskNewPos.second.setPlanOrder(String.valueOf(row));
//                            taskNewPos.second.setPlanDate(planDate);
//                            taskNewPos.second.setAssignee(assignee);
//                            taskNewPos.second.setEpicLinks(epicLinkList);
//                            taskNewPos.second.setProjectId(projectId);
//                            taskNewPos.second.setDescription(description);
//                            if (sectionId != null)
//                            taskNewPos.second.setSectionId(sectionId);
//                            taskNewPos.second.setPriority(priority);
//                            taskNewPos.second.setStatus(status);
//                            if (date.equals("null") || status.equals("deleted") || !assignee.equals(this.assignee.getId())) {
//                                taskNewPos.second.setPlanDate(null);
//                                viewModel.updateDbTask(taskNewPos.second);
//                                mBoardView.removeItem(i, j);
//                            } else if (taskNewPos.second.getPlanDate().equals(date)) {
//                                mBoardView.getAdapter(i).swapItems(j, row);
//                                viewModel.updateDbTask(taskNewPos.second);
//                                mBoardView.invalidate();
//                            } else
//                                for (int k = 0; k < mBoardView.getColumnCount(); k++) {
//                                    if (planDate.equals(Constants.dateColumnMap.get(k))) {
//                                        taskNewPos.second.setPlanDate(planDate);
//                                        viewModel.updateDbTask(taskNewPos.second);
//                                        mBoardView.moveItem(i, j, k, row - 1, false);
//                                    }
//                                }
//                        }
//                    }
//                }
//             if (!date.equals("null"))
//                 if (!hasTask) {
//                     viewModel.addDbTask(task);
//                     if (assignee.equals(this.assignee.getId())) {
//                         addTask(task);
//                     }
//                 }
//                viewModel.updateDbTask(task);
            catch (JSONException e) {
                e.printStackTrace();
            }
    });

    private Observer<PayloadTask> getTaskMutableData = task -> {
        addTask(task);
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        for (int i = 0; i < 100; i++) {
            Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
        }
        converter = new Converter();
        progressBar = view.findViewById(R.id.board_progress_bar);
        animRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_anim);
        animLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left_anim);
        animOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left_anim);
        animOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right_anim);

        df = new DecimalFormat("#.#");

        app.getComponent().inject(this);
        token = getActivity().getIntent().getStringExtra("token");
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        userNetworkManager = new UserNetworkManager(token);
        socket = app.getSocket();
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("server_create_connection", tokenJson);
        bottomSheetDialogViewModel = new ViewModelProvider(this).get(BottomSheetDialogViewModel.class);
        socket.on("update_task", onTaskUpdate);
        socket.on("create_duration_actual_log", onCreateActualDuration);
        socket.on("delete_duration_actual_log", onDeleteActualDuration);
        socket.on("update_duration_actual_log", onUpdateActualDuration);
           profileViewModel.setContext(getContext());
        profileViewModel.downloadUserData();
//        profileViewModel.getDbUserData();
        profileViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), userInfo);
//        viewModel.getAllDbTask();
        viewModel.getAllDbDurations();
        viewModel.downloadTasksByAssignee(user.getId());
//        viewModel.getAllDbTaskByAssignee(user.getId());
        fabUserName = view.findViewById(R.id.fab_user_name);
        fabImg = view.findViewById(R.id.fab_img);
        fabJobTitle = view.findViewById(R.id.fab_job_title);
        selectCurrentUser = view.findViewById(R.id.select_current_user);
        selectCurrentUser.setOnClickListener(this::selectCurrentAssignee);

        rightScroll = view.findViewById(R.id.right_scroll_to_current_date);
        rightScroll = view.findViewById(R.id.right_scroll_to_current_date);
        leftScroll = view.findViewById(R.id.left_scroll_to_current_date);
        rightScroll.setOnClickListener(this::scrollToCurrentDate);
        leftScroll.setOnClickListener(this::scrollToCurrentDate);
        addTask = view.findViewById(R.id.add_task);
        addTask.setOnClickListener(this::goToAddTask);
        mBoardView = view.findViewById(R.id.board_view);
        mBoardView.setLastColumn(15);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        mBoardView.setVisibility(View.VISIBLE);
        firstRun = true;
        mBoardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {
            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(toColumn).getItemList().get(toRow);
//                Pair<Long, PayloadTask> taskOldPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(fromColumn).getItemList().get(fromRow);
                Log.d(TAG, "onItemDragEnded() called with: fromColumn = [" + fromColumn + "], fromRow = [" + fromRow + "], toColumn = [" + toColumn + "], toRow = [" + toRow + "]");
                taskNewPos.second.setPlanDate(Constants.dateColumnMap.get(toColumn));
                taskNewPos.second.setPlanOrder(String.valueOf(toRow));
//                taskOldPos.second.setPlanOrder(String.valueOf(oldRow));
                viewModel.updateTask(taskNewPos.second);
                for (int i = 0; i < mBoardView.getAdapter(toColumn).getItemList().size(); i++) {
                    Pair<Long, PayloadTask> task = (Pair<Long, PayloadTask>) mBoardView.getAdapter(toColumn).getItemList().get(i);
                    task.second.setPlanOrder(String.valueOf(mBoardView.getAdapter(toColumn).getPositionForItem(task)));
                    Log.d(TAG, "onItemDragEnded: " + (mBoardView.getAdapter(toColumn).getPositionForItem(task)));
//                    int min = Integer.parseInt(task.second.getPlanOrder());
//                    if (min == Integer.parseInt(task.second.getPlanOrder())) {
//                        min++;
//                        taskNewPos.second.setPlanOrder(String.valueOf(min));
//
//                         viewModel.updateTask(task.second);
//                     } else
                         viewModel.updateTask(task.second);

                }
//                viewModel.updateTask(taskOldPos.second);
                int estimatedTimeNew = 0;
                int estimatedTimeOld = 0;
                HourCounterView hourEstimatedOld = mBoardView.getHeaderView(fromColumn).findViewById(R.id.hour_counter_estimated);
                HourCounterView hourEstimatedNew = mBoardView.getHeaderView(toColumn).findViewById(R.id.hour_counter_estimated);
                ArrayList<Pair<Long, PayloadTask>> newTasks = mBoardView.getAdapter(toColumn).getItemList();
                ArrayList<Pair<Long, PayloadTask>> oldTasks = mBoardView.getAdapter(fromColumn).getItemList();

                for (int i = 0; i < oldTasks.size(); i++) {
                    Pair<Long, PayloadTask> pair = oldTasks.get(i);
                    PayloadTask task = pair.second;
                    estimatedTimeOld += task.getDuration();
                }

                if (estimatedTimeOld % 60 == 0)
                    hourEstimatedOld.setProgress(String.valueOf(estimatedTimeOld / 60));
                else
                    hourEstimatedOld.setProgress(df.format(estimatedTimeOld / 60.0f));


                for (int i = 0; i < newTasks.size(); i++) {
                    Pair<Long, PayloadTask> pair = newTasks.get(i);
                    PayloadTask task = pair.second;
                    estimatedTimeNew += task.getDuration();
                }

                if (estimatedTimeNew % 60 == 0)
                    hourEstimatedNew.setProgress(String.valueOf(estimatedTimeNew / 60));
                else
                    hourEstimatedNew.setProgress(df.format(estimatedTimeNew / 60.0f));
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {

            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {

            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
//                if (mBoardView.getColumnCount() - newColumn == 3) {
//                    addColumn(DateHelper.getDate(DateHelper.compareDate(mBoardView.getColumnCount() + 1)), DateHelper.getDatOfWeek(mBoardView.getColumnCount() + 1), new ArrayList<>(), mBoardView.getColumnCount() + 1);
////                    addTask(payloadTaskList.get(0), mBoardView.getColumnCount());
////                    long id = sCreatedItems++;
////                    Pair item = new Pair<>(id, payloadTaskList.get(0));
////                    mBoardView.addItem(mBoardView.getColumnCount(), 0, item, false);
//                    viewModel.getAllDbTaskByAssigneeDate(assignee.getId(), DateHelper.compareDate(mBoardView.getColumnCount()));
//                    viewModel.getFilteredTaskListMutableLiveData().observe(getViewLifecycleOwner(), getFilteredTask);
//                }

                mBoardView.setLastColumn(newColumn);


                if (firstRun)
                    if (mBoardView.getFocusedColumn() == 15) {
                        if (newColumn < oldColumn)
                            leftScroll.startAnimation(animOutLeft);
                        if (newColumn > oldColumn)
                            rightScroll.startAnimation(animOutRight);
                        leftScroll.setVisibility(View.GONE);
                        rightScroll.setVisibility(View.GONE);
                    } else if (mBoardView.getFocusedColumn() < 15) {
                        leftScroll.setVisibility(View.GONE);
                        rightScroll.setVisibility(View.VISIBLE);
                        if (mBoardView.getFocusedColumn() == 14 && oldColumn > newColumn)
                            rightScroll.startAnimation(animRight);
                    } else if (mBoardView.getFocusedColumn() > 15) {
                        rightScroll.setVisibility(View.GONE);
                        leftScroll.setVisibility(View.VISIBLE);
                        if (mBoardView.getFocusedColumn() == 16 && oldColumn < newColumn)
                            leftScroll.startAnimation(animLeft);
                    }
            }

            @Override
            public void onColumnDragStarted(int position) {
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragChangedPosition(int oldPosition, int newPosition) {
//                Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragEnded(int position) {
//                Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
            }

        });
        mBoardView.setBoardCallback(new BoardView.BoardCallback() {
            @Override
            public boolean canDragItemAtPosition(int column, int dragPosition) {
                // Add logic here to prevent an item to be dragged
                return true;
            }

            @Override
            public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                // Add logic here to prevent an item to be dropped
                return true;
            }
        });
        viewModel.setContext(getContext());
        viewModel.getEpicLinksMutableLiveData().observe(getViewLifecycleOwner(), getEpicLinks);
        viewModel.getProjectMutableLiveData().observe(getViewLifecycleOwner(), getProjectEntity);
        viewModel.getDurationMutableLiveData().observe(getViewLifecycleOwner(), getDuration);
        viewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), shortMembers);
//        viewModel.downloadTasksByAssignee(user.getId());
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), getTask);
        oldAssignee = assignee.getId();
    }

    private Observer<List<TaskEntity>> getTaskEntity = taskEntityList -> {
        Toast.makeText(getActivity(),String.valueOf(taskEntityList.size()), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        mBoardView.setVisibility(View.GONE);
        List<PayloadTask> taskList;
        taskList = converter.fromTaskEntityToPayloadTaskList(taskEntityList);

        payloadTaskList = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getAssignee().equals(assignee.getId()))
                payloadTaskList.add(taskList.get(i));
        }
        viewModel.getProjectMutableLiveData();

        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute(payloadTaskList);
    };

    private void selectCurrentAssignee(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("members", (ArrayList<? extends Parcelable>) memberList);
            bundle.putParcelable("calendarViewModel", viewModel);
            BottomSheetCalendarSelectUser mySheetDialog = new BottomSheetCalendarSelectUser();
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }

    private void scrollToCurrentDate(View view) {
        mBoardView.scrollToColumn(15, true);
    }

    private void resetBoard() {
//        mBoardView.setLastColumn(mBoardView.getFocusedColumn());
        mBoardView.clearBoard();
    }

    private void addColumn(String month, String day, List<PayloadTask> payloadTaskList, int columnNumber) {
        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
        int estimatedTime = 0;
        int spentTime = 0;
        String date = Constants.dateColumnMap.get(columnNumber);
        for (int i = 0; i < payloadTaskList.size(); i++) {
            estimatedTime += payloadTaskList.get(i).getDuration();
        }

        int addItems = payloadTaskList.size();
        for (int i = 0; i < addItems; i++) {
            long id = sCreatedItems++;
            mItemArray.add(new Pair<Long, PayloadTask>(id, payloadTaskList.get(i)));
        }

        for (int j = 0; j < durationActualList.size(); j++) {
            if (Constants.dateColumnMap.get(columnNumber).equals(durationActualList.get(j).getDate()) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                spentTime += (durationActualList.get(j).getValue());
            }
        }

        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true, getContext(), token, epicLinksEntities, projectEntities, viewModel);
        listAdapter.setContext(getContext());
        listAdapter.setmDragOnLongPress(true);
        listAdapter.setmLayoutId(R.layout.column_item);
        listAdapter.setmGrabHandleId(R.id.item_layout);
        final View header = View.inflate(getActivity(), R.layout.column_header, null);
        int finalEstimatedTime = estimatedTime;
        int finalSpentTime = spentTime;

        header.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putInt("estimatedTime", finalEstimatedTime);
                bundle.putInt("spentTime", finalSpentTime);
                bundle.putString("id", assignee.getId());
                BottomSheetDayInfo mySheetDialog = new BottomSheetDayInfo();
                mySheetDialog.setArguments(bundle);
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                mySheetDialog.show(fm, "dayInfo");
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

        ((TextView) header.findViewById(R.id.header_date)).setText(month);
        ((TextView) header.findViewById(R.id.header_day)).setText(day);

        HourCounterView spentTimeCounter = ((HourCounterView) header.findViewById(R.id.hour_counter_spent));
        HourCounterView estimatedTimeCounter = ((HourCounterView) header.findViewById(R.id.hour_counter_estimated));

        if (estimatedTime % 60 == 0)
            estimatedTimeCounter.setProgress(String.valueOf(estimatedTime / 60));
        else {
            String s = df.format(estimatedTime / 60.0f);
            s = s.replace(',', '.');
            estimatedTimeCounter.setProgress(s);
        }

        if (spentTime % 60 == 0)
            spentTimeCounter.setProgress(String.valueOf(spentTime / 60));
        else {
            String s = df.format(spentTime / 60.0f);
            s = s.replace(',', '.');
            spentTimeCounter.setProgress(s);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header)
                .build();

        mBoardView.addColumn(columnProperties);
    }

    private void goToAddTask(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("members", (Serializable) memberList);
            intent.putExtra("calendarViewModel", viewModel);
            intent.putExtra("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
//            startActivityForResult(intent, 42, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            startActivityForResult(intent, 42);
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 42) {
            PayloadTask task = data.getParcelableExtra("task");
            if (!assignee.getId().equals(oldAssignee)) {
                viewModel.getAllDbTaskByAssignee(assignee.getId());
                viewModel.getAssigneeMutableLiveData();
//                addTask(task);
//                viewModel.getTaskDbLiveDataByAssignee(assignee.getId());
            }
        }
    }

    private void addTask(PayloadTask task, int column) {
        long id = sCreatedItems++;
        Pair item = new Pair<>(id, task);
        mBoardView.addItem(column, 0, item, false);
    }

    private void addTask(PayloadTask task) {
        long id = sCreatedItems++;
        int column = 0;
        Pair item = new Pair<>(id, task);
        for (Map.Entry<Integer, String> entry : Constants.dateColumnMap.entrySet()) {
            if (entry.getValue().equals(task.getPlanDate())) {
                column = entry.getKey();
            }
        }

        if (assignee.getId().equals(task.getAssignee())) {
            HourCounterView hourEstimatedNew = mBoardView.getHeaderView(column).findViewById(R.id.hour_counter_estimated);
            if (task.getDuration() % 60 == 0)
                hourEstimatedNew.setProgress(String.valueOf(Double.parseDouble(hourEstimatedNew.getProgress()) + task.getDuration() / 60));
            else
                hourEstimatedNew.setProgress(df.format(Double.parseDouble(hourEstimatedNew.getProgress()) + task.getDuration() / 60.0f));
            mBoardView.addItem(column, 0, item, false);
        } else {

        }
    }

    private static class MyDragItem extends DragItem {
        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);

            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
//            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

    }

    private class AsyncTask extends android.os.AsyncTask<List<PayloadTask>, Void, List<ArrayList<PayloadTask>>> {
        List<ArrayList<PayloadTask>> tasks = new ArrayList<>();

        @Override
        protected List<ArrayList<PayloadTask>> doInBackground(List<PayloadTask>... taskList) {
            if (taskList != null)
                for (int i = 0; i < 100; i++) {
                    ArrayList<PayloadTask> plannedTask = new ArrayList<>();
                    for (int j = 0; j < taskList[0].size(); j++) {
                        if (taskList[0].get(j).getPlanDate() != null) {
                            if (taskList[0].get(j).getStatus() != null)
                                if (taskList[0].get(j).getStatus().equals("wip") || taskList[0].get(j).getStatus().equals("done") || taskList[0].get(j).getStatus().equals("canceled") ||
                                        taskList[0].get(j).getStatus().equals("new") && !taskList[0].get(j).getStatus().equals("archived") && !taskList[0].get(j).getStatus().equals("deleted"))
                                    if (taskList[0].get(j).getPlanDate().equals(DateHelper.compareDate(i))) {
                                        plannedTask.add(taskList[0].get(j));
                                    }
                        }
                    }
                    tasks.add(plannedTask);
                }
            return null;
        }

        @Override
        protected void onPostExecute(List<ArrayList<PayloadTask>> taskList) {
            super.onPostExecute(taskList);

            resetBoard();
            for (int i = 0; i < tasks.size(); i++) {
                Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
                addColumn(DateHelper.getDate(DateHelper.compareDate(i)), DateHelper.getDatOfWeek(i), tasks.get(i), i);
            }
            mBoardView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
