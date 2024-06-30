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

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.MyViewHolder> {


    FragmentActivity activity;
    Context context;
    List<ReadWriteUserDetails> items;

    public PeopleAdapter(FragmentActivity activity, Context context, List<ReadWriteUserDetails> items) {
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

        ReadWriteUserDetails currentItem = items.get(position);

        holder.name.setText(currentItem.getName());

        Log.d("People Adapter", "user name " + currentItem.getName());


        holder.bio.setText(currentItem.getBio());

        int verified = currentItem.getVerified();

        if (verified == 2) {
            holder.verified.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(currentItem.getPhotoURL())
                .placeholder(R.color.gray_textbox)
                .error(R.color.gray_textbox)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = currentItem.getUid();
                Log.d("People Adapter", "uid " + userID);


                // Create a FragmentTransaction to replace the current fragment with the new one
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

                // Replace the current fragment with the new one
                transaction.replace(R.id.fragmentContainerView, ViewProfile.newInstance(userID));

                // Add the transaction to the back stack (optional, allows back navigation)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });


    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, bio;
        ImageView verified, image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name); // Adjust ID as per your layout
            bio = itemView.findViewById(R.id.bio); // Adjust ID as per your layout
            verified = itemView.findViewById(R.id.verified); // Adjust ID as per your layout
            image = itemView.findViewById(R.id.image); // Adjust ID as per your layout

        }
    }



}
