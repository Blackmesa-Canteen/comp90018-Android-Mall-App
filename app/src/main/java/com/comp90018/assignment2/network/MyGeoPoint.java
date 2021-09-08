package com.comp90018.assignment2.network;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Serializable object of Geo point for transmition
 *
 *
 * !!! deprecated, we have made DTOs to be parcelable. !!!
 *
 */

@Deprecated
public class MyGeoPoint extends GeoPoint implements Serializable {

    /**
     * Construct a new {@code GeoPoint} using the provided latitude and longitude values.
     *
     * @param latitude  The latitude of this {@code GeoPoint} in the range [-90, 90].
     * @param longitude The longitude of this {@code GeoPoint} in the range [-180, 180].
     */
    @Deprecated
    public MyGeoPoint(double latitude, double longitude) {
        super(latitude, longitude);
    }
}
