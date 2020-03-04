package com.emika.app.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.AuthCallback;
import com.emika.app.domain.repository.AuthRepository;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;

public class AuthViewModel extends ViewModel implements AuthCallback {
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<PayloadEmail> tokenPayloadMutableLiveData;
    private MutableLiveData<ModelAuth> authMutableLiveData;
    private MutableLiveData<ModelSignUp> signUpMutableLiveData;
    private AuthRepository authRepository;
    private AuthCallback authCallback;
    private String token, email, password;

    public AuthViewModel(String token){
        this.token = token;
        authMutableLiveData = new MutableLiveData<>();
        tokenPayloadMutableLiveData = new MutableLiveData<>();
        signUpMutableLiveData = new MutableLiveData<>();
        authCallback = this;
        authRepository = new AuthRepository(token);
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
}
