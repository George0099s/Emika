package com.emika.app.auth.data;

import android.util.Log;

import com.emika.app.auth.callback.TokenCallback;
import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.PayloadEmail;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;
import com.emika.app.auth.networkManager.AuthNetworkManager;

public class AuthRepository{
    private static final String TAG = "AuthRepository";
    private String token, password, email;
    private TokenCallback tokenCallback;
    private ModelSignUp modelSignUp;
    private PayloadEmail payloadEmail;
    private ModelAuth modelAuth;

    private AuthNetworkManager networkManager;

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthRepository(String token, String password, String email) {
        this.token = token;
        this.password = password;
        this.email = email;
        networkManager = new AuthNetworkManager(token, email, password);

        networkManager.registerCallBack(networkManager);
    }

    public void  getAuthPayloadEmailMutableLiveData() {
        networkManager.getAuthPayloadEmail();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASIC_URL) // Адрес сервера
//                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
//                .build();
//
//        AuthApi service = retrofit.create(AuthApi.class);
//        Call<ModelEmail> call = service.checkEmail(token, email);
//        call.enqueue(new Callback<ModelEmail>() {
//            @Override
//            public void onResponse(retrofit2.Call<ModelEmail> call, Response<ModelEmail> response) {
//                if (response.body() != null) {
//                    ModelEmail model = response.body();
//                    PayloadEmail payloadEmail = model.getPayload();
//                    tokenCallback.callbackCheckedEmail(payloadEmail);
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ModelEmail> call, Throwable t) {
//                Log.d("LOG", "Something went wrong :c");
//            }
//        });
    }


    public void getAuthModelAuthMutableLiveData() {
        networkManager.getAuthModelAuth();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASIC_URL) // Адрес сервера
//                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
//                .build();
//
//        AuthApi service = retrofit.create(AuthApi.class);
//        Call<ModelAuth> call = service.signIn(token, email, password);
//        call.enqueue(new Callback<ModelAuth>() {
//            @Override
//            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
//                if (response.body() != null) {
//                    ModelAuth model = response.body();
//                    tokenCallback.callbackModelAuthSignIn(model);
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
//                Log.d("LOG", "Something went wrong :c");
//            }
//        });
    }
    public void getAuthModelSignUpMutableLiveData() {
        networkManager.getAuthModelSignUp();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASIC_URL) // Адрес сервера
//                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
//                .build();
//
//        AuthApi service = retrofit.create(AuthApi.class);
//        Call<ModelSignUp> call = service.signUp(token, email, password);
//        call.enqueue(new Callback<ModelSignUp>() {
//            @Override
//            public void onResponse(retrofit2.Call<ModelSignUp> call, Response<ModelSignUp> response) {
//                if (response.body() != null) {
//                    ModelSignUp model = response.body();
//                    tokenCallback.callbackModelAuthSignUp(model);
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ModelSignUp> call, Throwable t) {
//                Log.d("LOG", "Something went wrong :c");
//            }
//        });
    }

//    @Override
//    public void callbackCheckedEmail(PayloadEmail payloadEmail) {
//        Log.d(TAG, "callbackCheckedEmail: " + payloadEmail.getExists());
//        setPayloadEmail(payloadEmail);
//    }
//
//    @Override
//    public void callbackModelAuthSignIn(ModelAuth modelAuth) {
//        setModelAuth(modelAuth);
//    }
//
//    @Override
//    public void callbackModelAuthSignUp(ModelSignUp modelSignUp) {
//        setModelSignUp(modelSignUp);
//    }

    public ModelSignUp getModelSignUp() {
//        getAuthModelSignUpMutableLiveData();
        networkManager.getAuthModelSignUp();
        return modelSignUp;
    }

    public void setModelSignUp(ModelSignUp modelSignUp) {
        this.modelSignUp = modelSignUp;
    }

    public PayloadEmail getPayloadEmail() {
//        getAuthPayloadEmailMutableLiveData();
//        networkManager.getAuthPayloadEmail();
        return payloadEmail;
    }

    public void setPayloadEmail(PayloadEmail payloadEmail) {
        this.payloadEmail = payloadEmail;
    }

    public ModelAuth getModelAuth() {
//        getAuthModelAuthMutableLiveData();
        networkManager.getAuthModelAuth();
        Log.d(TAG, "getModelAuth: " + modelAuth.getOk());
        return modelAuth;
    }

    public void setModelAuth(ModelAuth modelAuth) {
        this.modelAuth = modelAuth;
    }
}
