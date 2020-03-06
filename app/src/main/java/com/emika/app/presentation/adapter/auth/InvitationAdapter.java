package com.emika.app.presentation.adapter.auth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.emika.app.R;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.viewmodel.auth.JoinCompanyViewModel;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>{
    private static final String TAG = "InvitationAdapter";
    private List<Invitation> invitations;
    private String token;
    private JoinCompanyViewModel viewModel;
    private LifecycleOwner owner;
    private Context context;

    public InvitationAdapter(List<Invitation> invitations, String token, JoinCompanyViewModel viewModel, LifecycleOwner owner, Context context) {
        this.invitations = invitations;
        this.token = token;
        this.viewModel = viewModel;
        this.owner = owner;
        this.context = context;
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, parent, false);
        return new InvitationAdapter.InvitationViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        Invitation invitation = invitations.get(position);
        holder.title.setText(invitation.getCompany().getName());
        holder.creator.setText(invitation.getCreator().getFirstName());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setInviteId(invitation.getId());
                viewModel.getAcceptMutableLiveData().observe(owner, observeAccepted);
            }
        });
    }


    @Override
    public int getItemCount() {
        return invitations.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder {
        private TextView title, creator;
        private Button accept;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            accept = itemView.findViewById(R.id.invite_accept);
            title = itemView.findViewById(R.id.invite_title);
            creator = itemView.findViewById(R.id.invite_creator);
        }
    }

    private Observer<ModelAuth> observeAccepted = accepted -> {
        if (accepted!= null)
        if (accepted.getOk()){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, accepted.getError(), Toast.LENGTH_SHORT).show();
        }
    };

}
