package com.example.herhealth;

public class CombinedForumPost {

    private String forumId;
    private String name;
    private String description;
    private String created_by;
    private String image;
    private Posts post;

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

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public CombinedForumPost(String name, String description, String created_by, String image, Posts post) {
        this.name = name;
        this.description = description;
        this.created_by = created_by;
        this.image = image;
        this.post = post;
    }



    // Add getters and setters as needed
}
