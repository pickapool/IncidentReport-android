package com.example.incidentreport.UserActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incidentreport.Activities.Login;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.example.incidentreport.UserRepositories.UserRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserPanel_Report extends AppCompatActivity {

    EditText latitude, longitude,address,contact,number,filePath;
    Spinner _incident;
    Button saveReport,upload;
    FirebaseUser user;
    FusedLocationProviderClient fusedLocationProviderClient;
    Uri UriFilePath;
    int PICK_IMAGE_REQUEST = 103;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference;
    ImageView buttonHome,buttonFile,buttonProfile;
    LinearLayout showManage;
    boolean isDialogOpen = false;
    TextView editProfileButton,logOut,fullname,email;
    UserRepository myData;


    StorageTask uploadTask;
    Uri imageUri;
    String myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upserpanel_report_activity);

        fullname = findViewById(R.id.userFName);
        email = findViewById(R.id.userEmail);
        buttonHome = findViewById(R.id.buttonHome);
        buttonFile = findViewById(R.id.buttonFile);
        buttonProfile = findViewById(R.id.buttonProfile);
        showManage = findViewById(R.id.manageProfile);
        editProfileButton = findViewById(R.id.editProfileButton);
        logOut = findViewById(R.id.logOut);

        myData = new UserRepository();

        fullname.setText(myData._fullNames);
        email.setText(myData._myEmail);

        editProfileButton.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Report.this, UserPanel_Profile.class);
            startActivity(ii);
            finish();
        });
        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent ii = new Intent(UserPanel_Report.this, Login.class);
            startActivity(ii);
            finish();
        });

        buttonHome.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Report.this,UserPanel_Home.class);
            startActivity(ii);
            finish();
        });
        buttonFile.setOnClickListener(v -> {
            Intent ii = new Intent(UserPanel_Report.this,UserPanel_File.class);
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




        _incident = findViewById(R.id.incidents);
        latitude = findViewById(R.id.latitudeField);
        longitude = findViewById(R.id.longitudeField);
        address = findViewById(R.id.currenAddress);
        contact = findViewById(R.id.contactPerson);
        number = findViewById(R.id.contactNumber);
        saveReport = findViewById(R.id.saveReport);
        filePath = findViewById(R.id.incidentPicture);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference();

        upload = findViewById(R.id.uploadPictureIncident);

        upload.setOnClickListener(v -> {
        SelectImage();
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        saveReport.setOnClickListener(v -> {
            String key = reference.push().getKey();
            newUploadImage(key);
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        GetLocation();
    }

    void Save(String key,String storagePath){

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String Date  = dateFormat.format(new Date());

        String userUID = user.getUid();
        String incident = _incident.getSelectedItem().toString();
        String lat = latitude.getText().toString();
        String longi = longitude.getText().toString();
        String adds = address.getText().toString();
        String contP = contact.getText().toString();
        String contN = number.getText().toString();
        reference.child("incidents").child(key).setValue(new mReport(Date,key,user.getUid()
                ,incident,lat,longi,adds,contP,contN
                ,storagePath,"Pending","")).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UserPanel_Report.this,"Incident has been reported!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void GetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(UserPanel_Report.this, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            latitude.setText(String.valueOf(addressList.get(0).getLatitude()));
                            longitude.setText(String.valueOf(addressList.get(0).getLongitude()));
                            address.setText(String.valueOf(addressList.get(0).getAddressLine(0)));
                        } catch (IOException e) {
                            Toast.makeText(UserPanel_Report.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(UserPanel_Report.this,"Please toggle your Location",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            UriFilePath = data.getData();
            imageUri = data.getData();
            try {
                String name = data.getData().getPath();
                filePath.setText(name);
            }
            catch (Exception e) {
                // Log the exception
                Toast.makeText(UserPanel_Report.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }



    void newUploadImage(String uid) {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Sending Report...");
        progressDialog.show();

        if (filePath != null) {
            StorageReference ref = storageReference.child("Incidents").child(uid + ".jpg");
            uploadTask = ref.putFile(imageUri);
            uploadTask.continueWithTask(task ->
                    ref.getDownloadUrl())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri imgUri = (Uri) task.getResult();
                            myUri = imgUri.toString();
                            Save(uid, myUri);
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}