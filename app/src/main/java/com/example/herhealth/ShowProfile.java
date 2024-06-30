
package com.example.herhealth;


        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.core.view.GravityCompat;
        import androidx.drawerlayout.widget.DrawerLayout;
        import androidx.fragment.app.FragmentTransaction;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.squareup.picasso.Callback;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;


public class ShowProfile extends AppCompatActivity {



    private Toolbar toolbar;
    ImageView image;

    private String cachedImageUrl;
    Button Follow;

    TextView ViewAll;
    TextView ViewAll2;
    private static final String TAG = "ForumFragment";
    TextView PostTextView;
    TextView forumTextView;

    String userId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);



        //get random post

        // Get the user ID passed via Intent
        userId = getIntent().getStringExtra("USER_ID");

        // Retrieve the current user's UID
       // String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the "forums" node in Firebase Realtime Database
        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");

// Attach a ValueEventListener to retrieve all forums
        forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot forumsSnapshot) {
                if (forumsSnapshot.exists()) {
                    List<Posts> mostRecentPosts = new ArrayList<>();
                    List<String> forumNames = new ArrayList<>();

                    final int[] userPostCount = {0};

                    final long totalForumCount = forumsSnapshot.getChildrenCount();
                    final long[] processedForumCount = {0};

                    for (DataSnapshot forumSnapshot : forumsSnapshot.getChildren()) {

                        String createdBy = forumSnapshot.child("created_by").getValue(String.class);

                        if (createdBy.equals(userId)) {
                            String forumName = forumSnapshot.child("name").getValue(String.class);
                            forumNames.add(forumName);
                        }


                        String forumId = forumSnapshot.getKey();
                        DatabaseReference forumPostsRef = forumsRef.child(forumId).child("posts");

                        forumPostsRef.orderByChild("userId").equalTo(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            List<Posts> userPosts = new ArrayList<>();

                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                Posts post = postSnapshot.getValue(Posts.class);
                                                // Add a condition to check if the post belongs to the current user
                                                if (post.getUserId().equals(userId)) {
                                                    userPosts.add(post);
                                                    userPostCount[0]++;
                                                }
                                            }
                                            mostRecentPosts.addAll(userPosts);
                                        }

                                        processedForumCount[0]++;

                                        if (processedForumCount[0] == totalForumCount) {
                                            // All forums have been processed
                                            // Get the most recent post across all forums
                                            getRandomPost(mostRecentPosts);

                                            TextView postCountTextView = findViewById(R.id.postCount);
                                            postCountTextView.setText(String.valueOf(userPostCount[0]));

                                            // Display a random forum's name
                                            getRandomForumName(forumNames);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle errors
                                    }
                                });

                    }
                } else {
                    Toast.makeText(ShowProfile.this, "No forums found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ShowProfile.this, "Error fetching forums: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //retrieve data from firebase for profile
        image = findViewById(R.id.image);

        fetchData();
//*********************************************************************

        Follow = findViewById(R.id.follow);

        if (userId.equals(getCurrentUserId())) {
            // If the userId matches the currently logged-in user's ID,
            // hide the follow button
            Follow.setVisibility(View.GONE);
        } else {
            // If the userId is different from the currently logged-in user's ID,
            // show the follow button
            Follow.setVisibility(View.VISIBLE);

        }

        //follow

        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text of the button
                String buttonText = Follow.getText().toString();
                
                if(buttonText.equals("Follow")){
                    Follow.setText("Following");
                    Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.following, 0);
                    followUser(userId);

                    
                } else if (buttonText.equals("Following")) {
                    Follow.setText("Follow");
                    Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.follow, 0);
                    unfollowUser(userId);
                    
                }

                // Now you can use the buttonText variable as needed
                Log.d("Button Text", "Button text: " + buttonText);
            }
        });


        VerificationStatus();

        //  loadProfileImage();

        //  Picasso.get().load(ImageLoader.getInstance(requireContext()).uri_dp).into(image);








        // Set the Toolbar as the support action bar
        if (toolbar != null) {
           setSupportActionBar(toolbar);
        }


        // Customize the Toolbar in this fragment
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // Show the back button (hamburger icon)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            // The menu icon (hamburger icon) is clicked
            openDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openDrawer() {
        // Open the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void fetchData(){

        // Assuming you have a reference to the Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Assuming you have TextViews for name and bio in your layout
        TextView nameTextView = findViewById(R.id.name);
        TextView bioTextView = findViewById(R.id.bio);



        // Retrieve user data from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("photoURL").getValue(String.class);

                    // Update UI with retrieved data
                    nameTextView.setText(name);
                    bioTextView.setText(bio);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Save the image URL to SharedPreferences
                        cachedImageUrl = profileImageUrl;
                        SharedPreferences preferences = getSharedPreferences("CachedImageData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("cachedImageUrl", cachedImageUrl);
                        editor.apply();

                        // Load the image using Picasso
                        Picasso.get().load(profileImageUrl).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ShowProfile.this, "Error fetching user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Check if the currently logged-in user follows the profile user
        DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getCurrentUserId())
                .child("following")
                .child(userId);

        currentUserFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The currently logged-in user follows the profile user
                    // Set the text of the Follow button accordingly
                    Follow.setText("Following");
                    Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.following, 0);

                } else {
                    // The currently logged-in user does not follow the profile user
                    // Set the text of the Follow button accordingly
                    Follow.setText("Follow");
                    Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.follow, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ShowProfile.this, "Error fetching following data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();

        loadCachedProfileImage();
    }



    private void getRandomPost(List<Posts> posts) {
        if (!posts.isEmpty()) {
            int randomIndex = new Random().nextInt(posts.size());
            Posts randomPost = posts.get(randomIndex);
            Log.d(TAG, "Random post: " + randomPost.getContent());


            displayPostDetails(randomPost, PostTextView); // Update yourPostTextView with randomPost content
        } else {
            Log.d(TAG, "Posts list is empty");
            // Handle empty posts list
        }
    }

    private void getRandomForumName(List<String> forums) {
        if (!forums.isEmpty()) {
            int randomIndex = new Random().nextInt(forums.size());
            String randomForum = forums.get(randomIndex);
            Log.d(TAG, "Random forum: " + randomForum);
            forumTextView.setText(randomForum); // Update yourForumTextView with randomForum name
        } else {
            Log.d(TAG, "Forums list is empty");
            // Handle empty forums list
        }
    }



    private void displayPostDetails(Posts post, TextView postTextView) {
        if (post != null) {
            String content = post.getContent();

            // Check if the content exceeds the maximum length
            if (content.length() > 80) {
                // If it does, truncate the text to 80 characters and add "..."
                content = content.substring(0, 80) + "...";
            }

            postTextView.setText(content);

            // Toast.makeText(requireContext(), "displaying post.", Toast.LENGTH_SHORT).show();

        } else {
            // Handle the case where no post is available
            //Toast.makeText(requireContext(), "No posts available.", Toast.LENGTH_SHORT).show();
        }
    }


    private void VerificationStatus() {

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



                        } else if (verificationStatus == 2) {

                            ImageView verify = findViewById(R.id.verificationStatus);
                            verify.setVisibility(View.VISIBLE);

                        }
                    } else {

                        //user is not verified, default layout
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //   Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });



    }


    // Method to load the cached image
    private void loadCachedProfileImage() {
        // Retrieve the saved image URL from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("CachedImageData", MODE_PRIVATE);
        String savedImageUrl = preferences.getString("cachedImageUrl", null);

        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
            // Display the cached image URL
            Picasso.get().load(savedImageUrl).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    // Image loaded successfully
                    Toast.makeText(ShowProfile.this, "Image loaded from cache", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                    Toast.makeText(ShowProfile.this, "Error loading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Handle the case where there is no saved image URL
            // You might want to set a default image or show an error message
            Toast.makeText(ShowProfile.this, "No saved image URL", Toast.LENGTH_LONG).show();
        }
    }



    // Function to add the user to the following list
    private void followUser(String followedUserId) {
        DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getCurrentUserId())
                .child("following")
                .child(followedUserId);

        // Add followedUserId to the current user's following list
        currentUserFollowingRef.setValue(true);

        DatabaseReference followedUserFollowersRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(followedUserId)
                .child("followers")
                .child(getCurrentUserId());

        // Add current user's ID to the followed user's followers list
        followedUserFollowersRef.setValue(true);
    }

    // Function to remove the user from the following list
    private void unfollowUser(String followedUserId) {
        DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getCurrentUserId())
                .child("following")
                .child(followedUserId);

        // Remove followedUserId from the current user's following list
        currentUserFollowingRef.removeValue();

        DatabaseReference followedUserFollowersRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(followedUserId)
                .child("followers")
                .child(getCurrentUserId());

        // Remove current user's ID from the followed user's followers list
        followedUserFollowersRef.removeValue();
    }

    // Function to get the current user's ID (You need to implement this method)
    private String getCurrentUserId() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return currentUserId; // Replace "currentUserId" with your actual implementation
    }



}





