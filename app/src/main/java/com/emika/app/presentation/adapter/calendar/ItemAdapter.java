/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emika.app.presentation.adapter.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.di.EpicLinks;
import com.emika.app.features.calendar.DragItemAdapter;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {
    private static final String TAG = "ItemAdapter";
    @Inject
    EpicLinks epicLinksDi;
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private Context context;
    private String token;
    private CalendarNetworkManager calendarNetworkManager;
    private List<EpicLinksEntity> epicLinksEntities;
    private List<ProjectEntity> projectEntities;
    private long mLastClickTime = 0;

    public ItemAdapter(ArrayList<Pair<Long, PayloadTask>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context, String token, List<EpicLinksEntity> epicLinksEntities, List<ProjectEntity> projectEntities) {
        EmikaApplication.getInstance().getComponent().inject(this);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.context = context;
        this.token = token;
        this.projectEntities = projectEntities;
        this.epicLinksEntities = epicLinksEntities;
        calendarNetworkManager = new CalendarNetworkManager(token);
        setItemList(list);

    }

    public void setmLayoutId(int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    public void setmGrabHandleId(int mGrabHandleId) {
        this.mGrabHandleId = mGrabHandleId;
    }

    public void setmDragOnLongPress(boolean mDragOnLongPress) {
        this.mDragOnLongPress = mDragOnLongPress;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setList(ArrayList<Pair<Long, PayloadTask>> list) {
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PayloadTask task = mItemList.get(position).second;
        if (!task.getStatus().equals("deleted")) {
            holder.mText.setText(task.getName());
            holder.itemView.setTag(mItemList.get(position));
            holder.estimatedTime.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
            holder.spentTime.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
            holder.project.setText("Emika");
            holder.isDone.setOnClickListener(v -> {
                if (holder.isDone.isChecked()) {
                    holder.mText.setTextColor(context.getResources().getColor(R.color.task_name_done));
                    task.setStatus("done");
                    calendarNetworkManager.updateTask(task);
                } else {
                    holder.mText.setTextColor(context.getResources().getColor(R.color.black));
                    task.setStatus("wip");
                    calendarNetworkManager.updateTask(task);
                }
            });

            holder.refresh.setOnClickListener(v -> {
                holder.mText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                task.setStatus("wip");
                holder.taskTextCanceled.setVisibility(View.GONE);
                holder.priority.setVisibility(View.VISIBLE);
                holder.refresh.setVisibility(View.GONE);
                holder.isDone.setVisibility(View.VISIBLE);
                holder.isDone.setChecked(false);
                calendarNetworkManager.updateTask(task);
            });

            if (task.getStatus().equals("done")) {
                holder.mText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                holder.mText.setTextColor(context.getResources().getColor(R.color.task_name_done));
                holder.isDone.setVisibility(View.VISIBLE);
                holder.isDone.setChecked(true);
                holder.taskTextCanceled.setVisibility(View.GONE);
                holder.priority.setVisibility(View.VISIBLE);
                holder.refresh.setVisibility(View.GONE);

            } else if (task.getStatus().equals("canceled")) {
                holder.mText.setTextColor(context.getResources().getColor(R.color.task_name_done));
                holder.mText.setPaintFlags(holder.mText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.taskTextCanceled.setVisibility(View.VISIBLE);
                holder.isDone.setVisibility(View.GONE);
                holder.priority.setVisibility(View.GONE);
                holder.refresh.setVisibility(View.VISIBLE);
                holder.refresh.setChecked(true);
            } else {
                holder.mText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                holder.taskTextCanceled.setVisibility(View.GONE);
                holder.priority.setVisibility(View.VISIBLE);
                holder.refresh.setVisibility(View.GONE);
                holder.isDone.setVisibility(View.VISIBLE);
                holder.isDone.setChecked(false);
                holder.mText.setTextColor(context.getResources().getColor(R.color.black));
                holder.isDone.setChecked(false);
            }
//            holder.mText.setOnClickListener(v -> {
//                Intent intent = new Intent(context, TaskInfoActivity.class);
//                intent.putExtra("task", mItemList.get(position).second);
//                intent.putExtra("token", token);
//                context.startActivity(intent);
//            });
            if (task.getPriority() != null)
                switch (task.getPriority()) {
                    case "low":
                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_low));
                        holder.priority.setTextColor(context.getResources().getColor(R.color.low_priority));
                        holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_priority_low), null, null, null);
                        break;
                    case "normal":
                        holder.priority.setVisibility(View.GONE);
                        break;
                    case "high":
                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_high));
                        holder.priority.setTextColor(context.getResources().getColor(R.color.yellow));
                        holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_priority_high), null, null, null);
                        break;
                    case "urgent":
                        holder.priority.setBackground(context.getResources().getDrawable(R.drawable.shape_priority_urgent));
                        holder.priority.setTextColor(context.getResources().getColor(R.color.red));
                        holder.priority.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_task_urgent), null, null, null);
                        break;
                }
            if (task.getEpicLinks() != null && task.getEpicLinks().size() != 0) {
                for (int i = 0; i < task.getEpicLinks().size(); i++) {
                    for (int j = 0; j < epicLinksDi.getEpicLinksList().size(); j++) {
                        if (task.getEpicLinks().get(i).equals(epicLinksDi.getEpicLinksList().get(j).getId())) {
                            if (task.getEpicLinks().size() > 1)
                                holder.epicLink.setText(String.format("%s+%d", epicLinksDi.getEpicLinksList().get(j).getName(), task.getEpicLinks().size() - 1));
                            else
                                holder.epicLink.setText(epicLinksDi.getEpicLinksList().get(j).getName());

                        }
                    }
                }
            } else holder.epicLink.setVisibility(View.GONE);

            for (int i = 0; i < projectEntities.size(); i++) {
                if (task.getProjectId() != null)
                    if (task.getProjectId().equals(projectEntities.get(i).getId())) {
                        holder.project.setText(projectEntities.get(i).getName());
                    }
            }


            holder.priority.setText(Constants.priority.get(task.getPriority()));
            if (task.getDeadlineDate() != null)
                holder.deadLine.setText(DateHelper.getDate(task.getDeadlineDate()));
            else holder.deadLine.setVisibility(View.GONE);
        }
    }

    @Override
    public void changeItemPosition(int fromPos, int toPos) {
        super.changeItemPosition(fromPos, toPos);
    }

    @Override
    public void swapItems(int pos1, int pos2) {
        super.swapItems(pos1, pos2);
    }

    @Override
    public int getPositionForItemId(long id) {
        return super.getPositionForItemId(id);
    }

    @Override
    public long getDropTargetId() {
        return super.getDropTargetId();

    }


    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText, spentTime, estimatedTime, deadLine, project, priority, epicLink, taskTextCanceled;
        CardView cardView;
        FrameLayout layout;
        CheckBox isDone, refresh;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            epicLink = itemView.findViewById(R.id.calendar_task_epic_links);
            mText = (TextView) itemView.findViewById(R.id.text);
            layout = itemView.findViewById(R.id.item_layout);
            cardView = itemView.findViewById(R.id.card);
            spentTime = itemView.findViewById(R.id.item_task_spent_time);
            estimatedTime = itemView.findViewById(R.id.item_task_estimated_time);
            priority = itemView.findViewById(R.id.calendar_task_priority);
            deadLine = itemView.findViewById(R.id.calendar_task_deadline);
            project = itemView.findViewById(R.id.calendar_task_project);
            isDone = itemView.findViewById(R.id.task_done);
            refresh = itemView.findViewById(R.id.task_refresh);
            taskTextCanceled = itemView.findViewById(R.id.calendar_task_canceled);
        }

        @Override
        public void onItemClicked(View view) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            } else {
                Intent intent = new Intent(context, TaskInfoActivity.class);
                intent.putExtra("task", mItemList.get(getAdapterPosition()).second);
                intent.putExtra("token", token);
                context.startActivity(intent);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        }

        @Override
        public boolean onItemLongClicked(View view) {
//            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
