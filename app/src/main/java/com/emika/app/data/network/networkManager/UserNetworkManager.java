package com.emika.app.data.network.networkManager;

import android.util.Log;

import com.emika.app.data.network.callback.UserInfoCallback;
import com.emika.app.data.network.api.UserApi;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Model;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserNetworkManager {
    private static final String TAG = "CreateUserNetworkManage";
    private String token, firstName, lastName, jobTitle, bio;

    public UserNetworkManager(String token, String firstName, String lastName, String jobTitle, String bio){
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
    }

    public UserNetworkManager(String token){
        this.token = token;
    }


    public void updateUserInfo(UserInfoCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        UserApi service = retrofit.create(UserApi.class);
        Call<UpdateUserModel> call = service.updateAccountInfo(token, firstName, lastName, jobTitle, bio);
        Log.d(TAG, "updateUserInfo: " + firstName + " " + lastName);
        call.enqueue(new Callback<UpdateUserModel>() {
            @Override
            public void onResponse(retrofit2.Call<UpdateUserModel> call, Response<UpdateUserModel> response) {
                if (response.body() != null) {
                    UpdateUserModel model = response.body();
                    callback.updateInfo(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UpdateUserModel> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void getUserInfo(UserInfoCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        UserApi service = retrofit.create(UserApi.class);
        Call<Model> call = service.getUserInfo(token);
        Log.d(TAG, "getUserInfo: " + call.request().url());
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                if (response.body() != null) {
                    Model model = response.body();
                    Payload payload = model.getPayload();
                    Log.d(TAG, "onResponse: " + payload.getFirstName());
                    callback.getUserInfo(payload);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
