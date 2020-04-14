package com.emika.app.presentation.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.invites.InviteModel
import com.emika.app.presentation.viewmodel.profile.ManageInviteViewModel
import kotlinx.android.synthetic.main.activity_manage_invites.*
import org.json.JSONArray
import org.json.JSONObject

class ManageInvites : AppCompatActivity() {

    private var token: String? = EmikaApplication.getInstance().sharedPreferences.getString("token", null)
    private val viewModel: ManageInviteViewModel = ManageInviteViewModel(token)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_invites)
        initView()
    }

    private fun initView() {
        invite_send_invite.setOnClickListener(this::sendInvites)
    }

    private fun sendInvites(v: View) {
        val invite: JSONObject? = JSONObject()
        val invites: JSONArray? = JSONArray()
        invite!!.put("email", invite_email.text.toString())
        invite.put("first_name", invite_first_name.text.toString())
        invite.put("last_name", invite_last_name.text.toString())
        invite.put("is_admin", invite_is_manager.isChecked)


        invites!!.put(invite)
        viewModel.sendInvite(invites)
        viewModel.sendInviteMutableLiveData.observe(this, sendInvite)
    }

    private val sendInvite = Observer<InviteModel> { inviteModel ->
        if (inviteModel.payload) {
            Toast.makeText(this, "Invite has sent", Toast.LENGTH_SHORT).show()
            invite_email.setText("")
            invite_first_name.setText("")
            invite_last_name.setText("")
            invite_is_manager.setChecked(false)
        } else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
