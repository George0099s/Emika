package com.emika.app.presentation.viewmodel.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.domain.repository.auth.JoinCompanyRepository;

import java.util.List;

public class JoinCompanyViewModel extends ViewModel implements CompanyCallback, TokenCallback {
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
        repository.setToken(token);
        repository.getInvitations(this);
        return listInvitationMutableLiveData;
    }

    public void setToken(String token) {
        repository.setToken(token);
        this.token = token;
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
        repository.setToken(token);
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


    @Override
    public void getToken(String token) {
        this.token = token;
    }

    public void logOut() {
        repository.logOut(this);

    }
}
