package com.example.herhealth;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class Posts {

    String image;
    String video;
    String postId,name, title, content;

    Long likes, dislikes;
    String userId;

    String author;
    Long time;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Posts( ) { }

    public Posts( String postId, String name, String title, String content, Long likes, Long dislikes) {
        this.postId = postId;  //forum id
        this.name = name;
        this.title = title;
        this.time = System.currentTimeMillis();
        this.content = content;


        this.likes = likes;
        this.dislikes = dislikes;


    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTime() {
        return time;
    }

    public void setTime() {
        this.time = System.currentTimeMillis();
    }
    public void setTime(Long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
