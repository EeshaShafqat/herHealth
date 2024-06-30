
package com.example.herhealth;

        import android.os.Bundle;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;



public class ViewAllForums extends AppCompatActivity {

    private RecyclerView recyclerView;
    private YourForumsAdapter adapter;
    private List<ForumsClass> yourForumItemList;

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_forums);

        // Get the user ID passed via Intent
        userId = getIntent().getStringExtra("USER_ID");


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize yourForumItemList with your data
        yourForumItemList = new ArrayList<>();

        // Initialize and set adapter
        adapter = new YourForumsAdapter(this,this, yourForumItemList);
        recyclerView.setAdapter(adapter);

        fetchForumsData(userId);


    }

    private void fetchForumsData(String userId) {

        if (userId != null) {

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

