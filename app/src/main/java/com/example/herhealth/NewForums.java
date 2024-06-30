package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewForums extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    //create forum

    Button create_forum;

    RelativeLayout imageRL;

    EditText desc;
    EditText forum;

    //upload image

    ImageView cancel;
    ImageView image;
    ImageView uploadedImage;
    Uri selectedImageUri; // Variable to store the selected image URI
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forums);

        uploadedImage = findViewById(R.id.uploadedImage);

        //put an image
        image = findViewById(R.id.image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        cancel  = findViewById(R.id.cancelImage);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImageUri = null;

                // To change the background drawable
                uploadedImage.setBackgroundResource(R.drawable.womens_day1);
                uploadedImage.setImageResource(R.drawable.pink_square_border);
                cancel.setVisibility(View.INVISIBLE);

            }
        });



        forum = findViewById(R.id.create_forum);
        desc = findViewById(R.id.desc);

        //create forum
        create_forum = findViewById(R.id.create);
        create_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(NewForums.this, R.anim.scale);
                v.startAnimation(scaleAnimation);

                String forumName = forum.getText().toString();
                String description = desc.getText().toString();

                if (forumName.isEmpty()) {
                    forum.setError("Forum name is required");

                } else if (description.isEmpty()) {
                    desc.setError("Forum description is required");
                } else if (selectedImageUri == null) {
                    Toast.makeText(NewForums.this, "Image is required", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;

                    uploadImageToStorage(selectedImageUri, user.getUid().toString(), forumName,description);


                }

            }
        });

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


        //spinners

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                R.layout.spinner_dropdown
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();

                // Launch corresponding activity based on the selected item
                if ("New Post".equals(selectedItem)) {
                    Intent newPostIntent = new Intent(NewForums.this, NewPosts.class);
                    startActivity(newPostIntent);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


    }

    //drawer layout
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);

        } else if (itemId == R.id.forums) {
            Intent intent = new Intent(this, BottomNavigation.class);
            intent.putExtra("FRAGMENT_TO_LOAD", "home");
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


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selectedImageUri = null;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Store the selected image URI
            selectedImageUri = data.getData();

            // Display the selected image in the ImageView
            Picasso.get().load(selectedImageUri).into(uploadedImage);

            uploadedImage.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);

        }
    }


    public static void createForum(Context context,String userId, String forumName,String image, String description) {



        ForumsClass forum = new ForumsClass();
        forum.setName(forumName);
        forum.setCreated_by(userId);
        forum.setImage(image);
        forum.setDescription(description);


        //storing the forum locally
        ForumsManager.getInstance().addForum(forum);


        //adding to firebase

        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
        forumsRef.keepSynced(true);

        String forumId = forumsRef.push().getKey();
        forumsRef.child(forumId).setValue(forum);

        Toast.makeText(context,"Forum created successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, BottomNavigation.class);
        intent.putExtra("FRAGMENT_TO_LOAD","home");
        context.startActivity(intent);
    }


    private void uploadImageToStorage(Uri imageUri,String userId, String forumName, String description) {
        // Check if there is an active internet connection
        if (isInternetConnected()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a unique filename for the image
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "forum_images/" + timeStamp + ".jpg";
            StorageReference imageRef = storageRef.child(filename);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to Firebase Realtime Database or update the user's profile
                            runOnUiThread(() -> {


                               createForum(NewForums.this,userId, forumName, uri.toString(),description);




                            });
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful image upload
                        runOnUiThread(() -> {
                            Toast.makeText(NewForums.this, "Forum Creation Failed", Toast.LENGTH_SHORT).show();
                        });

                    });
        } else {
            // No internet connection, show a message to the user
            Toast.makeText(NewForums.this, "No internet connection. Image upload failed", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
