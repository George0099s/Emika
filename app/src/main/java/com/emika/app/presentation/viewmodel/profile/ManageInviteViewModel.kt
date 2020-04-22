package com.emika.app.presentation.viewmodel.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.network.callback.profile.CallbackInvites
import com.emika.app.data.network.callback.profile.CallbackSendInvite
import com.emika.app.data.network.pojo.invites.InviteModel
import com.emika.app.data.network.pojo.invites.PayloadInvite
import com.emika.app.domain.repository.profile.ProfileRepository
import org.json.JSONArray

class ManageInviteViewModel(val token: String?): ViewModel(), CallbackSendInvite, CallbackInvites {
    private val repository:ProfileRepository = ProfileRepository(token)
    var sendInviteMutableLiveData: MutableLiveData<InviteModel> = MutableLiveData()
    var invites: MutableLiveData<List<PayloadInvite>> = MutableLiveData()
    get() {
        return field
    }
    fun sendInvite(invites: JSONArray){
        repository.sendInvite(invites, this)
    }

    fun getInvites(){
        repository.getInvites( this)
    }

    fun revokeInvite(id: String){
        print(id)
        repository.revoke(this, id)
    }

    override fun onInviteSend(inviteModel: InviteModel) {
        sendInviteMutableLiveData.postValue(inviteModel)
        repository.getInvites(this)
    }

    override fun onInvitesDownloaded(invites: MutableList<PayloadInvite>) {
        this.invites.postValue(invites)
    }
}