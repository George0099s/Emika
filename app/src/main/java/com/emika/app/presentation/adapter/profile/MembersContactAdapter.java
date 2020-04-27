package com.emika.app.presentation.adapter.profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.user.Contact;
import com.emika.app.presentation.adapter.profile.ItemTouchHelper.ItemTouchHelperAdapter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MembersContactAdapter extends RecyclerView.Adapter<MembersContactAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Contact> contacts;
    private Context context;
    private PackageManager packageManager;
    public MembersContactAdapter(List<Contact> contacts, Context context, PackageManager packageManager) {
        this.contacts = contacts;
        this.context = context;
        this.packageManager = packageManager;
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
                holder.img.setOnClickListener(v -> {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "http://telegram.me/"+ contact.getValue();
                        i.setData(Uri.parse(url));
                        PackageInfo info = packageManager.getPackageInfo("org.telegram.messenger", PackageManager.GET_META_DATA);
                        i.setPackage("org.telegram.messenger");
                        context.startActivity(Intent.createChooser(i, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "Telegram not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                break;
            case "whatsapp": {
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_whats_app));
                holder.img.setOnClickListener(v -> {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "https://api.whatsapp.com/send?phone="+ contact.getValue();
                        i.setData(Uri.parse(url));
                        PackageInfo info = packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        i.setPackage("com.whatsapp");
                        context.startActivity(Intent.createChooser(i, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
                break;
            case "vk":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vk));
                holder.img.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String url = "vkontakte://profile"+ contact.getValue();
                    i.setData(Uri.parse(url));
                    context.startActivity(Intent.createChooser(i, "Share with"));
                });
                break;
            case "instagram":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_instagram));
                holder.img.setOnClickListener(v -> {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "http://instagram.com/_u/"+ contact.getValue();
                        i.setData(Uri.parse(url));
                        PackageInfo info = packageManager.getPackageInfo("com.instagram.android", PackageManager.GET_META_DATA);
                        i.setPackage("com.instagram.android");
                        context.startActivity(Intent.createChooser(i, "Share with"));
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "Instagram not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                break;
            case "facebook":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_facebook));
                holder.img.setOnClickListener(v -> {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "https://www.facebook.com/"+ contact.getValue();
                        i.setData(Uri.parse(url));
                        PackageInfo info = packageManager.getPackageInfo("com.facebook.katana", PackageManager.GET_META_DATA);
                        i.setPackage("com.facebook.katana");
                        context.startActivity(Intent.createChooser(i, "Share with"));
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "Facebook not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                break;
            case "linkedin":
                holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_linked_in));
                holder.img.setOnClickListener(v -> {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "https://www.linkedin.com/in/"+ contact.getValue();
                        i.setData(Uri.parse(url));
                        PackageInfo info = packageManager.getPackageInfo("com.linkedin.android", PackageManager.GET_META_DATA);
                        i.setPackage("com.linkedin.android");
                        context.startActivity(Intent.createChooser(i, "Share with"));
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "LinkedIn not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
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
