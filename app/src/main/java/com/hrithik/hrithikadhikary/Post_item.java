package com.hrithik.hrithikadhikary;


public class Post_item {

    private String mpost_id;
    private int mtype;
    private String mtweet;
    private String mvideo;
    private String mpicture;
    private String mdate;
    private int mlike;

    public Post_item(){

    }

    public Post_item(String post_id,int type, String tweet, String video, String picture, String date, int like){
        mpost_id=post_id;
        mtype=type;
        mtweet=tweet;
        mvideo=video;
        mpicture=picture;
        mdate=date;
        mlike=like;
    }

    public String getpost_id() {
        return mpost_id;
    }

    public void setpost_id(String mpost_id) {
        this.mpost_id = mpost_id;
    }

    public int gettype() {
        return mtype;
    }

    public void settype(int mtype) {
        this.mtype = mtype;
    }

    public String gettweet() {
        return mtweet;
    }

    public void settweet(String mtweet) {
        this.mtweet = mtweet;
    }

    public String getvideo() {
        return mvideo;
    }

    public void setvideo(String mvideo) {
        this.mvideo = mvideo;
    }

    public String getpicture() {
        return mpicture;
    }

    public void setpicture(String mpicture) {
        this.mpicture = mpicture;
    }

    public String getdate() {
        return mdate;
    }

    public void setdate(String mdate) {
        this.mdate = mdate;
    }

    public int getlike() {
        return mlike;
    }

    public void setlike(int mlike) {
        this.mlike = mlike;
    }
}
