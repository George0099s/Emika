package com.emika.app.presentation.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.emika.app.R;
import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.dbmanager.ProjectDbManager;
import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.AuthApi;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.TokenPayload;
import com.emika.app.presentation.ui.auth.AuthActivity;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.NetworkState;
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory;
import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StartActivity extends AppCompatActivity implements TokenCallback {
    private static final String TAG = "StartActivity";
    private String token;
    private SharedPreferences sharedPreferences;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private TokenDbManager tokenDbManager;
    private StartActivityViewModel startActivityViewModel;
    private ProjectDbManager projectDbManager;
    private LifecycleOwner lifecycleOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initViews();
    }

    private void initViews() {
        tokenDbManager = new TokenDbManager();
        lifecycleOwner = this;
        sharedPreferences = emikaApplication.getSharedPreferences();
        if (NetworkState.getInstance(getApplication()).isOnline())
            if (!sharedPreferences.getBoolean("logged in", false))
                Observable.fromCallable((new CallableGetToken()))
                        .subscribeOn(Schedulers.io())
                        .subscribe();
            else {
                validateToken(sharedPreferences.getString("token", ""));
//                tokenDbManager.getToken(this);
            }
        else {
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(emikaApplication, "Offline mode", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validateToken(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelToken> call = service.validateToken(token);
        call.enqueue(new Callback<ModelToken>() {
            @Override
            public void onResponse(Call<ModelToken> call, Response<ModelToken> response) {
                ModelToken model = response.body();
                if (model.getOk()) {
                    startActivityViewModel = new ViewModelProvider((ViewModelStoreOwner) lifecycleOwner, new TokenViewModelFactory(token)).get(StartActivityViewModel.class);
//                    tokenDbManager.insertToken(token);
                    sharedPreferences.edit().putString("token", token).apply();
                    startActivityViewModel.setToken(token);
                    if (sharedPreferences.getBoolean("logged in", false)) {
                        startActivityViewModel.getHasCompanyId().observe(lifecycleOwner, hasCompanyId);
                    } else {
                        Intent intent = new Intent(StartActivity.this, AuthActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(StartActivity.this, "Wrong token", Toast.LENGTH_SHORT).show();
                    Observable.fromCallable((new CallableGetToken()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                }
            }

            @Override
            public void onFailure(Call<ModelToken> call, Throwable t) {
                Log.d(TAG, "onResponse:123 signUp fail " + t.getMessage());

            }
        });
        return true;
    }

    private Observer<Boolean> hasCompanyId = has -> {
        if (has) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("token", startActivityViewModel.getToken());
            startActivityViewModel.fetchAllData();
            startActivity(intent);
        } else {
            Intent intent = new Intent(StartActivity.this, AuthActivity.class);
            intent.putExtra("token", startActivityViewModel.getToken());
            intent.putExtra("continue", true);
            startActivity(intent);
        }
    };

    private Boolean createToken() {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelToken> call = service.createToken();
        call.enqueue(new Callback<ModelToken>() {
            @Override
            public void onResponse(Call<ModelToken> call, Response<ModelToken> response) {
                ModelToken model = response.body();
                if (model.getOk()) {
                    TokenPayload payload = model.getTokenPayload();
                    token = payload.getToken();

                    Observable.fromCallable((new CallableValidateToken(token)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                } else {
                    Toast.makeText(StartActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelToken> call, Throwable t) {
                Log.d(TAG, "onResponse:321 signUp fail " + t.getMessage());

            }
        });
        return true;
    }

    @Override
    public void getToken(String token) {
        validateToken(token);
    }

    private class CallableGetToken implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return createToken();
        }

    }

    private class CallableValidateToken implements Callable<Boolean> {
        String token;

        public CallableValidateToken(String token) {
            this.token = token;
        }

        @Override
        public Boolean call() throws Exception {
            return validateToken(token);
        }

    }

}
