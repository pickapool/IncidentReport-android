package com.example.incidentreport.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentreport.Models.mNews;
import com.example.incidentreport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NewsAndUpdateAdapter extends RecyclerView.Adapter<NewsAndUpdateAdapter.ViewHolder> {

    Context context;
    List<mNews> mNewsList;
    DatabaseReference reference;
    FirebaseUser user;
    public NewsAndUpdateAdapter(Context context, List<mNews> mNewsList) {
        this.context = context;
        this.mNewsList = mNewsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_layout,parent,false);
        return new NewsAndUpdateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mNews news = mNewsList.get(position);
        if(mNewsList.size() >0){
            holder.title.setText(news.getTitle());
            holder.body.setText(news.getBody());
        }
            holder.editTitle.setOnClickListener(v -> {
                saveNews(news.getUid(), holder.title.getText().toString(), holder.body.getText().toString());
            });
            holder.editBody.setOnClickListener(v -> {
                saveNews(news.getUid(), holder.title.getText().toString(), holder.body.getText().toString());
            });
            holder.deleteNews.setOnClickListener(v -> {
                deleteNews(news.getUid());
            });
    }
    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView delete;
        EditText title,body;
        ImageView editTitle,editBody,deleteNews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.deleteNews);
            title = itemView.findViewById(R.id.newsLayoutTitle);
            body = itemView.findViewById(R.id.newsLayoutBody);
            editTitle = itemView.findViewById(R.id.editTitle);
            editBody = itemView.findViewById(R.id.editBody);
            deleteNews = itemView.findViewById(R.id.deleteNews);
            reference = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
        }
    }

    void saveNews(String uid,String title,String body){
        reference.child("NewsAndUpdate").child(uid).setValue(new mNews(uid,title,body)).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(context,"Content has been updated.",Toast.LENGTH_LONG).show();
            }
        });
    }

    void deleteNews(String uid){
        reference.child("NewsAndUpdate").child(uid).removeValue().addOnCompleteListener(task -> {
            Toast.makeText(context,"Content has been deleted.",Toast.LENGTH_LONG).show();
        });
    }
}
