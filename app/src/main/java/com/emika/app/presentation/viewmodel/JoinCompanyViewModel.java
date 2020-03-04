package com.emika.app.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.domain.repository.JoinCompanyRepository;

import java.util.List;

public class JoinCompanyViewModel extends ViewModel implements CompanyCallback {
    private static final String TAG = "JoinCompanyViewModel";
    private MutableLiveData<List<Invitation>> listInvitationMutableLiveData;
    private MutableLiveData<ModelAuth> acceptMutableLiveData;
    private String token;
    private String inviteId;
    private JoinCompanyRepository repository;


    public JoinCompanyViewModel(String token) {
        this.token = token;
        repository = new JoinCompanyRepository(token);
        listInvitationMutableLiveData = new MutableLiveData<>();
        acceptMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<List<Invitation>> getListInvitationMutableLiveData() {
        repository.getInvitations(this);
        return listInvitationMutableLiveData;
    }

    @Override
    public void invitations(List<Invitation> invitations) {
        listInvitationMutableLiveData.postValue(invitations);
    }

    @Override
    public void accepted(ModelAuth model) {
        acceptMutableLiveData.postValue(model);
    }

    public MutableLiveData<ModelAuth> getAcceptMutableLiveData() {
        repository.getAccepted(this);
        return acceptMutableLiveData;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        repository.setInviteId(inviteId);
        this.inviteId = inviteId;
    }
}
