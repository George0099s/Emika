package com.emika.app.presentation.adapter.chat;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.chat.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends PagedListAdapter<Message, ChatAdapter.ItemViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<Message> customList = new ArrayList<>();
    private static DiffUtil.ItemCallback<Message> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Message>() {
                @Nullable
                @Override
                public Object getChangePayload(@NonNull Message oldItem, @NonNull Message newItem) {
                    return super.getChangePayload(oldItem, newItem);
                }

                @Override
                public boolean areItemsTheSame(Message oldItem, Message newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Message oldItem, Message newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
            };



    private ListUpdateCallback listUpdateCallback = new ListUpdateCallback() {
        @Override
        public void onInserted(int position, int count) {
            notifyItemInserted(position);
        }

        @Override
        public void onRemoved(int position, int count) {
        notifyItemRemoved(position);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            notifyItemChanged(position, payload);
        }
    };
    private Context mCtx;
    private final AsyncPagedListDiffer<Message> mDiffer;
    public ChatAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        mDiffer = new AsyncPagedListDiffer<>(this, DIFF_CALLBACK);
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.OTHER_MSG:
                layout = R.layout.item_chat_left;
                break;
            case Message.USER_MSG:
                layout = R.layout.item_chat_right;
                break;

        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Message item = getItem(position);
        holder.textView.setText(item.getText());
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getIsEmika())
            return Message.OTHER_MSG;
        else
            return Message.USER_MSG;
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size() + customList.size();
    }

    public void update(Message message){
        customList.add(0, message);
        notifyItemRangeInserted(0, getCurrentList().size());
//        notifyItemInserted(0);
    }


    @Nullable
    @Override
    public PagedList<Message> getCurrentList() {
        return super.getCurrentList();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Message> currentList) {
        super.onCurrentListChanged(currentList);
    }



    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.message_body);
        }
    }

}
