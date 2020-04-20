package com.emika.app.presentation.viewmodel.profile;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.profile.UserRepository;
import com.emika.app.presentation.utils.Converter;

import java.io.File;
import java.util.List;

public class EditProfileViewModel extends ViewModel implements UserInfoCallback, UserDbCallback, CompanyInfoCallback{
    private static final String TAG = "EditProfileViewModel";
    private String token;

    private UserRepository repository;
    private MutableLiveData<Payload> userMutableLiveData;
    private CalendarRepository calendarRepository;
    private Converter converter;
    private Context context;
    private MutableLiveData<List<PayloadShortMember>> memberMutableLiveData;
    private MutableLiveData<PayloadCompanyInfo> companyInfoMutableLiveData;
    public EditProfileViewModel(String token) {
        this.token = token;
        repository = new UserRepository(token);
        userMutableLiveData = new MutableLiveData<>();
        converter = new Converter();
        calendarRepository = new CalendarRepository(token);
        memberMutableLiveData = new MutableLiveData<>();
        companyInfoMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Payload> getUserMutableLiveData() {
        repository.downloadUserInfo(this);
        repository.getUser(this);
        return userMutableLiveData;
    }

    public void  updateUser(User userDi){
        repository.updateUser(userDi, this);
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

    public MutableLiveData<PayloadCompanyInfo> getCompanyInfoMutableLiveData() {
        repository.getCompanyInfo(this);
        return companyInfoMutableLiveData;
    }

    @Override
    public void onCompanyInfoDownloaded(PayloadCompanyInfo companyInfo) {
        companyInfoMutableLiveData.postValue(companyInfo);
    }

}
