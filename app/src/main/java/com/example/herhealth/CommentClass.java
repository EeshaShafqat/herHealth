package com.example.herhealth;

public class CommentClass {

    String photoURL;
    String name;
    String commentText;

    String uid;

    public CommentClass() {
    }

    public CommentClass(String name, String commentText, String photoURL) {
        this.name = name;
        this.commentText = commentText;
        this.photoURL =photoURL;

    }

    // Getters and setters

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
