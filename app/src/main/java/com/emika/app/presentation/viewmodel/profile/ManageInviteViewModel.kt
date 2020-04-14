package com.emika.app.presentation.viewmodel.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emika.app.data.network.callback.profile.CallbackSendInvite
import com.emika.app.data.network.pojo.invites.InviteModel
import com.emika.app.domain.repository.profile.ProfileRepository
import org.json.JSONArray

class ManageInviteViewModel(val token: String?): ViewModel(), CallbackSendInvite  {
    private val repository:ProfileRepository = ProfileRepository(token)
    var sendInviteMutableLiveData: MutableLiveData<InviteModel> = MutableLiveData()

    fun sendInvite(invites: JSONArray){
        repository.sendInvite(invites, this)
    }

    override fun onInviteSend(inviteModel: InviteModel) {
        sendInviteMutableLiveData.postValue(inviteModel)
    }
}