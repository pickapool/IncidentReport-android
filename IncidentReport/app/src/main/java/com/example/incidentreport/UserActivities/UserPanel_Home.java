package com.example.incidentreport.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incidentreport.Activities.Login;
import com.example.incidentreport.Models.mNews;
import com.example.incidentreport.R;
import com.example.incidentreport.UserRepositories.UserRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserPanel_Home extends AppCompatActivity implements OnMapReadyCallback {

    ImageView buttonReport,buttonFile,buttonProfile;
    LinearLayout showManage;
    boolean isDialogOpen = false;
    TextView editProfileButton,logOut,fullname,email,newsTitle,newsBody;
    DatabaseReference reference;
    GoogleMap googleMaps;
    ArrayList<LatLng> markers = new ArrayList<>();
    ArrayList<LatLng> proneList = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 15 * 1000;
    ProgressBar progressBar;
    String imageURL ="";

    String[] Barangays = {"BagongBayan","Lindero","Lupaan","Magyapo","Necesito","Maria","Mauno","MayBunga","Liberato","Lugta","Liya"};
    String[] proneTypes = {"Landslide, Flooding",
            "Landslide, Flooding, Scouring, Erosion",
            "Landslide, Flooding, Scouring, Erosion",
            "Landslide,Flooding",
            "Landslide, Flooding, Scouring, Erosion",
            "Flooding, Scouring, Erosion",
            "Landslide, Flooding, Scouring, Erosion, Storm Surges",
            "Landslide, Flooding, Scouring, Erosion, Gullies and Creeks",
            "Landslide, Flooding, Scouring, Erosion, Critical Area",
            "Landslide, Flooding, Scouring, Erosion",
            "Landslide, Flooding, Scouring, Erosion"};
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpanel_home_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.incidentsMarker);
        mapFragment.getMapAsync(this);

        fullname = findViewById(R.id.userFName);
        email = findViewById(R.id.userEmail);
        newsTitle = findViewById(R.id.newsTitle);
        newsBody = findViewById(R.id.newsBody);
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        buttonReport = findViewById(R.id.buttonReport);
        buttonFile = findViewById(R.id.buttonFile);
        buttonProfile = findViewById(R.id.buttonProfile);
        showManage = findViewById(R.id.manageProfile);
        editProfileButton = findViewById(R.id.editProfileButton);
        logOut = findViewById(R.id.logOut);

        proneList.add(new LatLng(11.094298,122.046207));
        proneList.add(new LatLng(11.111467,122.048337));
        proneList.add(new LatLng(11.136308,122.073319));
        proneList.add(new LatLng(11.169103,122.059487));
        proneList.add(new LatLng(11.125585,122.071432));
        proneList.add(new LatLng(11.097907,122.088018));
        proneList.add(new LatLng(11.167401,122.038371));
        proneList.add(new LatLng(11.113780,122.137979));
        proneList.add(new LatLng(11.109037,122.071889));
        proneList.add(new LatLng(11.082552,122.051667));
        proneList.add(new LatLng(11.122010,122.052212));

        reference = FirebaseDatabase.getInstance().getReference();

        editProfileButton.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Home.this, UserPanel_Profile.class);
            startActivity(ii);
            finish();
        });
        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent ii = new Intent(UserPanel_Home.this, Login.class);
            startActivity(ii);
            finish();
        });
        buttonReport.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Home.this,UserPanel_Report.class);
            startActivity(ii);
            finish();
        });
        buttonFile.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Home.this,UserPanel_File.class);
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

        newsTitle.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= newsTitle.getRight() - newsTitle.getTotalPaddingRight()) {
                    Intent ii = new Intent(UserPanel_Home.this, UserPanel_NewsActivity.class);
                    ii.putExtra("title",newsTitle.getText().toString());
                    ii.putExtra("body",newsBody.getText().toString());
                    ii.putExtra("imageURL",imageURL);
                    startActivity(ii);
                    return true;
                }
            }
            return true;
        });
        Account();
        LoadDataMap();
        getLatestNews();
    }


    public void Account() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            reference.child("UserAccounts").orderByChild("uid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                            String fullNames = snapShot.child("fullName").getValue(String.class);
                            String myEmail = snapShot.child("email").getValue(String.class);
                            String password = snapShot.child("password").getValue(String.class);
                            String uid = snapShot.child("uid").getValue(String.class);
                            String filePath = snapShot.child("idFilePath").getValue(String.class);
                            Boolean isApprove = snapShot.child("isApprove").getValue(Boolean.class);
                            String adds = snapShot.child("address").getValue(String.class);
                            String contact = snapShot.child("contactNumber").getValue(String.class);

                            UserRepository._fullNames = fullNames;
                            UserRepository._myEmail = myEmail;
                            UserRepository._password = password;
                            UserRepository._uid = uid;
                            UserRepository._filePath = filePath;
                            UserRepository._isApprove = isApprove;
                            UserRepository._Address = adds;
                            UserRepository._contactNumber = contact;

                            fullname.setText(fullNames);
                            email.setText(myEmail);
                        }
                    } catch (Exception ee) {
                        Toast.makeText(UserPanel_Home.this, "Account" +ee.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void LoadDataMap() {
        markers.clear();
        title.clear();
        address.clear();
        try {

            reference.child("incidents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String latitude = (snapshot.child("latitude").getValue(String.class));
                        String longitude = (snapshot.child("longitude").getValue(String.class));
                        String incident = snapshot.child("incident").getValue(String.class);
                        String place = snapshot.child("address").getValue(String.class);

                        Float fLat = Float.parseFloat(latitude);
                        Float fLong = Float.parseFloat(longitude);

                        LatLng marks = new LatLng(fLat,fLong);
                        markers.add(marks);
                        title.add(incident);
                        address.add(place);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception ee){
            Toast.makeText(UserPanel_Home.this,"Load Map -"+ee.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMaps = googleMap;
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = () -> {
            //do Something
            LatLng move = new LatLng(11.156106,122.054483);

            googleMaps.clear();
            googleMaps.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            if(googleMaps !=null)
                {
                    progressBar.setVisibility(View.GONE);
                    for(int j=0;j<proneList.size();j++)
                    {
                        googleMaps.addMarker(new MarkerOptions()
                                .position(proneList.get(j))
                                .title(Barangays[j]+"-"+proneTypes[j])
                                .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE )));
                    }
                    for (int i = 0; i < markers.size();i++)
                    {
                        googleMaps.addMarker(new MarkerOptions().position(markers.get(i)).title(title.get(i)+" - "+address.get(i)));
                        googleMaps.moveCamera(CameraUpdateFactory.newLatLng(move));
                        googleMaps.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                    }
                }
            handler.postDelayed(runnable,delay);
            handler.removeCallbacks(runnable);
        }, delay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    void getLatestNews(){
        reference.child("NewsAndUpdate").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mNews news = snapshot.getValue(mNews.class);
                    newsTitle.setText(news.getTitle());
                    newsBody.setText(news.getBody());
                    imageURL = news.getImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}