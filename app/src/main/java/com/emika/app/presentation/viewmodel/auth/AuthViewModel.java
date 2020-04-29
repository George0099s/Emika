package com.emika.app.presentation.viewmodel.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.AuthCallback;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.domain.repository.auth.AuthRepository;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;

public class AuthViewModel extends ViewModel implements AuthCallback, TokenCallback {
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<PayloadEmail> tokenPayloadMutableLiveData;
    private MutableLiveData<ModelAuth> authMutableLiveData;
    private MutableLiveData<ModelSignUp> signUpMutableLiveData;
    private MutableLiveData<String> tokenLiveData;

    private MutableLiveData<ModelAuth> restorePassword;

    private AuthRepository authRepository;

    private AuthCallback authCallback;

    private String token, email, password;
    public AuthViewModel(String token){
        this.token = token;
        authMutableLiveData = new MutableLiveData<>();
        tokenPayloadMutableLiveData = new MutableLiveData<>();
        signUpMutableLiveData = new MutableLiveData<>();
        restorePassword = new MutableLiveData<>();
        authCallback = this;
        authRepository = new AuthRepository(token);
        tokenLiveData = new MutableLiveData<>();
    }
    public void getToken(){

    }

    public MutableLiveData<String> getTokenLiveData() {

        return tokenLiveData;
    }

    private void init() {
         authRepository.getPayloadEmail(authCallback);
    }
    private void signIn(){
        authRepository.getModelAuth(authCallback);
    }
    private void signUp(){
        authRepository.getModelSignUp(authCallback);
    }
    private void restorePassword(String email){
        authRepository.restorePassword(this, email);
    }

    public MutableLiveData<ModelAuth> getRestorePassword(String email) {
        restorePassword(email);
        return restorePassword;
    }
    public MutableLiveData<PayloadEmail> getTokenPayloadMutableLiveData() {
        init();
        return tokenPayloadMutableLiveData;
    }
    public MutableLiveData<ModelSignUp> getSignUpMutableLiveData() {
        signUp();
        return signUpMutableLiveData;
    }

    public MutableLiveData<ModelAuth> getAuthModelAuthMutableLiveData() {
        signIn();
        return authMutableLiveData;
    }

    public void setPassword(String password){
        authRepository.setPassword(password);
    }
    public void setEmail(String email){
        authRepository.setEmail(email);
    }

    @Override
    public void callbackCheckedEmail(PayloadEmail payloadEmail) {
        tokenPayloadMutableLiveData.postValue(payloadEmail);
    }

    @Override
    public void callbackModelAuthSignIn(ModelAuth modelAuth) {
        authMutableLiveData.postValue(modelAuth);
    }

    @Override
    public void callbackModelAuthSignUp(ModelSignUp modelSignUp) {
        signUpMutableLiveData.postValue(modelSignUp);
    }

    @Override
    public void callbackRestorePassword(ModelAuth modelAuth) {
        restorePassword.postValue(modelAuth);
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void getToken(String token) {
        tokenLiveData.postValue(token);
    }
}
