package com.example.herhealth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private HomePageAdapter adapter;
    private List<CombinedForumPost> combinedForumPosts;

    ImageView image;


    //for drawer layout

    private Toolbar toolbar;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        loadDisplay(view);


        //verified or not
        VerificationStatus(view);


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


//*********************************************************

// Initialize RecyclerView
        rv = view.findViewById(R.id.rv);

        // Initialize combinedForumPosts list
        combinedForumPosts = new ArrayList<>();

        // Initialize RecyclerView adapter
        adapter = new HomePageAdapter(requireContext(), combinedForumPosts,rv);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);


        // Fetch all forums
        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
        forumsRef.keepSynced(true);

        forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                    ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                    String forumId = forumSnapshot.getKey();


                    // Check if the current user is a member of the forum
                    isUserMemberOfForum(forumId, new ForumMembershipCallback() {
                        @Override
                        public void onResult(boolean isMember) {
                            if (isMember) {
                                // If the user is a member, add the forum to the combinedForumPosts list
                                addForumToCombinedList(forum, forumId);
                                adapter.notifyDataSetChanged(); // Notify adapter of data change
                            }
                        }
                    });


                    // Check if the current user follows the author of each post
                    if (forum != null && forum.getPosts() != null) {
                        for (DataSnapshot postSnapshot : forumSnapshot.child("posts").getChildren()) {
                            Posts post = postSnapshot.getValue(Posts.class);
                            String postId = postSnapshot.getKey();
                            post.setPostId(postId);

                            // Check if the current user is following the author of the post
                            isUserFollowing(post.getUserId(), new FollowingStatusCallback() {
                                @Override
                                public void onResult(boolean isFollowing) {
                                    if (isFollowing) {
                                        // Check if the post is already in the list
                                        // Check if the post is already in the list
                                        boolean isPostAlreadyAdded = false;
                                        for (CombinedForumPost combinedForumPost : combinedForumPosts) {
                                            Posts post = combinedForumPost.getPost();
                                            if (post != null && post.getPostId() != null && post.getPostId().equals(postSnapshot.getKey())) {
                                                isPostAlreadyAdded = true;
                                                break;
                                            }
                                        }


                                        // If the post is not already added, add it to the list
                                        if (!isPostAlreadyAdded) {
                                            CombinedForumPost combinedForumPost = new CombinedForumPost(
                                                    forum.getName(),
                                                    forum.getDescription(),
                                                    forum.getCreated_by(),
                                                    forum.getImage(),
                                                    post
                                            );
                                            combinedForumPost.setForumId(forumId);
                                            combinedForumPosts.add(combinedForumPost);
                                            adapter.notifyDataSetChanged(); // Notify adapter of data change
                                        }
                                    }
                                }
                            });
                        }
                    }
                }

                // Notify the adapter of the data change
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


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


    public void loadDisplay(View view) {
        image = view.findViewById(R.id.image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.keepSynced(true);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String photoUrl = dataSnapshot.child("photoURL").getValue(String.class);
                        if (photoUrl != null) {
                            // Load image into ImageView using Picasso
                            Picasso.get().load(photoUrl).into(image);
                        } else {
                            Log.d("dp", "photo url null");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("loadDisplay", "Error loading user data: " + databaseError.getMessage());
                }
            });
        }
    }


    private void VerificationStatus(View view) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the "verified" field exists
                    DataSnapshot verifiedSnapshot = dataSnapshot.child("verified");
                    if (verifiedSnapshot.exists()) {
                        // Retrieve the verification status
                        Integer verificationStatus = verifiedSnapshot.getValue(Integer.class);
                        if (verificationStatus != null) {
                            // Check the verification status
                            if (verificationStatus == 0) {
                                // User is not verified
                                // Handle accordingly
                            } else if (verificationStatus == 1) {
                                // User has a pending verification request
                                // Handle accordingly
                            } else if (verificationStatus == 2) {
                                // User is verified
                                ImageView verify = view.findViewById(R.id.verified);
                                verify.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Verification status is null
                            // Handle accordingly
                        }
                    } else {
                        // "verified" field does not exist
                        // Handle accordingly
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void isUserMemberOfForum(String forumId, ForumMembershipCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userForumRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid())
                    .child("memberOf")
                    .child(forumId);

            userForumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // If the snapshot exists, the user is a member of the forum
                    // Otherwise, the user is not a member
                    boolean isMember = dataSnapshot.exists();
                    callback.onResult(isMember);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    callback.onResult(false); // Assume user is not a member on error
                }
            });
        } else {
            // User is not logged in
            callback.onResult(false);
        }
    }

    // Define a callback interface
    private interface ForumMembershipCallback {
        void onResult(boolean isMember);
    }

    private void addForumToCombinedList(ForumsClass forum, String forumId) {
        if (forum != null && forum.getPosts() != null) {
            // Iterate through forum posts and create CombinedForumPost objects
            for (Map.Entry<String, Posts> entry : forum.getPosts().entrySet()) {
                String forumName = forum.getName();
                String forumDescription = forum.getDescription();
                String forumCreatedBy = forum.getCreated_by();
                String forumImage = forum.getImage();
                Posts post = entry.getValue();
                post.setPostId(entry.getKey());

                // Create CombinedForumPost object
                CombinedForumPost combinedForumPost = new CombinedForumPost(
                        forumName,
                        forumDescription,
                        forumCreatedBy,
                        forumImage,
                        post
                );

                combinedForumPost.setForumId(forumId);

                // Add the combined forum post to the list
                combinedForumPosts.add(combinedForumPost);
            }
        }
    }

        private void isUserFollowing(String userId, FollowingStatusCallback callback) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference userFollowingRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(user.getUid())
                        .child("following")
                        .child(userId);

                userFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // If the snapshot exists, the user is following the specified user
                        // Otherwise, the user is not following
                        boolean isFollowing = dataSnapshot.exists();
                        callback.onResult(isFollowing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        callback.onResult(false); // Assume user is not following on error
                    }
                });
            } else {
                // User is not logged in
                callback.onResult(false);
            }
        }

// Define a callback interface
        private interface FollowingStatusCallback {
            void onResult(boolean isFollowing);
        }




    }



