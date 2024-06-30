package com.example.herhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {


    FragmentActivity activity;
    Context context;
    List<Notifications> items;

    public NotificationAdapter(FragmentActivity activity, Context context, List<Notifications> items) {
        this.context = context;
        this.items = items;
        this.activity = activity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_search_result, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Notifications currentItem = items.get(position);

        holder.name.setText(currentItem.getUserId());

        Log.d("People Adapter", "user name " + currentItem.getUserId());


        holder.comment.setText(currentItem.getText());

        if(currentItem.isPost()){

            holder.postImage.setVisibility(View.VISIBLE);
        }
        else{

            holder.postImage.setVisibility(View.INVISIBLE);
        }

       // int verified = currentItem.getVerified();


//        Picasso.get().load(currentItem.getPhotoURL())
//                .placeholder(R.color.gray_textbox)
//                .error(R.color.gray_textbox)
//                .into(holder.image);



        //post to be opened, pass post ID as argument

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("Notification Adapter", "in on click " );
//
//
//                // Create a FragmentTransaction to replace the current fragment with the new one
//                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
//
//                // Replace the current fragment with the new one
//                transaction.replace(R.id.fragmentContainerView, ViewProfile.newInstance(userID));
//
//                // Add the transaction to the back stack (optional, allows back navigation)
//                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();
//
//            }
//        });


    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, comment;
        ImageView verified, image, postImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name); // Adjust ID as per your layout
            comment = itemView.findViewById(R.id.comment); // Adjust ID as per your layout
            verified = itemView.findViewById(R.id.verified); // Adjust ID as per your layout
            image = itemView.findViewById(R.id.image); // Adjust ID as per your layout
            postImage = itemView.findViewById(R.id.postImage);

        }
    }



}
