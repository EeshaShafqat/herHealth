package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DocVerify extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

        DrawerLayout drawerLayout;
        NavigationView navigationView;

        Toolbar toolbar;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_verify);

        RecyclerView recyclerView = findViewById(R.id.recycle);

        List<item> items = new ArrayList<item>();
        items.add(new item("marium", "Islamabad", 28, R.drawable.pdf));
        items.add(new item("Umer", "Multan", 16, R.drawable.pdf));
        items.add(new item("Murtaza", "Karachi", 15, R.drawable.pdf));
        items.add(new item("Nida", "Quetta", 35, R.drawable.pdf));
        items.add(new item("Mahnoor", "Sargodha", 40, R.drawable.pdf));
        items.add(new item("Alishba", "Islamabad", 22, R.drawable.pdf));
        items.add(new item("Eesha", "Islamabad", 24, R.drawable.pdf));
        items.add(new item("Abdullah", "Islamabad", 23, R.drawable.pdf));


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));




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


}

    //drawer layout
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {
            Intent intent = new Intent(this, AdminSettings.class);
            startActivity(intent);
            // Handle settings
        } else if (itemId == R.id.admin_panel) {
            Intent intent = new Intent(this, AdminMenu.class);
            startActivity(intent);
        } else if (itemId == R.id.signout) {
            // Handle sign out
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}

