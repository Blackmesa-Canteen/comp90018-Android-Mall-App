package com.comp90018.assignment2.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Calc some math stuff
 * 1. date converter
 * 2. Coordinate converter:
 *
 *  Could be used for convert coordinate in China if we use map API.
 *
 *  Chinese government has encrypted coordinate, if we use locating
 *  service in China, we need do coordinate converting, otherwise there
 *  will be a terrible error in the map.
 *
 *  Can be adapted to many common 3th party Map provider.
 *
 * @author xiaotian li
 */
public class Calculator {

    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private static final double PI = 3.14159265358979324;
    // The projection factor of a satellite ellipsoidal coordinate into a plane
    // map coordinate system.
    // Long radius of earth
    private static final double EARTH_MAJOR_RADIUS = 6378245.0;
    // Eccentricity of the ellipsoid
    private static final double ECCENTRICITY_RATIO = 0.00669342162296594323;

    /**
     * Long time -> convert to date -> convert to String in required format
     * @author xiaotian
     */
    public static String fromLongToDate(String format, Long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(time);
        return sdf.format(date);
    }

    // Common method
    /**
     * Check whether is in china, if not, do not need conver coordinate
     * @param lon double
     * @param lat double
     * @return is out of china?
     *
     */
    private static boolean outOfChina(double lon, double lat) {
        if ((lon < 72.004 || lon > 137.8347) && (lat < 0.8293 || lat > 55.8271)) {
            return true;
        } else {
            return false;
        }
    }

    private static double transformLat(double x, double y, double pi) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y, double pi) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * Baidu coordinates (BD09LL) to Mars coordinates (GCJ02)
     * @Param lat  baidu map latitude
     * @Param lon  baidu map longitude
     */
    public static Map<String, Double> baiduTomars(double lat, double lon) {
        Map<String, Double> mars_point = new HashMap<>();
        mars_point.put("lon", 0D);
        mars_point.put("lat", 0D);

        double x = lon - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        mars_point.put("lon", z * Math.cos(theta));
        mars_point.put("lat", z * Math.sin(theta));
        return mars_point;
    }

    /**
     * Mars coordinates (GCJ02) to Baidu coordinates (BD09LL)
     * @Param gcjLat  Mars coordinates lat
     * @Param gcjLon  Mars coordinates lon
     */
    public static Map<String, Double> marsTobaidu(double gcjLat, double gcjLon) {
        HashMap<String, Double> baidu_point = new HashMap<>();
        baidu_point.put("lon", 0D);
        baidu_point.put("lat", 0D);

        double x = gcjLon;
        double y = gcjLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        baidu_point.put("lon", z * Math.cos(theta) + 0.0065);
        baidu_point.put("lat", z * Math.sin(theta) + 0.006);
        return baidu_point;
    }

    /**
     * @Discription: Mars (GCJ02) to Earth (WGS84)
     * @Param lat  Mars (GCJ02) mar
     * @Param lon  Mars (GCJ02) lon
     */
    public static Map<String, Double> transformGCJ2WGS(double gcjLat, double gcjLon) {
        Map<String, Double> map = delta(gcjLat, gcjLon);
        double lat = map.get("lat");
        double lon = map.get("lon");
        map.get("lon");
        map.put("lat", gcjLat - lat);
        map.put("lon", gcjLon - lon);
        return map;
    }


    private static Map delta(double lat, double lon) {
        double dLat = transformLat(lon - 105.0, lat - 35.0, PI);
        double dLon = transformLon(lon - 105.0, lat - 35.0, PI);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ECCENTRICITY_RATIO * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_MAJOR_RADIUS * (1 - ECCENTRICITY_RATIO)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (EARTH_MAJOR_RADIUS / sqrtMagic * Math.cos(radLat) * PI);

        Map<String, Double> map = new HashMap<>();
        map.put("lat", dLat);
        map.put("lon", dLon);
        return map;
    }

    /**
     * @Discription: Earth coordinate system (WGS-84) to Mars coordinate system (GCJ02)
     * @Param wgLat  earth lat
     * @Param wgLon  earth lon
     */
    public static Map<String, Double> transform(double wgLat, double wgLon) {

        HashMap<String, Double> mars_point = new HashMap<>();
        mars_point.put("lon", 0D);
        mars_point.put("lat", 0D);
        // If it is a coordinate point out of China, the coordinate point passed in is returned directly
        if (outOfChina(wgLon, wgLat)) {
            mars_point.put("lon", wgLon);
            mars_point.put("lat", wgLat);
            return mars_point;
        }
        // If it is a coordinate point in China, the offset is calculated
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0, PI);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0, PI);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ECCENTRICITY_RATIO * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_MAJOR_RADIUS * (1 - ECCENTRICITY_RATIO)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (EARTH_MAJOR_RADIUS / sqrtMagic * Math.cos(radLat) * PI);
        mars_point.put("lon", wgLon + dLon);
        mars_point.put("lat", wgLat + dLat);

        return mars_point;
    }

    /**
     * read WGS-84 coordinate, check whether is in China or not
     * @param wgLat lat
     * @param wgLon lon
     * @return is in China?
     */
    public static boolean isOutChina(double wgLat, double wgLon) {
        return outOfChina(wgLon, wgLat);
    }
}
