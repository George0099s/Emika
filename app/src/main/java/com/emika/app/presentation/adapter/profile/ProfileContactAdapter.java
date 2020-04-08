package com.emika.app.presentation.adapter.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.user.Contact;

import java.util.List;

public class ProfileContactAdapter extends RecyclerView.Adapter<ProfileContactAdapter.ViewHolder> {

    List<Contact> contacts;
    private Context context;

    public ProfileContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_contact, parent, false);
        return new ProfileContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contact.setText(contact.getValue());
        switch (contact.getType()) {
            case "email":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mail));
                break;
            case "telegram":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_telegram));
                break;
            case "whatsapp":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_whats_app));
                break;
            case "vk":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vk));
                break;
            case "instagram":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_instagram));
                break;
            case "facebook":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_facebook));
                break;
            case "linkedin":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_linked_in));
                break;
            case "wechat":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wechat));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView contact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_contact_img);
            contact = itemView.findViewById(R.id.item_contact_text);
        }
    }
}
