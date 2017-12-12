package com.wugi.inc.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by storm on 12/11/2017.
 */

public class Venue {
    private String documentId;
    private ArrayList<String> address;
//    private GeoPoint geolocation;
    private ArrayList<String> parking;
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
}
