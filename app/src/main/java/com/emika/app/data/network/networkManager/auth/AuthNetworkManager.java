package com.emika.app.data.network.networkManager.auth;

import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.dao.TokenDao;
import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.network.callback.AuthCallback;
import com.emika.app.data.network.api.AuthApi;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.pojo.ModelEmail;
import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.TokenPayload;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;
import com.emika.app.presentation.utils.Constants;

import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthNetworkManager {
    private static final String TAG = "AuthNetworkManager";
    private String token, password, email;
    private AppDatabase db;
    private EmikaApplication emikaApplication = EmikaApplication.instance;
    private TokenDao tokenDao;
    private TokenDbManager tokenDbManager;

    public AuthNetworkManager(String token, String email, String password) {
        this.token = token;
        this.email = email;
        this.password = password;
        tokenDbManager = new TokenDbManager();
    }

    public AuthNetworkManager(String token){
        this.token = token;
    };

    public AuthNetworkManager() {

    }

    public void deleteToken(AuthCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelAuth> call = service.signIn(token, email, password);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    ModelAuth model = response.body();
                    callback.callbackModelAuthSignIn(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void getAuthModelAuth(AuthCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelAuth> call = service.signIn(token, email, password);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    ModelAuth model = response.body();
                    callback.callbackModelAuthSignIn(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void getAuthPayloadEmail(AuthCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelEmail> call = service.checkEmail(token, email);
        Log.d(TAG, "getAuthPayloadEmail: " + call.request().url());
        call.enqueue(new Callback<ModelEmail>() {
            @Override
            public void onResponse(retrofit2.Call<ModelEmail> call, Response<ModelEmail> response) {
                if (response.body() != null) {
                    ModelEmail model = response.body();
                    PayloadEmail payloadEmail = model.getPayload();
                    callback.callbackCheckedEmail(payloadEmail);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelEmail> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void getAuthModelSignUp(AuthCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelSignUp> call = service.signUp(token, email, password);
        call.enqueue(new Callback<ModelSignUp>() {
            @Override
            public void onResponse(retrofit2.Call<ModelSignUp> call, Response<ModelSignUp> response) {
                if (response.body() != null) {
                    ModelSignUp model = response.body();
                    callback.callbackModelAuthSignUp(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelSignUp> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void restorePassword(AuthCallback callback, String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelAuth> call = service.restorePassword(token, email);
        Log.d(TAG, "restorePassword: " + call.request().url() + " " + email);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    callback.callbackRestorePassword(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public Boolean createToken(TokenCallback callback){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelToken> call = service.createToken();
        call.enqueue(new Callback<ModelToken>() {
            @Override
            public void onResponse(Call<ModelToken> call, Response<ModelToken> response) {
                ModelToken model = response.body();
                if (model.getOk()) {
                    TokenPayload payload = model.getTokenPayload();
                    Log.d(TAG, "onResponse: " + payload.getToken());
                    token = payload.getToken();
                    Observable.fromCallable((new CallableValidateToken(token, callback)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                } else {
//                    Toast.makeText(, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ModelToken> call, Throwable t) {
                Log.d(TAG, "onResponse:321 signUp fail " + t.getMessage());

            }
        });
        return true;
    }

    public void validToken(String token, TokenCallback callback){
        Observable.fromCallable((new CallableValidateToken(token, callback)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private Boolean insertToken(String token){
        db = emikaApplication.getDatabase();
        tokenDao = db.tokenDao();
        tokenDao.deleteAll();
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenDao.insert(tokenEntity);
        return true;
    }

    private Boolean validateToken(String token, TokenCallback callback) {
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
                if (model.getOk()){
                    Observable.fromCallable((new CallableInsertToken(token)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    callback.getToken(token);
                } else {
                    Observable.fromCallable((new CallableGetToken(callback)))
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

    private class CallableGetToken implements Callable<Boolean> {
        public CallableGetToken(TokenCallback callback) {
            this.callback = callback;
        }

        TokenCallback callback;
        @Override
        public Boolean call() throws Exception {
            return createToken(callback);
        }

    }
    private class CallableValidateToken implements Callable<Boolean> {
        String token;

        TokenCallback callback;
        public CallableValidateToken(String token, TokenCallback callback) {

            this.token = token;
            this.callback = callback;
        }
        @Override
        public Boolean call() throws Exception {
            return validateToken(token, callback);
        }

    }
    private class CallableInsertToken implements Callable<Boolean> {
        String token;

        public CallableInsertToken(String token) {

            this.token = token;
        }
        @Override
        public Boolean call() throws Exception {
            return insertToken(token);
        }

    }

    public void logOut() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelAuth> call = service.logOut(token);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    ModelAuth model = response.body();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
