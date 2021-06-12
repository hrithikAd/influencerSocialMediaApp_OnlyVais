package com.hrithik.hrithikadhikary;

public class User {
    private String userId;
    private String displayName;
    private String photoUrl;
    private String role;

    public User(){

    }

    public User(String userId, String displayName, String photoUrl, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
