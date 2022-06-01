package com.example.incidentreport.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incidentreport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.incidentreport.Models.mUserAccount;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {

    EditText email,password,confirmPassword,fullName,filePath,address;
    Button registerAccount,upload,cancel;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private int STORAGE_PERMISSION_CODE = 101;
    private Uri UriFilePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    StorageTask uploadTask;
    Uri imageUri;
    String myUri;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;
    boolean isShow1 = false;
    boolean isShow2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);
        registerAccount= findViewById(R.id.registerAccountButton);
        fullName = findViewById(R.id.fullName);
        filePath = findViewById(R.id.idPicture);
        upload = findViewById(R.id.uploadID);
        cancel = findViewById(R.id.cancelSignUp);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        password.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_UP){
                if(event.getRawX() >=(password.getRight()-password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                    if(!isShow1){
                        password.setTransformationMethod(null);
                        isShow1 = true;
                    }else{
                        password.setTransformationMethod(new PasswordTransformationMethod());
                        isShow1 = false;
                    }
                    return  true;
                }
            }
            return false;
        });
        confirmPassword.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                if(event.getRawX() >=(confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                    if(!isShow2){
                        confirmPassword.setTransformationMethod(null);
                        isShow2 = true;
                    }else{
                        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                        isShow2 = false;
                    }
                    return  true;
                }
            }
            return false;
        });
        registerAccount.setOnClickListener(v -> {
            String Email = email.getText().toString();
            String Pass = password.getText().toString();
            String CPass = confirmPassword.getText().toString();
            String fName = fullName.getText().toString();
            String fPath = filePath.getText().toString();

            if(PasswordMatched(Pass,CPass)){
                Register(fName,Email,Pass);
            }else{
                Toast.makeText(this,"Password did not match",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(SignUp.this, Login.class);
                startActivity(ii);
                finish();
            }
        });

        upload.setOnClickListener(v -> {
            SelectImage();
        });

        //CheckPermissionLocation();
    }

    private boolean PasswordMatched(String password,String confirmPassword){
        boolean match =false;
        if(password.equals(confirmPassword)){
            match = true;
        }
        return match;
    }
    private void Register(String fullName,String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            newUploadImage(user,fullName,email,password);

                        } else {
                            Toast.makeText(SignUp.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void SendLinkVerification(FirebaseUser firebaseUser,String fullName,String email,String pass,String filePath){
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AddToDatabase(firebaseUser.getUid(),fullName,email,pass,filePath);
            } else {
                Toast.makeText(SignUp.this,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddToDatabase(String uid,String fullName,String email,String password,String filepath){
        reference.child("UserAccounts").child(uid).setValue(new mUserAccount(uid,fullName,"","",email,password,filepath,false)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent ii = new Intent(SignUp.this,Login.class);
                    startActivity(ii);
                    FirebaseAuth.getInstance().signOut();
                    Toast
                            .makeText(SignUp.this,
                                    "Please verify you email address check your inbox and wait until the admin approve!",
                                    Toast.LENGTH_SHORT)
                            .show();

                }else{
                    Toast.makeText(SignUp.this,
                            task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            UriFilePath = data.getData();
            imageUri = data.getData();
            try {
                String name = data.getData().getPath();
            filePath.setText(name);
            }
            catch (Exception e) {
                Toast.makeText(SignUp.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    void newUploadImage(FirebaseUser user,String fullName,String email,String password) {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Please wait while we register your account...");
        progressDialog.show();
        if (filePath != null) {
            StorageReference ref = storageReference.child("ID").child(user.getUid() + ".jpg");
            uploadTask = ref.putFile(imageUri);
            uploadTask.continueWithTask(task ->
                    ref.getDownloadUrl())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri imgUri = (Uri) task.getResult();
                            myUri = imgUri.toString();
                            SendLinkVerification(user,fullName,email,password,myUri);
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}