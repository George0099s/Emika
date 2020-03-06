package com.emika.app.domain.repository.auth;

import com.emika.app.data.db.dbmanager.TokenDbManager;
import com.emika.app.data.network.callback.CompanyCallback;
import com.emika.app.data.network.callback.TokenCallback;
import com.emika.app.data.network.networkManager.auth.AuthNetworkManager;
import com.emika.app.data.network.networkManager.auth.JoinCompanyNetworkManager;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.company.Model;
import com.emika.app.data.network.pojo.singIn.ModelAuth;

import java.util.List;

public class JoinCompanyRepository {
    private Model companyModel;
    private List<Invitation> invitations;
    private ModelAuth modelAuth;
    private String token;
    private TokenDbManager tokenDbManager;
    private String inviteId;
    private AuthNetworkManager authNetworkManager;

    private JoinCompanyNetworkManager networkManager;
    public JoinCompanyRepository(String token) {
        this.token = token;
        this.networkManager = new JoinCompanyNetworkManager(token);
        tokenDbManager = new TokenDbManager();
        authNetworkManager = new AuthNetworkManager();
    }

    public String getInviteId() {
        return inviteId;
    }

    public List<Invitation> getInvitations(CompanyCallback callback) {
        networkManager.setToken(token);
        networkManager.getInvitations(callback);
        return invitations;
    }

    public ModelAuth getAccepted(CompanyCallback callback) {
        networkManager.setInviteId(inviteId);
        networkManager.setToken(token);
        networkManager.acceptInvite(callback);
        return modelAuth;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public void setToken(String token) {
        networkManager.setToken(token);
        this.token = token;
    }

    public String getToken(TokenCallback callback) {
        tokenDbManager.getToken(callback);
        return token;
    }

    public void logOut(TokenCallback callback) {
        authNetworkManager.logOut();
        authNetworkManager.createToken(callback);
    }
}
