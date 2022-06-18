package com.example.incidentreport.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
    int delay = 10 * 1000;
    ProgressBar progressBar;
    String imageURL ="";

    String[] Barangays = {"BagongBayan","Lindero","Lupaan","Magyapo","Necesito","Maria","Mauno","MayBunga","Liberato","Lugta","Liya",
    "Virginia","Ban Ban","Cabariwan","Cadajug","Capnayan","Canituan","Bongbongan","Casit-an","Bario Laua-an","Igtadiao","Guisijan",
    "Guinbanga-an","Intao","Jaguicquican","Jinalinan","Lactudan","Latazon","Leon","Oloc","Omlot","Paningayan","Pascual",
    "Poblacion Lau-an","San Ramon","Santiago","Tibacan","Tigunhao"};
    int[] household = {192,449,161,62,91,386,146,164,135,233,120,56,48,159,308,88,154,65,307,0,93,607,486,186,227,61,64,0,192,326,
    70,112,40,549,37,156,42,93};
    String[] evacuations = {
            "Evacuation Area(TIGUNHAO) - KALAHI CIDSS DAY CARE CENTER,BRGY. HALL, TIGUNHAO PRIMARY SCHOOL",
            "Evacuation Area(LATAZON) -  LATAZON PRIMARY SCHOOL,KALAHI CIDSS DAY CARE CENTER",
            "Evacuation Area(GUIAMON) - BRGY. HALL, GUIAMON PRIMARY SCHOOL",
            "Evacuation Area(SAN RAMON) - SAN RAMON PRIMARY SCHOOL, DAY CARE CENTER",
            "Evacuation Area(LINDERO CADAJUG) -  LINDERO-CADAJUG ELEM.SCHOOL",
            "Evacuation Area(BAGONGBAYAN) - BRGY. COVERED GYM, BRGY. HALL, DAY CARE CENTER,GUISIJAN ELEM. SCHOOL",
            "Evacuation Area(GUISIJAN) -  CRANS CLASSROOM, GUISIJAN ELEM. SCHOOL, DAY CARE CENTER, BAGSAKAN CENTER, BRGY.HALL",
            "Evacuation Area(LUGTA) - BRGY. HEALTH CENTER, DAY CARE CENTER, LUGTA ELEM. SCHOOL, LUGTA EVACUATION CENTER",
            "Evacuation Area(GUINBANGA-AN) - GUINBANGA-AN ELEM. SCHOOL, BRGY. HALL, DAY CARE CENTER, BRGY HEALTH CENTER, GMJ MULTI-PURPOSE HALL, GMJ WAREHOUSE",
            "Evacuation Area(JAGUIQUICAN) -  JAGUIQUICAN ELEM. SCHOOL, BRGY. HALL",
            "Evacuation Area(MARIA) - MARIA ELEM. SCHOOL, DAY CARE CENTER, BRGY.HALL, ELNHS",
            "Evacuation Area(CAPNAYAN) - CAPNAYAN PRIMARY SCHOOL, BRGY. HALL, DAR CARE CENTER",
            "Evacuation Area(LEON) - LEON PRIMARY SCHOOL, BRGY. HALL, DAY CARE CENTER",
            "Evacuation Area(PANINGAYAN) - PANINGAYAN PRIMARY SCHOOL, BRGY, HALL",
            "Evacuation Area(SANTIAGO) - SANTIAGI ELEM. SCHOOL, BRY. BIRTHING CLINIC, BRGY. HALL, DAY CARE CENTER",
            "Evacuation Area(TIBACAN) - TIBACAN PRIMARY SCHOOL",
            "Evacuation Area(PANDANAN) - PANDANAN PRIMARY SCHOOL",
            "Evacuation Area(VIRGINIA) - VIRGINIA PRIMARY SCHOOL, BRGY. HALL, DAY CARE CENTER",
            "Evacuation Area(MAYBUNGA) - BRGY. HALL, MAYBUNGA PRIMARY SCHOOL"};
    ArrayList<LatLng> evacuationLocation = new ArrayList<>();
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
            "Landslide, Flooding, Scouring, Erosion",
            "Landslide, Flooding, Scouring, Erosion",
            "Landslide and Flooding",
            "Landslide, Flooding, Scouring, Erosion",
            "LANDSLIDE, SCOURING, EROSION",
            "FLOODING, SCOURING, EROSION",
            "FLOODING OCCURS ALONG CREEKS AND GULLIES",
            "LANDSLIDE, FLOODING, SCOURING, EROSION",
            "FLOODING, SCOURING, EROSION",
            "FLOODING,SCOURING,EROSION, BANK PROTECTION STRUCTURE IS PRESENT IN THE AREA",
            "LANDSLIDE, FLOODING, SCOURING,EROSION",
            "LANDSLIDE, SCOURING, EROSION ALONG THE CREEK",
            "LANDSLIDES, HILL SLOPES WITH STEEP CUTS",
            "FLOODING, SCOURING, EROSION",
            "LANDSLIDE FLOODING OCCURS IN CREEKS AND GULLIES",
            "LANDSLIDE, FLOODING, SCOURING, EROSION) ALONG THE HILLS AND MOUNTAINS",
            "LANDSLIDE,FLOODING,SCOURING,EROSION",
            "LANDSLIDE, FLOODING, SCOURING, EROSION",
            "LANDSLIDE, FLOODING,SCOURING, EROSION",
            "(FLOODING) AT THE CREEK",
            "LANDSLIDE, FLOODING, SCOURING, EROSION",
            "(FLOODING, SCOURING, EROSION), AREAS ALONG THE CREEK",
            "LANDSLIDE, FLOODING, SCOURING, EROSION",
            "FLOODING,EROSION",
            "LANDSLIDE",
            "LANDSLIDE, FLOODING, SCOURING, EROSION",
            "(LANDSLIDE) FLOODING OCCURS ALONG GULLIES AND CREEKS",
            "LANDSLIDE, FLOODING, SCOURING, EROSION"};
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
        proneList.add(new LatLng(11.123613,122.122567));
        proneList.add(new LatLng(11.172822,122.039002));
        proneList.add(new LatLng(11.136453,122.041631));
        proneList.add(new LatLng(11.104174,122.049924));
        proneList.add(new LatLng(11.119203,122.061844));
        proneList.add(new LatLng(11.096824,122.099444));
        proneList.add(new LatLng(11.147956,122.070773));
        proneList.add(new LatLng(11.119203,122.061844));
        proneList.add(new LatLng(11.150199,122.060008));
        proneList.add(new LatLng(11.121996,122.057427));
        proneList.add(new LatLng(11.090697,122.045953));
        proneList.add(new LatLng(11.086683,122.074793));
        proneList.add(new LatLng(11.157036,122.039775));
        proneList.add(new LatLng(11.083276,122.078945));
        proneList.add(new LatLng(11.217619,122.050620));
        proneList.add(new LatLng(11.152231,122.082302));
        proneList.add(new LatLng(11.160803,122.093515));
        proneList.add(new LatLng(11.143438,122.041808));
        proneList.add(new LatLng(11.127265,122.040312));
        proneList.add(new LatLng(11.121758,122.052339));
        proneList.add(new LatLng(11.118498,122.093292));
        proneList.add(new LatLng(11.433381,122.105733));
        proneList.add(new LatLng(11.146935,122.042373));
        proneList.add(new LatLng(11.170957,122.120157));
        proneList.add(new LatLng(11.113033,122.098115));
        proneList.add(new LatLng(11.134446,122.100379));
        proneList.add(new LatLng(11.146379,122.081014));

        evacuationLocation.add(new LatLng(11.145719,122.081416));
        evacuationLocation.add(new LatLng(11.153620,122.091594));
        evacuationLocation.add(new LatLng(11.143170,122.041779));
        evacuationLocation.add(new LatLng(11.172308755904094,122.12072547980095));
        evacuationLocation.add(new LatLng(11.108395745341754,122.04279084041313));
        evacuationLocation.add(new LatLng(11.092076761522549,122.04732502573977));
        evacuationLocation.add(new LatLng(11.086782521501908,122.04564930853667));
        evacuationLocation.add(new LatLng(11.080122269077213,122.05386561039296));
        evacuationLocation.add(new LatLng(11.625168131862011,125.01597733923387));
        evacuationLocation.add(new LatLng(11.084130882104164,122.07857268125926));
        evacuationLocation.add(new LatLng(11.097883450101065,122.08817436377664));
        evacuationLocation.add(new LatLng(11.143319056281022,122.04169628341336));
        evacuationLocation.add(new LatLng(11.143518060915381,122.04166901039353));
        evacuationLocation.add(new LatLng(11.143213790629625, 122.04167482574009));
        evacuationLocation.add(new LatLng(11.143224317196484,122.04171774108666));
        evacuationLocation.add(new LatLng(11.133364719156175,122.10024479690341));
        evacuationLocation.add(new LatLng(11.132080331559914,122.1204396969034));
        evacuationLocation.add(new LatLng(11.123488537775712,122.04585545457658));
        evacuationLocation.add(new LatLng(11.123488537775712,122.04593055643312));

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
        googleMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                final Dialog dialog = new Dialog(UserPanel_Home.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.layout_marker_details);

                TextView text = dialog.findViewById(R.id.markerDetail);
                for(int i=0;i<Barangays.length;i++)
                {
                    if(marker.getTitle().contains("Evacuation"))
                    {
                        text.setText(marker.getTitle());
                        break;
                    }else {
                        if(marker.getTitle().contains(Barangays[i])){
                            text.setText(marker.getTitle() +"\n"+"" +
                                    "HouseHold Numbers - "+household[i]);
                            break;
                        }
                    }
                }
                Button dialogBtn_cancel =dialog.findViewById(R.id.closeDetail);
                dialogBtn_cancel.setOnClickListener(v -> {
                    dialog.dismiss();
                });
                dialog.show();
                return false;
            }
        });
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
                                .title(Barangays[j]+"-"+proneTypes[j].toUpperCase())
                                .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE )));
                    }
                    for(int i=0;i<evacuations.length;i++)
                    {
                        googleMaps.addMarker(new MarkerOptions()
                                .position(evacuationLocation.get(i))
                                .title(evacuations[i])
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
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