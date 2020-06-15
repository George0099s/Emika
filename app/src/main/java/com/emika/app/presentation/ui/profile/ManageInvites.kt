package com.emika.app.presentation.ui.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.invites.InviteModel
import com.emika.app.data.network.pojo.invites.PayloadInvite
import com.emika.app.presentation.adapter.profile.InvitesAdapter
import com.emika.app.presentation.viewmodel.profile.ManageInviteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_manage_invites.*
import org.json.JSONArray
import org.json.JSONObject

class ManageInvites : AppCompatActivity() {

    private var token: String? = EmikaApplication.instance.sharedPreferences?.getString("token", null)
    private val viewModel: ManageInviteViewModel = ManageInviteViewModel(token)
    private var adapter: InvitesAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_invites)
        initView()
    }

    private fun initView() {

        invite_send_invite.setOnClickListener(this::sendInvites)
        invite_recycler.setHasFixedSize(true)
        invite_recycler.layoutManager = LinearLayoutManager(this)
        viewModel.getInvites()
        viewModel.invites.observe(this, getInvites)
    }

    private fun sendInvites(v: View) {
        val invite: JSONObject? = JSONObject()
        val invites: JSONArray? = JSONArray()
        if (invite_email.text.isEmpty()) {
            invite!!.put("email", invite_email.text.toString())
            invite.put("first_name", invite_first_name.text.toString())
            invite.put("last_name", invite_last_name.text.toString())
            invite.put("is_admin", invite_is_manager.isChecked)

            invites!!.put(invite)
            viewModel.sendInvite(invites)
            viewModel.sendInviteMutableLiveData.observe(this, sendInvite)
        } else
            invite_email.requestFocus()
            invite_email.error = getString(R.string.missing_email)
    }

    private val sendInvite = Observer<InviteModel> { inviteModel ->
        if (inviteModel.payload) {
            val snack = Snackbar.make(rootView,"Invite has sent",Snackbar.LENGTH_LONG)
            snack.show()
            invite_email.setText("")
            invite_first_name.setText("")
            invite_last_name.setText("")
            invite_is_manager.isChecked = false
            viewModel.invites
        } else {
            val snack = Snackbar.make(rootView, "Something went wrong", Snackbar.LENGTH_LONG)
            snack.show()
        }
    }
    private val getInvites = Observer<List<PayloadInvite>> { invites ->
        adapter = InvitesAdapter(invites as MutableList<PayloadInvite>, viewModel, this)
        invite_recycler.adapter = adapter
    }
}
