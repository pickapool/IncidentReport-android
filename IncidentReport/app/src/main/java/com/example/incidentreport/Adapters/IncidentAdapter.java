package com.example.incidentreport.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {

    Context context;
    List<mReport> incidents;
    String statusToChange;
    DatabaseReference reference;
    StorageReference storage;
    String URLS;
    public IncidentAdapter(Context context, List<mReport> incidents) {
        this.context = context;
        this.incidents = incidents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_incidents_reported_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mReport reports = incidents.get(position);
        if (incidents.size() > 0) {
            holder.dates.setText(reports.getDate());
            holder.incident.setText(reports.getIncident());
            holder.address.setText(reports.getAddress());
            holder.contactPerson.setText(reports.getContactPerson());
            holder.contactNumber.setText(reports.getContactNumber());
            holder.respondedBy.setText(reports.getRespondedBy());
            Picasso.get()
                    .load(reports.getReportPath())
                    .into(holder.proof);

            String status = reports.getStatus();
            if (status.equals("Pending")) {
                holder.status.setText(reports.getStatus());
                holder.status.setBackgroundResource(R.drawable.status_pending);
            } else if (status.equals("Forwarded")) {
                holder.status.setText(reports.getStatus());
                holder.status.setBackgroundResource(R.drawable.status_failed);
            } else {
                holder.status.setText(reports.getStatus());
                holder.status.setBackgroundResource(R.drawable.status_responded);
            }

            holder.status.setOnClickListener(v -> {
                showDialogEditStatus(holder.dialog, reports.getUid());
            });

        }
    }

    String downloadURL(StorageReference reference,String imageKey,String document){

        reference.child("Incidents").child(imageKey).child(document).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    URLS =  uri.toString();
                    Toast.makeText(context,uri.toString(),Toast.LENGTH_LONG).show();
                });
        return  URLS;
    }
    @Override
    public int getItemCount() {
        return incidents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dates, incident, address, contactPerson, contactNumber,respondedBy;
        ImageView proof;
        Button status;
        Dialog dialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dates = itemView.findViewById(R.id.dateIncident);
            incident = itemView.findViewById(R.id.incident);
            address = itemView.findViewById(R.id.addressIncident);
            contactPerson = itemView.findViewById(R.id.contactPersonIncident);
            contactNumber = itemView.findViewById(R.id.contactNumberIncident);
            proof = itemView.findViewById(R.id.proof);
            status = itemView.findViewById(R.id.statusUpdateButton);
            dialog = new Dialog(context);
            reference = FirebaseDatabase.getInstance().getReference();
            storage = FirebaseStorage.getInstance().getReference();
            respondedBy = itemView.findViewById(R.id.respondedBy);
        }
    }

    void showDialogEditStatus(Dialog dialog, String uid) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_status_dialog);
        dialog.setCancelable(false);
        Spinner statusText = dialog.findViewById(R.id.status);
        Button cancel = dialog.findViewById(R.id.cancelStatus);
        Button save = dialog.findViewById(R.id.saveStatus);
        Spinner responder = dialog.findViewById(R.id.respondedBy);
        EditText others = dialog.findViewById(R.id.othersBy);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        statusText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String get = parent.getSelectedItem().toString();
                if(get.equals("Responded")){
                    //show
                    responder.setVisibility(View.VISIBLE);
                }else{
                    //hide
                    responder.setVisibility(View.GONE);
                    others.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        responder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String get = parent.getSelectedItem().toString();
                if(get.equals("Others")){
                    //show
                    others.setVisibility(View.VISIBLE);
                }else{
                    //hide
                    others.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        save.setOnClickListener(ttv -> {
            statusToChange = statusText.getSelectedItem().toString();
            if(statusText.getSelectedItem().toString().equals("Responded")) {
                if (responder.getSelectedItem().toString().equals("Others")) {
                    updateStatus("status", statusToChange, uid, dialog, others.getText().toString());
                } else {
                    updateStatus("status", statusToChange, uid, dialog, responder.getSelectedItem().toString());
                }
            }else{
                updateStatus("status", statusToChange, uid, dialog, "");
            }

        });
        dialog.show();
    }
    void updateStatus(String columnName, String value, String incidentUID, Dialog dialog,String responder) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(columnName, value);
        hashMap.put("respondedBy", responder);
        reference.child("incidents").child(incidentUID).updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Status has been changed.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}
