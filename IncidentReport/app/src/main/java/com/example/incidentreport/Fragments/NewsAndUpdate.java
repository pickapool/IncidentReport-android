package com.example.incidentreport.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incidentreport.Adapters.NewsAndUpdateAdapter;
import com.example.incidentreport.Models.mNews;
import com.example.incidentreport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NewsAndUpdate extends Fragment {

    Button save;
    EditText title,body;
    DatabaseReference database;
    RecyclerView recyclerView;
    NewsAndUpdateAdapter adapter;
    List<mNews> mNewsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view= inflater.inflate(R.layout.fragment_news_and_update, container, false);
        save = view.findViewById(R.id.sendNews);
        title = view.findViewById(R.id.newsTitle);
        body = view.findViewById(R.id.newsBody);
        recyclerView = view.findViewById(R.id.newsItems);
        getNews();

        save.setOnClickListener(v -> {
            //Save
            SaveNews(title.getText().toString(),body.getText().toString());
        });
        return view;
    }

    void SaveNews(String titles,String bodys){
        String key = database.push().getKey();
        database.child("NewsAndUpdate").child(key).setValue(new mNews(key,titles,bodys,"")).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    title.setText("");
                    body.setText("");
                    Toast.makeText(getActivity(),"Content has been added to News and Update",Toast.LENGTH_LONG).show();
                }
            }
        });
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
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new NewsAndUpdateAdapter(getActivity(),mNewsList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}