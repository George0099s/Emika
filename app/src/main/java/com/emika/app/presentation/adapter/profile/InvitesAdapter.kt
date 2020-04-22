package com.emika.app.presentation.adapter.profile

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.invites.PayloadInvite
import com.emika.app.presentation.viewmodel.profile.ManageInviteViewModel

class InvitesAdapter(private val pendingInviteModels: MutableList<PayloadInvite>, private val viewModel: ManageInviteViewModel, private val context: Context) : RecyclerView.Adapter<InvitesAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var email: TextView? = null
        var status: TextView? = null
        var revoke: Button? = null

        init {
            email = itemView.findViewById(R.id.inviteEmail)
            status = itemView.findViewById(R.id.inviteDate)
            revoke = itemView.findViewById(R.id.inviteRevoke)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pending_invite, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pendingInviteModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var invite = pendingInviteModels[position]
        holder.email!!.text = invite.email
        holder.status!!.text = invite.inviteSentAt
        if (invite.status == ("activated")) {
            holder.revoke!!.visibility = View.GONE
            holder.status!!.text = context.getString(R.string.invitation_activated) + " " + invite.createdAt
//            holder.status!!.setTextColor(context.resources.getColor(R.color.green))
        }
        if (invite.status == ("revoked")) {
            holder.revoke!!.visibility = View.GONE
            holder.status!!.text = context.getString(R.string.revoked) + " " + invite.createdAt
//            holder.status!!.setTextColor(context.resources.getColor(R.color.red))
        }

        holder.revoke!!.setOnClickListener {
            viewModel.revokeInvite(invite.id)
            viewModel.invites
            pendingInviteModels[position].status = "revoked"
            notifyDataSetChanged()
        }
    }


}