package com.example.herhealth;

public class ReadWriteUserDetails {

    public String uid;
    public Boolean following;

    public String name;
    public String username;
    public String email;
    public String location;

    public int verified;



    public String password;

    public String photoURL;

    public Integer age;
    public String bio;

    public String certificate;

    public String role;

    public ReadWriteUserDetails(){}

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }
    public ReadWriteUserDetails(String name, String username, String email, String password, String location, String photoURL , Integer age, String bio, String certificate) {

        this.name = name;
        this.username = username;
        this.email = email;
        this.location = location;
        this.password = password;
        this.photoURL = photoURL;
        this.bio=bio;
        this.age = age;
        this.certificate=certificate;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String address) {
        this.location = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }
}

