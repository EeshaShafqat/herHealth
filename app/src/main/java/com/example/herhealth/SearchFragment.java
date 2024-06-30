package com.example.herhealth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment {

    Button people,posts,forums;

    private RecyclerView rv;

    private Toolbar toolbar;

    View view;

    private View line;

    List<ReadWriteUserDetails> peopleResults;
    List<CombinedForumPost> postResults;

    List<ForumsClass> forumResults;

    String checked;

    public SearchFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search, container, false);


        Log.d("SearchFragment", "onCreateView");

        line = view.findViewById(R.id.line);



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



        //search view

        SearchView searchView = view.findViewById(R.id.input);
        searchView.setQueryHint("Search people/ post/ forums");

        //Get the EditText within the SearchView
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        // Set the text color to black
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

        // Set the query hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black));







        //set onclick listeners

        people = view.findViewById(R.id.people);
        posts = view.findViewById(R.id.posts);
        forums = view.findViewById(R.id.forums);

        rv = view.findViewById(R.id.rv); // Replace with the actual RecyclerView ID in fragment_home.xml

        checked = "people";
        people.setBackgroundResource(R.drawable.button_focused);
        // setupPeopleRecyclerView("");

        people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setBackgroundResource(R.drawable.button_focused);
                posts.setBackgroundResource(R.drawable.pink_buttons);
                forums.setBackgroundResource(R.drawable.pink_buttons);

                checked = "people";

                Log.d("SearchFragment", "Checked: " + checked);
                setupPeopleRecyclerView(""); // Initialize with an empty query
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setBackgroundResource(R.drawable.button_focused);
                people.setBackgroundResource(R.drawable.pink_buttons);
                forums.setBackgroundResource(R.drawable.pink_buttons);

                checked = "posts";
                Log.d("SearchFragment", "Checked: " + checked);

                setupPostRecyclerView(""); // Initialize with an empty query
            }
        });

        forums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setBackgroundResource(R.drawable.button_focused);
                posts.setBackgroundResource(R.drawable.pink_buttons);
                people.setBackgroundResource(R.drawable.pink_buttons);

                checked = "forums";
                Log.d("SearchFragment", "Checked: " + checked);
                setupForumRecyclerView("");
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



    private void setupPeopleRecyclerView(String query) {
        Log.d("SearchFragment", "setupPeopleRecyclerView with query: " + query);

        peopleResults = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(layoutManager);
        PeopleAdapter adapter = new PeopleAdapter(requireActivity(),requireContext(), peopleResults);
        rv.setAdapter(adapter);

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.keepSynced(true);

        if (!query.isEmpty()) {
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    peopleResults.clear(); // Clear the previous results
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        String currentUSer = userSnapshot.getKey();


                        if (currentUSer != null) {
                            String key = userSnapshot.getKey();

                            ReadWriteUserDetails user = new ReadWriteUserDetails();

                            String image = (String) userSnapshot.child("photoURL").getValue();
                            // Assuming userSnapshot represents a specific user node in your database
                            String name = (String) userSnapshot.child("name").getValue();

                            String bio = (String) userSnapshot.child("bio").getValue();

                            user.setPhotoURL(image);
                            user.setUid(key);
                            user.setName(name);
                            user.setBio(bio);


                            //set following status

                            // Check if the currently logged-in user follows the profile user
                            DatabaseReference currentUserFollowingRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(getCurrentUserId())
                                    .child("following")
                                    .child(key);

                            currentUserFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // The currently logged-in user follows the profile user
                                        user.setFollowing(true);
                                        Log.d("Search Fragment", " user following true " );


                                    } else {
                                        // The currently logged-in user does not follow the profile user
                                        user.setFollowing(false);
                                        Log.d("Search Fragment", " user following false " );

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle errors
                                    Toast.makeText(requireContext(), "Error fetching following data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                            //***************************************






                            Log.d(getTag(),"uid " + currentUSer);
                            if (name != null && bio != null && (name.toLowerCase().contains(query.toLowerCase()) ||
                                    bio.toLowerCase().contains(query.toLowerCase()))) {

                                peopleResults.add(user);

                            }
                        }
                    }
                    // Update the adapter with the search results
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Log.e("SearchFragment", "Error fetching users: " + databaseError.getMessage());
                }
            });
        } else {
            // Clear the results and notify the adapter
            peopleResults.clear();
            adapter.notifyDataSetChanged();
        }
    }





    private void setupPostRecyclerView(String query) {
        Log.d("SearchFragment", "setupPostRecyclerView with query: " + query);

        postResults = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(layoutManager);
        HomePageAdapter adapter2 = new HomePageAdapter(requireContext(), postResults,rv);
        rv.setAdapter(adapter2);

        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
        forumsRef.keepSynced(true);

        if (!query.isEmpty()) {

        forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postResults.clear(); // Clear the previous results
                for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                    ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                    String forumId = forumSnapshot.getKey();

                    if (forum != null && forum.getPosts() != null) {
                        for (Map.Entry<String, Posts> entry : forum.getPosts().entrySet()) {
                            String forumName = forum.getName();
                            String forumDescription = forum.getDescription();
                            String forumCreatedBy = forum.getCreated_by();
                            String forumImage = forum.getImage();
                            Posts post = entry.getValue();
                            post.setPostId( entry.getKey());

                            // Check if the post matches the search query
                            if (post != null && (query.isEmpty() ||
                                    forumName.toLowerCase().contains(query.toLowerCase()) ||
                                    post.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                    post.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                    post.getContent().toLowerCase().contains(query.toLowerCase()))) {
                                CombinedForumPost combinedForumPost = new CombinedForumPost(
                                        forumName,
                                        forumDescription,
                                        forumCreatedBy,
                                        forumImage,
                                        post
                                );

                                combinedForumPost.setForumId(forumId);
                                postResults.add(combinedForumPost);
                            }
                        }
                    }
                }
                // Sort the list by time (recent first)
                Collections.sort(postResults, new Comparator<CombinedForumPost>() {
                    @Override
                    public int compare(CombinedForumPost post1, CombinedForumPost post2) {
                        return Long.compare(post2.getPost().getTime(), post1.getPost().getTime());
                    }
                });

                // Notify the adapter of the data change
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("SearchFragment", "Error fetching posts: " + databaseError.getMessage());
            }
        });

        } else {
            // Clear the results and notify the adapter
            postResults.clear();
            adapter2.notifyDataSetChanged();
        }
    }



    private void setupForumRecyclerView(String query) {
        Log.d("SearchFragment", "setupForumRecyclerView with query: " + query);

        forumResults = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(layoutManager);
        YourForumsAdapter adapter = new YourForumsAdapter(requireActivity(),requireContext(), forumResults);
        rv.setAdapter(adapter);

        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
        forumsRef.keepSynced(true);

        if (!query.isEmpty()) {
        forumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forumResults.clear(); // Clear the previous results
                for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                    ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                    String forumId = forumSnapshot.getKey();

                    if (forum != null) {
                        String forumName = forum.getName();
                        String forumDescription = forum.getDescription();

                        // Check if the forum name or description contains the query
                        if (forumName.toLowerCase().contains(query.toLowerCase()) ||
                                forumDescription.toLowerCase().contains(query.toLowerCase())) {
                            // Fetch count of posts in this forum
                            int postCount = 0;
                            if (forum.getPosts() != null) {
                                postCount = forum.getPosts().size();
                            }

                            forum.setForumId(forumId);
                            // Update the forum with post count
                            forum.setPostCount(postCount);

                            forumResults.add(forum);
                        }
                    }
                }
                // Notify the adapter of the data change
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("SearchFragment", "Error fetching forums: " + databaseError.getMessage());
            }
        });
        } else {
            // Clear the results and notify the adapter
            forumResults.clear();
            adapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        Log.d("SearchFragment", "onStart");

        SearchView searchView = view.findViewById(R.id.input);

        // Set TextWatcher for the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               //  Perform search when submit button is clicked
                Log.d("SearchFragment", "onQueryTextSubmit: " + query);

                if(checked.equals("people")){
                    setupPeopleRecyclerView(query.toLowerCase());
                } else if (checked.equals("posts")) {
                    setupPostRecyclerView(query.toLowerCase());
                } else if (checked.equals("forums")) {
                    setupForumRecyclerView(query.toLowerCase());
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d("SearchFragment", "onQueryTextChange: " + newText);
                 //Perform search as text changes

                if(checked.equals("people")){
                    setupPeopleRecyclerView(newText.toLowerCase());
                } else if (checked.equals("posts")) {
                    setupPostRecyclerView(newText.toLowerCase());
                } else if (checked.equals("forums")) {
                    setupForumRecyclerView(newText.toLowerCase());
                }


                return true; // Return true to consume the event
            }
        });
    }





    private String getCurrentUserId() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return currentUserId; // Replace "currentUserId" with your actual implementation
    }




}