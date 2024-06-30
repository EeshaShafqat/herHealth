package com.example.herhealth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuDialogFragment extends DialogFragment {

    TextView forumsTextView;
    TextView Signout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_layout, container, false);

        // Get a reference to the "Forums" TextView
        forumsTextView = view.findViewById(R.id.forums);
        Signout = view.findViewById(R.id.signOut);


        // Set an OnClickListener for the "Forums" TextView
        forumsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event
                // Start the WelcomeToForums activity
                Intent intent = new Intent(getActivity(), WelcomeToForums.class);
                startActivity(intent);
                // Dismiss the menu dialog
                dismiss();
            }
        });

        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Customize the position of the menu (top-left corner)
        Dialog dialog = getDialog();
        if (dialog != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT; // Set top-left gravity
            layoutParams.x = 0; // Margin from the left in pixels
            layoutParams.y = 0; // Margin from the top in pixels
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);


        // Set a dim background overlay
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.3f); // Adjust the dim amount as needed

        return dialog;
    }
}
