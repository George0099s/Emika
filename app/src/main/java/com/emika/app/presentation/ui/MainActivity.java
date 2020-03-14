package com.emika.app.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.emika.app.R;
import com.emika.app.presentation.ui.calendar.BoardFragment;
import com.emika.app.presentation.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private FragmentManager fragmentManager;
    private ProfileFragment profileFragment = new ProfileFragment();
    private BoardFragment boardFragment = new BoardFragment();
    Fragment active = boardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
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

}
