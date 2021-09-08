package com.comp90018.assignment2.network;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Serializable object of Geo point for transmition
 */
public class MyGeoPoint extends GeoPoint implements Serializable {

    /**
     * Construct a new {@code GeoPoint} using the provided latitude and longitude values.
     *
     * @param latitude  The latitude of this {@code GeoPoint} in the range [-90, 90].
     * @param longitude The longitude of this {@code GeoPoint} in the range [-180, 180].
     */
    public MyGeoPoint(double latitude, double longitude) {
        super(latitude, longitude);
    }
}
