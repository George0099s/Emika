package com.emika.app.auth.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.auth.callback.TokenCallback;
import com.emika.app.auth.data.AuthRepository;
import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.PayloadEmail;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;
import com.emika.app.auth.networkManager.AuthNetworkManager;
import com.emika.app.utils.NetworkService;

public class AuthViewModel extends ViewModel implements TokenCallback{
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<PayloadEmail> tokenPayloadMutableLiveData;
    private MutableLiveData<ModelAuth> authMutableLiveData;
    private MutableLiveData<ModelSignUp> signUpMutableLiveData;
    private AuthRepository authRepository;
    private TokenCallback tokenCallback;
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        authMutableLiveData = new MutableLiveData<>();
        tokenPayloadMutableLiveData = new MutableLiveData<>();
        signUpMutableLiveData = new MutableLiveData<>();
    }

    public void init() {
//       tokenPayloadMutableLiveData.setValue(authRepository.getPayloadEmail());
       authRepository.getAuthPayloadEmailMutableLiveData();
    }

    public void signIn(){
       authMutableLiveData.setValue(authRepository.getModelAuth());
    }
    public void signUp(){
        signUpMutableLiveData.setValue(authRepository.getModelSignUp());
    }

    public MutableLiveData<PayloadEmail> getTokenPayloadMutableLiveData() {
//        tokenPayloadMutableLiveData.setValue(authRepository.getPayloadEmail());
        return tokenPayloadMutableLiveData;
    }
    public MutableLiveData<ModelSignUp> getSignUpMutableLiveData() {
//        signUp();
        return signUpMutableLiveData;
    }

    public MutableLiveData<ModelAuth> getAuthModelAuthMutableLiveData() {
//        signIn();
        return authMutableLiveData;
    }

    public void setPassword(String password){
        authRepository.setPassword(password);
    }

    @Override
    public void callbackCheckedEmail(PayloadEmail payloadEmail) {
        Log.d(TAG, "callbackCheckedEmail: " + payloadEmail.getExists());
        tokenPayloadMutableLiveData.setValue(payloadEmail);
    }

    @Override
    public void callbackModelAuthSignIn(ModelAuth modelAuth) {
        authMutableLiveData.setValue(modelAuth);
    }

    @Override
    public void callbackModelAuthSignUp(ModelSignUp modelSignUp) {
        signUpMutableLiveData.setValue(modelSignUp);
    }
}
