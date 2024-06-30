package com.example.herhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmail extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    Button change;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_email);

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
//*************************************************************************************************





        //**********************************************************
        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Retrieve the current email and new email from EditText fields
                //String currentEmail = ((EditText) findViewById(R.id.age)).getText().toString().trim();
                String newEmail = ((EditText) findViewById(R.id.emailchange)).getText().toString().trim();


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updateEmail(newEmail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Email updated successfully
                                Toast.makeText(UpdateEmail.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateEmail.this, Settings.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Email update failed
                                Toast.makeText(UpdateEmail.this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });




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
