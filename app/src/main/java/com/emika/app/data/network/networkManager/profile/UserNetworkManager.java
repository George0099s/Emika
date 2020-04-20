package com.emika.app.data.network.networkManager.profile;

import android.util.Log;
import android.widget.Toast;

import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.CompanyApi;
import com.emika.app.data.network.api.MemberApi;
import com.emika.app.data.network.callback.profile.CallbackInvites;
import com.emika.app.data.network.callback.profile.CallbackSendInvite;
import com.emika.app.data.network.callback.user.MemberCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.api.UserApi;
import com.emika.app.data.network.pojo.invites.InviteModel;
import com.emika.app.data.network.pojo.invites.PayloadInvite;
import com.emika.app.data.network.pojo.invites.PendingInviteModel;
import com.emika.app.data.network.pojo.member.ModelMember;
import com.emika.app.data.network.pojo.member.PayloadMember;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Model;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.Converter;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private Converter converter;
    public UserNetworkManager(String token, String firstName, String lastName, String jobTitle, String bio){
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
        converter = new Converter();
    }

    public UserNetworkManager(String token){
        this.token = token;
        converter = new Converter();
    }


    public void updateUserInfo(User userDi, UserInfoCallback callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        UserApi service = retrofit.create(UserApi.class);

        Call<UpdateUserModel> call = service.updateAccountInfo(token, userDi.getFirstName(), userDi.getLastName(), userDi.getJobTitle(), userDi.getBio(), converter.fromListContactToJSON(userDi.getContacts()));
        Log.d(TAG, "updateUserInfo: " + call.request().url());
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

    public void getMemberInfo(String memberId, MemberCallback callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        MemberApi service = retrofit.create(MemberApi.class);
        Call<ModelMember> call = service.getMember(memberId, token);
        call.enqueue(new Callback<ModelMember>() {
            @Override
            public void onResponse(retrofit2.Call<ModelMember> call, Response<ModelMember> response) {
                if (response.body() != null) {
                    ModelMember model = response.body();
                    PayloadMember member = model.getPayload();
                    callback.onMemberInfoLoaded(member);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelMember> call, Throwable t) {
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void getUserInfo(UserInfoCallback callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
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

    public void sendInvite(JSONArray invites, CallbackSendInvite callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<InviteModel> call = service.sendInvite(token, invites);
        Log.d(TAG, "sendInvite: " + call.request().url());
        call.enqueue(new Callback<InviteModel>() {
            @Override
            public void onResponse(retrofit2.Call<InviteModel> call, Response<InviteModel> response) {
                if (response.body() != null) {
                    InviteModel model = response.body();
                    Log.d(TAG, "onResponse: " + model.getPayload());
                    callback.onInviteSend(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InviteModel> call, Throwable t) {
                callback.onInviteSend(null);
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
    public void downloadInvites(CallbackInvites callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<PendingInviteModel> call = service.fetchInvitation(token);
        call.enqueue(new Callback<PendingInviteModel>() {
            @Override
            public void onResponse(retrofit2.Call<PendingInviteModel> call, Response<PendingInviteModel> response) {
                if (response.body() != null) {
                    PendingInviteModel model = response.body();
                    List<PayloadInvite> list = model.getPayloadInvite();
                    if (list != null)
                        callback.onInvitesDownloaded(list);
                    else
                        callback.onInvitesDownloaded(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<PendingInviteModel> call, Throwable t) {
                callback.onInvitesDownloaded(null);
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }

    public void uploadPhoto(UserInfoCallback callback, File userImageFile){
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();

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

    public void revokeInvite(CallbackInvites callback, String id) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<InviteModel> call = service.revokeInvite(id, token);
        call.enqueue(new Callback<InviteModel>() {
            @Override
            public void onResponse(retrofit2.Call<InviteModel> call, Response<InviteModel> response) {
                if (response.body() != null) {
                    InviteModel model = response.body();

//                        callback.onInvitesDownloaded(model.getPayload());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InviteModel> call, Throwable t) {
                callback.onInvitesDownloaded(null);
                Log.d(TAG, "Something went wrong :c");
            }
        });
    }
}
