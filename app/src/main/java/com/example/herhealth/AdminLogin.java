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

public class AdminLogin extends AppCompatActivity {

    TextView userLogin;

    EditText email;
    EditText pass;

    Button login;
    private FirebaseAuth authAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        authAdmin = FirebaseAuth.getInstance();


        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);

        userLogin = findViewById(R.id.UserLogin);
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLogin.this, Login.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.LoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



    //"""""""""""""""""""""""""""
    private void performLogin(String userEmail , String userPassword) {


        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data in SharedPreferences
        editor.apply();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(AdminLogin.this, new OnCompleteListener<AuthResult>() {
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

                                            if ("admin".equals(role)) {
                                                // Role is user, allow login

                                                String usernamefromDB = snapshot.child("username").getValue(String.class);
                                                Toast.makeText(AdminLogin.this, "Welcome " + usernamefromDB, Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(AdminLogin.this, AdminMenu.class);
                                                startActivity(intent);
                                            } else {
                                                // Role is not user, show error message
                                                Toast.makeText(AdminLogin.this, "Only admin are allowed to login", Toast.LENGTH_SHORT).show();
                                            }


                                        } else {
                                            // Handle the case where the user data does not exist
                                            Toast.makeText(AdminLogin.this, "No such account", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                pass.setError("Invalid Credentials");
                                pass.requestFocus();
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                email.setError("No such User exists");
                                email.requestFocus();
                            } else {
                                Toast.makeText(AdminLogin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authAdmin.getCurrentUser() != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminLogin.this, AdminMenu.class);
            startActivity(intent);
        }

        else{
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }
}