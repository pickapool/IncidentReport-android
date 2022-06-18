package com.example.incidentreport.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.incidentreport.Adapters.SummaryAdapter;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.example.incidentreport.UserActivities.UserPanel_Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Summary extends Fragment {

    DatabaseReference reference;
    SummaryAdapter adapter;
    RecyclerView recyclerView;
    List<mReport> mReportList;
    Button refreshSummary;
    Button openFilter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_summary, container, false);
        recyclerView = view.findViewById(R.id.summaryData);
        refreshSummary = view.findViewById(R.id.refreshSummary);
        openFilter = view.findViewById(R.id.filterSummary);


        openFilter.setOnClickListener(v -> {
           ShowFilterDialog();
        });
        refreshSummary.setOnClickListener(v -> GetSummary());
        GetSummary();

        return  view;
    }

    void GetSummary(){
        mReportList = new ArrayList<>();
        reference.child("incidents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    mReportList.add(new mReport(reports.getDate(),reports.getIncident(),reports.getAddress(),
                            reports.getContactPerson(),reports.getContactNumber(),reports.getStatus(),reports.getRespondedBy()));
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void ShowFilterDialog(){

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.filter_summary);

        Button cancel = dialog.findViewById(R.id.cancelFilterSummary);
        Button save = dialog.findViewById(R.id.filterButtonSummary);
        CheckBox CDate = dialog.findViewById(R.id.dateCheckBox);
        CheckBox CIncident = dialog.findViewById(R.id.incidentCheckBox);
        CheckBox CStatus = dialog.findViewById(R.id.statusCheckBox);
        Spinner year = dialog.findViewById(R.id.yearFilter);
        Spinner month = dialog.findViewById(R.id.monthFilter);
        Spinner incident = dialog.findViewById(R.id.incidentFilter);
        Spinner status = dialog.findViewById(R.id.statusFilter);

        final String[] getYear = {""};
        final String[] getMonth = {""};
        final String[] getIncident = {""};
        final String[] getStatus = {""};
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getYear[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMonth[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        incident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getIncident[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getStatus[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        save.setOnClickListener(v -> {
            if(CDate.isChecked() && !CIncident.isChecked() && !CStatus.isChecked()){
                //Date Only
                FilterDate(getYear[0], getMonth[0]);

            }else if(CIncident.isChecked() && !CDate.isChecked() && !CStatus.isChecked()){
                //Incident Only
                FilterIncident(getIncident[0]);

            }else if(CStatus.isChecked() && !CIncident.isChecked() && !CDate.isChecked()){
                FilterStatus(getStatus[0]);

                //Status Only
            }else if(CDate.isChecked() && CIncident.isChecked() && !CStatus.isChecked()){
                FilterDateAndIncident(getYear[0], getMonth[0], getIncident[0]);

                //Date and Incident
            }else if(CDate.isChecked() && !CIncident.isChecked() && CStatus.isChecked()){
                FilterDateAndStatus(getYear[0], getMonth[0], getStatus[0]);

                //Date and Status
            }else if(!CDate.isChecked() && CIncident.isChecked() && CStatus.isChecked()){
                //Incident and Status
                FilterStatusAndIncident(getStatus[0], getIncident[0]);

            }else if(CDate.isChecked() && CIncident.isChecked() && CStatus.isChecked()){
                //Filter 3
                FilterThree(getYear[0], getMonth[0], getIncident[0], getStatus[0]);

            }else{
                dialog.dismiss();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    void FilterDate(String year, String month){
        mReportList = new ArrayList<>();
        reference.child("incidents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    String reportMonth = reports.getDate().substring(0,2);
                    String reportYear = reports.getDate().substring(6,10);
                    if(reportYear.equals(year) && reportMonth.equals(dateTextToNumber(month))) {
                        mReportList.add(new mReport(reports.getDate(), reports.getIncident(), reports.getAddress(),
                                reports.getContactPerson(), reports.getContactNumber(), reports.getStatus(), reports.getRespondedBy()));
                    }
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterDateAndIncident(String year,String month,String incident){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("incident").equalTo(incident).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    String reportMonth = reports.getDate().substring(0,2);
                    String reportYear = reports.getDate().substring(6,10);
                    if(reportYear.equals(year) && reportMonth.equals(dateTextToNumber(month))) {
                        mReportList.add(new mReport(reports.getDate(), reports.getIncident(), reports.getAddress(),
                                reports.getContactPerson(), reports.getContactNumber(), reports.getStatus(), reports.getRespondedBy()));
                    }
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterThree(String year,String month,String incident,String status){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    String reportMonth = reports.getDate().substring(0,2);
                    String reportYear = reports.getDate().substring(6,10);
              //      Toast.makeText(Summary.this,reportMonth+"-"+reportYear,Toast.LENGTH_LONG).show();
                    if(reportYear.equals(year) && reportMonth.equals(dateTextToNumber(month)) && reports.getIncident().equals(incident)) {
                        mReportList.add(new mReport(reports.getDate(), reports.getIncident(), reports.getAddress(),
                                reports.getContactPerson(), reports.getContactNumber(), reports.getStatus(),reports.getRespondedBy()));
                    }
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterDateAndStatus(String year,String month,String status){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    String reportMonth = reports.getDate().substring(0,2);
                    String reportYear = reports.getDate().substring(6,10);
                    if(reportYear.equals(year) && reportMonth.equals(dateTextToNumber(month))) {
                        mReportList.add(new mReport(reports.getDate(), reports.getIncident(), reports.getAddress(),
                                reports.getContactPerson(), reports.getContactNumber(), reports.getStatus(),reports.getRespondedBy()));
                    }
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterStatusAndIncident(String status,String incident){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    if (reports.getIncident().equals(incident)) {
                        mReportList.add(new mReport(reports.getDate(),reports.getIncident(),reports.getAddress(),
                                reports.getContactPerson(),reports.getContactNumber(),reports.getStatus(),reports.getRespondedBy()));
                    }
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterIncident(String incident){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("incident").equalTo(incident).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                        mReportList.add(new mReport(reports.getDate(), reports.getIncident(), reports.getAddress(),
                                reports.getContactPerson(), reports.getContactNumber(), reports.getStatus(),reports.getRespondedBy()));
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void FilterStatus(String status){
        mReportList = new ArrayList<>();
        reference.child("incidents").orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReportList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    mReportList.add(new mReport(reports.getDate(),reports.getIncident(),reports.getAddress(),
                            reports.getContactPerson(),reports.getContactNumber(),reports.getStatus(),reports.getRespondedBy()));
                }
                mReportList.add(new mReport("Date","Incidents","Address","Remarks","Contact Number","Status","Responded By"
                ));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new SummaryAdapter(getActivity(),mReportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    String dateTextToNumber(String date){
        String num = "";
        switch (date){
            case "January":
                num="01";
                break;
            case "February":
                num="02";
                break;
            case "March":
                num="03";
                break;
            case "April":
                num="04";
                break;
            case "May":
                num="05";
                break;
            case "June":
                num="06";
                break;
            case "July":
                num="07";
                break;
            case "August":
                num="08";
                break;
            case "September":
                num="09";
                break;
            case "October":
                num="10";
                break;
            case "November":
                num="11";
                break;
            case "December":
                num="12";
                break;
        }
        return num;
    }
}