package com.emika.app.data.network.networkManager;

import android.util.Log;

import com.emika.app.data.network.callback.CreateCompanyCallback;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.api.CompanyApi;
import com.emika.app.presentation.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyNetworkManager {
    private static final String TAG = "CreateCompanyNetworkMan";
    private String token;
    private String name;
    private String size;
    public CompanyNetworkManager(String token){
        this.token = token;
    }
    public void updateUserInfo(CreateCompanyCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();
        
        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<ModelAuth> call = service.createCompany(token, name, size);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    ModelAuth model = response.body();
                    callback.callbackModelAuth(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
