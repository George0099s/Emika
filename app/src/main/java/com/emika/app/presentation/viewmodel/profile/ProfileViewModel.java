package com.emika.app.presentation.viewmodel.profile;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.profile.UserRepository;
import com.emika.app.presentation.utils.Converter;
import com.emika.app.presentation.utils.NetworkState;

import java.io.File;

public class ProfileViewModel extends ViewModel implements UserInfoCallback, UserDbCallback {

    private String token;
    private UserRepository repository;
    private MutableLiveData<Payload> userMutableLiveData;
    private CalendarRepository calendarRepository;
    private Converter converter;
    private Context context;
    public ProfileViewModel(String token) {
        repository = new UserRepository(token);
        userMutableLiveData = new MutableLiveData<>();
        this.token = token;
        converter = new Converter();
        calendarRepository = new CalendarRepository(token);
    }

    public MutableLiveData<Payload> getUserMutableLiveData() {
        repository.downloadUserInfo(this);
        repository.getUser(this);
        return userMutableLiveData;
    }

//    public void downloadUserInfo(){
//        repository.downloadUserInfo(this);
//    }

    public void updateUser(String firstName, String lastName, String bio, String jobTitle){
        repository.updateUser(firstName, lastName, bio, jobTitle, this);
    }

    public void updateImage(File userPhoto){
        repository.updateUserImage(this, userPhoto);
    }


    @Override
    public void updateInfo(UpdateUserModel model) {
        if (model.getOk())
            repository.downloadUserInfo(this);
    }

    @Override
    public void getUserInfo(Payload userModel) {
//        calendarRepository.insertDbUser(userModel);
        userMutableLiveData.postValue(userModel);
    }

    @Override
    public void onUserLoaded(UserEntity user) {
        userMutableLiveData.postValue(converter.fromUserEntityToPayloadUser(user));
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
