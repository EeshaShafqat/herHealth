package com.example.herhealth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    static Context context;
    static List<CommentClass> ls;

    public CommentAdapter(Context context, List<CommentClass> ls) {
        this.context = context;
        this.ls = ls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.comments_layout, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {

        Log.d("PostAdapter", "onBindViewHolder called for position: " + position);

        String photoURL = ls.get(position).getPhotoURL();
        String userName = ls.get(position).getUsername();
        String commentText = ls.get(position).getCommentText();


        if(photoURL!=null){

            Picasso.get().load(photoURL).placeholder(R.drawable.placeholder_profile).into(holder.image);
        }

        if(userName!=null){
            holder.username.setText(userName);

        }

        if(commentText!=null){
            holder.comment.setText(commentText);

        }



    }


        @Override
        public int getItemCount () {
            return ls != null ? ls.size() : 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView username, comment;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.image);
                username = itemView.findViewById(R.id.username);
                comment = itemView.findViewById(R.id.comment);

            }


        }

    }



    //DownVote functionality

    // Add a new method for handling downvote clicks






