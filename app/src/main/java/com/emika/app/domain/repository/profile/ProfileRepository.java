package com.emika.app.domain.repository.profile;

import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.profile.CallbackInvites;
import com.emika.app.data.network.callback.profile.CallbackSendInvite;
import com.emika.app.data.network.callback.user.MemberCallback;
import com.emika.app.data.network.networkManager.calendar.CalendarNetworkManager;
import com.emika.app.data.network.networkManager.profile.UserNetworkManager;
import com.google.gson.JsonArray;

import org.json.JSONArray;

public class ProfileRepository {
    private String token;
    private CalendarNetworkManager networkManager;
    private UserNetworkManager userNetworkManager;
    public ProfileRepository(String token) {
        this.token = token;
        networkManager = new CalendarNetworkManager(token);
        userNetworkManager = new UserNetworkManager(token);
    }

    public void getAllMembers(ShortMemberCallback callback){
        networkManager.getAllShortMembers(callback);
    }

    public void getMemberInfo(MemberCallback callback, String memberId){
        userNetworkManager.getMemberInfo(memberId, callback);
    }

    public void sendInvite(JSONArray invites, CallbackSendInvite callbackSendInvite){
        userNetworkManager.sendInvite(invites, callbackSendInvite);
    }

    public void getInvites(CallbackInvites callbackInvites){
        userNetworkManager.downloadInvites(callbackInvites);
    }

    public void revoke(CallbackInvites callbackInvites, String id){
        userNetworkManager.revokeInvite(callbackInvites, id);
    }


}
