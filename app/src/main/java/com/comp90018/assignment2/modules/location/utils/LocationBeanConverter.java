package com.comp90018.assignment2.modules.location.utils;

import android.util.Log;

import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.utils.Calculator;
import com.comp90018.assignment2.utils.Constants;

import java.util.Map;

public class LocationBeanConverter {

    private static final String TAG = "LocBConv[dev]";

    public static void convertLocationToBdMapLocation(LocationBean targetLocationBean) {
        double lat = 0, lon = 0;

        if (targetLocationBean != null) {

            // if out of china, no need to convert coordinate
            /*
             * The latitude and longitude obtained in the location outside China must be WGS84.
             */
            if (Calculator.isOutChina(targetLocationBean.getLatitude(), targetLocationBean.getLongitude())) {
                return;
            }

            // if in China, need to decrypt coordinate to get high accuracy
            if (targetLocationBean.getCoordinateSystemType() == Constants.WGS84) {

                // change WGS84 default coordinate to GCJ02
                Map<String, Double> gcj02Coordinate = Calculator.transform(targetLocationBean.getLatitude(), targetLocationBean.getLongitude());

                double gcjLat = 0, gcjLon = 0;

                if (gcj02Coordinate.get("lat") != null && gcj02Coordinate.get("lon") != null) {
                    gcjLat = gcj02Coordinate.get("lat");
                    gcjLon = gcj02Coordinate.get("lon");

                    // change GCJ02 to BD09LL for map showing
                    Map<String, Double> bd09llCoordinate = Calculator.marsTobaidu(gcjLat, gcjLon);
                    if (bd09llCoordinate.get("lat") != null && bd09llCoordinate.get("lon") != null) {
                        lat = bd09llCoordinate.get("lat");
                        lon = bd09llCoordinate.get("lon");

                        // convert bean
                        targetLocationBean.setCoordinateSystemType(Constants.BD09LL);
                        targetLocationBean.setLatitude(lat);
                        targetLocationBean.setLongitude(lon);
                    } else {
                        Log.w(TAG, "Can't find target location");
                    }
                } else {
                    // show warn
                    Log.w(TAG, "Can't find target location");
                }

            } else if (targetLocationBean.getCoordinateSystemType() == Constants.GCJ02) {
                Map<String, Double> bd09llCoordinate = Calculator.marsTobaidu(targetLocationBean.getLatitude(), targetLocationBean.getLongitude());
                if (bd09llCoordinate.get("lat") != null && bd09llCoordinate.get("lon") != null) {
                    lat = bd09llCoordinate.get("lat");
                    lon = bd09llCoordinate.get("lon");

                    // convert bean
                    targetLocationBean.setCoordinateSystemType(Constants.BD09LL);
                    targetLocationBean.setLatitude(lat);
                    targetLocationBean.setLongitude(lon);
                } else {
                    Log.w(TAG, "Can't find target location");
                }
            }
        }
    }
}
