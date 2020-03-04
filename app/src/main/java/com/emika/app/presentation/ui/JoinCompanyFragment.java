package com.emika.app.presentation.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emika.app.R;
import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.presentation.adapter.InvitationAdapter;
import com.emika.app.presentation.utils.viewModelFactory.JoinCompanyViewModelFactory;
import com.emika.app.presentation.viewmodel.JoinCompanyViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinCompanyFragment extends Fragment {
    private RecyclerView invitationRecycler;
    private Button checkInvitation;
    private JoinCompanyViewModel viewModel;
    private TextView title, text, createCompany;
    private String token;
    private InvitationAdapter adapter;
    private FragmentManager fm;
    private TextView logout;

    public JoinCompanyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_existing_company, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        fm = getParentFragmentManager();
        token = getActivity().getIntent().getStringExtra("token");
        invitationRecycler = view.findViewById(R.id.invitation_recycler);
        createCompany = view.findViewById(R.id.create_company);
        createCompany.setOnClickListener(this::goToCreateCompany);
        invitationRecycler.setHasFixedSize(true);
        invitationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        checkInvitation = view.findViewById(R.id.check_invitation);
        checkInvitation.setOnClickListener(this::checkInvitations);
        title = view.findViewById(R.id.waiting_to_join);
        text = view.findViewById(R.id.invitation_text);
    }
    private void goToCreateCompany(View view){
        fm.beginTransaction().replace(R.id.auth_container, new CreateCompany()).commit();
    }
    private void checkInvitations(View view) {
        viewModel = new ViewModelProvider(this, new JoinCompanyViewModelFactory(token)).get(JoinCompanyViewModel.class);
        viewModel.getListInvitationMutableLiveData().observe(getViewLifecycleOwner(), observeInvitation);
    }

    private Observer<List<Invitation>> observeInvitation = invitations -> {
        if (invitations.size() != 0) {
            adapter = new InvitationAdapter(invitations, token, viewModel, this, getContext());
            invitationRecycler.setAdapter(adapter);
        } else
            Toast.makeText(getContext(), "You have no invitation", Toast.LENGTH_SHORT).show();
    };
}
