package com.emika.app.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
        networkManager.getUserInfo(this);
        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.main_container, boardFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, profileFragment).hide(profileFragment).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {

            case R.id.menu_calendar:
                if (active != boardFragment) {
                    fragmentManager.beginTransaction().hide(active).show(boardFragment).commit();
                    active = boardFragment;
                }
                return true;

            case R.id.menu_profile:
                if (active != profileFragment) {
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                }
                return true;
        }

        return false;
    };

    @Override
    public void updateInfo(UpdateUserModel model) {

    }

    @Override
    public void getUserInfo(Payload userModel) {
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
    }
}
