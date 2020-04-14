package com.emika.app.data.network.callback.profile

import com.emika.app.data.network.pojo.invites.InviteModel

interface CallbackSendInvite {
    fun onInviteSend(inviteModel: InviteModel)
}