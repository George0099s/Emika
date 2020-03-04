package com.emika.app.domain.repository;

import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.networkManager.JoinCompanyNetworkManager;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.company.Model;
import com.emika.app.data.network.pojo.singIn.ModelAuth;

import java.util.List;

public class JoinCompanyRepository {
    private Model companyModel;
    private List<Invitation> invitations;
    private ModelAuth modelAuth;
    private String token;

    private String inviteId;

    private JoinCompanyNetworkManager networkManager;
    public JoinCompanyRepository(String token) {
        this.token = token;
        this.networkManager = new JoinCompanyNetworkManager(token);
    }

    public String getInviteId() {
        return inviteId;
    }

    public List<Invitation> getInvitations(CompanyCallback callback) {
        networkManager.getInvitations(callback);
        return invitations;
    }

    public ModelAuth getAccepted(CompanyCallback callback) {
        networkManager.setInviteId(inviteId);
        networkManager.acceptInvite(callback);
        return modelAuth;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }
}
