package com.emika.app.domain.repository.auth;

import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;

public class CreateUserRepository {

    private String token, firstName, lastName, jobTitle, bio;
    private static final String TAG = "CreateUserRepository";
    private UpdateUserModel updateUserModel;
    private Payload userPayload;
    private UserNetworkManager userNetworkManager;
    private AuthNetworkManager authNetworkManager;
    private TokenDbManager tokenDbManager;
    public CreateUserRepository(String token, String firstName, String lastName, String jobTitle, String bio){
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
        userNetworkManager = new UserNetworkManager(token, firstName, lastName, jobTitle, bio);
        authNetworkManager = new AuthNetworkManager(token);
        tokenDbManager = new TokenDbManager();
    }
    public void setToken(String token) {
        userNetworkManager.setToken(token);
        this.token = token;
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

    public String getExistToken(TokenCallback callback) {
        tokenDbManager.getToken(callback);
        return token;
    }
    public void logOut(TokenCallback callback) {
        authNetworkManager.logOut();
        authNetworkManager.createToken(callback);
    }
}
