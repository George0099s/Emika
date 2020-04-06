package com.emika.app.data.network.networkManager.profile;

import android.util.Log;
import android.widget.Toast;

import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.api.UserApi;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Model;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.utils.Constants;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserNetworkManager {
    private static final String TAG = "UserNetworkManage";
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
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                Log.d(TAG, "onResponse: " + call.request().url());
                if (response.body() != null) {
                    Model model = response.body();
                    Payload payload = model.getPayload();
                    callback.getUserInfo(payload);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void uploadPhoto(UserInfoCallback callback, File userImageFile){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), userImageFile);
        MultipartBody.Part requestImg = MultipartBody.Part.createFormData("file", userImageFile.getName(), requestBody);
        UserApi service = retrofit.create(UserApi.class);
        Call<UpdateUserModel> updateImage = service.updateUserImage(token, requestImg);
        updateImage.enqueue(new Callback<UpdateUserModel>() {

            @Override
            public void onResponse(Call<UpdateUserModel> call, Response<UpdateUserModel> response) {
                if (response.body() != null) {
                    UpdateUserModel object = response.body();
                    callback.updateInfo(object);
                }
            }

            @Override
            public void onFailure(Call<UpdateUserModel> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
