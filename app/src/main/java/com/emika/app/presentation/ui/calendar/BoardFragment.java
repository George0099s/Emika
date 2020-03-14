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
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.db.dbmanager.TaskDbManager;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.features.calendar.BoardView;
import com.emika.app.features.calendar.ColumnProperties;
import com.emika.app.features.calendar.DragItem;
import com.emika.app.presentation.adapter.calendar.ItemAdapter;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BoardFragment extends Fragment {
    private static final String TAG = "BoardFragment";
    private static int sCreatedItems = 0;
    private BoardView mBoardView;
    private String token;
    private int mColumns;
    private FloatingActionButton addTask;
    private List<PayloadTask> payloadTaskList = new ArrayList<>();
    private CalendarViewModel viewModel;
    private ProgressBar progressBar;
    private ItemAdapter listAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private Observer<List<PayloadTask>> getTask = taskList -> {
        setTasks(taskList);

//        Observable.just(1, 2, 3, 4, 5, 6)
//                .subscribeOn(Schedulers.io())
////                .subscribeOn(Schedulers.computation())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .doOnNext(integer -> Log.d(TAG, ": " + integer + " on: " + currentThread().getName()))
//                .subscribe(integer -> Log.d(TAG, ": " +  + integer + " on: " + currentThread().getName()));
//        Observable.fromCallable((new SetTaskCallable(taskList)))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
    };
    private Observer<Boolean> setColumn = current -> {
        Toast.makeText(getContext(), "Current", Toast.LENGTH_SHORT).show();
        if (current)
            mBoardView.scrollToColumn(15, true);
    };

    private List<PayloadTask> setTasks(List<PayloadTask> taskList) {
        for (int i = 0; i < 40; i++) {
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
            mBoardView.scrollToColumn(15,true);
        return taskList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        initViews(view);
        return view;
    }


    private void initViews(View view) {
        listAdapter = new ItemAdapter();
        listAdapter.setContext(getContext());
        listAdapter.setmDragOnLongPress(true);
        listAdapter.setmLayoutId(R.layout.column_item);
        listAdapter.setmGrabHandleId(R.id.item_layout);
        addTask = view.findViewById(R.id.add_task);

        addTask.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getParentFragmentManager(), "1");
//            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),
//                    R.style.BottomSheetStyleDialogTheme);
//            View bottomSheetView = LayoutInflater.from(getContext())
//                    .inflate(R.layout.bottom_sheet_dialog_fragment,
//                            view.findViewById(R.id.bottom_sheet_add_task));
//            bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            bottomSheetDialog.setContentView(bottomSheetView);
//            bottomSheetDialog.show();

        });
        token = getActivity().getIntent().getStringExtra("token");
        mBoardView = view.findViewById(R.id.board_view);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        viewModel.setContext(getContext());
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), getTask);
        viewModel.getCurrentDate().observe(getViewLifecycleOwner(), setColumn);
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
//                PayloadTask payloadTask = taskNewPos.second;
//                viewModel.updateTask(payloadTask);
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
//                Log.d(TAG, "onItemChangedColumn() called with: oldColumn = [" + oldColumn + "], newColumn = [" + newColumn + "]");

//                mBoardView.getAdapter(oldColumn).getItemList().get()
                TextView itemCount1 = mBoardView.getHeaderView(oldColumn).findViewById(R.id.header_day);
                itemCount1.setText(String.valueOf(mBoardView.getAdapter(oldColumn).getItemCount()));
                TextView itemCount2 = mBoardView.getHeaderView(newColumn).findViewById(R.id.header_day);
                itemCount2.setText(String.valueOf(mBoardView.getAdapter(newColumn).getItemCount()));
            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {

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
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_board, menu);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.action_disable_drag).setVisible(mBoardView.isDragEnabled());
//        menu.findItem(R.id.action_enable_drag).setVisible(!mBoardView.isDragEnabled());
//        menu.findItem(R.id.action_grid).setVisible(!mGridLayout);
//        menu.findItem(R.id.action_list).setVisible(mGridLayout);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_disable_drag:
//                mBoardView.setDragEnabled(false);
//                getActivity().invalidateOptionsMenu();
//                return true;
//            case R.id.action_enable_drag:
//                mBoardView.setDragEnabled(true);
//                getActivity().invalidateOptionsMenu();
//                return true;
//            case R.id.action_grid:
//                mGridLayout = true;
//                resetBoard();
//                getActivity().invalidateOptionsMenu();
//                return true;
//            case R.id.action_list:
//                mGridLayout = false;
//                resetBoard();
//                getActivity().invalidateOptionsMenu();
//                return true;
//            case R.id.action_add_column:
//                addColumn();
//                return true;
//            case R.id.action_remove_column:
//                mBoardView.removeColumn(0);
//                return true;
//            case R.id.action_clear_board:
//                mBoardView.clearBoard();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void resetBoard() {
        mBoardView.clearBoard();
        mBoardView.setCustomDragItem(new MyDragItem(getActivity(), R.layout.column_item));
        mBoardView.setCustomColumnDragItem(new MyColumnDragItem(getActivity(), R.layout.column_drag_layout));

    }

    private void addColumn(String month, String day, List<PayloadTask> payloadTaskList) {
        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
        int addItems = payloadTaskList.size();
        for (int i = 0; i < addItems; i++) {
            long id = sCreatedItems++;
            mItemArray.add(new Pair<Long, PayloadTask>(id, payloadTaskList.get(i)));
        }
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true, getContext());
        final View header = View.inflate(getActivity(), R.layout.column_header, null);
        ((TextView) header.findViewById(R.id.header_date)).setText(month);
        ((TextView) header.findViewById(R.id.header_day)).setText(day);


//        header.setOnClickListener(v -> {  //ДОБАВЛЕНИЕ ТАСКА
//            long id = sCreatedItems++;
//            Pair item = new Pair<>(id, payloadTaskList.get(0));
//            mBoardView.addItem(mBoardView.getColumnOfHeader(v), payloadTaskList.get(0).getOrder(), item, true);
        //mBoardView.moveItem(4, 0, 0, true);
//            mBoardView.removeItem(column, 0);
        //mBoardView.moveItem(0, 0, 1, 3, false);
        //mBoardView.replaceItem(0, 0, item1, true);
//            ((TextView) header.findViewById(R.id.header_day)).setText(String.valueOf(mItemArray.size()));
//        });

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


}
