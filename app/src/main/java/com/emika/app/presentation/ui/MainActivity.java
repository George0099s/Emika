package com.emika.app.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.emika.app.R;
import com.emika.app.features.BoardFragment;
import com.emika.app.presentation.ui.calendar.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private FragmentManager fragmentManager;
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

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.menu_calendar:
                if (active != boardFragment)
                fragmentManager.beginTransaction().hide(active).show(boardFragment);
                return true;
        }

        return false;
    };

}
