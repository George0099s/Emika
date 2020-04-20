package com.emika.app.domain.repository.profile;

import android.content.Context;

import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.dbmanager.UserDbManager;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.user.MemberCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.emika.app.di.User;
import com.emika.app.presentation.utils.NetworkState;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import java.io.File;

public class UserRepository {
    private String token;
    private UserNetworkManager networkManager;
    private CalendarNetworkManager calendarNetworkManager;
    private UserDbManager userDbManager;
    private Context context;
    public UserRepository(String token) {
        networkManager = new UserNetworkManager(token);
        userDbManager = new UserDbManager();
        calendarNetworkManager = new CalendarNetworkManager(token);
        this.token = token;
    }


    public void downloadUserInfo(UserInfoCallback callback) {
        networkManager.getUserInfo(callback);
    }

    public void getUser(UserDbCallback callback){
        userDbManager.getUser(callback);
    }

    public void updateUser(User userDi, UserInfoCallback callback) {
        networkManager.updateUserInfo(userDi, callback);
    }

    public void updateUserImage(UserInfoCallback callback, File photo){
        networkManager.uploadPhoto(callback, photo);
    }

    public void getAllMembers(ShortMemberCallback callback){
        calendarNetworkManager.getAllShortMembers(callback);
    }

    public void getCompanyInfo(CompanyInfoCallback callback) {
        calendarNetworkManager.downLoadCompanyInfo(callback);
    }
}
