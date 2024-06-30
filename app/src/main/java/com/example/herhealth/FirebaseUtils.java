package com.example.herhealth;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.Locale;

public class FirebaseUtils {

    public static void getUserData(String userId, ValueEventListener valueEventListener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(valueEventListener);
    }




    //userID
    public String addPost( String forumId, Posts post) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("forums").child(forumId).child("posts");
        String postId = postsRef.push().getKey();

   //     Posts post = new Posts();
//        post.setContent(postContent);
//        post.setAuthor(userId);
//        post.setTime(System.currentTimeMillis());

        postsRef.child(postId).setValue(post);

        return "Post added successfully!";
    }


    public void viewUserForums(String userId) {
        DatabaseReference forumsRef = FirebaseDatabase.getInstance().getReference("forums");
        forumsRef.orderByChild("Created_by").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Your forums:");
                for (DataSnapshot forumSnapshot : dataSnapshot.getChildren()) {
                    ForumsClass forum = forumSnapshot.getValue(ForumsClass.class);
                    System.out.println("- " + forum.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }





}
