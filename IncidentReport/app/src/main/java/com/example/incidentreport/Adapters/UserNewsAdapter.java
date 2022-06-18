package com.example.incidentreport.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentreport.Models.mNews;
import com.example.incidentreport.Models.mReport;
import com.example.incidentreport.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserNewsAdapter extends RecyclerView.Adapter<UserNewsAdapter.ViewHolder>{

    Context context;
    List<mNews> list;

    public UserNewsAdapter(Context context, List<mNews> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_news,parent,false);
        return new UserNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserNewsAdapter.ViewHolder holder, int position) {
        mNews reports = list.get(position);
        holder.title.setText(reports.getTitle());
        holder.body.setText(reports.getBody());
        Picasso.get()
                .load(reports.getImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,body;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            body = itemView.findViewById(R.id.newsBody);
            image = itemView.findViewById(R.id.newsImage);
        }
    }
}
