package com.example.herhealth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class YourPostsFragment extends Fragment {

    private RecyclerView rv;

    private PostAdapter adapter;

    public YourPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_your_posts, container, false);


        // Initialize RecyclerView
        rv = view.findViewById(R.id.rv); // Replace with the actual RecyclerView ID in fragment_home.xml



        List<ForumsClass> forumsWithUserPosts = new ArrayList<>();
        List<CombinedForumPost> updatedForumPostList = new ArrayList<>();

        adapter = new PostAdapter(requireContext(), updatedForumPostList);
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

//...



        return view;
    }

    private void fetchPostData() {

        List<ForumsClass> forumsWithUserPosts = new ArrayList<>();
        List<CombinedForumPost> updatedForumPostList = new ArrayList<>();

        adapter = new PostAdapter(requireContext(), updatedForumPostList);
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