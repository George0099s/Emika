package com.emika.app.presentation.adapter.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.user.Contact;
import com.emika.app.presentation.adapter.profile.ItemTouchHelper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.List;

public class MembersContactAdapter extends RecyclerView.Adapter<MembersContactAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    List<Contact> contacts;
    private Context context;

    public MembersContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_contact, parent, false);
        return new MembersContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        switch (contact.getType()) {
            case "email":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mail));
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { contact.getValue() });
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
                    context.startActivity(Intent.createChooser(intent, "Send Email"));
                });
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

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_member_contact_img);
        }
    }
}
