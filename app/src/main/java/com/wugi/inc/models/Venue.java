package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class Venue {
    private String documentId;
    private ArrayList<String> address;
    private GeoPoint geolocation;
    private ArrayList<String> parking;
    private ArrayList<String> recentPhotos;
    private boolean active;
    private String imageThumbURL;
    private String name;
    private String neighborhood;
    private String phoneNumber;
    private String siteURL;
    private String type;
    private Date createdAt;
    private Date updatedAt;
    private boolean servesFood;
    private boolean openLate;
    private boolean deleted;
    private BrowseVenueType browseVenueType;

    public Venue(DocumentSnapshot document){
        this.documentId = document.getId();
        if (document.contains("address"))
            this.address = (ArrayList<String>) document.get("address");
        if (document.contains("geolocation"))
            this.geolocation = document.getGeoPoint("geolocation");
        if (document.contains("parking"))
            this.parking = (ArrayList<String>) document.get("parking");
        if (document.contains("recentPhotos"))
            this.recentPhotos = (ArrayList<String>) document.get("recentPhotos");
        if (document.contains("active"))
            this.active = document.getBoolean("active");
        if (document.contains("imageThumbURL"))
            this.imageThumbURL = document.getString("imageThumbURL");
        if (document.contains("name"))
            this.name = document.getString("name");
        if (document.contains("neighborhood"))
            this.neighborhood = document.getString("neighborhood");
        if (document.contains("phoneNumber"))
            this.phoneNumber = document.getString("phoneNumber");
        if (document.contains("siteURL"))
            this.siteURL = document.getString("siteURL");
        if (document.contains("type"))
            this.type = document.getString("type");
        if (document.contains("createdAt"))
            this.createdAt = document.getDate("createdAt");
        if (document.contains("updatedAt"))
            this.updatedAt = document.getDate("updatedAt");
        if (document.contains("servesFood"))
            this.servesFood = document.getBoolean("servesFood");
        if (document.contains("openLate"))
            this.openLate = document.getBoolean("openLate");
        if (document.contains("deleted"))
            this.deleted = document.getBoolean("deleted");
    }

    public ArrayList<String> getRecentPhotos() {
        return recentPhotos;
    }

    public void setRecentPhotos(ArrayList<String> recentPhotos) {
        this.recentPhotos = recentPhotos;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public ArrayList<String> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<String> address) {
        this.address = address;
    }

    public GeoPoint getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }

    public ArrayList<String> getParking() {
        return parking;
    }

    public void setParking(ArrayList<String> parking) {
        this.parking = parking;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImageThumbURL() {
        return imageThumbURL;
    }

    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isServesFood() {
        return servesFood;
    }

    public void setServesFood(boolean servesFood) {
        this.servesFood = servesFood;
    }

    public boolean isOpenLate() {
        return openLate;
    }

    public void setOpenLate(boolean openLate) {
        this.openLate = openLate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public BrowseVenueType getBrowseVenueType() {
        return browseVenueType;
    }

    public void setBrowseVenueType(BrowseVenueType browseVenueType) {
        this.browseVenueType = browseVenueType;
    }
}
