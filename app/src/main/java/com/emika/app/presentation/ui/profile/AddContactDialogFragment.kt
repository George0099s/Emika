package com.emika.app.presentation.ui.profile

import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.user.Contact
import com.emika.app.presentation.adapter.profile.AddContactAdapter
import com.emika.app.presentation.viewmodel.profile.AddContactDialogViewModel
import com.google.android.material.snackbar.Snackbar


class AddContactDialogFragment : DialogFragment() {


    private var contacts: MutableList<Contact> = arrayListOf()
    private lateinit var cancel: Button
    companion object {
        fun newInstance() = AddContactDialogFragment()
    }

    private lateinit var viewModel: AddContactDialogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView=  inflater.inflate(R.layout.add_contact_dialog_fragment, container, false)
        val myListView = rootView.findViewById(R.id.addContactRecycler) as RecyclerView
        val email= Contact()
        email.type = "email"
        contacts.add(email)
        val telegram = Contact()
        telegram.type = "telegram"
        contacts.add(telegram)
        val whatsapp = Contact()
        whatsapp.type = "whatsapp"
        contacts.add(whatsapp)
        val vk = Contact()
        vk.type = "vk"
        contacts.add(vk)
        val instagram = Contact()
        instagram.type = "instagram"
        contacts.add(instagram)
        val linkedin = Contact()
        linkedin.type = "linkedin"
        contacts.add(linkedin)
        val facebook = Contact()
        facebook.type = "facebook"
        contacts.add(facebook)
        val wechat = Contact()
        wechat.type = "wechat"
        contacts.add(wechat)
        myListView.layoutManager = LinearLayoutManager(context)
        myListView.setHasFixedSize(true)
        val adapter = AddContactAdapter(contacts ,context!!, fragmentManager!!)
        myListView.adapter = adapter
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddContactDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}
