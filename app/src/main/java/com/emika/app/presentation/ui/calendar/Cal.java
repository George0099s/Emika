package com.emika.app.presentation.ui.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.emika.app.features.hourcounter.HourCounterView;
import com.emika.app.features.hourcounter.LinearHourCounterView;
import com.emika.app.presentation.adapter.calendar.ItemAdapter;
import com.emika.app.presentation.ui.chat.ChatActivity;
import com.emika.app.presentation.ui.profile.ProfileActivity;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.DateHelper;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetDialogViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class Cal extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.board_layout);
//    }


    private static final String TAG = "BoardFragment";
    private static int sCreatedItems = 0;
    @Inject
    Assignee assignee;
    @Inject
    EpicLinks epicLinks;
    @Inject
    User user;
    private long mLastClickTime = 0;
    private BoardView mBoardView;
    private ConstraintLayout selectCurrentUser;
    private String token;
    private int mColumns;
    private List<EpicLinksEntity> epicLinksEntities = new ArrayList<>();
    private ImageButton rightScroll, leftScroll;
    private EmikaApplication app = EmikaApplication.instance;
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
    private boolean firstRun = true;
    private List<PayloadDurationActual> durationActualList = new ArrayList<>();
    private JSONObject tokenJson = new JSONObject();
    private Socket socket;
    private DecimalFormat df;
    private Animation animRight;
    private Animation animLeft;
    private Animation animOutLeft;
    private Animation animOutRight;
    private String oldAssignee;
    private Vibrator vibrator;
    private TextView date, day;
    private StartActivityViewModel startActivityViewModel;
    private Converter converter;
    private Animation anim;
    AnimationSet set = new AnimationSet(true);
    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    LayoutAnimationController controller;


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
        if (currentAssignee.getPictureUrl() != null) {
            Glide.with(this).load(currentAssignee.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(fabImg);

        }

        else {
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into(fabImg);
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
        if (userInfo.getPictureUrl() != null)
            Glide.with(this).load(userInfo.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into((ImageView)findViewById(R.id.profile));
        else
            Glide.with(this).load("https://api.emika.ai/public_api/common/files/default").apply(RequestOptions.circleCropTransform()).into((ImageView)findViewById(R.id.profile));
        Glide.with(this).load(assignee.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(fabImg);
        viewModel.getAssigneeMutableLiveData().observe(this, getAssignee);
        viewModel.insertDbUser(userInfo);
    };

    private Emitter.Listener onCreateActualDuration = args -> Objects.requireNonNull(this).runOnUiThread(() -> {
        String id, status, taskId, projectId, companyId, date, person, createdAt, createdBy;
        int value;
        double estimatedTime = 0;
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
                    LinearHourCounterView spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                    LinearHourCounterView estimatedTimeHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated);
                    estimatedTime = Double.parseDouble(estimatedTimeHourCounterView.getProgress());

                    if (estimatedTime % 60 == 0) {
                        if(estimatedTime - Integer.parseInt(String.valueOf(value / 60)) < 0)
                            estimatedTimeHourCounterView.setProgress(String.valueOf(0));
                        else
                            estimatedTimeHourCounterView.setProgress(String.valueOf(estimatedTime - value / 60));
                    } else {
                        if (estimatedTime - value / 60.0f < 0)
                            estimatedTimeHourCounterView.setProgress("0");
                        else {
                            String s = df.format(estimatedTime - value / 60.0f);
                            s = s.replace(',', '.');
                            estimatedTimeHourCounterView.setProgress(s);
                        }
                    }

                    estimatedTimeHourCounterView.setVisibility(View.GONE);
                    spentHourCounterView.setVisibility(View.VISIBLE);
                    value += Double.parseDouble(spentHourCounterView.getProgress()) * 60;

                    if (value % 60 == 0)
                        spentHourCounterView.setProgress(String.valueOf(Integer.parseInt(spentHourCounterView.getProgress()) + value / 60));
                    else {
                        String s = df.format((Double.parseDouble(spentHourCounterView.getProgress()) + value / 60.0f));
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


    private Emitter.Listener onDeleteActualDuration = args -> Objects.requireNonNull(this).runOnUiThread(() -> {
        String id, date;
        date = null;
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        PayloadDurationActual durationActual = new PayloadDurationActual();
        LinearHourCounterView spentHourCounterView = null, estimatedTimeHourCounterView = null;
        try {
            jsonArray = new JSONArray(Arrays.toString(args));
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            durationActual.setId(id);
            int spentTime = 0;
            int taskSpentTime = 0;
            double estimatedTime = 0;
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

                        spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                        if (spentTime % 60 == 0)
                            spentHourCounterView.setProgress(String.valueOf(spentTime / 60));
                        else {
                            String s = df.format(spentTime / 60.0f);
                            s = s.replace(',', '.');
                            spentHourCounterView.setProgress(s);
                        }
                    }
                }


                if (Constants.dateColumnMap.get(i).equals(date)){
                    estimatedTimeHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated);
                    for (int q = 0; q < mBoardView.getAdapter(i).getItemList().size(); q++) {
                        Pair<Long, PayloadTask> task = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(q);
                        estimatedTime += task.second.getDuration();
                    }

                    estimatedTime = estimatedTime / 60;
                    if (estimatedTime % 60 == 0) {
                        estimatedTimeHourCounterView.setProgress(String.valueOf(estimatedTime - spentTime / 60));
                    } else {
                        String s = df.format(estimatedTime - spentTime / 60.0f);
                        s = s.replace(',', '.');
                        estimatedTimeHourCounterView.setProgress(s);
                    }
                    if (spentTime == 0) {
                        estimatedTimeHourCounterView.setVisibility(View.VISIBLE);
                        spentHourCounterView.setVisibility(View.GONE);
                    } else {
                        estimatedTimeHourCounterView.setVisibility(View.GONE);
                        spentHourCounterView.setVisibility(View.VISIBLE);
                    }
                }


            }
            viewModel.deleteDuration(durationActual);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " );
    }


    private Emitter.Listener onUpdateActualDuration = args -> Objects.requireNonNull(this).runOnUiThread(() -> {
        String id, value, status, taskId, projectId, companyId, date, person, createdAt, createdBy;
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        double estimatedTime = 0;
        LinearHourCounterView spentHourCounterView = null, estimatedTimeHourCounterView = null;
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
            durationActual.setId(id);
            durationActual.setDate(date);
            durationActual.setValue(Integer.valueOf(value));
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

                        spentHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
                        if (spentTime % 60 == 0)
                            spentHourCounterView.setProgress(String.valueOf(spentTime / 60));
                        else {
                            String s = df.format(spentTime / 60.0f);
                            s = s.replace(',', '.');
                            spentHourCounterView.setProgress(s);
                        }
                    }
                }

                if (Constants.dateColumnMap.get(i).equals(date)) {
                    estimatedTimeHourCounterView = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated);
                    for (int q = 0; q < mBoardView.getAdapter(i).getItemList().size(); q++) {
                        Pair<Long, PayloadTask> task = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(q);
                        estimatedTime += task.second.getDuration();
                    }

                    if (estimatedTime % 60 < 0) {
                        if (estimatedTime /60 - spentTime == 0)
                            estimatedTimeHourCounterView.setProgress(String.valueOf(0));
                        else
                            estimatedTimeHourCounterView.setProgress(String.valueOf(estimatedTime /60 - spentTime));
                    } else {
                        if (estimatedTime / 60.0f - spentTime / 60.0f < 0)
                            estimatedTimeHourCounterView.setProgress("0");
                        else {
                            String s = df.format(estimatedTime / 60.0f - spentTime / 60.0f);
                            s = s.replace(',', '.');
                            estimatedTimeHourCounterView.setProgress(s);
                        }
                    }
                    if (spentTime == 0) {
                        estimatedTimeHourCounterView.setVisibility(View.VISIBLE);
                        spentHourCounterView.setVisibility(View.GONE);
                    } else {
                        estimatedTimeHourCounterView.setVisibility(View.GONE);
                        spentHourCounterView.setVisibility(View.VISIBLE);
                    }
                }
            }
            viewModel.updateDbDuration(durationActual);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });

    private Observer<List<PayloadTask>> getTask = taskList -> {
        viewModel.getProjectMutableLiveData();
        for (int i = 0; i < 365; i++) {
            int estimatedTime = 0;
            int taskSpentTime = 0;
            int spentTime = 0;
            LinearHourCounterView spentTimeCounter = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_spent);
            LinearHourCounterView estimatedTimeCounter = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated);
            spentTimeCounter.setProgress("0");
            final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
            mBoardView.getAdapter(i).setItemList(mItemArray);
            for (int j = 0; j < taskList.size(); j++) {
                if (taskList.get(j).getPlanDate() != null && !taskList.get(j).getStatus().equals("archived") && !taskList.get(j).getStatus().equals("deleted"))
                    if (taskList.get(j).getPlanDate().equals(Constants.dateColumnMap.get(i))) {
                        long id = sCreatedItems++;
                        mItemArray.add(new Pair<Long, PayloadTask>(id, taskList.get(j)));
                        Collections.sort(mItemArray, (lhs, rhs) -> {
                            if ((Integer.parseInt(lhs.second.getPlanOrder()) == Integer.parseInt(rhs.second.getPlanOrder()) )) {
                                return 0;
                            } else if ((Integer.parseInt(lhs.second.getPlanOrder()) < Integer.parseInt(rhs.second.getPlanOrder()) )) {
                                return -1;
                            } else {
                                return 1;
                            }
                        });
                        mBoardView.getAdapter(i).setItemList(mItemArray);
                        mBoardView.getAdapter(i).notifyDataSetChanged();
                    }
            }

            for (int s = 0; s < durationActualList.size(); s++) {
                if (Constants.dateColumnMap.get(i).equals(durationActualList.get(s).getDate()) && durationActualList.get(s).getPerson().equals(assignee.getId())) {
                    spentTime += (durationActualList.get(s).getValue());
                    if (spentTime % 60 == 0)
                        spentTimeCounter.setProgress(String.valueOf(spentTime / 60));
                    else {
                        String s2 = df.format(spentTime / 60.0f);
                        s2 = s2.replace(',', '.');
                        spentTimeCounter.setProgress(s2);
                    }
                }
            }

            for (int s = 0; s < mBoardView.getAdapter(i).getItemList().size(); s++) {
                Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(s);
                estimatedTime += taskNewPos.second.getDuration();
                taskSpentTime += taskNewPos.second.getDurationActual();
            }

            estimatedTime -= taskSpentTime;
            if (estimatedTime > 0) {
                if (estimatedTime % 60 == 0)
                    estimatedTimeCounter.setProgress(String.valueOf(estimatedTime / 60));
                else {
                    String s = df.format(estimatedTime / 60.0f);
                    s = s.replace(',', '.');
                    estimatedTimeCounter.setProgress(s);
                }
            } else estimatedTimeCounter.setProgress("0");

            if (spentTime == 0) {
                estimatedTimeCounter.setVisibility(View.VISIBLE);
                spentTimeCounter.setVisibility(View.GONE);
            } else {
                estimatedTimeCounter.setVisibility(View.GONE);
                spentTimeCounter.setVisibility(View.VISIBLE);
            }
        }
        if (firstRun) {
            viewModel.getAllDbTaskByAssignee(assignee.getId());
            firstRun = false;
        }
    };

    private Observer<List<PayloadTask>> getFilteredTask = taskList -> {
        int t = mBoardView.getColumnCount() - 1;
        mBoardView.clearBoard();
        for (int i = 0; i < taskList.size(); i++) {
            addTask(taskList.get(i), t);
        }
    };

    private Emitter.Listener onTaskUpdate = args -> runOnUiThread(() -> {
        Boolean hasTask = false;
        int row;
        JSONObject jsonObject;
        String date, name, assignee, id, priority, planDate, deadlineDate, estimatedTime, spentTime, status, description, parentId, projectId, sectionId;
        JSONArray epicLinks = new JSONArray();
        List<String> epicLinkList = new ArrayList<>();
//        if (getActivity() != null)
            try {
                JSONArray jsonArray = new JSONArray(Arrays.toString(args));
                jsonObject = jsonArray.getJSONObject(0);
                id = jsonObject.getString("_id");
                Log.d(TAG, ": " + id);
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
                deadlineDate = jsonObject.getString("deadline_date");
                estimatedTime = jsonObject.getString("duration");
                spentTime = jsonObject.getString("duration_actual");
                if (epicLinks.length() != 0)
                    for (int i = 0; i < epicLinks.length(); i++) {
                        epicLinkList.add((String) epicLinks.get(i));
                    }

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
                for (int i = 0; i < mBoardView.getColumnCount(); i++) {
                    for (int j = 0; j < mBoardView.getAdapter(i).getItemCount(); j++) {
                        Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(i).getItemList().get(j);
                        assert taskNewPos.second != null;
                        assert taskNewPos.second.getId() != null;
                        if (taskNewPos.second.getId().equals(id) && taskNewPos.second.getId() != null) {
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
                            mBoardView.getAdapter(i).notifyItemInserted(j);
                            int estimatedTimeOld = 0;
                            int taskSpentTimeOld = 0;



                            if (date.equals("null")) {
                                taskNewPos.second.setPlanDate(null);
                                viewModel.updateDbTask(taskNewPos.second);
                                mBoardView.removeItem(i, j);
                                mBoardView.getAdapter(i).notifyItemRemoved(j);
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j, mBoardView.getAdapter(i).getItemCount());
                            } else if (!assignee.equals(oldAssignee)) {
                                taskNewPos.second.setAssignee(assignee);
                                viewModel.updateDbTask(taskNewPos.second);
                                mBoardView.removeItem(i, j);
                                mBoardView.getAdapter(i).notifyItemRemoved(j);
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j, mBoardView.getAdapter(i).getItemCount());

                            } else if (taskNewPos.second.getStatus().equals("deleted")){
                                taskNewPos.second.setPlanDate(planDate);
                                viewModel.updateDbTask(taskNewPos.second);
                                mBoardView.removeItem(i, j);
                                mBoardView.getAdapter(i).notifyItemRemoved(j);
                                mBoardView.getAdapter(i).notifyItemRangeChanged(j+1, mBoardView.getAdapter(i).getItemCount());

                            } else
//                            if (taskNewPos.second.getPlanDate().equals(planDate))
                            {
//                            Log.d("date 2 ", ": " + taskNewPos.second.getPlanDate());
//                            if (!taskNewPos.second.getPlanOrder().equals(row)) {
//                                    taskNewPos.second.setPlanOrder(String.valueOf(row));
//                                    Collections.sort(mBoardView.getAdapter(i).getItemList(), (Comparator<Pair<Long, PayloadTask>>) (lhs, rhs) -> {
//                                        if ((Integer.parseInt(lhs.second.getPlanOrder()) == Integer.parseInt(rhs.second.getPlanOrder()) )) {
//                                            return 0;
//                                        } else if ((Integer.parseInt(lhs.second.getPlanOrder()) < Integer.parseInt(rhs.second.getPlanOrder()) )) {
//                                            return -1;
//                                        } else {
//                                            return 1;
//                                        }
//                                    });
//                                    mBoardView.getAdapter(i).notifyDataSetChanged();
//                                }
//                                mBoardView.invalidate();
//                                viewModel.updateDbTask(taskNewPos.second);
//                        } else if (!taskNewPos.second.getPlanDate().equals(planDate) || !taskNewPos.second.getPlanDate().equals(Constants.dateColumnMap.get(i))) {
                                taskNewPos.second.setPlanOrder(String.valueOf(row));
                                for (int k = 0; k < mBoardView.getColumnCount(); k++) {
                                    if (!Constants.dateColumnMap.containsValue(planDate)){
                                        mBoardView.getAdapter(i).getItemList().remove(taskNewPos);
                                    }
                                    if (planDate.equals(Constants.dateColumnMap.get(k))) {
                                        taskNewPos.second.setPlanDate(planDate);
                                        mBoardView.moveItem(i, j, k, 0, false);
                                        Collections.sort(mBoardView.getAdapter(k).getItemList(), (Comparator<Pair<Long, PayloadTask>>) (lhs, rhs) -> {

                                            if ((Integer.parseInt(lhs.second.getPlanOrder()) == Integer.parseInt(rhs.second.getPlanOrder()) )) {
                                                return 0;
                                            } else if ((Integer.parseInt(lhs.second.getPlanOrder()) < Integer.parseInt(rhs.second.getPlanOrder()) )) {
                                                return -1;
                                            } else {
                                                return 1;
                                            }
                                        });
                                        mBoardView.getAdapter(i).notifyDataSetChanged();
                                        mBoardView.getAdapter(k).notifyDataSetChanged();
                                        int estimatedTimeOldNewDate = 0;
                                        int taskSpentTimeOldNewDate = 0;
                                        LinearHourCounterView hourEstimatedOldNewDate = mBoardView.getHeaderView(k).findViewById(R.id.hour_counter_estimated);
                                        ArrayList<Pair<Long, PayloadTask>> oldTasksNewDate = mBoardView.getAdapter(k).getItemList();
                                        for (int m = 0; m < oldTasksNewDate.size(); m++) {
                                            Pair<Long, PayloadTask> pair = oldTasksNewDate.get(m);
                                            PayloadTask task = pair.second;
                                            estimatedTimeOldNewDate += task.getDuration();
                                            taskSpentTimeOldNewDate += task.getDurationActual();
                                        }
                                        estimatedTimeOldNewDate -= taskSpentTimeOldNewDate;
                                        if(estimatedTimeOldNewDate > 0) {
                                            if (estimatedTimeOldNewDate % 60 == 0)
                                                hourEstimatedOldNewDate.setProgress(String.valueOf(estimatedTimeOld / 60));
                                            else {
                                                String s = df.format(estimatedTimeOldNewDate / 60.0f);
                                                s = s.replace(',', '.');
                                                hourEstimatedOldNewDate.setProgress(s);
                                            }
                                        } else
                                            hourEstimatedOldNewDate.setProgress("0");

                                        int estimatedTimeNew = 0;
                                        LinearHourCounterView hourEstimatedNew = mBoardView.getHeaderView(k).findViewById(R.id.hour_counter_estimated);
                                        ArrayList<Pair<Long, PayloadTask>> newTasks = mBoardView.getAdapter(k).getItemList();
                                        for (int p = 0; p < newTasks.size(); p++) {
                                            Pair<Long, PayloadTask> pair = newTasks.get(p);
                                            PayloadTask task = pair.second;
                                            estimatedTimeNew += task.getDuration();
                                        }
                                        if (estimatedTimeNew % 60 == 0)
                                            hourEstimatedNew.setProgress(String.valueOf(estimatedTimeNew / 60));
                                        else
                                            hourEstimatedNew.setProgress(df.format(estimatedTimeNew / 60.0f));
                                        taskNewPos.second.setPlanDate(planDate);
                                    }
                                }
                            }
                            LinearHourCounterView hourEstimatedOld = mBoardView.getHeaderView(i).findViewById(R.id.hour_counter_estimated);
                            ArrayList<Pair<Long, PayloadTask>> oldTasks = mBoardView.getAdapter(i).getItemList();
                            for (int m = 0; m < oldTasks.size(); m++) {
                                Pair<Long, PayloadTask> pair = oldTasks.get(m);
                                PayloadTask task = pair.second;
                                estimatedTimeOld += task.getDuration();
                                taskSpentTimeOld += task.getDurationActual();
                            }

                            estimatedTimeOld -= taskSpentTimeOld;
                            if(estimatedTimeOld > 0) {
                                if (estimatedTimeOld % 60 == 0)
                                    hourEstimatedOld.setProgress(String.valueOf(estimatedTimeOld / 60));
                                else {
                                    String s = df.format(estimatedTimeOld / 60.0f);
                                    s = s.replace(',', '.');
                                    hourEstimatedOld.setProgress(s);
                                }
                            } else
                                hourEstimatedOld.setProgress("0");
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
                        task.setPlanDate(planDate);
                        task.setProjectId(projectId);
                        task.setSectionId(sectionId);
                        task.setEpicLinks(epicLinkList);
                        task.setAssignee(assignee);
                        task.setPriority(priority);
                        task.setPlanOrder(String.valueOf(row));
                        task.setStatus(status);
                        if (!status.equals("deleted") && !status.equals("archived")) {
                            addTask(task);
                        }
                        viewModel.updateDbTask(task);
                        viewModel.addDbTask(task);
                    }
                viewModel.updateDbTask(task2);
                viewModel.addDbTask(task2);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
    });



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Glide.with(this).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.chat));
        findViewById(R.id.chat).setOnClickListener(v ->{
            socket.emit("server_read_messages", tokenJson);
            startActivity(new Intent(this, ChatActivity.class));
        });
        findViewById(R.id.profile).setOnClickListener(v ->{
            startActivity(new Intent(this, ProfileActivity.class));
        });
        initViews();
    }

    private void openDayInfo(){
        View header = View.inflate(this, R.layout.column_header, null);
        LinearHourCounterView estimatedTimeCounter = header.findViewById(R.id.hour_counter_estimated);
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
            bundle.putString("estimatedTime", estimatedTimeCounter.getProgress());
            bundle.putParcelableArrayList("actualDurationList", (ArrayList<? extends Parcelable>) durationActualList);
            bundle.putString("id", assignee.getId());
            BottomSheetDayInfo mySheetDialog = new BottomSheetDayInfo();
            mySheetDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mySheetDialog.show(fm, "dayInfo");
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }
    private void initViews() {
        findViewById(R.id.day_info).setOnClickListener(v -> openDayInfo());
        anim = AnimationUtils.loadAnimation(this,R.anim.alpha_anim);

        day = findViewById(R.id.main_day);
        date = findViewById(R.id.main_date);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        converter = new Converter();
        animRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right_anim);
        animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left_anim);
        animOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left_anim);
        animOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right_anim);
        df = new DecimalFormat("#.#");
        app.getComponent().inject(this);
        token = EmikaApplication.instance.getSharedPreferences().getString("token", "");
        startActivityViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(StartActivityViewModel.class);
        startActivityViewModel.setToken(token);
        startActivityViewModel.fetchAllData();
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(CalendarViewModel.class);
        profileViewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ProfileViewModel.class);
        bottomSheetDialogViewModel = new ViewModelProvider(this).get(BottomSheetDialogViewModel.class);
        userNetworkManager = new UserNetworkManager(token);
        socket = app.getSocket();
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("server_create_connection", tokenJson);
        socket.on("update_task", onTaskUpdate);
        socket.on("create_duration_actual_log", onCreateActualDuration);
        socket.on("delete_duration_actual_log", onDeleteActualDuration);
        socket.on("update_duration_actual_log", onUpdateActualDuration);
        profileViewModel.setContext(this);
        profileViewModel.downloadUserData();
        profileViewModel.getUserMutableLiveData().observe(this, userInfo);
        viewModel.downloadDurationActualLog();
        viewModel.downloadTasksByAssignee(user.getId());
        viewModel.getProjectMutableLiveData();
        fabUserName = findViewById(R.id.fab_user_name);
        fabImg = findViewById(R.id.fab_img);
        fabJobTitle = findViewById(R.id.fab_job_title);
        selectCurrentUser = findViewById(R.id.select_current_user);
        selectCurrentUser.setOnClickListener(this::selectCurrentAssignee);

        rightScroll = findViewById(R.id.right_scroll_to_current_date);
        rightScroll = findViewById(R.id.right_scroll_to_current_date);
        leftScroll = findViewById(R.id.left_scroll_to_current_date);
        rightScroll.setOnClickListener(this::scrollToCurrentDate);
        leftScroll.setOnClickListener(this::scrollToCurrentDate);
        addTask = findViewById(R.id.add_task);
        addTask.setTransitionName("shared_element");
        addTask.setOnClickListener(this::goToAddTask);

        mBoardView = findViewById(R.id.board_view);
        mBoardView.setLastColumn(15);
        for (int i = 0; i < 365; i++) {
            Constants.dateColumnMap.put(i, DateHelper.compareDate(i));
            addColumn2(DateHelper.getDate(DateHelper.compareDate(i)), DateHelper.getDatOfWeek(i), Constants.dateColumnMap.get(i));
            if (i == 15)
                mBoardView.getHeaderView(i).findViewById(R.id.item_layout_task).setBackground(getResources().getDrawable(R.drawable.shape_foreground_stroke));
        }

        mBoardView.setBoardListener(boardListener);
        viewModel.setContext(this);
        viewModel.getEpicLinksMutableLiveData().observe(this, getEpicLinks);
        viewModel.getProjectMutableLiveData().observe(this, getProjectEntity);
        viewModel.getDurationMutableLiveData().observe(this, getDuration);
        viewModel.getMembersMutableLiveData().observe(this, shortMembers);
        viewModel.getAllDbTaskByAssignee(assignee.getId());
        viewModel.getListMutableLiveData().observe(this, getTask);

        oldAssignee = assignee.getId();
    }

    private BoardView.BoardListener boardListener = new BoardView.BoardListener(){
        @Override
        public void onItemDragStarted(int column, int row) {
        }

        @Override
        public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
            Pair<Long, PayloadTask> taskNewPos = (Pair<Long, PayloadTask>) mBoardView.getAdapter(toColumn).getItemList().get(toRow);
            taskNewPos.second.setPlanDate(Constants.dateColumnMap.get(toColumn));
            taskNewPos.second.setPlanOrder(String.valueOf(toRow));
            viewModel.updateTask(taskNewPos.second);
            for (int i = 0; i < mBoardView.getAdapter(toColumn).getItemList().size(); i++) {
                Pair<Long, PayloadTask> task = (Pair<Long, PayloadTask>) mBoardView.getAdapter(toColumn).getItemList().get(i);
                task.second.setPlanOrder(String.valueOf(mBoardView.getAdapter(toColumn).getPositionForItem(task)));
                viewModel.updateTask(task.second);
            }
            int estimatedTimeNew = 0;
            int estimatedTimeOld = 0;
            int spentTimeNew = 0;
            int spentTimeOld = 0;
            LinearHourCounterView hourEstimatedOld = mBoardView.getHeaderView(fromColumn).findViewById(R.id.hour_counter_estimated);
            LinearHourCounterView hourEstimatedNew = mBoardView.getHeaderView(toColumn).findViewById(R.id.hour_counter_estimated);
            ArrayList<Pair<Long, PayloadTask>> newTasks = mBoardView.getAdapter(toColumn).getItemList();
            ArrayList<Pair<Long, PayloadTask>> oldTasks = mBoardView.getAdapter(fromColumn).getItemList();

            for (int i = 0; i < oldTasks.size(); i++) {
                Pair<Long, PayloadTask> pair = oldTasks.get(i);
                PayloadTask task = pair.second;
                estimatedTimeOld += task.getDuration();
                spentTimeOld += task.getDurationActual();
            }

            if (estimatedTimeOld % 60 == 0)
                hourEstimatedOld.setProgress(String.valueOf(estimatedTimeOld / 60 - spentTimeOld / 60));
            else
                hourEstimatedOld.setProgress(df.format(estimatedTimeOld / 60.0f - spentTimeOld / 60.0f));

            for (int i = 0; i < newTasks.size(); i++) {
                Pair<Long, PayloadTask> pair = newTasks.get(i);
                PayloadTask task = pair.second;
                estimatedTimeNew += task.getDuration();
                spentTimeNew += task.getDurationActual();
            }

            if (estimatedTimeNew % 60 == 0)
                hourEstimatedNew.setProgress(String.valueOf(estimatedTimeNew / 60 - spentTimeNew / 60));
            else
                hourEstimatedNew.setProgress(df.format(estimatedTimeNew / 60.0f - spentTimeNew / 60.0f));
        }

        @Override
        public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {

        }

        @Override
        public void onItemChangedColumn(int oldColumn, int newColumn) {

        }

        @Override
        public void onFocusedColumnChanged(int oldColumn, int newColumn) {
            mBoardView.setLastColumn(newColumn);

            date.setText(DateHelper.getDate(Constants.dateColumnMap.get(mBoardView.getFocusedColumn())));
            day.setText( DateHelper.getDatOfWeek(mBoardView.getFocusedColumn()));
            date.startAnimation(anim);
            day.startAnimation(anim);
            if (!firstRun)
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
            FragmentManager fm = getSupportFragmentManager();
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

//    private void addColumn(String month, String day, List<PayloadTask> payloadTaskList, int columnNumber) {
//        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();
//        int estimatedTime = 0;
//        int spentTime = 0;
//        int tasSpentTime = 0;
//        String date = Constants.dateColumnMap.get(columnNumber);
//
//        int addItems = payloadTaskList.size();
//        for (int i = 0; i < addItems; i++) {
//            long id = sCreatedItems++;
//            mItemArray.add(new Pair<Long, PayloadTask>(id, payloadTaskList.get(i)));
//        }
//
//        for (int i = 0; i < payloadTaskList.size(); i++) {
//            estimatedTime += payloadTaskList.get(i).getDuration();
//        }
//
//        for (int i = 0; i < payloadTaskList.size(); i++) {
//            tasSpentTime += payloadTaskList.get(i).getDurationActual();
//        }
//
//
//        for (int j = 0; j < durationActualList.size(); j++) {
//        if (Constants.dateColumnMap.get(columnNumber).equals(durationActualList.get(j).getDate()) && durationActualList.get(j).getPerson().equals(assignee.getId())) {
//            spentTime += (durationActualList.get(j).getValue());
//        }
//        }
//
//        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true, getContext(), token, epicLinksEntities, projectEntities, viewModel);
//        listAdapter.setContext(getContext());
//        listAdapter.setmDragOnLongPress(true);
//        listAdapter.setmLayoutId(R.layout.column_item);
//        listAdapter.setmGrabHandleId(R.id.item_layout);
//        final View header = View.inflate(getActivity(), R.layout.column_header, null);
//
//        estimatedTime = estimatedTime - tasSpentTime;
//        if (estimatedTime < 0)
//            estimatedTime = 0;
//
//        int finalEstimatedTime = estimatedTime;
//        int finalSpentTime = spentTime;
//
//        header.setOnClickListener(v -> {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//            return;
//        } else {
//            Bundle bundle = new Bundle();
//            bundle.putString("date", date);
//            bundle.putInt("estimatedTime", finalEstimatedTime);
//            bundle.putInt("spentTime", finalSpentTime);
//            bundle.putString("id", assignee.getId());
//            BottomSheetDayInfo mySheetDialog = new BottomSheetDayInfo();
//            mySheetDialog.setArguments(bundle);
//            FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
//            mySheetDialog.show(fm, "dayInfo");
//        }
//        mLastClickTime = SystemClock.elapsedRealtime();
//    });
//
//        ((TextView) header.findViewById(R.id.header_date)).setText(month);
//        ((TextView) header.findViewById(R.id.header_day)).setText(day);
//
//        HourCounterView spentTimeCounter = ((HourCounterView) header.findViewById(R.id.hour_counter_spent));
//        HourCounterView estimatedTimeCounter = ((HourCounterView) header.findViewById(R.id.hour_counter_estimated));
//
//        if (estimatedTime % 60 == 0)
//            estimatedTimeCounter.setProgress(String.valueOf(estimatedTime / 60));
//        else {
//            String s = df.format(estimatedTime / 60.0f);
//            s = s.replace(',', '.');
//            estimatedTimeCounter.setProgress(s);
//        }
//
//        if (spentTime % 60 == 0)
//            spentTimeCounter.setProgress(String.valueOf(spentTime / 60));
//        else {
//            String s = df.format(spentTime / 60.0f);
//            s = s.replace(',', '.');
//            spentTimeCounter.setProgress(s);
//        }
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//
//        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
//                .setLayoutManager(layoutManager)
//                .setHasFixedItemSize(false)
//                .setColumnBackgroundColor(Color.TRANSPARENT)
//                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
//                .setHeader(header)
//                .build();
//
//        mBoardView.addColumn(columnProperties);
//    }


    private void addColumn2(String month, String day, String date) {
        final ArrayList<Pair<Long, PayloadTask>> mItemArray = new ArrayList<>();

        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout,
                true, this, token, epicLinksEntities, projectEntities, viewModel, getSupportFragmentManager(), this);
        listAdapter.setContext(this);
        listAdapter.setmDragOnLongPress(true);
        listAdapter.setmLayoutId(R.layout.column_item);
        listAdapter.setmGrabHandleId(R.id.item_layout);
        final View header = View.inflate(this, R.layout.column_header, null);
        LinearHourCounterView estimatedTimeCounter = ((LinearHourCounterView) header.findViewById(R.id.hour_counter_estimated));

        header.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("estimatedTime", estimatedTimeCounter.getProgress());
                bundle.putParcelableArrayList("actualDurationList", (ArrayList<? extends Parcelable>) durationActualList);
                bundle.putString("id", assignee.getId());
                BottomSheetDayInfo mySheetDialog = new BottomSheetDayInfo();
                mySheetDialog.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                mySheetDialog.show(fm, "dayInfo");
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });
//
        ((TextView) header.findViewById(R.id.header_date)).setText(month);
        ((TextView) header.findViewById(R.id.header_day)).setText(day);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

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
            Intent intent = new Intent(this, AddTaskActivity.class);
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "shared_element");
            intent.putExtra("token", token);
            intent.putParcelableArrayListExtra("members", (ArrayList<? extends Parcelable>) memberList);
            intent.putExtra("calendarViewModel", viewModel);
            intent.putExtra("date", Constants.dateColumnMap.get(mBoardView.getFocusedColumn()));
//            startActivity(intent, options.toBundle());
            startActivityForResult(intent, 1);
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.getAssigneeMutableLiveData();
        viewModel.getAllDbTaskByAssignee(assignee.getId());
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
            LinearHourCounterView hourEstimatedNew = mBoardView.getHeaderView(column).findViewById(R.id.hour_counter_estimated);
            if (task.getDuration() % 60 == 0)
                hourEstimatedNew.setProgress(String.valueOf(Double.parseDouble(hourEstimatedNew.getProgress()) + task.getDuration() / 60));
            else
                hourEstimatedNew.setProgress(df.format(Double.parseDouble(hourEstimatedNew.getProgress()) + task.getDuration() / 60.0f));
            mBoardView.addItem(column, mBoardView.getAdapter(column).getItemCount(), item, false);
            mBoardView.getAdapter(column).notifyDataSetChanged();
        } else {

        }
    }
}