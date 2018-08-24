package com.example.josh_assignment.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PostEntity extends RealmObject {
    @PrimaryKey
    private long event_date;
    private String event_name;
    private String thumbnail_image;
    private String id;
    private int views;
    private int likes;
    private int shares;

    public String getEvent_name() {
        return event_name;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public long getEvent_date() {
        return event_date;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public int getShares() {
        return shares;
    }



}
