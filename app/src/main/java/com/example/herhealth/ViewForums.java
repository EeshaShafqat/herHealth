package com.example.herhealth;

import static android.content.Context.MODE_PRIVATE;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ViewForums extends Fragment {

    private RecyclerView rv;

    Button Follow;
    ImageView shine;
    String forumId;

    private Context mContext;

    private Toolbar toolbar;
    ImageView image;

    private String cachedImageUrl;


    private static final String TAG = "ViewForumFragment";

    // Static method to create a new instance of the fragment
    public static ViewForums newInstance(String forumId) {
        ViewForums fragment = new ViewForums();
        Bundle args = new Bundle();
        args.putString("FORUM_ID", forumId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_forums, container, false);



        // Initialize RecyclerView
        rv = view.findViewById(R.id.rv); // Replace with the actual RecyclerView ID in fragment_home.xml


        // Get the forum ID passed via arguments
        // Retrieve the user ID from arguments
        if (getArguments() != null) {
            forumId = getArguments().getString("FORUM_ID");
        }

        //retrieve data from firebase for profile
        image = view.findViewById(R.id.image);

        fetchData(view);
//**************************** Hide Show Description *****************************************

        TextView showDescTextView = view.findViewById(R.id.descShow);
        TextView bioTextView = view.findViewById(R.id.bio);
        TextView hideDescTextView = view.findViewById(R.id.descHide);


        bioTextView.setVisibility(View.GONE);
        hideDescTextView.setVisibility(View.GONE);
        showDescTextView.setVisibility(View.VISIBLE);

        hideDescTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bioTextView.setVisibility(View.GONE);
                hideDescTextView.setVisibility(View.GONE);
                showDescTextView.setVisibility(View.VISIBLE);
            }
        });

        showDescTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bioTextView.setVisibility(View.VISIBLE);
                hideDescTextView.setVisibility(View.VISIBLE);
                showDescTextView.setVisibility(View.GONE);
            }
        });




//**************************************************************


        Follow = view.findViewById(R.id.follow);
        shine = view.findViewById(R.id.shine);

        //follow button for forums visible to all


        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        shineStart();
                    }
                });
            }
        },3,3, TimeUnit.SECONDS);

        //follow

        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
                v.startAnimation(scaleAnimation);


                // Get the text of the button
                String buttonText = Follow.getText().toString();

                if(buttonText.equals("Follow")){

                    followForum(forumId);

                    fetchFollowers(view);


                } else if (buttonText.equals("Following")) {

                    unfollowForum(forumId);

                    fetchFollowers(view);


                }

                // Now you can use the buttonText variable as needed
                Log.d("Button Text", "Button text: " + buttonText);
            }
        });


//***************************************************************



//*************************************************************************************************

        //Displaying posts and forums


        fetchPostData(forumId);




        //**************************************************************************************




        // Access the Toolbar from the activity
        toolbar = requireActivity().findViewById(R.id.toolbar);


        // Set the Toolbar as the support action bar
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        }


        // Customize the Toolbar in this fragment
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // Show the back button (hamburger icon)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        // Enable options menu in the fragment
        setHasOptionsMenu(true);

        return view;
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
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawerLayout);
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void fetchData(View view){

        // Assuming you have a reference to the Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("forums").child(forumId);

        userRef.keepSynced(true);

        // Assuming you have TextViews for name and bio in your layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView bioTextView = view.findViewById(R.id.bio);
        TextView createdByTextView = view.findViewById(R.id.createdBy);

        TextView followerCountTextView = view.findViewById(R.id.followerCount);
        TextView postCountTextView = view.findViewById(R.id.postCount);


        // Retrieve user data from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String createdBy = dataSnapshot.child("created_by").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("image").getValue(String.class);

                    // Check if createdBy is the same as the current user ID
                    String currentUserId = getCurrentUserId();
                    if (currentUserId != null && currentUserId.equals(createdBy)) {
                        // If createdBy is the same as the current user ID, hide the Follow button
                        Follow.setVisibility(View.GONE);
                        shine.setVisibility(View.GONE);
                    } else {
                        // Otherwise, show the Follow button
                        Follow.setVisibility(View.VISIBLE);
                    }



                    if (dataSnapshot.hasChild("postCount")) {
                        // Fetch count of posts in this forum
                        String postCount = dataSnapshot.child("postCount").getValue(Integer.class).toString();

                        postCountTextView.setText(postCount);



                    }



                    if (dataSnapshot.hasChild("followers")) {
                        DataSnapshot followersSnapshot = dataSnapshot.child("followers");
                        long followerCount = followersSnapshot.getChildrenCount();
                        followerCountTextView.setText(String.valueOf(followerCount));


                        // Check if the current user is following this user
                        if (followersSnapshot.hasChild(getCurrentUserId())) {

                            Follow.setText(R.string.following);
                            Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.following, 0);
                        }
                        else {

                            Follow.setText(R.string.follow);
                            Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.follow, 0);
                        }

                    }




                    // Update UI with retrieved data
                    nameTextView.setText(name);
                    bioTextView.setText(description);

                    // Retrieve the name corresponding to createdByUserId
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(createdBy);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            if (userSnapshot.exists()) {
                                String createdByUserName = userSnapshot.child("name").getValue(String.class);
                                createdByTextView.setText(createdByUserName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                            Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
                        }
                    });


                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Save the image URL to SharedPreferences
                        cachedImageUrl = profileImageUrl;
                        SharedPreferences preferences = mContext.getSharedPreferences("CachedImageData", MODE_PRIVATE);
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
                Toast.makeText(mContext, "Error fetching user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();

        loadCachedProfileImage();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }




    //modify so that for current forum id, we get all posts
    private void fetchPostData(String forumId) {


        List<CombinedForumPost> updatedForumPostList = new ArrayList<>();

        PostForumAdapter adapter = new PostForumAdapter(requireContext(), updatedForumPostList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        DatabaseReference forumPostsRef = FirebaseDatabase.getInstance().getReference("forums").child(forumId).child("posts");
        forumPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list of posts
                updatedForumPostList.clear();

                // Retrieve forum name and image
                DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("forums").child(forumId);
                forumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot forumSnapshot) {
                        if (forumSnapshot.exists()) {
                            String forumName = forumSnapshot.child("name").getValue(String.class);
                            String forumImage = forumSnapshot.child("image").getValue(String.class);

                            // Iterate through posts
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Posts post = postSnapshot.getValue(Posts.class);
                                if (post != null) {
                                    // Create a new CombinedForumPost object
                                    CombinedForumPost combinedForumPost = new CombinedForumPost(
                                            forumName,
                                            forumSnapshot.child("description").getValue(String.class),
                                            forumSnapshot.child("created_by").getValue(String.class),
                                            forumImage,
                                            post
                                    );
                                    combinedForumPost.setForumId(forumId);
                                    updatedForumPostList.add(combinedForumPost);
                                }
                            }
                            // Notify adapter of data change
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }



    // Method to load the cached image
    private void loadCachedProfileImage() {
        // Retrieve the saved image URL from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("CachedImageData", MODE_PRIVATE);
        String savedImageUrl = preferences.getString("cachedImageUrl", null);

        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
            // Display the cached image URL
            Picasso.get().load(savedImageUrl).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    // Image loaded successfully
                    Toast.makeText(requireActivity(), "Image loaded from cache", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                    Toast.makeText(mContext, "Error loading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Handle the case where there is no saved image URL
            // You might want to set a default image or show an error message
            Toast.makeText(mContext, "No saved image URL", Toast.LENGTH_LONG).show();
        }
    }



    // Function to add the user to the following list
    private void followForum(String followedforumId) {

        Log.d("ViewProfile","in follow");

        DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getCurrentUserId())
                .child("memberOf")
                .child(followedforumId);

        // Add followedUserId to the current user's following list
        currentUserFollowingRef.setValue(true);

        DatabaseReference followedUserFollowersRef = FirebaseDatabase.getInstance()
                .getReference("forums")
                .child(followedforumId)
                .child("followers")
                .child(getCurrentUserId());

        // Add current user's ID to the followed user's followers list
        followedUserFollowersRef.setValue(true);

    }

    private void unfollowForum(String followedForumId) {
        DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getCurrentUserId())
                .child("memberOf")
                .child(followedForumId);

        // Remove followedUserId from the current user's following list
        currentUserFollowingRef.removeValue();

        DatabaseReference followedUserFollowersRef = FirebaseDatabase.getInstance()
                .getReference("forums")
                .child(followedForumId)
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


    private void fetchFollowers(View view) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("forums").child(forumId);

        userRef.keepSynced(true);

        // Assuming you have TextViews for follower count in your layout
        TextView followerCountTextView = view.findViewById(R.id.followerCount);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data

                    long followerCount =0;
                    // Fetch and update follower count
                    if (dataSnapshot.hasChild("followers")) {
                        DataSnapshot followersSnapshot = dataSnapshot.child("followers");
                        followerCount = followersSnapshot.getChildrenCount();
                        followerCountTextView.setText(String.valueOf(followerCount));



                        // Check if the current user is following this user
                        if (followersSnapshot.hasChild(getCurrentUserId())) {

                            Follow.setText(R.string.following);
                            Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.following, 0);
                        }
                        else {

                            Follow.setText(R.string.follow);
                            Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.follow, 0);
                        }

                    } else{
                        Follow.setText(R.string.follow);
                        Follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.follow, 0);
                        followerCountTextView.setText(String.valueOf(followerCount));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(mContext, "Error fetching follower count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shineStart() {
        Animation animation = new TranslateAnimation(
                0, Follow.getWidth() + shine.getWidth() + 50, // fromXDelta, toXDelta
                0, 0 // fromYDelta, toYDelta
        );


        animation.setDuration(550);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateInterpolator());
        shine.startAnimation(animation);

    }



}