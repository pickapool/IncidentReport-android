package com.example.incidentreport.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.incidentreport.Activities.Login;
import com.example.incidentreport.Fragments.Dashboard;
import com.example.incidentreport.Fragments.Incidents;
import com.example.incidentreport.Fragments.NewsAndUpdate;
import com.example.incidentreport.Fragments.Summary;
import com.example.incidentreport.Fragments.Users;
import com.example.incidentreport.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Drawer set up
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Admin.this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState ==null) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, new Dashboard()).commit();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.adminEmail);
        navEmail.setText(user.getEmail());

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,new Dashboard()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.listOfUsers:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,new Users()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.reportedIncidents:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,new Incidents()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.summary:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,new Summary()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.newsUpdate:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,new NewsAndUpdate()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.logOutAdmin:
                FirebaseAuth.getInstance().signOut();
                Intent ii = new Intent(Admin.this, Login.class);
                startActivity(ii);
                finish();
                break;
        }

        return true;
    }
}