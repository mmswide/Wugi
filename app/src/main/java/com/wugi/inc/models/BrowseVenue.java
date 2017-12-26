package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class BrowseVenue {
    private String documentId;
    private String VenueName;
    private String VenueThumImag;
    private Date createdAt;
    private Date updatedAt;

    public BrowseVenue(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("VenueName"))
            this.VenueName = document.getString("VenueName");
        if (document.contains("VenueThumImag"))
            this.VenueThumImag = document.getString("VenueThumImag");
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

    public String getVenueName() {
        return VenueName;
    }

    public void setVenueName(String venueName) {
        VenueName = venueName;
    }

    public String getVenueThumImag() {
        return VenueThumImag;
    }

    public void setVenueThumImag(String venueThumImag) {
        VenueThumImag = venueThumImag;
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
}
