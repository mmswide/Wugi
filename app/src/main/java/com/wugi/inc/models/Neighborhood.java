package com.wugi.inc.models;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by storm on 1/11/2018.
 */

public class Neighborhood {
    private String documentID;
    private String name;

    public Neighborhood(DocumentSnapshot document){
        this.documentID = document.getId();
        if (document.contains("name"))
            this.name = document.getString("name");
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
