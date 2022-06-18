package com.example.incidentreport.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Dashboard extends Fragment implements OnChartValueSelectedListener {

    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList barList;

    ArrayList pieList;
    PieData peiData;
    PieDataSet pieDataSet;
    PieChart pieChart;
    TextView chartTitle;

    DatabaseReference reference;
    int nAccident,nFire,nFlood,nLandSlide,nStorm,nOthers;
    int ladavu,mdrrmc,bfp,others,pending;
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<String> labelsPie = new ArrayList<>();

    TextView acc,flod,fire,land,storm,oth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        acc = view.findViewById(R.id.accidentNumber);
        flod = view.findViewById(R.id.floodNumber);
        fire = view.findViewById(R.id.fireNumber);
        land = view.findViewById(R.id.landslideNumber);
        storm = view.findViewById(R.id.stormSurge);
        oth = view.findViewById(R.id.otherNumber);
        chartTitle = view.findViewById(R.id.chartTitle);

        barChart = view.findViewById(R.id.barchart);
        pieChart = view.findViewById(R.id.pieChart);
        reference = FirebaseDatabase.getInstance().getReference();
        labels.add("Vehicular-Accident");
        labels.add("Flood");
        labels.add("Landslide");
        labels.add("Storm-Surge");
        labels.add("Fire");
        labels.add("Others");
        labelsPie.add("LaDAVU");
        labelsPie.add("MDRRMC");
        labelsPie.add("BFP");
        labelsPie.add("Others");
        getNumberOfAccidents();
        getNumberOfResponder();
        return view;

    }

    private void getNumberOfAccidents(){
        barList = new ArrayList();
        reference.child("incidents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barList.clear();
                nAccident = 0;nFlood = 0; nLandSlide = 0;nStorm  = 0;nOthers = 0;nFire= 0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    if(reports.getIncident().equals("Vehicular Accident")){
                        nAccident++;
                    }else if(reports.getIncident().equals("Fire")){
                       nFire++;
                    }else if(reports.getIncident().equals("Landslide")){
                        nLandSlide++;
                    }else if(reports.getIncident().equals("Storm Surge")){
                        nStorm++;
                    }else if(reports.getIncident().equals("Flood")){
                        nFlood++;
                    }else{
                        nOthers++;
                    }
                }
                barList.add(new BarEntry(0f,nAccident));
                barList.add(new BarEntry(1f,nFlood));
                barList.add(new BarEntry(2f,nLandSlide));
                barList.add(new BarEntry(3f,nStorm));
                barList.add(new BarEntry(4f,nFire));
                barList.add(new BarEntry(5f,nOthers));


                barDataSet = new BarDataSet(barList,"Number of Incidents");
                barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.setFitBars(true);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChart.animateXY(1000,2000);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                acc.setText("Vehicular Accidents: "+nAccident);
                flod.setText("Floods: "+nFlood);
                fire.setText("Fire Incidents: "+nFire);
                land.setText("Landslides: "+nLandSlide);
                storm.setText("Storm Surges: "+nStorm);
                oth.setText("Others: "+nOthers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getNumberOfResponder(){
        pieList = new ArrayList();
        reference.child("incidents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pieList.clear();
                ladavu=0;mdrrmc=0;bfp=0;pending=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mReport reports = snapshot.getValue(mReport.class);
                    if(reports.getRespondedBy().equals("LaDAVU")){
                        ladavu++;
                    }else if(reports.getRespondedBy().equals("MDRRMC")){
                        mdrrmc++;
                    }else if(reports.getRespondedBy().equals("BFP")){
                        bfp++;
                    }else if (reports.getRespondedBy().equals("")){
                        pending++;
                    }else{
                        others++;
                    }
                }
                float total = ladavu+mdrrmc+bfp+others+pending;
                float val1 = (float)((ladavu/total)*100);
                float val2 = (float)((mdrrmc/total)*100);
                float val3 = (float)((bfp/total)*100);
                float val4 = (float)((others/total)*100);
                float val5 = (float)((pending/total)*100);
                pieList.add(new PieEntry(val1,"LaDAVU"));
                pieList.add(new PieEntry(val2,"MDRRMC"));
                pieList.add(new PieEntry(val3,"BFP"));
                pieList.add(new PieEntry(val4,"OTHERS"));
                pieList.add(new PieEntry(val5,"Pending"));
                pieDataSet = new PieDataSet(pieList,"Number of Responders");
                peiData = new PieData(pieDataSet);
                pieChart.setData(peiData);
                pieChart.animateXY(1000,2000);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieChart.setDrawSliceText(false);
                pieChart.getData().setDrawValues(false);
                pieChart.setOnChartValueSelectedListener(Dashboard.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        DecimalFormat df = new DecimalFormat("0.00");
        PieEntry pe = (PieEntry) e;
        chartTitle.setText(pe.getLabel()+"-"+df.format(e.getY())+"%");
    }

    @Override
    public void onNothingSelected() {

    }
}