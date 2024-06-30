package com.example.herhealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    List<item> items;

    public MyAdapter(Context context, List<item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        item currentItem = items.get(position);
        holder.name.setText(currentItem.getName());
        holder.location.setText(currentItem.getLocation());
        holder.age.setText(String.valueOf(currentItem.getAge())); // Assuming getAge() returns an int
        holder.pdf.setImageResource(currentItem.getPdf()); // Assuming getPdf() returns a drawable resource id
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, age;
        ImageView pdf;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.submit); // Adjust ID as per your layout
            location = itemView.findViewById(R.id.location); // Adjust ID as per your layout
            age = itemView.findViewById(R.id.age); // Adjust ID as per your layout
            pdf = itemView.findViewById(R.id.pdf); // Adjust ID as per your layout
        }
    }
}
