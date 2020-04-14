package com.emika.app.presentation.viewmodel.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.domain.repository.profile.ProfileRepository;

import java.util.List;

public class AllMembersViewModel extends ViewModel implements ShortMemberCallback  {
    private String token;
    private ProfileRepository repository;
    private MutableLiveData<List<PayloadShortMember>> memberMutableLiveData;
    public AllMembersViewModel(String token) {
        this.token = token;
        repository = new ProfileRepository(token);
        memberMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public void allMembers(List<PayloadShortMember> shortMembers) {
        memberMutableLiveData.postValue(shortMembers);
    }

    public MutableLiveData<List<PayloadShortMember>> getMemberMutableLiveData() {
        repository.getAllMembers(this);
        return memberMutableLiveData;
    }
}
