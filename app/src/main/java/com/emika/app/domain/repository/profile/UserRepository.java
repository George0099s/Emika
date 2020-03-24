package com.emika.app.domain.repository.profile;

import android.content.Context;

import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.presentation.utils.NetworkState;

import java.io.File;

public class UserRepository {
    private String token;
    private UserNetworkManager networkManager;
    private UserDbManager userDbManager;
    private Context context;
    public UserRepository(String token) {
        networkManager = new UserNetworkManager(token);
        userDbManager = new UserDbManager();
        this.token = token;
    }


    public void downloadUserInfo(UserInfoCallback callback) {
        networkManager.getUserInfo(callback);
    }

    public void getUser(UserDbCallback callback){
        userDbManager.getUser(callback);
    }

    public void updateUser(String firstName, String lastName, String bio, String jobTitle, UserInfoCallback callback) {
        networkManager.setFirstName(firstName);
        networkManager.setLastName(lastName);
        networkManager.setBio(bio);
        networkManager.setJobTitle(jobTitle);
        networkManager.updateUserInfo(callback);
    }

    public void updateUserImage(UserInfoCallback callback, File photo){
        networkManager.uploadPhoto(callback, photo);
    }
}
