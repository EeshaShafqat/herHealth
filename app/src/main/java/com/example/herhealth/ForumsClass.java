package com.example.herhealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumsClass {


    Integer postCount;
    public String forumId;

    public String name;
    public String description;

    public String created_by;

    public String image;



    public Map<String, Posts> posts = new HashMap<>();


    public ForumsClass(){}
    public ForumsClass(String name, String description,String created_by) {
        this.name = name;
        this.description = description;
        this.created_by = created_by;

    }

    public ForumsClass(String name, String description,String created_by, String image) {
        this.name = name;
        this.description = description;
        this.created_by = created_by;
        this.image = image;
    }

    public Map<String, Posts> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Posts> posts) {
        this.posts = posts;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }
}
