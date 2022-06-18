package com.example.incidentreport.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.incidentreport.Activities.Login;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.example.incidentreport.UserRepositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPanel_File extends AppCompatActivity {

    ImageView buttonHome,buttonReport,buttonProfile;
    LinearLayout showManage;
    boolean isDialogOpen = false;
    TextView editProfileButton,logOut,fullname,email;
    UserRepository myData;

    DatabaseReference ref;

    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpanel_file_activity);

        table = findViewById(R.id.reportTable);
        table.setStretchAllColumns(true);
        ref = FirebaseDatabase.getInstance().getReference();


        fullname = findViewById(R.id.userFName);
        email = findViewById(R.id.userEmail);
        buttonHome = findViewById(R.id.buttonHome);
        buttonReport = findViewById(R.id.buttonReport);
        buttonProfile = findViewById(R.id.buttonProfile);
        showManage = findViewById(R.id.manageProfile);
        editProfileButton = findViewById(R.id.editProfileButton);
        logOut = findViewById(R.id.logOut);


        myData = new UserRepository();
        fullname.setText(myData._fullNames);
        email.setText(myData._myEmail);

        editProfileButton.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_File.this, UserPanel_Profile.class);
            startActivity(ii);
            finish();
        });
        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent ii = new Intent(UserPanel_File.this, Login.class);
            startActivity(ii);
            finish();
        });

        buttonHome.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_File.this,UserPanel_Home.class);
            startActivity(ii);
            finish();
        });
        buttonReport.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_File.this,UserPanel_Report.class);
            startActivity(ii);
            finish();
        });
        buttonProfile.setOnClickListener(v -> {
            if(isDialogOpen){
                isDialogOpen = false;
                showManage.setVisibility(View.GONE);
            }else{
                showManage.setVisibility(View.VISIBLE);

                isDialogOpen = true;
            }
        });

        getReports();
    }


    private void getReports(){
        ref.child("incidents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int position = 1;
                table.removeAllViews();
                AddRowHeader();
                for(DataSnapshot snap: dataSnapshot.getChildren()){

                    mReport incidents = snap.getValue(mReport.class);
                    if(incidents.getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        AddRow(incidents.getDate(), incidents.getIncident(), incidents.getAddress(),
                                incidents.getContactPerson(), incidents.getStatus(), position);
                        position++;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void AddRowHeader(){
        TableRow row = new TableRow(UserPanel_File.this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setPadding(5,5,5,5);
        row.setBackgroundResource(R.drawable.table_border);
        row.setLayoutParams(params);
        row.addView(textView("Date"));
        row.addView(textView("Incident"));
        row.addView(textView("Address"));
        row.addView(textView("Remarks"));
        row.addView(textView("Status"));
        table.addView(row,0);
    }
    TextView textView(String value){
        TextView textView = new TextView(UserPanel_File.this);
        textView.setText(value);
        textView.setLayoutParams(new TableRow.LayoutParams(40,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f));
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine(false);
        textView.setBackgroundResource(R.drawable.table_no_border);
        return textView;
    }

    private void AddRow(String date,String incident,String address,String contactPerson,String status,int position){
        TableRow row = new TableRow(UserPanel_File.this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setBackgroundResource(R.drawable.table_no_border);
        row.setLayoutParams(params);
        row.setPadding(8,8,8,8);
        row.addView(textView(date));
        row.addView(textView(incident));
        row.addView(textView(address));
        row.addView(textView(contactPerson));
        row.addView(textView(status));

        table.addView(row,position);
    }

}