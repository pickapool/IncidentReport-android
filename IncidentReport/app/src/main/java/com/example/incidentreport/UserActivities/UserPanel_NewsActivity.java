package com.example.incidentreport.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.incidentreport.R;
import com.squareup.picasso.Picasso;

public class UserPanel_NewsActivity extends AppCompatActivity {

    String title,body,url;
    TextView tTitle, tBody;
    ImageView image,newsBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pael_news);
        Intent ii = getIntent();
        title = ii.getStringExtra("body");
        body = ii.getStringExtra("title");
        url = ii.getStringExtra("imageURL");

        tTitle = findViewById(R.id.newsTitle);
        tBody = findViewById(R.id.newsBody);
        image = findViewById(R.id.newsImage);
        newsBack = findViewById(R.id.newsBack);
        tTitle.setText(title);
        tBody.setText(body);
        Picasso.get()
                .load(url)
                .into(image);
        newsBack.setOnClickListener(v -> {
            Intent ii1 = new Intent(UserPanel_NewsActivity.this,UserPanel_Home.class);
            startActivity(ii1);
            finish();
        });
    }
}