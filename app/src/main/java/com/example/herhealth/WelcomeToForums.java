package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class WelcomeToForums extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button save;

    ImageView info;
    Animation jumpAnimation;
    Animation slideAnimation;
    CardView certificateTextView;

    TextView certificateTV;
    ImageView attachFile;

    Uri selectedFileUri;

    private TextView name;
    private TextView bio;

    private TextView location;
    private TextView age;

    ImageView image;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Uri selectedImageUri; // Variable to store the selected image URI
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String PENDING_IMAGE_URI_KEY = "pendingImageUri";
    private static final int FILE_PICKER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_forums);

        name = findViewById(R.id.name);
        bio = findViewById(R.id.bioV);
        age = findViewById(R.id.ageV);
        location = findViewById(R.id.locationV);


        image = findViewById(R.id.image);


        // Find the info icon ImageView
        info = findViewById(R.id.info);


        // Set an OnClickListener
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("info", "in onclick");


                // Add your onClick logic here
                // Create a notification with pending verification request message
                showPendingVerificationView();
            }
        });






        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Load user details from Firebase
            loadUserDetailsFromFirebase(user.getUid());
        }


        certificateTextView = findViewById(R.id.cardVerify);
        certificateTV = findViewById(R.id.certificate);

        attachFile = findViewById(R.id.attach);

        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Allow all file types
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);

            }
        });



        //Uploading dp


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

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





        save = findViewById(R.id.SaveBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(WelcomeToForums.this, R.anim.scale);
                v.startAnimation(scaleAnimation);

                // Check if a file is selected before saving
                if (selectedFileUri != null) {
                    // Upload the selected file to Firebase Storage
                    uploadFileToStorage(selectedFileUri);
                } else {
                    Log.d("Welcome to Forums", "No attached document");

                }



                // Check if an image is selected before uploading
                if (selectedImageUri != null) {
                    // Upload the selected image to Firebase Storage
                   // Toast.makeText(WelcomeToForums.this,"uploading...",Toast.LENGTH_SHORT).show();
                    uploadImageToStorage(selectedImageUri);

                } else {
                    Log.d("Welcome to Forums", "No Image Selected");


                }

                // Add other save-related logic here

                // Retrieve values from EditText fields
                String nameValue = name.getText().toString();

                String ageText = age.getText().toString();
                Integer ageValue = Integer.parseInt(ageText);
                String bioValue = bio.getText().toString();
                String locationValue = location.getText().toString();


                // Save values to SharedPreferences
                saveDataToSharedPreferences("name", nameValue);
                saveIntDataToSharedPreferences("age", ageValue);
                saveDataToSharedPreferences("bio", bioValue);
                saveDataToSharedPreferences("location", locationValue);

                saveDataToFirebaseDatabase(nameValue, ageValue, bioValue, locationValue);

                Intent i = new Intent(WelcomeToForums.this, BottomNavigation.class);
                i.putExtra("FRAGMENT_TO_LOAD","profile");
                startActivity(i);

            }
        });


    }

    private void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void saveIntDataToSharedPreferences(String key, Integer value) {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    private void saveDataToFirebaseDatabase(String name, Integer age, String bio, String location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
            referenceProfile.keepSynced(true);
            referenceProfile.child(user.getUid()).child("name").setValue(name);
            referenceProfile.child(user.getUid()).child("age").setValue(age);
            referenceProfile.child(user.getUid()).child("bio").setValue(bio);
            referenceProfile.child(user.getUid()).child("location").setValue(location)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(WelcomeToForums.this, "Data saved to Firebase", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(WelcomeToForums.this, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show();
                    });

            // Check if the user is already verified
            referenceProfile.child(user.getUid()).child("verified").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // User is not verified yet
                        int verifiedStatus = 0; // Default status when no file is attached
                        if (selectedFileUri != null) {
                            // File is attached
                            verifiedStatus = 1;
                        }
                        referenceProfile.child(user.getUid()).child("verified").setValue(verifiedStatus)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(WelcomeToForums.this, "Verified status saved to Firebase", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(WelcomeToForums.this, "Failed to save verified status to Firebase", Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        int verifiedStatus = dataSnapshot.getValue(Integer.class);
                        if (verifiedStatus == 0) {
                            // User is not verified yet

                            if (selectedFileUri != null) {
                                // File is attached
                                verifiedStatus = 1;
                            }
                            referenceProfile.child(user.getUid()).child("verified").setValue(verifiedStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(WelcomeToForums.this, "Verified status saved to Firebase", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(WelcomeToForums.this, "Failed to save verified status to Firebase", Toast.LENGTH_SHORT).show();
                                    });


                            Toast.makeText(WelcomeToForums.this, "User is not verified yet", Toast.LENGTH_SHORT).show();
                        } else if (verifiedStatus == 1) {


                            // User verification is pending


                            Toast.makeText(WelcomeToForums.this, "User verified is pending", Toast.LENGTH_SHORT).show();
                        } else if (verifiedStatus == 2) {

                            Toast.makeText(WelcomeToForums.this, "User is  verified", Toast.LENGTH_SHORT).show();
                        } else {

                            // User is verified


                            Toast.makeText(WelcomeToForums.this, "User is already verified", Toast.LENGTH_SHORT).show();;
                        }



                        // User is already verified

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WelcomeToForums.this, "Error checking verified status", Toast.LENGTH_SHORT).show();
                }
            });
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
            Picasso.get().load(selectedImageUri).into(image);
        } else if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get selected file URI

                selectedFileUri = data.getData();
                if (selectedFileUri != null) {

                    String fileNameWithExtension = getFileNameFromUri(selectedFileUri);

                    // Set file name to certificateTextView
                    certificateTV.setText(fileNameWithExtension);

                    //upload file to storage when save is clicked
                }
            }
        }
    }




    private void uploadImageToStorage(Uri imageUri) {
        // Check if there is an active internet connection
        if (isInternetConnected()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a unique filename for the image
            String filename = "profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";
            StorageReference imageRef = storageRef.child(filename);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to Firebase Realtime Database or update the user's profile
                            runOnUiThread(() -> {
                                Toast.makeText(WelcomeToForums.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                Picasso.get().load(uri).into(image);

                                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                String photoURL = uri.toString();
                                editor.putString("photoURL", photoURL);
                                editor.apply();

                                updateProfileWithImage(uri.toString());
                            });
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful image upload
                        runOnUiThread(() -> {
                            Toast.makeText(WelcomeToForums.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        });

                        // Save the image URI to SharedPreferences for later retry
                        savePendingImageUri(imageUri);
                    });
        } else {
            // No internet connection, show a message to the user
            Toast.makeText(WelcomeToForums.this, "No internet connection. Image upload failed", Toast.LENGTH_SHORT).show();

            // Save the image URI to SharedPreferences for later retry
            savePendingImageUri(imageUri);
        }
    }

    private void savePendingImageUri(Uri imageUri) {
        // Save the pending image URI to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PENDING_IMAGE_URI_KEY, imageUri.toString());
        editor.apply();
    }


    // Check if there is an active internet connection
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void updateProfileWithImage(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
            referenceProfile.keepSynced(true);
            referenceProfile.child(user.getUid()).child("photoURL").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(WelcomeToForums.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(WelcomeToForums.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }



    private void loadUserDetailsFromFirebase(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the "verified" field exists
                    if (dataSnapshot.hasChild("verified")) {
                        // Retrieve the verification status
                        int verificationStatus = dataSnapshot.child("verified").getValue(Integer.class);
                        // Check the verification status
                        if (verificationStatus == 0) {

                            //user is not verified, default layout

                        } else if (verificationStatus == 1) {
                            // User is not verified but has a pending verification request
                            //indicate pending status

                            ImageView info = findViewById(R.id.info);
                            info.setVisibility(View.VISIBLE);


                            jumpAnimation = AnimationUtils.loadAnimation(WelcomeToForums.this, R.anim.jump_icon);

// Apply the animation to the info icon
                            info.startAnimation(jumpAnimation);

                            jumpAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // Animation started
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    // Animation ended
                                    showPendingVerificationView();
                                    Log.d("animation", "animation ended");
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // Animation repeated
                                }
                            });




                            RelativeLayout certificate = findViewById(R.id.verify);

                            certificate.setVisibility(View.GONE);

                        } else if (verificationStatus == 2) {

                            ImageView verify = findViewById(R.id.verificationStatus);
                            verify.setVisibility(View.VISIBLE);

                            RelativeLayout certificate = findViewById(R.id.verify);
                            certificate.setVisibility(View.GONE);
                        }
                    } else {

                        //user is not verified, default layout
                    }

                    // Retrieve user details from dataSnapshot
                    String nameValue = dataSnapshot.child("name").getValue(String.class);
                    Integer ageValue = dataSnapshot.child("age").getValue(Integer.class);
                    String bioValue = dataSnapshot.child("bio").getValue(String.class);
                    String locationValue = dataSnapshot.child("location").getValue(String.class);
                    String photoUrl = dataSnapshot.child("photoURL").getValue(String.class);

                    // Update UI with retrieved user details
                    name.setText(nameValue);

                    if (ageValue != null && !ageValue.equals(0)) {
                        age.setText(String.valueOf(ageValue));
                    }

                    if (bioValue != null && !bioValue.isEmpty()) {
                        bio.setText(bioValue);
                    }

                    if (locationValue != null && !locationValue.isEmpty()) {
                        location.setText(locationValue);
                    }

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Picasso.get().load(photoUrl).into(image);

                    } else {
                        // Handle the case where photoUrl is null or empty
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WelcomeToForums.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });



    }



    private String getFileNameFromUri(Uri uri) {
        String fileNameWithExtension = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (displayNameIndex != -1) {
                fileNameWithExtension = cursor.getString(displayNameIndex);
            }
            cursor.close();
        }
        return fileNameWithExtension;
    }


    private void uploadFileToStorage(Uri fileUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference certificatesRef = storageRef.child("certificates").child(user.getUid()).child(fileUri.getLastPathSegment());
            certificatesRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File uploaded successfully
                        Toast.makeText(WelcomeToForums.this, "File uploaded", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded file
                        certificatesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the certificate URL to Firebase Realtime Database
                            saveCertificateUrlToFirebaseDatabase(user.getUid(), uri.toString());
                        }).addOnFailureListener(e -> {
                            // Handle failure to get download URL
                            Toast.makeText(WelcomeToForums.this, "Failed to get certificate URL", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failed upload
                        Toast.makeText(WelcomeToForums.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void saveCertificateUrlToFirebaseDatabase(String userId, String certificateUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("certificateUrl").setValue(certificateUrl)
                .addOnSuccessListener(aVoid -> {
                    // Certificate URL saved successfully
                    Toast.makeText(WelcomeToForums.this, "Certificate URL saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to save certificate URL
                    Toast.makeText(WelcomeToForums.this, "Failed to save certificate URL", Toast.LENGTH_SHORT).show();
                });
    }


    // Function to create the notification
    private void showPendingVerificationView() {
        // Find the pending verification view in your layout
        View pendingVerificationView = findViewById(R.id.notification);

        info.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);

        // Show the pending verification view with animation
        pendingVerificationView.setVisibility(View.VISIBLE);

        slideAnimation = AnimationUtils.loadAnimation(WelcomeToForums.this, R.anim.slide_in_bottom);
// Apply the animation to the info icon
        pendingVerificationView.startAnimation(slideAnimation);


        slideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended
                info.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);

                // Show the pending verification view with animation
                pendingVerificationView.setVisibility(View.GONE);
                Log.d("animation", "slide animation ended");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });



    }
}