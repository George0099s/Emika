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

package com.emika.app.features;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.emika.app.R;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.features.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.List;

class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {
    private static final String TAG = "ItemAdapter";
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    ItemAdapter(ArrayList<Pair<Long, PayloadTask>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
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
        holder.mText.setText(mItemList.get(position).second.getName());
        holder.itemView.setTag(mItemList.get(position));
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
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemClicked(View view) {

            Toast.makeText(view.getContext(), mItemList.get(getAdapterPosition()).second.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
