package com.emika.app.presentation.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emika.app.R;

public class AuthActivity extends AppCompatActivity {
    final FragmentManager fm = getSupportFragmentManager();
    private AuthFragment authFragment;
    private CreateCompany createCompany;
    private ImageView logo;
    private Boolean continueRegistr;
    private TextView logOut;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initViews();

    }

    private void initViews() {

        continueRegistr = getIntent().getBooleanExtra("continue", false);
        token = getIntent().getStringExtra("token");
        if (continueRegistr) {
            createCompany = new CreateCompany();
            fm.beginTransaction().add(R.id.auth_container, createCompany).commit();
        } else {
            authFragment = new AuthFragment();
            fm.beginTransaction().add(R.id.auth_container, authFragment).commit();
        }
            logo = findViewById(R.id.logo);
        Glide.with(this).asGif().load("https://my.emika.ai/static/assets/img/main/self5.gif").apply(RequestOptions.circleCropTransform()).into(logo);
    }

}
