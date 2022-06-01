package com.example.incidentreport.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incidentreport.Activities.Login;
import com.example.incidentreport.Models.mUserAccount;
import com.example.incidentreport.R;
import com.example.incidentreport.UserRepositories.UserRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserPanel_Profile extends AppCompatActivity {

    ImageView buttonReport,buttonFile,buttonProfile,buttonHome;
    LinearLayout showManage;
    boolean isDialogOpen = false;
    TextView editProfileButton,logOut,fullname,email;
    UserRepository myData;

    EditText fname,emails,password,contact,adds;
    TextView cFname,cEmail;
    ImageView showEditEmail,showEditPassword;

    Button updateButton;
    FirebaseUser user;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpanel_profile_activity);

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //EditProfile
        fname = findViewById(R.id.editFullName);
        emails = findViewById(R.id.editEmailAddress);
        password = findViewById(R.id.editPassword);
        contact =findViewById(R.id.editContactNumber);
        adds = findViewById(R.id.editAddress);

        cFname = findViewById(R.id.userFName1);
        cEmail = findViewById(R.id.userEmail1);
        showEditEmail = findViewById(R.id.editEmailAddressButton);
        showEditPassword = findViewById(R.id.editPasswordButton);
        updateButton = findViewById(R.id.updateProfileButton);

        showEditEmail.setOnClickListener(v -> {
            showDialogEditEmail();
        });
        showEditPassword.setOnClickListener(v -> {
            showDialogEditPassword();
        });

        fname.setText(UserRepository._fullNames);
        emails.setText(UserRepository._myEmail);
        password.setText(UserRepository._password);
        contact.setText(UserRepository._contactNumber);
        adds.setText(UserRepository._Address);
        cFname.setText(UserRepository._fullNames);
        cEmail.setText(UserRepository._myEmail);

        //Dialaog
        fullname = findViewById(R.id.userFName);
        email = findViewById(R.id.userEmail);
        buttonReport = findViewById(R.id.buttonReport);
        buttonFile = findViewById(R.id.buttonFile);
        buttonProfile = findViewById(R.id.buttonProfile);
        showManage = findViewById(R.id.manageProfile);
        editProfileButton = findViewById(R.id.editProfileButton);
        logOut = findViewById(R.id.logOut);
        buttonHome = findViewById(R.id.buttonHome);

        myData = new UserRepository();
        fullname.setText(myData._fullNames);
        email.setText(myData._myEmail);


        updateButton.setOnClickListener(v -> {
            UpdateProfile();
        });

        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent ii = new Intent(this, Login.class);
            startActivity(ii);
            finish();
        });
        buttonReport.setOnClickListener(v -> {
            Intent ii = new Intent(this,UserPanel_Report.class);
            startActivity(ii);
            finish();
        });
        buttonFile.setOnClickListener(v -> {
            Intent ii = new Intent(this,UserPanel_File.class);
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
        buttonHome.setOnClickListener(v -> {
            Intent ii = new Intent(this,UserPanel_Home.class);
            startActivity(ii);
            finish();
        });

    }

    private void UpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fName = fname.getText().toString();
        String emai = emails.getText().toString();
        String passwo = password.getText().toString();
        String conta =contact.getText().toString();
        String add = adds.getText().toString();
        reference.child("UserAccounts").child(user.getUid())
                .setValue(new mUserAccount(user.getUid(),fName,add,conta,emai,passwo,UserRepository._filePath,UserRepository._isApprove))
                .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(UserPanel_Profile.this,"Profile has been update",Toast.LENGTH_LONG).show();
                fullname.setText(fName);
                cFname.setText(fName);

                UserRepository._fullNames = fName;
                UserRepository._myEmail = emai;
                UserRepository._password = passwo;
                UserRepository._contactNumber = conta;
                UserRepository._Address = add;
            }else{
                Toast.makeText(UserPanel_Profile.this,"There is a problem updating your profile, please try again.",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void changeEmail(String newEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(UserRepository._myEmail,UserRepository._password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        SendLinkVerification(user,newEmail);                                    }
                                });
                    }
                });
    }

    private void SendLinkVerification(FirebaseUser firebaseUser,String newEmail){
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserPanel_Profile.this,
                        "Please verify your new email and login again!",
                        Toast.LENGTH_SHORT).show();
                UpdateEmail(newEmail);
            } else {
                Toast.makeText(UserPanel_Profile.this,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateEmail(String newEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fName = fname.getText().toString();
        String passwo = password.getText().toString();
        String conta =contact.getText().toString();
        String add = adds.getText().toString();
        reference.child("UserAccounts").child(user.getUid())
                .setValue(new mUserAccount(user.getUid(),fName,add,conta,newEmail,passwo,UserRepository._filePath,UserRepository._isApprove))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Intent ii = new Intent(this,Login.class);
                        startActivity(ii);
                        finish();
                    }else{
                        Toast.makeText(UserPanel_Profile.this,"There is a problem updating your profile, please try again.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    void showDialogEditEmail(){
       /* LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.change_email_dialog,null);*/
        Dialog dialog = new Dialog(UserPanel_Profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.change_email_dialog);

        EditText emailField = dialog.findViewById(R.id.newEmailField);
        Button cancel = dialog.findViewById(R.id.cancelEditEmail);
        Button save = dialog.findViewById(R.id.saveEditEmail);

        final String[] newEmail = {""};
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newEmail[0] = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        save.setOnClickListener(v -> {
            changeEmail(newEmail[0]);
        });
        dialog.show();
    }

    void showDialogEditPassword(){
        Dialog dialog = new Dialog(UserPanel_Profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.change_password_dialog);

        EditText newPassword = dialog.findViewById(R.id.newPasswordField);
        Button cancel = dialog.findViewById(R.id.cancelEditPassword);
        Button save = dialog.findViewById(R.id.saveEditPassword);

        final String[] newPasswords = {""};
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPasswords[0] = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        save.setOnClickListener(v -> {
            changePassword(newPasswords[0]);
        });
        dialog.show();
    }

    private void changePassword(String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(UserRepository._myEmail,UserRepository._password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        UpdatePassword(newPassword);
                                    }
                                });
                    }
                });
    }

    private void UpdatePassword(String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fName = fname.getText().toString();
        String emaa = emails.getText().toString();
        String conta =contact.getText().toString();
        String add = adds.getText().toString();
        reference.child("UserAccounts").child(user.getUid())
                .setValue(new mUserAccount(user.getUid(),fName,add,conta,emaa,newPassword,UserRepository._filePath,UserRepository._isApprove))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Intent ii = new Intent(this,Login.class);
                        startActivity(ii);
                        finish();
                        Toast.makeText(UserPanel_Profile.this,"Password has been changed!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(UserPanel_Profile.this,"There is a problem updating your profile, please try again.",Toast.LENGTH_LONG).show();
                    }
                });
    }
}