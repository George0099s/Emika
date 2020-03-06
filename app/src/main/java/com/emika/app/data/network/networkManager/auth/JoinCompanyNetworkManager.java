package com.emika.app.data.network.networkManager.auth;

import android.util.Log;

import com.emika.app.data.network.api.CompanyApi;
import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.company.Model;
import com.emika.app.data.network.pojo.company.Payload;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.presentation.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinCompanyNetworkManager {
    private static final String TAG = "JoinCompanyNetworkManag";

    private String token, inviteId;

    public JoinCompanyNetworkManager(String token){
        this.token = token;

    }

    public void getInvitations(CompanyCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<Model> call = service.checkInvitation(token);
        Log.d(TAG, "getInvitations: " + call.request().url());
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                if (response.body() != null) {
                    Model company = response.body();
                    Payload payload = company.getPayload();
                    List<Invitation> invitations = payload.getInvitations();
                    if (invitations != null)
                    callback.invitations(invitations);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void acceptInvite(CompanyCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<ModelAuth> call = service.acceptInvite(inviteId, token);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(retrofit2.Call<ModelAuth> call, Response<ModelAuth> response) {
                if (response.body() != null) {
                    ModelAuth company = response.body();
                    callback.accepted(company);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelAuth> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void setToken(String token) {
        Log.d(TAG, "setToken: " + token);
        this.token = token;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }
}
