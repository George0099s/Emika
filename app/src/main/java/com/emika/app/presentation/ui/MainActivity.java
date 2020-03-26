package com.emika.app.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements UserInfoCallback {
    private static final String TAG = "MainActivity";
    @Inject User user;
    private BottomNavigationView navigationView;
    private Payload userPayload = new Payload();
    private FragmentManager fragmentManager;
    private ProfileFragment profileFragment = new ProfileFragment();
    private BoardFragment boardFragment = new BoardFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private EmikaApplication  app = EmikaApplication.getInstance();
    private UserNetworkManager networkManager;
    Fragment active = boardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        app.getComponent().inject(this);
        networkManager = new UserNetworkManager(getIntent().getStringExtra("token"));
        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.main_container, boardFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, profileFragment).hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, chatFragment).hide(chatFragment).commit();
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
                    active = profileFragment;

                }
                return true;
            case R.id.menu_chat:
                if (active != chatFragment) {
                    fragmentManager.beginTransaction().hide(active).show(chatFragment).commit();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
                    active = chatFragment;
                }
                return true;

        }

        return false;
    };

    @Override
    public void updateInfo(UpdateUserModel model) {

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void getUserInfo(Payload userModel) {
//        user.setFirstName(userModel.getFirstName());
//        user.setLastName(userModel.getLastName());
//        user.setPictureUrl(userModel.getPictureUrl());
//        Log.d(TAG, "getUserInfo: " + user.getPictureUrl());
    }
}
