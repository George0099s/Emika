package com.emika.app.presentation.viewmodel.profile;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.user.UserInfoCallback;
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.di.User;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.domain.repository.profile.UserRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.List;

import javax.inject.Inject;

public class ProfileViewModel extends ViewModel implements UserInfoCallback, UserDbCallback, ShortMemberCallback, CompanyInfoCallback, Parcelable {
    private static final String TAG = "ProfileViewModel";
    private String token;
    private UserRepository repository;
    @Inject
    User userDi;
    private MutableLiveData<Payload> userMutableLiveData;

    private CalendarRepository calendarRepository;
    private Converter converter;
    private Context context;
    private MutableLiveData<List<PayloadShortMember>> memberMutableLiveData;
    private MutableLiveData<PayloadCompanyInfo> companyInfoMutableLiveData;
    private MutableLiveData<User> user;
    public ProfileViewModel(String token) {
        EmikaApplication.instance.getComponent().inject(this);
        this.token = token;
        repository = new UserRepository(token);
        userMutableLiveData = new MutableLiveData<>();
        converter = new Converter();
        calendarRepository = new CalendarRepository(token);
        memberMutableLiveData = new MutableLiveData<>();
        companyInfoMutableLiveData = new MutableLiveData<>();
        user = new MutableLiveData<>();
        user.setValue(userDi);
    }

    public void sss(){
        Log.d(TAG, "sss: " + 123);
    }

    protected ProfileViewModel(Parcel in) {
        token = in.readString();
    }

    public static final Creator<ProfileViewModel> CREATOR = new Creator<ProfileViewModel>() {
        @Override
        public ProfileViewModel createFromParcel(Parcel in) {
            return new ProfileViewModel(in);
        }

        @Override
        public ProfileViewModel[] newArray(int size) {
            return new ProfileViewModel[size];
        }
    };

    public void downloadUserData(){
        repository.downloadUserInfo(this);
    }
    public void getDbUserData(){
        repository.getUser(this);

    }
    public MutableLiveData<Payload> getUserMutableLiveData() {

        return userMutableLiveData;
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
    public MutableLiveData<List<PayloadShortMember>> getMemberMutableLiveData() {
        repository.getAllMembers(this);
        return memberMutableLiveData;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        memberMutableLiveData.postValue(shortMembers);
    }

    public MutableLiveData<PayloadCompanyInfo> getCompanyInfoMutableLiveData() {
        repository.getCompanyInfo(this);
        return companyInfoMutableLiveData;
    }

    @Override
    public void onCompanyInfoDownloaded(PayloadCompanyInfo companyInfo) {
        companyInfoMutableLiveData.postValue(companyInfo);
    }

    public MutableLiveData<User> getUser() {
//        user.setValue(userDi);
        return user;
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        super.onCleared();
    
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }
}
