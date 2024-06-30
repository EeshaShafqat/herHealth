package com.example.herhealth;


public class Notifications {
    private String userId;
    private String text;
    private String postId;
    private boolean isPost;

    // Constructor
    public Notifications(String userId, String text, String postId, boolean isPost) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = isPost;
    }


    // Getter and setter methods for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter methods for text
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter and setter methods for postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter and setter methods for isPost
    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }


}
