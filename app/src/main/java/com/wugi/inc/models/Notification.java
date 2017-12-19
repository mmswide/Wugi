package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Gallery;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by storm on 12/17/2017.
 */

public class Notification {
    private String documentId;
    private Gallery gallery;
    private Event event;
    private Date createdAt;
    private Date updatedAt;
    private String title;
    private String description;
    private ArrayList<String> images;

    public Notification(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("title"))
            this.title = document.getString("title");
        if (document.contains("images"))
            this.images = (ArrayList<String>) document.get("images");
        if (document.contains("description"))
            this.description = document.getString("description");
        if (document.contains("createdAt"))
            this.createdAt = document.getDate("createdAt");
        if (document.contains("updatedAt"))
            this.updatedAt = document.getDate("updatedAt");
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
