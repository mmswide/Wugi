package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class Event {
    private String documentId;
    private String name;
    private String dayOfWeek;
    private Date startDate;
    private Date endDate;
    private String description;
    private String imageURL;

    private Venue venue;
    private String age;
    private ArrayList<String> dressCode;
    private Boolean publish;
    private int feature;
    private String imageThumbURL;
    private String imageFeatureURL;
    private BrowseEvent browseEvent;
    private String browseEventRef;
    private DressCodeType dressCodeType;

    public Event(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("name"))
            this.name = document.getString("name");
        if (document.contains("dayOfWeek"))
            this.dayOfWeek = document.getString("dayOfWeek");
        if (document.contains("startDate"))
            this.startDate = document.getDate("startDate");
        if (document.contains("endDate"))
            this.endDate = document.getDate("endDate");
        if (document.contains("description"))
            this.description = document.getString("description");
        if (document.contains("imageURL"))
            this.imageURL = document.getString("imageURL");
        if (document.contains("age"))
            this.age = document.getString("age");
        if (document.contains("dressCode"))
            this.dressCode = (ArrayList<String>) document.get("dressCode");
        if (document.contains("publish"))
            this.publish = document.getBoolean("publish");
        if (document.contains("feature"))
            this.feature = document.getDouble("feature").intValue();
        if (document.contains("imageThumbURL"))
            this.imageThumbURL = document.getString("imageThumbURL");
        if (document.contains("imageFeatureURL"))
            this.imageFeatureURL = document.getString("imageFeatureURL");
        if (document.contains("browseEvent")) {
            this.browseEventRef = document.getDocumentReference("browseEvent").getId();
        }
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageFeatureURL() {
        return imageFeatureURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public ArrayList<String> getDressCode() {
        return dressCode;
    }

    public void setDressCode(ArrayList<String> dressCode) {
        this.dressCode = dressCode;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public int getFeature() {
        return feature;
    }

    public void setFeature(int feature) {
        this.feature = feature;
    }

    public String getImageThumbURL() {
        return imageThumbURL;
    }

    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }

    public void setImageFeatureURL(String imageFeatureURL) {
        this.imageFeatureURL = imageFeatureURL;
    }
    public BrowseEvent getBrowseEvent() {
        return browseEvent;
    }

    public void setBrowseEvent(BrowseEvent browseEvent) {
        this.browseEvent = browseEvent;
    }

    public String getBrowseEventRef() {
        return browseEventRef;
    }

    public void setBrowseEventRef(String browseEventRef) {
        this.browseEventRef = browseEventRef;
    }

    public DressCodeType getDressCodeType() {
        return dressCodeType;
    }

    public void setDressCodeType(DressCodeType dressCodeType) {
        this.dressCodeType = dressCodeType;
    }
}
