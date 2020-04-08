/*
 * Copyright 2014 Magnus Woxblom
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emika.app.presentation.ui.calendar;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    User user;
    @Inject
    Assignee assignee;
    @Inject
    EpicLinks epicLinks;
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
    private Observer<List<EpicLinksEntity>> getEpicLinks = epicLinksEntities -> {
        this.epicLinksEntities = epicLinksEntities;
    };
    private Observer<List<PayloadTask>> getTask = taskList -> {
        payloadTaskList = taskList;
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute(taskList);
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
            Log.d(TAG, ":create " + value);
            status = jsonObject.getString("status");
            taskId = jsonObject.getString("task_id");
            projectId = jsonObject.getString("project_id");
            companyId = jsonObject.getString("company_id");
            date = jsonObject.getString("date");
            createdAt = jsonObject.getString("created_at");
            createdBy = jsonObject.getString("created_by");
            person = jsonObject.getString("person");
            int spentTime = 0;
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
                    HourCounterView hourEstimatedNew = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                    hourEstimatedNew.setProgress(hourEstimatedNew.getProgress() + value / 60);
                }
            }
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
                            spentTime += (durationActualList.get(j).getValue() / 60);
                        }
                        ((HourCounterView) mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent)).setProgress(spentTime);
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
        try {
            jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            value = jsonObject.getString("value");
            Log.d(TAG, ":update " + value);
            status = jsonObject.getString("status");
            taskId = jsonObject.getString("task_id");
            projectId = jsonObject.getString("project_id");
            companyId = jsonObject.getString("company_id");
            date = jsonObject.getString("date");
            createdAt = jsonObject.getString("created_at");
            createdBy = jsonObject.getString("created_by");
            person = jsonObject.getString("person");


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
                            spentTime += (durationActualList.get(j).getValue() / 60);
                        }
                        ((HourCounterView) mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent)).setProgress(spentTime);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View view) {
        app.getComponent().inject(this);
        token = getActivity().getIntent().getStringExtra("token");
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
        fabUserName = view.findViewById(R.id.fab_user_name);
        fabImg = view.findViewById(R.id.fab_img);
        fabJobTitle = view.findViewById(R.id.fab_job_title);
        selectCurrentUser = view.findViewById(R.id.select_current_user);
        selectCurrentUser.setOnClickListener(this::selectCurrentAssignee);
        userNetworkManager = new UserNetworkManager(token);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        profileViewModel.setContext(getContext());
        profileViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), userInfo);
        rightScroll = view.findViewById(R.id.right_scroll_to_current_date);
        rightScroll = view.findViewById(R.id.right_scroll_to_current_date);
        leftScroll = view.findViewById(R.id.left_scroll_to_current_date);
        rightScroll.setOnClickListener(this::scrollToCurrentDate);
        leftScroll.setOnClickListener(this::scrollToCurrentDate);
        addTask = view.findViewById(R.id.add_task);
        addTask.setOnClickListener(this::goToAddTask);
        mBoardView = view.findViewById(R.id.board_view);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        viewModel.getDurationMutableLiveData().observe(getViewLifecycleOwner(), getDuration);
        viewModel.getEpicLinksMutableLiveData().observe(getViewLifecycleOwner(), getEpicLinks);
        viewModel.getProjectMutableLiveData().observe(getViewLifecycleOwner(), getProjectEntity);
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
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                Toast.makeText(app, oldRow + " " + newRow, Toast.LENGTH_SHORT).show();
                Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(newColumn).getItemList().get(newRow);
                taskNewPos.second.setPlanDate(Constants.dateColumnMap.get(newColumn));
                taskNewPos.second.setPlanOrder(String.valueOf(newRow + 1));
                viewModel.updateTask(taskNewPos.second);
                int estimatedTimeNew = 0;
                HourCounterView hourEstimatedOld = mBoardView.getHeaderView(oldColumn).findViewById(R.id.hour_counter_estimated);
                HourCounterView hourEstimatedNew = mBoardView.getHeaderView(newColumn).findViewById(R.id.hour_counter_estimated);
                ArrayList<Pair<Long, PayloadTask>> newTasks = mBoardView.getAdapter(newColumn).getItemList();
                hourEstimatedOld.setProgress(hourEstimatedOld.getProgress() - taskNewPos.second.getDuration() / 60);
                for (int i = 0; i < newTasks.size(); i++) {
                    Pair<Long, PayloadTask> pair = newTasks.get(i);
                    PayloadTask task = pair.second;
                    estimatedTimeNew += task.getDuration() / 60;
                    hourEstimatedNew.setProgress(estimatedTimeNew);
                }

            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {

            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
                if (firstRun)
                    if (mBoardView.getFocusedColumn() == 15) {
                        leftScroll.setVisibility(View.GONE);
                        rightScroll.setVisibility(View.GONE);
                    } else if (mBoardView.getFocusedColumn() < 15) {
                        leftScroll.setVisibility(View.GONE);
                        rightScroll.setVisibility(View.VISIBLE);
                    } else if (mBoardView.getFocusedColumn() > 15) {
                        rightScroll.setVisibility(View.GONE);
                        leftScroll.setVisibility(View.VISIBLE);
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
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), getTask);
        viewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), shortMembers);
    }
    private Emitter.Listener onTaskUpdate = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        Boolean hasTask = false;
        int row;
        JSONObject jsonObject = null;
        String name, assignee, id, priority, planDate, deadlineDate, estimatedTime, spentTime, status;
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
                assignee = jsonObject.getString("assignee");
                row = jsonObject.getInt("plan_order");
                epicLinks = jsonObject.getJSONArray("epic_links");
                if (epicLinks != null && epicLinks.length() != 0)
                    for (int i = 0; i < epicLinks.length(); i++) {
                        epicLinkList.add((String) epicLinks.get(i));
                    }
                deadlineDate = jsonObject.getString("deadline_date");
                estimatedTime = jsonObject.getString("duration");
                spentTime = jsonObject.getString("duration_actual");
                for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                    for (int j = 0; j < mBoardView.getAdapter(i).getItemCount(); j++) {
                        Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(j);
                        if (taskNewPos.second.getId().equals(id)) {
                            hasTask = true;
                            taskNewPos.second.setName(name);
                            taskNewPos.second.setDurationActual(Integer.valueOf(spentTime));
                            taskNewPos.second.setDuration(Integer.valueOf(estimatedTime));
                            taskNewPos.second.setDeadlineDate(deadlineDate);
                            taskNewPos.second.setPlanOrder(String.valueOf(row));
                            taskNewPos.second.setAssignee(assignee);
                            taskNewPos.second.setEpicLinks(epicLinkList);
                            taskNewPos.second.setPriority(priority);
                            taskNewPos.second.setStatus(status);
                            if (taskNewPos.second.getPlanDate().equals(planDate)) {
//                                mBoardView.removeItem(i, j);
//                                mBoardView.addItem(i, row-1, taskNewPos, false);
                                mBoardView.getAdapter(i).swapItems(j, row-1);
                                mBoardView.invalidate();
                            } else {
                                for (int k = 0; k < mBoardView.getColumnCount(); k++) {
                                    if (planDate.equals(Constants.dateColumnMap.get(k))) {
                                        taskNewPos.second.setPlanDate(planDate);
//                                        mBoardView.removeItem(i, j);
                                        mBoardView.moveItem(i, j, k, row -1, false);
                                    }
                                }
                            }
//

                        }
                    }
                }
                if (assignee.equals(this.assignee.getId()) && !hasTask) {
                    PayloadTask task = new PayloadTask();
                    task.setName(name);
                    task.setId(id);
                    task.setDurationActual(Integer.valueOf(spentTime));
                    task.setDuration(Integer.valueOf(estimatedTime));
                    task.setDeadlineDate(deadlineDate);
                    task.setPlanDate(planDate);
                    task.setAssignee(assignee);
                    task.setPriority(priority);
                    task.setStatus(status);
                    addTask(task);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    });
    private void selectCurrentAssignee(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", (ArrayList<? extends Parcelable>) memberList);
        bundle.putParcelable("viewModel", viewModel);
        BottomSheetCalendarSelectUser mySheetDialog = new BottomSheetCalendarSelectUser();
        mySheetDialog.setArguments(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }

    private void scrollToCurrentDate(View view) {
        mBoardView.scrollToColumn(15, true);
    }

    private void resetBoard() {
        mBoardView.clearBoard();
    }

    private void addColumn(String month, String day, List<PayloadTask> payloadTaskList, int columnNumber) {
        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
        int estimatedTime = 0;
        int spentTime = 0;
        for (int i = 0; i < payloadTaskList.size(); i++) {
            estimatedTime += payloadTaskList.get(i).getDuration() / 60;
        }

        int addItems = payloadTaskList.size();
        for (int i = 0; i < addItems; i++) {
            long id = sCreatedItems++;
            mItemArray.add(new Pair<Long, PayloadTask>(id, payloadTaskList.get(i)));
        }


        for (int j = 0; j < durationActualList.size(); j++) {
            if (Constants.dateColumnMap.get(columnNumber).equals(durationActualList.get(j).getDate()) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
                spentTime += (durationActualList.get(j).getValue() / 60);
            }
        }

        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true, getContext(), token, epicLinksEntities, projectEntities);
        listAdapter.setContext(getContext());
        listAdapter.setmDragOnLongPress(true);
        listAdapter.setmLayoutId(R.layout.column_item);
        listAdapter.setmGrabHandleId(R.id.item_layout);

        final View header = View.inflate(getActivity(), R.layout.column_header, null);

        ((TextView) header.findViewById(R.id.header_date)).setText(month);
        ((TextView) header.findViewById(R.id.header_day)).setText(day);


        ((HourCounterView) header.findViewById(R.id.hour_counter_spent)).setProgress(spentTime);
        ((HourCounterView) header.findViewById(R.id.hour_counter_estimated)).setProgress(estimatedTime);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header)
                .build();

        mBoardView.addColumn(columnProperties);
        mBoardView.scrollToColumn(mBoardView.getFocusedColumn() + 1, true);
    }

    private void goToAddTask(View v) {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("members", (Serializable) memberList);
        intent.putExtra("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
        startActivityForResult(intent, 42, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 42) {
            PayloadTask task = data.getParcelableExtra("task");
            addTask(task);
        }
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
            mBoardView.addItem(column, 0, item, true);
        } else {
//
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
            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
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
                for (int i = 0; i < 40; i++) {
                    ArrayList<PayloadTask> plannedTask = new ArrayList<>();
                    for (int j = 0; j < taskList[0].size(); j++) {
                        if (taskList[0].get(j).getPlanDate() != null && taskList[0].get(j).getAssignee().equals(assignee.getId())) {
                            if (taskList[0].get(j).getStatus() != null)
                                if (taskList[0].get(j).getStatus().equals("wip") || taskList[0].get(j).getStatus().equals("done") || taskList[0].get(j).getStatus().equals("canceled") ||
                                        taskList[0].get(j).getStatus().equals("new") && !taskList[0].get(j).getStatus().equals("archived"))
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
        }
    }

}
