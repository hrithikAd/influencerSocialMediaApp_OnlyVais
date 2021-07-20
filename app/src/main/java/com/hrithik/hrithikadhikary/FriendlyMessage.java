package com.hrithik.hrithikadhikary;

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String userId;
    private String timestamp;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }




    public FriendlyMessage(String text, String name, String photoUrl, String userId) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.userId = userId;
    }

    public FriendlyMessage(String text, String name, String photoUrl, String userId, String timestamp) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId(){return userId;}

    public void setUserId(String userId){this.userId = userId;}

    public  String getTimestamp(){return timestamp;}

    public void setTimestamp(String timestamp){this.timestamp = timestamp;}

}