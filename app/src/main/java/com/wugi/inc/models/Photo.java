package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by storm on 12/17/2017.
 */

public class Photo {
    private String documentId;
    private String title;
    private Date createdAt;
    private Date updatedAt;
    private boolean active;
    private String filename;
    private Gallery gallery;

    public Photo(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("title"))
            this.title = document.getString("title");
        if (document.contains("createdAt"))
            this.createdAt = document.getDate("createdAt");
        if (document.contains("updatedAt"))
            this.updatedAt = document.getDate("updatedAt");
        if (document.contains("active"))
            this.active = document.getBoolean("active");
        if (document.contains("filename"))
            this.filename = document.getString("filename");
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }
}
