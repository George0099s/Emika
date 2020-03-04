package com.emika.app.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.callback.UserInfoCallback;
import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.domain.repository.CreateUserRepository;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;

public class CreateAccountViewModel extends ViewModel implements UserInfoCallback, TokenCallback {
    private MutableLiveData<UpdateUserModel> updatedUserLiveData;
    private MutableLiveData<Payload> userLiveData;
    private MutableLiveData<ModelToken> modelTokenMutableLiveData;

    private CreateUserRepository repository;
    private String token;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String bio;
    private UserInfoCallback callback;
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



    @Override
    public void getToken(ModelToken model) {
        modelTokenMutableLiveData.postValue(model);
    }

    public MutableLiveData<ModelToken> getModelTokenMutableLiveData() {
        repository.getToken(this);
        return modelTokenMutableLiveData;
    }
}
