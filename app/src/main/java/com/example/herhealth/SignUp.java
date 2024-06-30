package com.example.herhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextView login;

    EditText nameEditText, usernameEditText, emailEditText, passwordEditText, locationEditText;
    Button signUpButton;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


        nameEditText = findViewById(R.id.name);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        locationEditText = findViewById(R.id.location);
        signUpButton = findViewById(R.id.SignUpBtn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get user input from EditText fields
                String name = nameEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String location = locationEditText.getText().toString().trim();

                // Check if any field is empty
                if (name.isEmpty()) {
                    nameEditText.setError("Name is required");
                } else if (username.isEmpty()) {
                    usernameEditText.setError("Username is required");
                } else if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                } else if (location.isEmpty()) {
                    locationEditText.setError("Location is required");
                } else {
                    // Proceed with user signup
                    performSignUp(name, username, email, password, location);
                }
            }
        });

    }


    private void performSignUp(String name, String username, String email, String password, String location) {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Handle signup failure
                        } else {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Create user data object with role
                                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name, username, email, password, location, null, null, null,null );
                                writeUserDetails.setRole("user");

                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
                                referenceProfile.keepSynced(true);
                                referenceProfile.child(user.getUid()).setValue(writeUserDetails)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUp.this,"Successfully signed in", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SignUp.this, AIChat.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle signup failure
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    // Method to save user details to SharedPreferences
    private void saveDataToSharedPreferences(ReadWriteUserDetails details) {
        if (details != null) {
            // Use SharedPreferences to save the user details
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

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

            // Add similar checks for other fields in the constructor
            editor.apply();
        }

    }
}


