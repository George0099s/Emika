package com.emika.app.presentation.viewmodel.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.domain.repository.profile.UserRepository;

import java.io.File;
import java.lang.invoke.MutableCallSite;

public class ProfileViewModel extends ViewModel implements UserInfoCallback {

    private String token;
    private UserRepository repository;
    private MutableLiveData<Payload> userMutableLiveData;

    public ProfileViewModel(String token) {
        repository = new UserRepository(token);
        userMutableLiveData = new MutableLiveData<>();
        this.token = token;
    }

    public MutableLiveData<Payload> getUserMutableLiveData() {
        repository.getUser(this);
        return userMutableLiveData;
    }

    public void updateUser(String firstName, String lastName, String bio, String jobTitle){
        repository.updateUser(firstName, lastName, bio, jobTitle, this);
    }

    public void updateImage(File userPhoto){
        repository.updateUserImage(this, userPhoto);
    }

    @Override
    public void updateInfo(UpdateUserModel model) {
        if (model.getOk())
            repository.getUser(this);
    }

    @Override
    public void getUserInfo(Payload userModel) {
        userMutableLiveData.postValue(userModel);
    }
}
