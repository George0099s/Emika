package com.emika.app.data.network.callback.profile

import com.emika.app.data.network.pojo.invites.InviteModel
import com.emika.app.data.network.pojo.invites.PayloadInvite

interface CallbackInvites {
    fun onInvitesDownloaded(invites: MutableList<PayloadInvite>)
}