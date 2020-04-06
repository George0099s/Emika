package com.emika.app.presentation.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.data.network.pojo.chat.Suggestion;
import com.emika.app.di.User;
import com.emika.app.presentation.adapter.chat.QuickAnswerAdapter;
import com.emika.app.presentation.adapter.chat.ChatAdapter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.chat.ChatViewModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    @Inject
    User user;
    private ChatViewModel viewModel;
    private String token;
    private EditText messageBody;
    private RecyclerView chatRecycler;
    private RecyclerView quickAnswerRecycler;
    private QuickAnswerAdapter quickAnswerAdapter;
    private ChatAdapter adapter;
    private int offset = 0;
    private int limit = 25;
    private Button sendMessage;
    private Socket socket;
    private ImageView emikaImg;
//    private Observer<PayloadChat> getMessage = chat -> {
//        if (chat != null) {
//            if (chat.getMessages() != null) {
//                adapter = new ChatAdapterOld(getContext(), chat.getMessages());
//                chatRecycler.setAdapter(adapter);
//                chatRecycler.scrollToPosition(0);
//            }
//        }
//    };
    private Emitter.Listener onUpdateSuggestion = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                String text;
                Boolean active;
                Suggestion suggestion;
                List<Suggestion> suggestions = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(Arrays.toString(args));
                    JSONArray array = jsonArray.getJSONArray(0);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = array.getJSONObject(i);
                            text = jsonObject.getString("text");
                            suggestion = new Suggestion();
                            suggestion.setText(text);
                            suggestions.add(suggestion);
                        }
                        if (suggestions.size() > 0) {
                            quickAnswerRecycler.setVisibility(View.VISIBLE);
                            quickAnswerAdapter = new QuickAnswerAdapter(suggestions, viewModel, adapter);
                            quickAnswerRecycler.setAdapter(quickAnswerAdapter);
                        } else {
                            quickAnswerRecycler.setVisibility(View.GONE);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };
    private Emitter.Listener onNewMessage = args -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
        String text, createdAt, id;
        Boolean isEmika;
        Message message;
        PagedList<Message> messagePagedList = adapter.getCurrentList();
        try {
            JSONArray jsonArray = new JSONArray(Arrays.toString(args));
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            createdAt = jsonObject.getString("created_at");
            isEmika = jsonObject.getBoolean("is_emika");
            id = jsonObject.getString("account_id");
            text = jsonObject.getString("text");
            message = new Message();
            message.setId(id);
            message.setIsEmika(isEmika);
            message.setText(text);
            message.setCreatedAt(createdAt);
            chatRecycler.scrollToPosition(0);
            viewModel.updateMessage(message);
            adapter.notifyItemInserted(0);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    });

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        adapter = new ChatAdapter(getContext());
        EmikaApplication.getInstance().getComponent().inject(this);
        emikaImg = view.findViewById(R.id.chat_emika_img);
        socket = EmikaApplication.getInstance().getSocket();
        token = getActivity().getIntent().getStringExtra("token");
        Glide.with(getContext()).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(emikaImg);
        socket.on("new_message", onNewMessage);
        socket.on("update_suggestions", onUpdateSuggestion);
        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ChatViewModel.class);
        chatRecycler = view.findViewById(R.id.chat_recycler);
        quickAnswerRecycler = view.findViewById(R.id.recycler_chat_quick_answer);
        LinearLayoutManager horizontal = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        quickAnswerRecycler.setLayoutManager(horizontal);
        quickAnswerRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setHasFixedSize(true);
        viewModel.getItemPagedList().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            chatRecycler.scrollToPosition(0);
        });
        chatRecycler.setAdapter(adapter);
        sendMessage = view.findViewById(R.id.chat_send_message);
        sendMessage.setOnClickListener(this::sendMessage);
        messageBody = view.findViewById(R.id.chat_body_message);
    }

    private void sendMessage(View view) {
        if (!messageBody.getText().toString().isEmpty()) {
            Message message = new Message();
            message.setText(messageBody.getText().toString());
            message.setAccountId(user.getId());
            message.setIsEmika(false);
            viewModel.sendMessage(message);
            messageBody.setText("");
        } else {
            messageBody.requestFocus();
            messageBody.setError("You can't send empty message");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }
}
