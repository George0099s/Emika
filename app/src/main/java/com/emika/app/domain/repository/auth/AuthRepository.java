package com.emika.app.domain.repository.auth;

import com.emika.app.data.network.callback.AuthCallback;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;

public class AuthRepository{
    private static final String TAG = "AuthRepository";
    private String token, password, email;
    private ModelSignUp modelSignUp;
    private PayloadEmail payloadEmail;
    private ModelAuth modelAuth;

    private AuthNetworkManager networkManager;

    public void setPassword(String password) {
        this.password = password;
        networkManager.setPassword(password);
    }

    public AuthRepository(String token) {
        this.token = token;
        networkManager = new AuthNetworkManager(token, email, password);
    }


    public ModelSignUp getModelSignUp(AuthCallback callback) {
        networkManager.getAuthModelSignUp(callback);
        return modelSignUp;
    }

    public PayloadEmail getPayloadEmail(AuthCallback callback) {
        networkManager.getAuthPayloadEmail(callback);
        return payloadEmail;
    }


    public ModelAuth getModelAuth(AuthCallback callback) {
        networkManager.getAuthModelAuth(callback);
        return modelAuth;
    }

    public void setEmail(String email) {
        this.email = email;
        networkManager.setEmail(email);
    }

    public void restorePassword(AuthCallback callback, String email) {
        networkManager.restorePassword(callback, email);
    }
}
