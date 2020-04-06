package com.emika.app.presentation.adapter.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.Suggestion;
import com.emika.app.deprecated.ChatAdapterOld;
import com.emika.app.di.User;
import com.emika.app.presentation.viewmodel.chat.ChatViewModel;

import java.util.List;

import javax.inject.Inject;

public class QuickAnswerAdapter extends RecyclerView.Adapter<QuickAnswerAdapter.ViewHolder> {
    private static final String TAG = "QuickAnswerAdapter";
    private List<Suggestion> suggestions;
    private  ChatViewModel viewModel;
    private ChatAdapter adapter;
    @Inject
    User userDi;

    public QuickAnswerAdapter(List<Suggestion> suggestions, ChatViewModel viewModel, ChatAdapter adapter) {
        this.suggestions = suggestions;
        this.viewModel = viewModel;
        this.adapter = adapter;
        EmikaApplication.getInstance().getComponent().inject(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_quick_answer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);
        holder.quickAnswer.setText(suggestion.getText());
        holder.quickAnswer.setOnClickListener(v -> {
            Message message = new Message();
            message.setText(suggestion.getText());
            message.setIsEmika(false);
            message.setAccountId(userDi.getId());
            viewModel.sendMessage(message);
//            adapter.update(message);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView quickAnswer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quickAnswer = itemView.findViewById(R.id.item_quick_answer);
        }

    }
}
