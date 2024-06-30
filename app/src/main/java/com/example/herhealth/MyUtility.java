package com.example.herhealth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class MyUtility {


    //signout code
    public static void signOut(Context context) {
        FirebaseAuth.getInstance().signOut();

        // Clear SharedPreferences on uninstall
        SharedPreferences preferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);


    }


}
