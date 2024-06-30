package com.example.herhealth;

public class ReadWriteAdminDetails {

    public String name;
    public String username;
    public String email;

    public String password;

    public String photoURL;

    public String designation;


    public ReadWriteAdminDetails(){}


    public ReadWriteAdminDetails(String name, String username, String email, String password, String designation) {

        this.name = name;
        this.username = username;
        this.email = email;
        this.designation = designation;
        this.password = password;
    }



    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

