package com.example.alphachat.model;

import com.google.firebase.Timestamp;

public class UserModel {

    private String email;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String profilePicUrl;

    public UserModel() {
    }

    public UserModel(String email, String username, Timestamp createdTimestamp, String userId, String profilePicUrl) {
        this.email = email;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.profilePicUrl = profilePicUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
