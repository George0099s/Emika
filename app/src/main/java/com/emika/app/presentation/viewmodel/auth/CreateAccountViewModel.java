package com.emika.app.presentation.viewmodel.auth;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.domain.repository.auth.CreateUserRepository;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;

public class CreateAccountViewModel extends ViewModel implements UserInfoCallback, TokenCallback {
    private static final String TAG = "CreateAccountViewModel";
    private MutableLiveData<UpdateUserModel> updatedUserLiveData;
    private MutableLiveData<Payload> userLiveData;
    private MutableLiveData<String> modelTokenMutableLiveData;

    private CreateUserRepository repository;

    private String token;

    private String firstName;
    private String lastName;
    private String jobTitle;
    private String bio;
    private UserInfoCallback callback;
    private TokenDbManager tokenDbManager;
    public CreateAccountViewModel(String token, String firstName, String lastName, String jobTitle, String bio){
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
        callback = this;
        modelTokenMutableLiveData = new MutableLiveData<>();
        updatedUserLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();
        repository = new CreateUserRepository(token, this.firstName, this.lastName, this.jobTitle, this.bio);
        tokenDbManager = new TokenDbManager();
    }

    public CreateAccountViewModel(String token) {
        this.token = token;
        Log.d(TAG, "CreateAccountViewModel: " + this.token);
        callback = this;
        modelTokenMutableLiveData = new MutableLiveData<>();
        updatedUserLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();
        repository = new CreateUserRepository(this.token, this.firstName, this.lastName, this.jobTitle, this.bio);
        tokenDbManager = new TokenDbManager();

    }

    public void setToken(String token) {
        repository.setToken(token);
        this.token = token;
    }

    public MutableLiveData<UpdateUserModel> getUpdatedUserLiveData() {
        repository.getUpdateUserModel(callback);
        return updatedUserLiveData;
    }

    public MutableLiveData<Payload> getUserLiveData() {
        repository.getUserPayload(callback);
        return userLiveData;
    }

    @Override
    public void updateInfo(UpdateUserModel model) {
        updatedUserLiveData.postValue(model);
    }

    @Override
    public void getUserInfo(Payload userModel) {
        userLiveData.postValue(userModel);
    }

    public void setFirstName(String firstName) {
        repository.setFirstName(firstName);
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        repository.setLastName(lastName);
        this.lastName = lastName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public void logOut(){
        repository.logOut(this);
    }
    @Override
    public void getToken(String token) {
        modelTokenMutableLiveData.postValue(token);
    }

    public MutableLiveData<String> getModelTokenMutableLiveData() {
        repository.getToken(this);
        return modelTokenMutableLiveData;
    }
    public MutableLiveData<String> getExistTokenMutableLiveData() {
        repository.getExistToken(this);
        return modelTokenMutableLiveData;
    }
}
