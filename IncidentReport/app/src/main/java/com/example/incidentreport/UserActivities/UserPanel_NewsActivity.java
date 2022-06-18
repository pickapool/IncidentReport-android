package com.example.incidentreport.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.incidentreport.Adapters.NewsAndUpdateAdapter;
import com.example.incidentreport.Adapters.UserNewsAdapter;
import com.example.incidentreport.Models.mNews;
import com.example.incidentreport.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserPanel_NewsActivity extends AppCompatActivity {

    ImageView newsBack;
    List<mNews> mNewsList;
    DatabaseReference database;
    RecyclerView recyclerView;
    UserNewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pael_news);

        database = FirebaseDatabase.getInstance().getReference();
        newsBack = findViewById(R.id.newsBack);
        recyclerView = findViewById(R.id.adapterNews);
        newsBack.setOnClickListener(v -> {
            Intent ii1 = new Intent(UserPanel_NewsActivity.this,UserPanel_Home.class);
            startActivity(ii1);
            finish();
        });
        getNews();
    }

    void getNews(){
        mNewsList = new ArrayList<>();
        database.child("NewsAndUpdate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNewsList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mNews newss = snapshot.getValue(mNews.class);
                    mNewsList.add(new mNews(newss.getUid(),newss.getTitle(), newss.getBody(),newss.getImage()));
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(UserPanel_NewsActivity.this));
                adapter = new UserNewsAdapter(UserPanel_NewsActivity.this,mNewsList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}