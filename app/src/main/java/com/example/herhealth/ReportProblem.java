package com.example.herhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ReportProblem extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    Button report;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_problem);


        //drawer

        // hooks
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        navigationView.setItemIconTintList(null);




        //toolbar
        setSupportActionBar(toolbar);

//navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // Adjust the translationX property of the specific views you want to slide
                View existingLayout = findViewById(R.id.existing_layout);
                Toolbar toolbar = findViewById(R.id.toolbar);

                // Calculate the new translationX value based on the drawer's slideOffset
                float translationX = drawerView.getWidth() * slideOffset;

                // Apply translationX to the specific views
                existingLayout.setTranslationX(translationX);
                // toolbar.setTranslationX(translationX);


            }




        };




        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Adjust layout parameters of the Toolbar

        ViewGroup.MarginLayoutParams toolbarParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        toolbarParams.setMarginStart(0);  // Set the desired start margin (adjust as needed)
        toolbar.setLayoutParams(toolbarParams);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.settings);



        //*******************************************************

        report = findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblem.this, Settings.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else if (itemId == R.id.forums) {
            Intent intent = new Intent(this, BottomNavigation.class);
            intent.putExtra("FRAGMENT_TO_LOAD","home");
            startActivity(intent);
        } else if (itemId == R.id.chatbot) {
            Intent intent = new Intent(this, AIChat.class);
            startActivity(intent);
        } else if (itemId == R.id.signout) {
            MyUtility.signOut(this);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}


