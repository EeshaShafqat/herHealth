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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewPosts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    ImageView image;
    ImageView videoButton;
    ImageView cancel;
    ImageView uploadedImage;
    TextureVideoView cropTextureView;
    Uri selectedImageUri; // Variable to store the selected image URI
    Uri selectedVideoUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int VIDEO_PICK_REQUEST = 2;

    RelativeLayout imageRL;

    private static final String PENDING_IMAGE_URI_KEY = "pendingImageUri";

    Button post;
    EditText addTitle;
    EditText addDescription;

    DatabaseReference postsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_posts);

        fetchForumData();


        image = findViewById(R.id.gallery);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        // Initialize the video button
        videoButton = findViewById(R.id.video);

        // Set onClickListener for the video button
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open video gallery when the video button is clicked
                openVideoGallery();
            }
        });

        cancel = findViewById(R.id.cancelImage);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImageUri = null;

                uploadedImage.setBackgroundResource(R.drawable.womens_day1);
                uploadedImage.setImageResource(R.drawable.pink_square_border);

            }
        });


        //the imageview to add the selected image on
        uploadedImage = findViewById(R.id.uploadedImage);
        cropTextureView = findViewById(R.id.cropTextureView);


        // Set onClickListener for the video view
        cropTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the playback state
                if (cropTextureView.isPlaying()) {
                    // If video is playing, pause it
                    cropTextureView.pause();
                } else {
                    // If video is not playing, resume it
                    cropTextureView.play();
                }
            }
        });



        post = findViewById(R.id.post);
        addTitle = findViewById(R.id.addTitle);
        addDescription = findViewById(R.id.description);

        // Get the selected forum name from the spinner
        Spinner spinner2 = findViewById(R.id.spinner2);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(NewPosts.this, R.anim.scale);
                v.startAnimation(scaleAnimation);

                String title = addTitle.getText().toString();
                String description = addDescription.getText().toString();


                String selectedForum = spinner2.getSelectedItem().toString();

                // Create a Post object

                if (selectedForum==null || selectedForum.isEmpty() ||selectedForum.equals("Add to a Forum")) {
                    Toast.makeText(NewPosts.this,"select a forum", Toast.LENGTH_LONG).show();
                }
                else if (title.isEmpty()) {
                    addTitle.setError("Title is missing");
                } else if (description.isEmpty()) {
                    addTitle.setError("Description is missing");

                } else if (selectedImageUri != null) {

                    Posts post = new Posts();

                    post.setTitle(title);
                    post.setContent(description);
                    post.setImage(selectedImageUri.toString());
                    post.setTime();

                    savePostToForum(selectedForum, post);
                    Intent intent = new Intent(NewPosts.this, BottomNavigation.class);
                    intent.putExtra("FRAGMENT_TO_LOAD","profile");
                    startActivity(intent);

                }
                else{

                    Posts post = new Posts();

                    post.setTitle(title);
                    post.setContent(description);
                    post.setTime();

                    savePostToForum(selectedForum, post);

                    Intent intent = new Intent(NewPosts.this, BottomNavigation.class);
                    intent.putExtra("FRAGMENT_TO_LOAD","home");
                    startActivity(intent);
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






        //spinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.new_post,
                R.layout.spinner_dropdown
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner1 = findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter);



        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();

                // Launch corresponding activity based on the selected item
                if ("New Forum".equals(selectedItem)) {
                    Intent newPostIntent = new Intent(NewPosts.this, NewForums.class );
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


    private void fetchForumData() {
        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");

        // Assuming your ForumsClass has a 'name' field
        forumsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> forumNames = new ArrayList<>();
                for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                    ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                    if (forum != null) {
                        // Check if the "posts" node exists
                        if (forumSnapshot.child("posts").exists()) {
                            // "posts" node exists, retrieve posts
                            // You may want to update the code here to handle posts
                            // For now, let's assume there is a method getPosts() in ForumsClass
                            forumNames.add(forum.getName() + " (Posts: " + forum.getPosts().size() + ")");
                        } else {
                            // "posts" node doesn't exist, consider the forum without posts
                            forumNames.add(forum.getName() + " (No Posts)");
                        }
                    }
                }

                // Update the spinner2 with forum names
                updateSpinner2(forumNames);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPosts.this, "Error fetching forum data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateSpinner2(List<String> forumNames) {

        forumNames.add(0, "Add to a Forum");



        CustomArrayAdapter adapter2 = new CustomArrayAdapter(
                this,
                R.layout.spinner2_dropdown,
                forumNames
        );

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner2 = findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();

                // Do stuff based on what is selected



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Image pick request
            selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).into(uploadedImage);
            uploadedImage.setVisibility(View.VISIBLE);
            cropTextureView.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);

        } else if (requestCode == VIDEO_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Video pick request
            selectedVideoUri = data.getData();
            populateVideo(selectedVideoUri);

        }
    }



    private void savePostToForum(String forumName, Posts post) {
        String forumNamesWithoutCount = forumName.split("\\(")[0].trim();

        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("forums");
        forumRef.orderByChild("name").equalTo(forumNamesWithoutCount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                        String forumId = forumSnapshot.getKey();
                        postsRef = forumRef.child(forumId).child("posts");

                        // Create a unique post ID
                        String postId = postsRef.push().getKey();

                        // Set the user's UID within the post
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Fetch the user's name from the "users" node using the UID
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String authorName = dataSnapshot.child("name").getValue(String.class);

                                    // Set the author's name and UID to the post
                                    post.setUserId(userId);
                                    post.setAuthor(authorName); // Set the author's name

                                    // Check if a video is selected
                                    if (selectedVideoUri != null) {
                                        // Upload the video to Firebase Storage
                                        uploadVideoToStorage(selectedVideoUri, forumId, postId, post);
                                    } else if (selectedImageUri != null) {
                                        // Upload the image to Firebase Storage
                                        uploadImageToStorage(selectedImageUri, forumId, postId, post);
                                    } else {
                                        // No image or video selected, directly set the post in the Realtime Database
                                        postsRef.child(postId).setValue(post);

                                        // Increase the post count in the forum
                                        increasePostCount(forumId);
                                        incrementUserPostCount();

                                        Toast.makeText(NewPosts.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle the case where user data doesn't exist
                                    Toast.makeText(NewPosts.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(NewPosts.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(NewPosts.this, "Selected forum not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPosts.this, "Error finding forum", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadVideoToStorage(Uri videoUri, String forumId, String postId, Posts post) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the video
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "post_videos/" + timeStamp + ".mp4";
        StorageReference videoRef = storageRef.child(filename);

        // Upload the video to Firebase Storage
        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Video uploaded successfully, now get the download URL
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the download URL to Firebase Realtime Database
                        post.setVideo(uri.toString()); // Save video download URL
                        postsRef.child(postId).setValue(post);

                        // Increase the post count in the forum
                        increasePostCount(forumId);
                        incrementUserPostCount();

                        Toast.makeText(NewPosts.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful video upload
                    Toast.makeText(NewPosts.this, "Video upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageToStorage(Uri imageUri, String forumId, String postId, Posts post) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "post_images/" + timeStamp + ".jpg";
        StorageReference imageRef = storageRef.child(filename);

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the download URL to Firebase Realtime Database
                        post.setImage(uri.toString());
                        postsRef.child(postId).setValue(post);

                        // Increase the post count in the forum
                        increasePostCount(forumId);
                        incrementUserPostCount();

                        Toast.makeText(NewPosts.this, "Post created successfully!", Toast.LENGTH_SHORT).show();

                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful image upload
                    Toast.makeText(NewPosts.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }



    // Method to increase the post count in the forum
    private void increasePostCount(String forumId) {
        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("forums").child(forumId);
        forumRef.child("postCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentPostCount = (long) dataSnapshot.getValue();
                    forumRef.child("postCount").setValue(currentPostCount + 1);
                } else {
                    // Handle the case where post count doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPosts.this, "Error increasing post count", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void incrementUserPostCount() {
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("postCount");

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentPostCount = (long) dataSnapshot.getValue();
                    // Increment post count by 1
                    currentUserRef.setValue(currentPostCount + 1);
                } else {
                    // If post count doesn't exist, set it to 1
                    currentUserRef.setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPosts.this, "Error updating post count", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to open the video gallery
    private void openVideoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, VIDEO_PICK_REQUEST);
    }



    // Method to populate the selected video into the TextureVideoView
    private void populateVideo(Uri videoUri) {
        // Set the visibility of TextureVideoView to visible and ImageView to invisible

        cropTextureView.setVisibility(View.VISIBLE);
        uploadedImage.setVisibility(View.GONE);

        // Load the video into the TextureVideoView
        cropTextureView.setDataSource(this,videoUri);
        cropTextureView.play();

    }



}
