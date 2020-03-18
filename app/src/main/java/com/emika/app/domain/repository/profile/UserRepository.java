package com.emika.app.domain.repository.profile;

import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;

import java.io.File;

public class UserRepository {
    private String token;
    private UserNetworkManager networkManager;

    public UserRepository(String token) {
        networkManager = new UserNetworkManager(token);
        this.token = token;
    }


    public void getUser(UserInfoCallback callback) {
        networkManager.getUserInfo(callback);
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
