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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ForumFragment extends Fragment {

    private RecyclerView rv;




    private Context mContext;

    private Toolbar toolbar;
    ImageView image;

    private String cachedImageUrl;
    Button EditProfile;


    private static final String TAG = "ForumFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);



        // Initialize RecyclerView
        rv = view.findViewById(R.id.rv); // Replace with the actual RecyclerView ID in fragment_home.xml





        //retrieve data from firebase for profile
        image = view.findViewById(R.id.image);

        fetchData(view);
//*********************************************************************

        VerificationStatus(view);

          //  loadProfileImage();

      //  Picasso.get().load(ImageLoader.getInstance(requireContext()).uri_dp).into(image);




        EditProfile = view.findViewById(R.id.editProfile);
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
                v.startAnimation(scaleAnimation);

                Intent intent = new Intent(requireActivity(), WelcomeToForums.class);
                startActivity(intent);
            }
        });

//**********************************************************************************************
//        WILL NEED THIS CODE TO GET FOLLOWING LIST AND SHOW

//        ViewAll= view.findViewById(R.id.view);
//        ViewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create a new instance of the fragment you want to replace ForumFragment with
//                YourPostsFragment newFragment = new YourPostsFragment();
//
//                // Create a FragmentTransaction to replace the current fragment with the new one
//                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//
//                // Replace the current fragment with the new one
//                transaction.replace(R.id.fragmentContainerView, newFragment);
//
//                // Add the transaction to the back stack (optional, allows back navigation)
//                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();
//            }
//        });

//*************************************************************************************************

        //Displaying posts and forums

        ImageButton yourposts = view.findViewById(R.id.posts);
        ImageButton yourforums = view.findViewById(R.id.forums);

        TextView posts = view.findViewById(R.id.yourposts);
        TextView forums = view.findViewById(R.id.yourforums);



        yourposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forums.setVisibility(View.INVISIBLE);
                posts.setVisibility(View.VISIBLE);

                yourposts.setSelected(true);
                yourforums.setSelected(false);

                fetchPostData();
            }
        });

        yourforums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posts.setVisibility(View.INVISIBLE);
                forums.setVisibility(View.VISIBLE);

                yourposts.setSelected(false);
                yourforums.setSelected(true);

                fetchForumsData();
            }
        });


        yourposts.setSelected(true);
        yourposts.performClick();



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
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.keepSynced(true);
        // Assuming you have TextViews for name and bio in your layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView bioTextView = view.findViewById(R.id.bio);

        TextView followerCountTextView = view.findViewById(R.id.followerCount);
        TextView followingCountTextView = view.findViewById(R.id.followingCount);
        TextView postCountTextView = view.findViewById(R.id.postCount);




        // Retrieve user data from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("photoURL").getValue(String.class);


                    if (dataSnapshot.hasChild("postCount")) {
                        // Fetch count of posts in this forum
                        String postCount = dataSnapshot.child("postCount").getValue(Integer.class).toString();

                        postCountTextView.setText(postCount);


                    }


                    if (dataSnapshot.hasChild("followers")) {
                        DataSnapshot followersSnapshot = dataSnapshot.child("followers");


                        long followerCount = followersSnapshot.getChildrenCount();
                        followerCountTextView.setText(String.valueOf(followerCount));
                    }



                    // Count following
                    long followingCount = 0;
                    long memberOfCount = 0;

                    if (dataSnapshot.hasChild("following")) {

                        DataSnapshot followingSnapshot = dataSnapshot.child("following");
                        followingCount = followingSnapshot.getChildrenCount();

                    }

                    if (dataSnapshot.hasChild("memberOf")) {

                        // Assuming you also want to count the number of forums the user is a member of
                        DataSnapshot memberOfSnapshot = dataSnapshot.child("memberOf");
                        memberOfCount = memberOfSnapshot.getChildrenCount();

                    }

                        // Calculate total following count
                        long totalFollowingCount = followingCount + memberOfCount;
                        followingCountTextView.setText(String.valueOf(totalFollowingCount));




                    // Update UI with retrieved data
                    nameTextView.setText(name);
                    bioTextView.setText(bio);

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


    // Check if there is an active internet connection
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }




    @Override
    public void onResume() {
        super.onResume();

        loadCachedProfileImage();
    }





    private void VerificationStatus(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

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

                            ImageView verify = view.findViewById(R.id.verificationStatus);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }



    private void fetchForumsData() {

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize yourForumItemList with your data
        List<ForumsClass> yourForumItemList = new ArrayList<>();

        // Initialize and set adapter
        YourForumsAdapter adapter = new YourForumsAdapter(requireActivity(),requireContext(), yourForumItemList);
        rv.setAdapter(adapter);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch forums
            DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
            forumsRef.keepSynced(true);

            forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                        ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                        String forumId = forumSnapshot.getKey();

                        if (forum != null && forum.getCreated_by().equals(userId)) {
                            // Fetch count of posts in this forum
                            int postCount = 0;
                            if (forum.getPosts() != null) {
                                postCount = forum.getPosts().size();
                            }

                            // Update the forum with post count
                            forum.setPostCount(postCount);
                            forum.setForumId(forumId);

                            // Add the forum to the list
                            yourForumItemList.add(forum);
                        }
                    }

                    // Notify adapter that data has changed
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }


    private void fetchPostData() {



        List<ForumsClass> forumsWithUserPosts = new ArrayList<>();
        List<CombinedForumPost> updatedForumPostList = new ArrayList<>();

        PostAdapter adapter = new PostAdapter(requireContext(), updatedForumPostList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

// Retrieve current user's UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch all forums
            DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
            forumsRef.keepSynced(true);

            forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                        ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                        String forumId = forumSnapshot.getKey();

                        if (forum != null && forum.getPosts() != null) {
                            // Check if the current user has posts in this forum
                            boolean userHasPostsInForum = false;

                            for (Map.Entry<String, Posts> entry : forum.getPosts().entrySet()) {
                                String postUserId = entry.getValue().getUserId();

                                if (postUserId != null && userId.equals(postUserId)) {
                                    userHasPostsInForum = true;

                                    // Log when a post that matches the user ID is encountered
                                    Log.d("YourPostsFragment", "Encountered a post for user ID: " + userId);
                                    String forumName = forum.getName();
                                    String forumDescription = forum.getDescription();
                                    String forumCreatedBy = forum.getCreated_by();
                                    String forumImage = forum.getImage();

                                    Posts post = entry.getValue();
                                    post.setPostId( entry.getKey());

                                    // Create a new CombinedForumPost object for each post
                                    CombinedForumPost combinedForumPost = new CombinedForumPost(
                                            forumName,
                                            forumDescription,
                                            forumCreatedBy,
                                            forumImage,
                                            post
                                    );

                                    combinedForumPost.setForumId(forumId);
                                    // Add the combined forum post to the list
                                    updatedForumPostList.add(combinedForumPost);
                                }
                            }

                            // Check if the current user has posts in this forum
                            if (userHasPostsInForum) {
                                // Add the forum to the list (with updated posts map)
                                forumsWithUserPosts.add(forum);
                            }
                        }
                    }

                    // Log the size of forumsWithUserPosts
                    Log.d("YourPostsFragment", "Number of forums with user posts: " + forumsWithUserPosts.size());
                    // Log the size of updatedForumPostList
                    Log.d("YourPostsFragment", "Number of updated forum posts: " + updatedForumPostList.size());

                    // Update the RecyclerView with forums containing user's posts
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }

    }


}





