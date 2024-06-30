


package com.example.herhealth;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.util.Log;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;





public class ViewAllPosts extends AppCompatActivity {

    private RecyclerView rv;

    private PostAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_posts);


        String userId = getIntent().getStringExtra("USER_ID");

       // String forumId = getIntent().getStringExtra("Forum_ID");

        // Initialize RecyclerView
        rv = findViewById(R.id.rv); // Replace with the actual RecyclerView ID in fragment_home.xml



        List<ForumsClass> forumsWithUserPosts = new ArrayList<>();
        List<CombinedForumPost> updatedForumPostList = new ArrayList<>();

        adapter = new PostAdapter(ViewAllPosts.this, updatedForumPostList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

// Retrieve current user's UID

        if (userId != null) {


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
                                    Log.d("View All Posts", "Encountered a post for user ID: " + userId);

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

    }
}