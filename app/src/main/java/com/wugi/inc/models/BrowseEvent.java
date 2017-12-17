package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class BrowseEvent {
    private String documentId;
    private String EventName;
    private String BrowseEventImg;
    private Date createdAt;
    private Date updatedAt;

    public BrowseEvent(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("EventName"))
            this.EventName = document.getString("EventName");
        if (document.contains("BrowseEventImg"))
            this.BrowseEventImg = document.getString("BrowseEventImg");
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

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getBrowseEventImg() {
        return BrowseEventImg;
    }

    public void setBrowseEventImg(String browseEventImg) {
        BrowseEventImg = browseEventImg;
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
