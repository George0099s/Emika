package com.emika.app.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.user.Contact
import com.emika.app.di.User
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.profile.CreateContactDialogViewModel
import kotlinx.android.synthetic.main.create_contact_dialog_fragment.*
import kotlinx.android.synthetic.main.create_contact_dialog_fragment.view.*
import kotlinx.android.synthetic.main.create_contact_dialog_fragment.view.createContactValue
import javax.inject.Inject

class CreateContactDialogFragment : DialogFragment() {
    private val contact: Contact = Contact()
    private var token: String? = null
    private var contactType: TextView? = null
    private var cancel:Button? = null
    @Inject
    lateinit var userDi: User
    companion object {
        fun newInstance() = CreateContactDialogFragment()
    }

    private lateinit var viewModel: CreateContactDialogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.create_contact_dialog_fragment, container, false)
        EmikaApplication.instance.component?.inject(this)
        token = EmikaApplication.instance.sharedPreferences?.getString("token", token)
        cancel = rootView.findViewById(R.id.createContactCancel) as Button
        contactType = rootView.findViewById(R.id.createContactType) as TextView
        contact.type = arguments!!.getString("type","")
        when (contact.type){
            "email" ->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.email_string))
             "telegram"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.telegram))
             "whatsapp"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.whats_app))
            "vk"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.vk))
            "facebook"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.facebook))
            "linkedin"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.linkedin))
            "wechat"->  contactType!!.text = String.format(resources.getString(R.string.set_contact_type), resources.getString(R.string.wechat))
        }



        cancel!!.setOnClickListener { onDismiss(dialog!!) }
        rootView.createContactOk.setOnClickListener {
            contact.value = rootView.createContactValue.text.toString()
            userDi.contacts.add(contact)
            viewModel.addContact(userDi)
            onDismiss(dialog!!)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(CreateContactDialogViewModel::class.java)

        // TODO: Use the ViewModel
    }

}
