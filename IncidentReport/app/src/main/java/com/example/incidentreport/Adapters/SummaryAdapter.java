package com.example.incidentreport.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    Context context;
    List<mReport> reportList;

    public SummaryAdapter(Context context, List<mReport> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.summary_report_layout, parent, false);
        return new SummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mReport report = reportList.get(position);
        if(reportList.size()>0){
            holder.date.setText(report.getDate());
            holder.incident.setText(report.getIncident());
            holder.address.setText(report.getAddress());
            holder.cPerson.setText(report.getContactPerson());
            holder.cNumber.setText(report.getContactNumber());
            holder.status.setText(report.getStatus());
            holder.respondedBy.setText(report.getRespondedBy());
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,incident,address,cPerson,cNumber,status,respondedBy;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.summaryDate);
            incident = itemView.findViewById(R.id.summaryIncident);
            address = itemView.findViewById(R.id.summaryAddress);
            cPerson = itemView.findViewById(R.id.summaryContactPerson);
            cNumber = itemView.findViewById(R.id.summaryContactNumber);
            status = itemView.findViewById(R.id.summaryStatus);
            respondedBy = itemView.findViewById(R.id.respondedBy);
        }
    }
}
