package com.example.incidentreport.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentreport.Models.mUserAccount;
import com.example.incidentreport.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class UserAccountAdapter extends RecyclerView.Adapter<UserAccountAdapter.ViewHolder> {

    Context context;
    List<mUserAccount> userLists;
    String statusToChange;
    DatabaseReference reference;

    public UserAccountAdapter(Context context, List<mUserAccount> userLists) {
        this.context = context;
        this.userLists = userLists;
    }

    @NonNull
    @Override
    public UserAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_account_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAccountAdapter.ViewHolder holder, int position) {
        mUserAccount account = userLists.get(position);
        if(userLists.size()>0){
            holder.userName.setText(account.getFullName());
            holder.address.setText(account.getAddress());
            holder.contactNumber.setText(account.getContactNumber());
            holder.email.setText(account.getEmail());
            Picasso.get()
                    .load(account.getIdFilePath())
                    .into(holder.userIdImage);

            boolean status = account.isApprove();
            if(status){
                holder.userStatus.setText("Approved");
                holder.userStatus.setBackgroundResource(R.drawable.status_responded);
            }else{
                holder.userStatus.setText("Denied");
                holder.userStatus.setBackgroundResource(R.drawable.status_failed);
            }
            holder.userStatus.setOnClickListener(v -> {
                showDialogEditStatus(holder.dialog, account.getUid());
            });
        }
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName,address,contactNumber,email;
        ImageView userIdImage;
        Button userStatus;
        Dialog dialog;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userFullName);
            address = itemView.findViewById(R.id.userAddress);
            contactNumber = itemView.findViewById(R.id.userContactNumber);
            email = itemView.findViewById(R.id.userEmail);
            userIdImage = itemView.findViewById(R.id.userID);
            userStatus = itemView.findViewById(R.id.statusAccountButton);
            dialog = new Dialog(context);
            reference = FirebaseDatabase.getInstance().getReference();
        }
    }

    void showDialogEditStatus(Dialog dialog, String uid) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_user_status);
        dialog.setCancelable(false);
        Spinner statusText = dialog.findViewById(R.id.userStatus);
        Button cancel = dialog.findViewById(R.id.cancelStatus);
        Button save = dialog.findViewById(R.id.saveStatus);
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        save.setOnClickListener(ttv -> {
            statusToChange = statusText.getSelectedItem().toString();
            if(statusToChange.equals("Approve")){
                updateStatus("isApprove", true, uid, dialog);
            }else{
                updateStatus("isApprove", false, uid, dialog);
            }
        });
        dialog.show();
    }
    void updateStatus(String columnName, boolean value, String userUid, Dialog dialog) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(columnName, value);
        reference.child("UserAccounts").child(userUid).updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(value){
                    Toast.makeText(context, "Account has been Approved!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Account has been Denied!", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }
}
