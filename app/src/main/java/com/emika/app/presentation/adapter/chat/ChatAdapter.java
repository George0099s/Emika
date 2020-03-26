package com.emika.app.presentation.adapter.chat;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.di.User;

import java.util.List;

import javax.inject.Inject;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Message> mMessages;
    private static final String TAG = "ChatAdapter";

    public ChatAdapter(Context context, List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.mMessageView.setText(String.valueOf(Html.fromHtml(message.getText())));
        viewHolder.messageTime.setText(message.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
            if (mMessages.get(position).getIsEmika())
                return Message.OTHER_MSG;
            else return Message.USER_MSG;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessageView, messageTime;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTime = itemView.findViewById(R.id.message_time);
            mMessageView = (TextView) itemView.findViewById(R.id.message_body);
        }


        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

    }

    public void update(Message message){
        mMessages.add(0,message);
        notifyDataSetChanged();
    }
}