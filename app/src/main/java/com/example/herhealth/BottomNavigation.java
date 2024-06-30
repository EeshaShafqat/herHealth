package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BottomNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        ImageLoader.getInstance(this).loadProfileImage();

        //**************************drawer layout***********************************************

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



            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Set a custom icon and title when the drawer is open
                // getSupportActionBar().setIcon(R.drawable.ic_menu);
                getSupportActionBar().setTitle("herHealth");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Reset the icon and title when the drawer is closed
                // getSupportActionBar().setIcon(R.drawable.ic_menu);
                getSupportActionBar().setTitle("herHealth");
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






        //***************************************************************************************


        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                int itemId = item.getItemId();

                if (itemId == R.id.add) {
                    Intent i = new Intent(BottomNavigation.this, NewForums.class);
                    startActivity(i);

                } else {

                    if (itemId == R.id.profile) {

                        selected = new ForumFragment();

                    } else if (itemId == R.id.search) {

                        selected = new SearchFragment();
                    } else if (itemId == R.id.home) {

                        selected = new HomeFragment();
                    }


                    if (selected != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selected,"abc").commit();
                        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_color_selector);
                    }

                }

                    return true;
                }

        });

        Intent intent = getIntent();

        if (intent.hasExtra("FRAGMENT_TO_LOAD")) {
            String fragmentToLoad = intent.getStringExtra("FRAGMENT_TO_LOAD");

            Fragment selected = null;

            if ("add".equals(fragmentToLoad)) {
                Intent i = new Intent(this, NewForums.class);
                startActivity(i);

            }

            else {
                // Check the value of fragmentToLoad and load the corresponding fragment
                if ("home".equals(fragmentToLoad)) {
                    // Load the HomeFragment
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    selected = new HomeFragment();


                } else if ("profile".equals(fragmentToLoad)) {
                    // Load another fragment
                    selected = new ForumFragment();

                } else if ("search".equals(fragmentToLoad)) {
                    // Load another fragment
                    selected = new SearchFragment();
                }


                if (selected != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selected,"abc").commit();

                }
            }
        }
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




    private Drawable resizeIcon(int drawableId, int sizePx) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, sizePx, sizePx);
        }
        return drawable;
    }
}
