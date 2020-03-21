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

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

public class BoardFragment extends Fragment {
    private static final String TAG = "BoardFragment";
    private static int sCreatedItems = 0;
    @Inject
    User user;
    private BoardView mBoardView;
    private ConstraintLayout selectCurrentUser;
    private String token;
    private int mColumns;
    private boolean isOpen = false;
    private Button rightScroll, leftScroll;
    private EmikaApplication app = EmikaApplication.getInstance();
    private FloatingActionButton addTask;
    private List<PayloadTask> payloadTaskList = new ArrayList<>();
    private CalendarViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private BottomSheetDialogViewModel bottomSheetDialogViewModel;
    private ItemAdapter listAdapter;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private UserNetworkManager userNetworkManager;
    private Boolean first = true;
    private ImageView fabImg;
    private TextView fabUserName, fabJobTitle;
    private List<PayloadShortMember> memberList;

    private List<PayloadTask> setTasks(List<PayloadTask> taskList) {
        for (int i = 0; i < 20; i++) {
            ArrayList<PayloadTask> plannedTask = new ArrayList<>();
            for (int j = 0; j < taskList.size(); j++) {
                if (taskList.get(j).getPlanDate() != null) {
                    if (taskList.get(j).getPlanDate().equals(DateHelper.compareDate(i))) {
                        plannedTask.add(taskList.get(j));
                    }
                }
            }
            Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
            addColumn(DateHelper.getDate(DateHelper.compareDate(i)), "monday", plannedTask);
        }
        mBoardView.scrollToColumn(15, true);
        return taskList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewModel.getUserMutableLiveData();
    }

    private void initViews(View view) {
        app.getComponent().inject(this);
        bottomSheetDialogViewModel = new ViewModelProvider(this).get(BottomSheetDialogViewModel.class);
        token = getActivity().getIntent().getStringExtra("token");
        fabUserName = view.findViewById(R.id.fab_user_name);
        fabImg = view.findViewById(R.id.fab_img);
        fabJobTitle = view.findViewById(R.id.fab_job_title);
        selectCurrentUser = view.findViewById(R.id.select_current_user);
        selectCurrentUser.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList("members", (ArrayList<? extends Parcelable>) memberList);
            BottomSheetSelectUser mySheetDialog = new BottomSheetSelectUser();
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            mySheetDialog.show(fm, "modalSheetDialog");
        });
        userNetworkManager = new UserNetworkManager(token);

        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        profileViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), userInfo);

        rightScroll = view.findViewById(R.id.right_scroll_to_current_date);
        leftScroll = view.findViewById(R.id.left_scroll_to_current_date);
        rightScroll.setOnClickListener(this::scrollToCurrentDate);
        leftScroll.setOnClickListener(this::scrollToCurrentDate);

        addTask = view.findViewById(R.id.add_task);
        addTask.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()).toString());
            startActivityForResult(intent, 42, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        });
        mBoardView = view.findViewById(R.id.board_view);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        viewModel.setCurrentColumn();
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

        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), getTask);
        viewModel.getCurrentDate().observe(getViewLifecycleOwner(), setColumn);
        viewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), shortMembers);

    }

    private Observer<List<PayloadShortMember>> shortMembers = members ->{
        memberList = members;
        Toast.makeText(app,String.valueOf(memberList.size()), Toast.LENGTH_SHORT).show();
    };
    private void scrollToCurrentDate(View view) {
        viewModel.setCurrentColumn();
    }


//    private void resetBoard() {
//        mBoardView.clearBoard();
//        mBoardView.setCustomDragItem(new MyDragItem(getActivity(), R.layout.column_item));
//        mBoardView.setCustomColumnDragItem(new MyColumnDragItem(getActivity(), R.layout.column_drag_layout));
//    }
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

    private static class MyColumnDragItem extends DragItem {

        MyColumnDragItem(Context context, int layoutId) {
            super(context, layoutId);
            setSnapToTouch(false);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            LinearLayout clickedLayout = (LinearLayout) clickedView;
            View clickedHeader = clickedLayout.getChildAt(0);
            RecyclerView clickedRecyclerView = (RecyclerView) clickedLayout.getChildAt(1);

            View dragHeader = dragView.findViewById(R.id.drag_header);
            ScrollView dragScrollView = dragView.findViewById(R.id.drag_scroll_view);
            LinearLayout dragLayout = dragView.findViewById(R.id.drag_list);

            Drawable clickedColumnBackground = clickedLayout.getBackground();
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground);
            }

            Drawable clickedRecyclerBackground = clickedRecyclerView.getBackground();
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground);
            }

            dragLayout.removeAllViews();

            ((TextView) dragHeader.findViewById(R.id.header_date)).setText(((TextView) clickedHeader.findViewById(R.id.header_date)).getText());
            ((TextView) dragHeader.findViewById(R.id.header_day)).setText(((TextView) clickedHeader.findViewById(R.id.header_day)).getText());
            for (int i = 0; i < clickedRecyclerView.getChildCount(); i++) {
                View view = View.inflate(dragView.getContext(), R.layout.column_item, null);
                ((TextView) view.findViewById(R.id.header_date)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.header_date)).getText());
                dragLayout.addView(view);

                if (i == 0) {
                    dragScrollView.setScrollY(-clickedRecyclerView.getChildAt(i).getTop());
                }
            }

            dragView.setPivotY(0);
            dragView.setPivotX(clickedView.getMeasuredWidth() / 2);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            super.onStartDragAnimation(dragView);
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            super.onEndDragAnimation(dragView);
            dragView.animate().scaleX(1).scaleY(1).start();
        }

    }

    private Observer<List<PayloadTask>> getTask = taskList -> {
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute(taskList);
        Toast.makeText(app, String.valueOf(mBoardView.getColumnCount()), Toast.LENGTH_SHORT).show();
    };

    private Observer<Boolean> setColumn = current -> {
        if (current) {
            mBoardView.scrollToColumn(15, true);
        }
    };


    private Observer<Payload> userInfo = userInfo -> {
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setId(userInfo.getId());
        user.setBio(userInfo.getBio());
        user.setPictureUrl(userInfo.getPictureUrl());
        user.setJobTitle(userInfo.getJobTitle());
        fabUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        fabJobTitle.setText(user.getJobTitle());
        Glide.with(this).load(user.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(fabImg);
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
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
        mBoardView.addItem(column, 0, item, true);

//            Log.d(TAG, "addTask: " + mBoardView.getItemCount(mBoardView.getFocusedColumn()));
//            mBoardView.moveItem(4, 0, 0, true);
//            mBoardView.removeItem(column, 0);
//            mBoardView.moveItem(0, 0, 1, 3, false);
//            mBoardView.replaceItem(0, 0, item1, true);
//            ((TextView) header.findViewById(R.id.header_day)).setText(String.valueOf(mItemArray.size()));
    }

    private class SetTaskCallable implements Callable<List<PayloadTask>> {
        List<PayloadTask> taskList;

        public SetTaskCallable(List<PayloadTask> taskList) {
            this.taskList = taskList;
        }

        @Override
        public List<PayloadTask> call() throws Exception {
            return setTasks(taskList);
        }
    }

    private class AsyncTask extends android.os.AsyncTask<List<PayloadTask>, Void, List<ArrayList<PayloadTask>>> {
        List<ArrayList<PayloadTask>> tasks = new ArrayList<>();

        @Override
        protected List<ArrayList<PayloadTask>> doInBackground(List<PayloadTask>... taskList) {
            for (int i = 0; i < 40; i++) {
                ArrayList<PayloadTask> plannedTask = new ArrayList<>();
                for (int j = 0; j < taskList[0].size(); j++) {
                    if (taskList[0].get(j).getPlanDate() != null && taskList[0].get(j).getAssignee().equals(user.getId())){
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
