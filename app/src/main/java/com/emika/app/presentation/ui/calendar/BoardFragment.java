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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.Assignee;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class BoardFragment extends Fragment {
    private static final String TAG = "BoardFragment";
    private static int sCreatedItems = 0;
    @Inject
    User user;
    @Inject
    Assignee assignee;
    private BoardView mBoardView;
    private ConstraintLayout selectCurrentUser;
    private String token;
    private int mColumns;
    private Button rightScroll, leftScroll;
    private EmikaApplication app = EmikaApplication.getInstance();
    private FloatingActionButton addTask;
    private List<PayloadTask> payloadTaskList = new ArrayList<>();
    private ProfileViewModel profileViewModel;
    private BottomSheetDialogViewModel bottomSheetDialogViewModel;
    private UserNetworkManager userNetworkManager;
    private ImageView fabImg;
    private CalendarViewModel viewModel;
    private TextView fabUserName, fabJobTitle;
    private List<PayloadShortMember> memberList;
    private boolean isOpen = false;
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


    private Observer<Boolean> setColumn = current -> {
        if (current) {
            mBoardView.scrollToColumn(15, true);
        }
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
    };

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
        bottomSheetDialogViewModel = new ViewModelProvider(this).get(BottomSheetDialogViewModel.class);
        token = getActivity().getIntent().getStringExtra("token");
        fabUserName = view.findViewById(R.id.fab_user_name);
        fabImg = view.findViewById(R.id.fab_img);
        fabJobTitle = view.findViewById(R.id.fab_job_title);
        selectCurrentUser = view.findViewById(R.id.select_current_user);
        selectCurrentUser.setOnClickListener(this::selectCurrentAssignee);
        userNetworkManager = new UserNetworkManager(token);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
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
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        mBoardView.setVisibility(View.VISIBLE);
        mBoardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                if (fromColumn != toColumn || fromRow != toRow) {

//                    Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(newColumn).getItemList().get(newRow);
                taskNewPos.second.setPlanDate(Constants.dateColumnMap.get(newColumn));
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
//                PayloadTask payloadTask = taskNewPos.second;
//                viewModel.updateTask(payloadTask);
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {

            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
                if (mBoardView.getFocusedColumn() == 15) {
                    leftScroll.setVisibility(View.GONE);
                    rightScroll.setVisibility(View.GONE);
                } else if (mBoardView.getFocusedColumn() < 15) {
                    leftScroll.setVisibility(View.GONE);
                    rightScroll.setVisibility(View.VISIBLE);
                } else {
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
        if (mBoardView.getFocusedColumn() < 15) {
            leftScroll.setVisibility(View.GONE);
            rightScroll.setVisibility(View.VISIBLE);
            rightScroll.callOnClick();
        } else {
            rightScroll.setVisibility(View.GONE);
            leftScroll.setVisibility(View.VISIBLE);
        }
        viewModel.setCurrentColumn();
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), getTask);
        viewModel.getCurrentDate().observe(getViewLifecycleOwner(), setColumn);
        viewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), shortMembers);
    }

    private Observer<List<PayloadTask>> getTask = taskList -> {
        resetBoard();
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute(taskList);
    };

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
        viewModel.setCurrentColumn();
    }

    private void resetBoard() {
        mBoardView.clearBoard();
    }

    private void addColumn(String month, String day, List<PayloadTask> payloadTaskList) {
        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
        int estimatedTime = 0;
        int spentTime = 0;
        for (int i = 0; i < payloadTaskList.size(); i++) {
            estimatedTime += payloadTaskList.get(i).getDuration() / 60;
            spentTime += payloadTaskList.get(i).getDurationActual() / 60;
        }

        int addItems = payloadTaskList.size();
        for (int i = 0; i < addItems; i++) {
            long id = sCreatedItems++;
            mItemArray.add(new Pair<Long, PayloadTask>(id, payloadTaskList.get(i)));
        }

        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true, getContext(), token);
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
        if (assignee.getId().equals(user.getId())) {
            mBoardView.addItem(column, 0, item, true);
        }else {

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
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
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
            for (int i = 0; i < 40; i++) {
                ArrayList<PayloadTask> plannedTask = new ArrayList<>();
                for (int j = 0; j < taskList[0].size(); j++) {
                    if (taskList[0].get(j).getPlanDate() != null && taskList[0].get(j).getAssignee().equals(assignee.getId())) {
                        if (taskList[0].get(j).getStatus().equals("wip") || taskList[0].get(j).getStatus().equals("done") || taskList[0].get(j).getStatus().equals("canceled") ||
                                taskList[0].get(j).getStatus().equals("new"))
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

            for (int i = 0; i < tasks.size(); i++) {
                Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
                addColumn(DateHelper.getDate(DateHelper.compareDate(i)), "monday", tasks.get(i));
            }
        }
    }

}
