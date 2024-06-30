package com.example.herhealth;

import java.util.ArrayList;
import java.util.List;

public class ForumsManager {

    private static ForumsManager instance;
    private List<ForumsClass> forumsList;

    private ForumsManager() {
        // Private constructor to prevent instantiation outside the class
        forumsList = new ArrayList<>();
    }

    public static synchronized ForumsManager getInstance() {
        if (instance == null) {
            instance = new ForumsManager();
        }
        return instance;
    }

    public List<ForumsClass> getForumsList() {
        return forumsList;
    }

    public void addForum(ForumsClass forum) {
        forumsList.add(forum);
    }

    // Add other methods as needed...

}
