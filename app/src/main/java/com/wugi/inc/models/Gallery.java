package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by storm on 12/17/2017.
 */

public class Gallery {

    private String documentID;
    private Venue venue;
    private Date eventDate;
    private boolean active;
    private String title;
    private String cover;

    public Gallery(DocumentSnapshot document){
        this.documentID = document.getId();
        if (document.contains("eventDate"))
            this.eventDate = document.getDate("eventDate");
        if (document.contains("active"))
            this.active = document.getBoolean("active");
        if (document.contains("title"))
            this.title = document.getString("title");
        if (document.contains("cover"))
            this.cover = document.getString("cover");
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
