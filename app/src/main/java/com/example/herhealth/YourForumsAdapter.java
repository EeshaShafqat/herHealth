package com.example.herhealth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class YourForumsAdapter extends RecyclerView.Adapter<YourForumsAdapter.ViewHolder> {

    static Context context;
    static List<ForumsClass> ls;

    FragmentActivity activity;

    public YourForumsAdapter(FragmentActivity activity,Context context, List<ForumsClass> ls) {
        this.context = context;
        this.ls = ls;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.yourforumlayout,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("PostAdapter", "onBindViewHolder called for position: " + position);

        String forumId = ls.get(position).getForumId();
        String ForumImage = ls.get(position).getImage();
        String ForumName = ls.get(position).getName();
        String ForumDesc = ls.get(position).getDescription();
        Integer PostCount = ls.get(position).getPostCount();
        String created_by = ls.get(position).getCreated_by();



        holder.setData(ForumImage, ForumName, ForumDesc, PostCount.toString(),forumId);

        //to view all posts in that forum
        // Set click listener on the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Your Forums Adapter", "uid " + forumId);


                // Create a FragmentTransaction to replace the current fragment with the new one
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

                // Replace the current fragment with the new one
                transaction.replace(R.id.fragmentContainerView, ViewForums.newInstance(forumId));

                // Add the transaction to the back stack (optional, allows back navigation)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });




    }



    @Override
    public int getItemCount() {
        return ls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView image,view,edit;
        TextView category, content, postCount,membersCount;
        RelativeLayout forumDetail;

        Button follow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            content = itemView.findViewById(R.id.Content);
            postCount = itemView.findViewById(R.id.postcount);
            forumDetail = itemView.findViewById(R.id.foumDetail);
            follow = itemView.findViewById(R.id.follow);


            view = itemView.findViewById(R.id.view);

            membersCount = itemView.findViewById(R.id.memberscount);


        }


        public void setData( String ForumImage,String ForumName,String Description, String postCount, String forumId) {

            //R.drawable.ic_launcher_background
            //this.image.setImageResource(image); load from picasso
            this.category.setText(ForumName);

            Picasso.get()
                    .load(ForumImage)
                    .placeholder(R.drawable.womens_day1) // Replace R.drawable.placeholder_image with your placeholder image resource
                    .into(this.image);




            this.content.setText(Description);
           //set postCount
            this.postCount.setText(postCount);

            // Count followers for the forum
            DatabaseReference forumFollowersRef = FirebaseDatabase.getInstance()
                    .getReference("forums")
                    .child(forumId)
                    .child("followers");

            forumFollowersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Get the count of followers
                    long followersCount = dataSnapshot.getChildrenCount();
                    // Set the text of members count
                    membersCount.setText(String.valueOf(followersCount));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("YourForumsAdapter", "Error: " + databaseError.getMessage());
                }
            });


        }
    }







    // Function to get the current user's ID
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }






}
