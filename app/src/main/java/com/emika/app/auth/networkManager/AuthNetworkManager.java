package com.emika.app.auth.networkManager;

import android.util.Log;

import com.emika.app.auth.callback.TokenCallback;
import com.emika.app.auth.data.pojo.ModelEmail;
import com.emika.app.auth.data.pojo.PayloadEmail;
import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;
import com.emika.app.network.AuthApi;
import com.emika.app.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthNetworkManager {
    private static final String TAG = "AuthNetworkManager";
    private String token, password, email;
    private TokenCallback tokenCallback;
    public AuthNetworkManager(String token, String email, String password) {
        this.token = token;
        this.email = email;
        this.password = password;
    }

    public void registerCallBack(TokenCallback callback){
        this.tokenCallback = callback;
    }

    public void getAuthPayloadEmail() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        AuthApi service = retrofit.create(AuthApi.class);
        Call<ModelEmail> call = service.checkEmail(token, email);
        call.enqueue(new Callback<ModelEmail>() {
            @Override
            public void onResponse(retrofit2.Call<ModelEmail> call, Response<ModelEmail> response) {
                if (response.body() != null) {
                    ModelEmail model = response.body();
                    PayloadEmail payloadEmail = model.getPayload();
                    tokenCallback.callbackCheckedEmail(payloadEmail);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelEmail> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }


    public void getAuthModelAuth() {
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
                    tokenCallback.callbackModelAuthSignIn(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void getAuthModelSignUp() {
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
                    tokenCallback.callbackModelAuthSignUp(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelSignUp> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
}
