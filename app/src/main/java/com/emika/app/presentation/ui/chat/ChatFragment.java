package com.emika.app.presentation.ui.chat;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.pojo.chat.Message;
import com.emika.app.di.User;
import com.emika.app.features.calendar.MemberItemDecoration;
import com.emika.app.presentation.adapter.chat.ChatAdapter;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.chat.ChatViewModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class ChatFragment extends Fragment {

    @Inject
    User user;
    private ChatViewModel viewModel;
    private String token;
    private EditText messageBody;
    private RecyclerView chatRecycler;
    private ChatAdapter adapter;
    private int offset = 0;
    private int limit = 25;
    private JSONObject tokenJson;
    private Button sendMessage;
    private Manager manager;
    private Socket socket;
    private ImageView emikaImg;

    private static final String TAG = "ChatFragment";

    private Observer<List<Message>> getMessage = messages -> {
        if (messages != null) {
            adapter = new ChatAdapter(getContext(), messages);
            chatRecycler.setAdapter(adapter);
        }
    };
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        EmikaApplication.getInstance().getComponent().inject(this);
        manager = EmikaApplication.getInstance().getManager();
        socket = manager.socket("/all");
        token = getActivity().getIntent().getStringExtra("token");
        tokenJson = new JSONObject();
        emikaImg = view.findViewById(R.id.chat_emika_img);
        Glide.with(getContext()).asGif().load(R.drawable.emika_gif).apply(RequestOptions.circleCropTransform()).into(emikaImg);
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("server_create_connection", tokenJson);
        socket.on("new_message", onNewMessage);
        socket.on("create_connection_successful", onConnectionSuccessful);

        socket.connect();

        viewModel = new ViewModelProvider(this, new TokenViewModelFactory(token)).get(ChatViewModel.class);
        viewModel.getMessageMutableLiveData(offset, limit).observe(getViewLifecycleOwner(), getMessage);
        chatRecycler = view.findViewById(R.id.chat_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setNestedScrollingEnabled(false);
        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setHasFixedSize(true);
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
            adapter.update(message);
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


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(() -> {
                String text, createdAt;
                Boolean isEmika;
                Message message;
                try {
                    JSONArray jsonArray = new JSONArray(Arrays.toString(args));
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    createdAt = jsonObject.getString("created_at");
                    isEmika = jsonObject.getBoolean("is_emika");
                    text = jsonObject.getString("text");
                    Log.d(TAG, "call: " + text);
                    message = new Message();
                    message.setIsEmika(isEmika);
                    message.setText(text);
                    message.setCreatedAt(createdAt);
                    if(message!= null) {
                        adapter.update(message);
                    }else {
                        Toast.makeText(getContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };
    private Emitter.Listener onConnectionSuccessful = args -> {
        Log.d(TAG, ":asdasdasdasdasdasdad  " + args.length);

    };

}
