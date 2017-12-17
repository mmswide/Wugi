package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class BrowseVenueType {
    private String documentId;
    private String VenueTypeName;
    private String VenueTypeThumImg;
    private Date createdAt;
    private Date updatedAt;

    public BrowseVenueType(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("VenueTypeName"))
            this.VenueTypeName = document.getString("VenueTypeName");
        if (document.contains("VenueTypeThumImg"))
            this.VenueTypeThumImg = document.getString("VenueTypeThumImg");
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

    public String getVenueTypeName() {
        return VenueTypeName;
    }

    public void setVenueTypeName(String venueTypeName) {
        VenueTypeName = venueTypeName;
    }

    public String getVenueTypeThumImg() {
        return VenueTypeThumImg;
    }

    public void setVenueTypeThumImg(String venueTypeThumImg) {
        VenueTypeThumImg = venueTypeThumImg;
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
