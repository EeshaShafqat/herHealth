package com.example.herhealth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class YourForumsFragment extends Fragment {



    private RecyclerView recyclerView;
    private YourForumsAdapter adapter;
    private List<ForumsClass> yourForumItemList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_your_forums, container, false);


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize yourForumItemList with your data
        yourForumItemList = new ArrayList<>();

        // Initialize and set adapter
        adapter = new YourForumsAdapter(requireActivity(),requireContext(), yourForumItemList);
        recyclerView.setAdapter(adapter);

        fetchForumsData();



        return view;
    }

    private void fetchForumsData() {

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize yourForumItemList with your data
        yourForumItemList = new ArrayList<>();

        // Initialize and set adapter
        adapter = new YourForumsAdapter(requireActivity(),requireContext(), yourForumItemList);
        recyclerView.setAdapter(adapter);

        fetchForumsData();


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



}

