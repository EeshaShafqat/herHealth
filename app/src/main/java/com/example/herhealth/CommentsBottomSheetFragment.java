package com.example.herhealth;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herhealth.CommentAdapter;
import com.example.herhealth.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CommentsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "postId";
     String postId;
     RecyclerView recyclerView;
     CommentAdapter commentAdapter;
    List<CommentClass> commentList;

    ValueEventListener commentsListener;

    // Method to create a new instance of CommentsBottomSheetFragment with postId as argument
    public static CommentsBottomSheetFragment newInstance(String postId) {
        CommentsBottomSheetFragment fragment = new CommentsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_container, container, false);

        recyclerView = view.findViewById(R.id.rv);

        commentList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(commentAdapter);

//**********************************************************************************************

        // Initialize Firebase components
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference commentsReference = firebaseDatabase.getReference("comments").child(postId);

        // Retrieve and display comments from Firebase
        commentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing list before adding new comments
                commentList.clear();

                // Check if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    // Iterate through each comment in the dataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the CommentClass object from the dataSnapshot
                        CommentClass comment = snapshot.getValue(CommentClass.class);
                        if (comment != null) {
                            // Add the comment to the list
                            commentList.add(comment);
                        }
                    }
                    // Notify the adapter that the data set has changed
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur during the fetch operation
                Log.e("CommentsBottomSheetFragment", "Error fetching comments: " + databaseError.getMessage());
            }
        };

        // Add the ValueEventListener to the database reference
        commentsReference.addValueEventListener(commentsListener);
        //************************************************************************* ADD COMMENT ***************


        // Initialize EditText and send button
        EditText addCommentEditText = view.findViewById(R.id.addComment);
        ImageView sendButton = view.findViewById(R.id.send);
        // Inside the onClickListener for the sendButton
        // Set click listener for the send button
        // Inside the onClickListener for the sendButton
        // Inside the onClickListener for the sendButton
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the comment text
                String commentText = addCommentEditText.getText().toString().trim();

                if (!commentText.isEmpty()) {
                    // Get the current user
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String uid = currentUser.getUid();
                                    String name = dataSnapshot.child("name").getValue(String.class);
                                    String photoURL = dataSnapshot.child("photoURL").getValue(String.class);

                                    // Create a new CommentClass object
                                    CommentClass comment = new CommentClass(name, commentText, photoURL);
                                    comment.setUid(uid);

                                    // Save the comment under the postId in the "comments" node
                                    String commentId = commentsReference.push().getKey();
                                    commentsReference.child(commentId).setValue(comment)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Comment saved successfully
                                                    Log.d("CommentsBottomSheetFragment", "onClick: Comment saved to Firebase");

                                                    // Clear the EditText after sending the comment
                                                    addCommentEditText.setText("");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle error while saving the comment
                                                    Log.e("CommentsBottomSheetFragment", "Error saving comment: " + e.getMessage());
                                                }
                                            });
                                } else {
                                    Log.e("CommentsBottomSheetFragment", "User details not found in database");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors that may occur during the fetch operation
                                Log.e("CommentsBottomSheetFragment", "Error fetching user details: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Log.e("CommentsBottomSheetFragment", "Current user is null");
                    }
                }
            }
        });











//*************************************************************************





        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove the ValueEventListener to prevent memory leaks
        if (commentsListener != null) {
            FirebaseDatabase.getInstance().getReference("comments").child(postId).removeEventListener(commentsListener);
        }
    }




}
