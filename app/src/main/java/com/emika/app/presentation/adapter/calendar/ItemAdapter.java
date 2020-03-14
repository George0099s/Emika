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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;

import com.emika.app.R;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.features.calendar.DragItemAdapter;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.DateHelper;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {
    private static final String TAG = "ItemAdapter";
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private Context context;
    private DateHelper dateHelper;

    public ItemAdapter(ArrayList<Pair<Long, PayloadTask>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.context = context;
        dateHelper = new DateHelper();
        setItemList(list);
    }

    public ItemAdapter() {
        dateHelper = new DateHelper();
//        setItemList(list);
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
        holder.mText.setText(task.getName());
        holder.itemView.setTag(mItemList.get(position));
        holder.estimatedTime.setText(String.format("%sh", String.valueOf(task.getDuration() / 60)));
        holder.spentTime.setText(String.format("%sh", String.valueOf(task.getDurationActual() / 60)));
        holder.project.setText("Emika");
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
        holder.priority.setText(Constants.priority.get(task.getPriority()));
        if (task.getDeadlineDate() != null)
            holder.deadLine.setText(DateHelper.getDate(task.getDeadlineDate()));
        else holder.deadLine.setVisibility(View.GONE);
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
        TextView mText, spentTime, estimatedTime, deadLine, project, priority;
        CardView cardView;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
            cardView = itemView.findViewById(R.id.card);
            spentTime = itemView.findViewById(R.id.item_task_spent_time);
            estimatedTime = itemView.findViewById(R.id.item_task_estimated_time);
            priority = itemView.findViewById(R.id.calendar_task_priority);
            deadLine = itemView.findViewById(R.id.calendar_task_deadline);
            project = itemView.findViewById(R.id.calendar_task_project);
        }

        @Override
        public void onItemClicked(View view) {
            Intent intent = new Intent(context, TaskInfoActivity.class);
            intent.putExtra("task", mItemList.get(getAdapterPosition()).second);
            context.startActivity(intent);
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
