package com.example.incidentreport.Fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.incidentreport.Adapters.IncidentAdapter;
import com.example.incidentreport.Adapters.NewsAndUpdateAdapter;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.example.incidentreport.UserActivities.UserPanel_Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Incidents extends Fragment {

    Button responded,pending,failed,refresh;
    RecyclerView recyclerView;
    DatabaseReference reference;
    List<mReport> mReportList;
    IncidentAdapter adapter;
    int rep,pen,fail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidents, container, false);
        responded = view.findViewById(R.id.filterResponded);
        pending = view.findViewById(R.id.filterPending);
        failed = view.findViewById(R.id.filterFailed);
        refresh = view.findViewById(R.id.refreshIncidents);
        recyclerView = view.findViewById(R.id.incidentsReported);

        getIncidents("none");
        responded.setOnClickListener(v -> {
            getIncidents("Responded");
        });
        pending.setOnClickListener(v -> {
            getIncidents("Pending");
        });
        failed.setOnClickListener(v -> {
            getIncidents("Failed");
        });
        refresh.setOnClickListener(v -> {
            getIncidents("none");
        });
        return view;
    }

    void getIncidents(String filter){
        mReportList = new ArrayList<>();
        if(filter.equals("none")) {
            reference.child("incidents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mReportList.clear();
                    rep = 0;
                    fail = 0;
                    pen = 0;
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        mReport report = snapshot.getValue(mReport.class);
                        mReportList.add(new mReport(report.getDate(),report.getUid(),report.getUserUid(),
                                report.getIncident(),report.getLatitude(),report.getLongitude(),
                                report.getAddress(),report.getContactPerson(),report.getContactNumber(),
                                report.getReportPath(),report.getStatus(),report.getRespondedBy()));
                        if(report.getStatus().equals("Failed")){
                            fail++;
                        }else if(report.getStatus().equals("Pending")){
                            pen++;
                        }else{
                            rep++;
                        }
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapter = new IncidentAdapter(getActivity(),mReportList);
                    recyclerView.setAdapter(adapter);
                    responded.setText("RESPONDED - " + rep);
                    pending.setText("PENDING - " + pen);
                    failed.setText("FAILED - " + fail);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            reference.child("incidents").orderByChild("status").equalTo(filter).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mReportList.clear();
                    rep = 0;
                    fail = 0;
                    pen = 0;

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        mReport report = snapshot.getValue(mReport.class);
                        mReportList.add(new mReport(report.getDate(),report.getUid(),report.getUserUid(),
                                report.getIncident(),report.getLatitude(),report.getLongitude(),
                                report.getAddress(),report.getContactPerson(),report.getContactNumber(),
                                report.getReportPath(),report.getStatus(),report.getRespondedBy()));
                        if(report.getStatus().equals("Failed")){
                            fail++;
                        }else if(report.getStatus().equals("Pending")){
                            pen++;
                        }else{
                            rep++;
                        }
                    }
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter = new IncidentAdapter(getActivity(),mReportList);
                    recyclerView.setAdapter(adapter);
                    responded.setText("RESPONDED - " + rep);
                    pending.setText("PENDING - " + pen);
                    failed.setText("FAILED - " + fail);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}