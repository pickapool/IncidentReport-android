package com.example.incidentreport.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.incidentreport.Adapters.IncidentAdapter;
import com.example.incidentreport.Adapters.UserAccountAdapter;
import com.example.incidentreport.Models.mUserAccount;
import com.example.incidentreport.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Users extends Fragment {

    boolean isOpenFilter= false;
    RecyclerView recyclerView;
    Button refresh,filter;
    DatabaseReference reference;
    List<mUserAccount> accountList;
    UserAccountAdapter userAccountAdapter;
    LinearLayout linearLayout;
    Spinner status;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.usersContainer);
        refresh = view.findViewById(R.id.refreshUsers);
        filter = view.findViewById(R.id.filterUser);
        linearLayout  = view.findViewById(R.id.filterContainer);
        status = view.findViewById(R.id.filterSpinner);

        getUsers("none");

        refresh.setOnClickListener(v -> {
            getUsers("none");
        });
        filter.setOnClickListener(v -> {
            if(isOpenFilter){
                linearLayout.setVisibility(View.GONE);
                isOpenFilter = false;
            }else{
                linearLayout.setVisibility(View.VISIBLE);
                isOpenFilter = true;
            }
        });
        linearLayout.setVisibility(View.GONE);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linearLayout.setVisibility(View.GONE);
                getUsers(status.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                linearLayout.setVisibility(View.GONE);
            }
        });

        return view;
    }

    void getUsers(String filter){
        accountList = new ArrayList<>();
        reference.child("UserAccounts")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                accountList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mUserAccount userAccount = snapshot.getValue(mUserAccount.class);
                    if(filter.equals("Approve")){
                        if(userAccount.isApprove()){
                            accountList.add(new mUserAccount(userAccount.getUid(), userAccount.getFullName(), userAccount.getAddress(),
                                    userAccount.getContactNumber(), userAccount.getEmail(), userAccount.getPassword(), userAccount.getIdFilePath(),
                                    userAccount.isApprove()));
                        }
                    }else if(filter.equals("Deny")){
                        if(!userAccount.isApprove()){
                            accountList.add(new mUserAccount(userAccount.getUid(), userAccount.getFullName(), userAccount.getAddress(),
                                    userAccount.getContactNumber(), userAccount.getEmail(), userAccount.getPassword(), userAccount.getIdFilePath(),
                                    userAccount.isApprove()));
                        }
                    }else{
                        accountList.add(new mUserAccount(userAccount.getUid(), userAccount.getFullName(), userAccount.getAddress(),
                                userAccount.getContactNumber(), userAccount.getEmail(), userAccount.getPassword(), userAccount.getIdFilePath(),
                                userAccount.isApprove()));
                    }

                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                userAccountAdapter = new UserAccountAdapter(getActivity(), accountList);
                recyclerView.setAdapter(userAccountAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
