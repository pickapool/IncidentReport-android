package com.example.incidentreport.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incidentreport.AdminActivities.Admin;
import com.example.incidentreport.Models.mUserAccount;
import com.example.incidentreport.R;
import com.example.incidentreport.UserActivities.UserPanel_Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView buttonSignUp;
    EditText email,password;
    Button signIn;
    private FirebaseAuth mAuth;
    private int STORAGE_PERMISSION_CODE = 1;
    String[] PERMISSIONS;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    boolean isApprove;
    CheckBox showPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        buttonSignUp = findViewById(R.id.signUpButton);
        email = findViewById(R.id.emailSignIn);
        password = findViewById(R.id.passwordSignIn);
        signIn = findViewById(R.id.SignIn);

        mAuth = FirebaseAuth.getInstance();
        showPassword = findViewById(R.id.loginShowPassword);

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                password.setTransformationMethod(null);
            }else{
                password.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        signIn.setOnClickListener(v -> {
            String _email = email.getText().toString();
            String _password = password.getText().toString();
            SignIn(_email,_password);
        });

        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this,SignUp.class);
            startActivity(intent);
        });

    }
    private void SignIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        GoToPanel();
                    }
                }).addOnFailureListener(e ->{
                Toast.makeText(Login.this,"There was a problem while signing in!",Toast.LENGTH_LONG).show();
        });
    }

    private boolean isVerified(FirebaseUser user){
        boolean  verify = false;
        if(user!=null){
            verify = user.isEmailVerified();
        }
        return  verify;
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckPermissionLocation();
    }

    private void CheckPermissionLocation(){
        if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    GoToPanel();
            }
        }else{
            requestLocationPermission();
        }
    }
    private void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Location permission is needed to use this app")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Login.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION},STORAGE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    System.exit(0);
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION},STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(!email.getText().toString().equals("")){
                    GoToPanel();
                }
            } else {
                CheckPermissionLocation();
            }
        }
    }
    private void GoToPanel() {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Logging in...");
        progressDialog.show();
            if (mAuth.getCurrentUser().getEmail().equals("evs66149@gmail.com")) {
                if (isVerified(FirebaseAuth.getInstance().getCurrentUser())) {
                    Intent ii = new Intent(Login.this, Admin.class);
                    startActivity(ii);
                    finish();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Your email is not verified, please check your inbox.", Toast.LENGTH_LONG).show();
                }
            } else if(email.getText().toString().equals("") || !mAuth.getCurrentUser().equals(null)){
                if (isVerified(FirebaseAuth.getInstance().getCurrentUser())) {
                    approve(FirebaseAuth.getInstance().getCurrentUser().getUid(),progressDialog);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Your email is not verified, please check your inbox.", Toast.LENGTH_LONG).show();
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
    }
    void approve(String uid,ProgressDialog dialog){
            reference.child("UserAccounts").orderByChild("uid").equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                mUserAccount account = snapshot.getValue(mUserAccount.class);
                                String approve =  String.valueOf(account.isApprove());
                                if(approve.equals("true")){
                                    dialog.dismiss();
                                    Intent ii = new Intent(Login.this, UserPanel_Home.class);
                                    startActivity(ii);
                                    finish();
                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(Login.this, "Your email is still not approve by the admin.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
    }
}