package com.example.herhealth;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private boolean isFirstItemSelected = true;

    public CustomArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;

        // Apply gray-out effect to the first item when the spinner is open
        if (position == 0 ) {
            textView.setTextColor(Color.GRAY); // Adjust color as needed
        } else {
            textView.setTextColor(Color.BLACK); // Adjust color as needed
        }

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        // Reset the flag after the first item is selected
        if (position != 0) {
            isFirstItemSelected = false;
        }

        return view;
    }
}
