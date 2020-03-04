package com.emika.app.domain.repository;

import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.callback.UserInfoCallback;
import com.emika.app.data.network.networkManager.AuthNetworkManager;
import com.emika.app.data.network.networkManager.UserNetworkManager;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.viewmodel.CreateAccountViewModel;

public class CreateUserRepository {

    private String token, firstName, lastName, jobTitle, bio;
    private UpdateUserModel updateUserModel;
    private Payload userPayload;
    private UserNetworkManager userNetworkManager;
    private AuthNetworkManager authNetworkManager;
    public CreateUserRepository(String token, String firstName, String lastName, String jobTitle, String bio){
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
        userNetworkManager = new UserNetworkManager(token, firstName, lastName, jobTitle, bio);
        authNetworkManager = new AuthNetworkManager(token);
    }

    public UpdateUserModel getUpdateUserModel(UserInfoCallback callback) {
        userNetworkManager.updateUserInfo(callback);
        return updateUserModel;
    }

    public Payload getUserPayload(UserInfoCallback callback) {
        userNetworkManager.getUserInfo(callback);
        return userPayload;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        userNetworkManager.setFirstName(firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        userNetworkManager.setLastName(lastName);
        this.lastName = lastName;
    }

    public String getToken(TokenCallback callback) {
        authNetworkManager.createToken(callback);
        return token;
    }
}
