package com.example.herhealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageLoader {

    private static ImageLoader instance;
    private Context context;

    public String uri_dp;
    private ImageLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
        return instance;
    }

    public void loadProfileImage() {
        // Check for internet connectivity
        if (isInternetConnected()) {
            // Fetch the user's UID
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Construct the path to the user's profile image in Firebase Storage
            String path = "profile_images/" + uid + ".jpg";

            // Load the image using Picasso
            FirebaseStorage.getInstance().getReference().child(path).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        // Image loaded successfully, now display it in the ImageView
                        //Picasso.get().load(uri).into(imageView);

                        uri_dp = uri.toString();
                        // Save the download URL to SharedPreferences
                        saveImageUrlToPreferences(uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(context, "Error loading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        loadCachedProfileImage();
                    });
        } else {
            // Internet is not available, load the cached image
            loadCachedProfileImage();
        }
    }

    private void loadCachedProfileImage() {
        // Retrieve the saved image URL from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String savedImageUrl = preferences.getString("photoURL", null);

        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
            // Display the saved image URL from SharedPreferences
            uri_dp = savedImageUrl;
        } else {
            // Handle the case where there is no saved image URL
            // You might want to set a default image or show an error message
            Toast.makeText(context, "No saved image URL", Toast.LENGTH_LONG).show();
        }
    }

    private void saveImageUrlToPreferences(String imageUrl) {
        // Save the download URL to SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("photoURL", imageUrl);
        editor.apply();
    }

    // Check if there is an active internet connection
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
