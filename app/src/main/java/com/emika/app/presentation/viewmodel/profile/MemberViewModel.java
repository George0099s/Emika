package com.emika.app.presentation.viewmodel.profile;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.user.MemberCallback;
import com.emika.app.data.network.pojo.member.PayloadMember;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.domain.repository.profile.ProfileRepository;
import com.emika.app.features.calendar.swipe.ListSwipeHelper;

import java.util.List;

public class MemberViewModel extends ViewModel implements MemberCallback, ShortMemberCallback {
    private String token;
    private ProfileRepository repository;
    private static final String TAG = "MemberViewModel";
    private MutableLiveData<List<PayloadShortMember>> membersMutableLiveData;

    private MutableLiveData<PayloadMember> memberMutableLiveData;
    public MemberViewModel(String token) {
        this.token = token;
        repository = new ProfileRepository(token);
        memberMutableLiveData = new MutableLiveData<>();
        membersMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<PayloadMember> getMemberMutableLiveData(String memberId) {
        repository.getMemberInfo(this, memberId);
        return memberMutableLiveData;
    }


    public MutableLiveData<List<PayloadShortMember>> getMembersMutableLiveData() {
        repository.getAllMembers(this);
        return membersMutableLiveData;
    }

    @Override
    public void onMemberInfoLoaded(PayloadMember member) {
        Log.d(TAG, "onMemberInfoLoaded: " + member.getExtraLeaders().size());
        memberMutableLiveData.postValue(member);
    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        membersMutableLiveData.postValue(shortMembers);
    }
}
