package com.emika.app.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.di.UserModule;
import com.emika.app.presentation.ui.calendar.BoardFragment;
import com.emika.app.presentation.ui.chat.ChatFragment;
import com.emika.app.presentation.ui.profile.ProfileFragment;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Inject User user;
    private BottomNavigationView navigationView;
    private Payload userPayload = new Payload();
    private FragmentManager fragmentManager;
    private ProfileFragment profileFragment = new ProfileFragment();
    private BoardFragment boardFragment = new BoardFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private EmikaApplication  app = EmikaApplication.getInstance();
    private ConstraintLayout main;
    private UserNetworkManager networkManager;
    private Socket socket;
    private String token;
    private JSONObject tokenJson;
    private FirebaseAnalytics mFirebaseAnalytics;
    Fragment active = boardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        app.getComponent().inject(this);
        socket = app.getSocket();
        token = getIntent().getStringExtra("token");
        tokenJson = new JSONObject();
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkManager = new UserNetworkManager(token);
        main = findViewById(R.id.main);
        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.main_container, boardFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, profileFragment).hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, chatFragment, "chatFragment").hide(chatFragment).commit();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chat_bg));

        KeyboardVisibilityEvent.setEventListener(
                MainActivity.this,
                isOpen -> {
                    if (isOpen)
                        navigationView.setVisibility(View.GONE);
                    else
                        navigationView.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {

            case R.id.menu_calendar:
                if (active != boardFragment) {
                    fragmentManager.beginTransaction().hide(active).show(boardFragment).commit();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chat_bg));
                    active = boardFragment;
                }
                return true;

            case R.id.menu_profile:
                if (active != profileFragment) {
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
                    setSupportActionBar(findViewById(R.id.profile_toolbar));
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    active = profileFragment;

                }
                return true;
            case R.id.menu_chat:
                if (active != chatFragment) {
                    fragmentManager.beginTransaction().hide(active).show(chatFragment).commit();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
                    socket.emit("server_read_messages", tokenJson);
                    active = chatFragment;
                }
                return true;

        }

        return false;
    };

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
