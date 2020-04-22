package com.emika.app.presentation.adapter.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.user.Contact
import com.emika.app.presentation.ui.profile.CreateContactDialogFragment

class AddContactAdapter(private val contacts: List<Contact>, private val context: Context, private val fm: FragmentManager) : RecyclerView.Adapter<AddContactAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.item_contact_img) as ImageView
        val text = itemView.findViewById(R.id.item_contact_text) as TextView
        val item = itemView.findViewById(R.id.item_add_contact) as LinearLayout
    }
    val bundle = Bundle()
    val fr = fm.findFragmentByTag("addContactDialog")
    val createContact = CreateContactDialogFragment()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_add_contact, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        when (contact.type) {
            "email" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_mail))
                holder.text.text = "Email"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView();
                    bundle.putString("type","email")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "telegram" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_telegram))
                holder.text.text = "Telegram"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView()
                    bundle.putString("type","telegram")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "whatsapp" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_whats_app))
                holder.text.text = "What's app"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView()
                    bundle.putString("type","whatsapp")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "vk" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_vk))
                holder.text.text = "VK"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView();
                    bundle.putString("type","vk")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "instagram" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_instagram))
                holder.text.text = "Instagram"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView()
                    bundle.putString("type","instagram")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "facebook" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_facebook))
                holder.text.text = "Facebook"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView()
                    bundle.putString("type","facebook")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "linkedin" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_linked_in))
                holder.text.text = "Linked In"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView();
                    bundle.putString("type","linkedin")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
            "wechat" -> {
                holder.img.setImageDrawable(context.resources.getDrawable(R.drawable.ic_wechat))
                holder.text.text = "WeChat"
                holder.item.setOnClickListener {
                    fr!!.onDestroyView()
                    bundle.putString("type","wechat")
                    createContact.arguments = bundle
                    createContact.isCancelable = true
                    createContact.show(fm, "createContactDialog")
                }
            }
        }
    }
}