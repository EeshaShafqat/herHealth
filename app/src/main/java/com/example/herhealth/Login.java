package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    LinearLayout signup;

    EditText email;
    EditText pass;
    Button login;
    private FirebaseAuth authProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup = findViewById(R.id.SignUp);

        authProfile = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });



        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);




        login = findViewById(R.id.LoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation scaleAnimation = AnimationUtils.loadAnimation(Login.this, R.anim.scale);
                v.startAnimation(scaleAnimation);

                String Email = email.getText().toString().trim();
                String password = pass.getText().toString();


                if (Email.isEmpty()) {
                    email.setError("Email is required");
                }

                if (password.isEmpty()) {
                    pass.setError("Password is required");
                }

                if (!password.isEmpty() && !Email.isEmpty()) {
                    performLogin(Email, password);
                }




            }
        });
    }

    private void performLogin(String userEmail, String userPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String role = snapshot.child("role").getValue(String.class);
                                            if ("user".equals(role)) {
                                                // Role is user, allow login
                                                handleSuccessfulLogin(user);
                                            } else {
                                                // Role is not user, show error message
                                                Toast.makeText(Login.this, "Only users are allowed to login", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Role data does not exist, show error message
                                           // Toast.makeText(Login.this, "Role data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                        Toast.makeText(Login.this, "Failed to retrieve role data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // Handle login failure
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = authProfile.getCurrentUser();
        if (user != null) {

            //***************************************************************************************
            // Retrieve user data

                Toast.makeText(this, "user is not null", Toast.LENGTH_LONG).show();
                FirebaseUtils.getUserData(user.getUid(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                          //  ReadWriteUserDetails userDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);


                        }
                        else{
                            Toast.makeText(Login.this, "datasnapshot does not exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if needed
                    }
                });



            //***********************************************************************************



            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            handleSuccessfulLogin(user);
        }

        else{
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToSharedPreferences(ReadWriteUserDetails details) {
        if (details != null) {
            // Use SharedPreferences to save the user details
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            Toast.makeText(this, details.getName(),Toast.LENGTH_LONG).show();

            // Check and save each field individually
            if (details.getName() != null) {
                editor.putString("name", details.getName());
            }

            if (details.getUsername() != null) {
                editor.putString("username", details.getUsername());
            }

            if (details.getEmail() != null) {
                editor.putString("email", details.getEmail());
            }

            if (details.getPassword() != null) {
                editor.putString("password", details.getPassword());
            }

            if (details.getLocation() != null) {
                editor.putString("location", details.getLocation());
            }

            if (details.getPhotoURL() != null) {
               // Toast.makeText(this,"downloading image", Toast.LENGTH_LONG).show();
                loadProfileImage(editor);

            }

            if (details.getAge() != null) {
                editor.putInt("age", details.getAge());
            }


            if (details.getBio() != null) {
                editor.putString("bio", details.getBio());
            }

            if (details.getCertificate() != null) {
                editor.putString("certificate", details.getCertificate());
            }
            // Add similar checks for other fields in the constructor

            editor.apply();
        }
    }


    private void loadProfileImage(SharedPreferences.Editor editor) {

        Log.d("LoadProfileImage", "Loading profile image...");

        // Fetch the user's UID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Construct the path to the user's profile image in Firebase Storage
        String path = "profile_images/" + uid + ".jpg";

        // Load the image using Picasso
        FirebaseStorage.getInstance().getReference().child(path).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // Image loaded successfully, now display it in the ImageView
                    Log.d("LoadProfileImage", "Image loaded successfully: " + path);
                   // Picasso.get().load(uri).into(image);
                    String photoURL = uri.toString();
                    editor.putString("photoURL", photoURL);

                })
                .addOnFailureListener(e -> {
                    Log.e("LoadProfileImage", "Failed to load image: " + e.getMessage(), e);
                });


    }



    private void handleSuccessfulLogin(FirebaseUser user) {
        // Handle successful login here
        // For example, navigate to the desired activity
        Intent intent = new Intent(Login.this, AIChat.class);
        startActivity(intent);
    }

}