package com.example.herhealth;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView name,location, age;
    ImageView pdf;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        location = itemView.findViewById(R.id.location);
        age = itemView.findViewById(R.id.age);
        pdf = itemView.findViewById(R.id.pdf);



    }
}
