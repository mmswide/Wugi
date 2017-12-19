package com.wugi.inc.models;

import android.support.v4.app.Fragment;

import com.wugi.inc.fragments.BrowseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BrowseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public enum Type {
    EVENT_TYPE  (0),
    VENUE_TYPE  (1),
    TYPE_TYPE   (2)
    ;

    final private int typeCode;

    private Type(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }

}
