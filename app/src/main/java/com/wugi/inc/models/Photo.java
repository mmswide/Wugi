package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by storm on 12/17/2017.
 */

public class Photo {
    private String documentId;
    private boolean active;
    private String filename;
    private Gallery gallery;

    public Photo(DocumentSnapshot document){
        this.documentId = document.getId();
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
