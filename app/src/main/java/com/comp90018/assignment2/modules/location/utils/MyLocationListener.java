package com.comp90018.assignment2.modules.location.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.bean.OpenStreetMapResultBean;
import com.comp90018.assignment2.utils.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * get live location with android GPS
 * <p>
 * can get coordinate
 * can add text address
 *
 * @author xiaotian li
 */
public class MyLocationListener implements LocationListener {

    private final Context context;

    private static String TAG = "Location[dev]";

    private OnGotLocationBeanCallback onGotLocationBeanCallback;

    private OkHttpClient okHttpClient;

    private final static Handler mHandler = new Handler(Looper.getMainLooper());

    public MyLocationListener(Context context) {
        this.context = context;
        this.okHttpClient = new OkHttpClient();
    }

    @Override
    public void onLocationChanged(Location loc) {
        double locLatitude = loc.getLatitude();
        double locLongitude = loc.getLongitude();
//        Toast.makeText(
//                context,
//                "Location changed: Lat: " + locLatitude + " Lng: "
//                        + locLongitude, Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + locLongitude;
        Log.d(TAG, longitude);
        String latitude = "Latitude: " + locLatitude;
        Log.d(TAG, latitude);

        // get text address from coordinates
        String textAddress = null;

        getTextAddressWithCoordinate(locLatitude, locLongitude);
    }

    /**
     * get text from coordinate
     * @param locLatitude WGS84
     * @param locLongitude WGS84
     */
    private void getTextAddressWithCoordinate(double locLatitude, double locLongitude) {
        // set up query URL
        String queryURL = Constants.OPEN_STREET_MAP_APT_ROOT
                + Constants.REVERSE_PARSE_PATH
                + "&lat="
                + locLatitude
                + "&lon="
                + locLongitude;

        Log.d(TAG, "URL: " + queryURL);

        // query to OpenStreetMap api to get text address
        Request request = new Request.Builder()
                .url(queryURL)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.w(TAG, "query open street map api failed");
                e.printStackTrace();

                // put result bean to callback
                LocationBean locationBean = LocationBean.builder()
                        .latitude(locLatitude)
                        .longitude(locLongitude)
                        .coordinateSystemType(Constants.WGS84)
                        .textAddress("Target Address").build();

                if (onGotLocationBeanCallback != null) {

                    // update UI in main thread
                    synchronized (mHandler) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGotLocationBeanCallback.gotLocationCallback(locationBean);
                            }
                        });
                    }
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // seal the response into object
                String responseBody = response.body().string();
                OpenStreetMapResultBean resultBean = JSON.parseObject(responseBody, OpenStreetMapResultBean.class);

                // set text address to result bean
                // set text address to result bean
                // get detailed information
                if (resultBean != null
                        && resultBean.getFeatures() != null
                        && resultBean.getFeatures().get(0) != null
                        && resultBean.getFeatures().get(0).getProperties() != null
                        && resultBean.getFeatures().get(0).getProperties().getAddress() != null) {

                    LocationBean locationBean = LocationBean.builder()
                            .latitude(locLatitude)
                            .longitude(locLongitude)
                            .coordinateSystemType(Constants.WGS84)
                            .textAddress(resultBean.getFeatures().get(0).getProperties().getDisplayName())
                            .gotDetailedAddressInfo(true)
                            .road(resultBean.getFeatures().get(0).getProperties().getAddress().getRoad())
                            .suburb(resultBean.getFeatures().get(0).getProperties().getAddress().getSuburb())
                            .city(resultBean.getFeatures().get(0).getProperties().getAddress().getCity())
                            .state(resultBean.getFeatures().get(0).getProperties().getAddress().getState())
                            .postcode(resultBean.getFeatures().get(0).getProperties().getAddress().getPostcode())
                            .country(resultBean.getFeatures().get(0).getProperties().getAddress().getCountry())
                            .countryCode(resultBean.getFeatures().get(0).getProperties().getAddress().getCountryCode())
                            .build();

                    if (onGotLocationBeanCallback != null) {
                        synchronized (mHandler) {
                            // update UI in main thread
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGotLocationBeanCallback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }
                } else if (resultBean != null
                        && resultBean.getFeatures() != null
                        && resultBean.getFeatures().get(0) != null
                        && resultBean.getFeatures().get(0).getProperties() != null){

                    // if missing some detailed information
                    LocationBean locationBean = LocationBean.builder()
                            .latitude(locLatitude)
                            .longitude(locLongitude)
                            .coordinateSystemType(Constants.WGS84)
                            .textAddress(resultBean.getFeatures().get(0).getProperties().getDisplayName())
                            .gotDetailedAddressInfo(false)
                            .build();

                    if (onGotLocationBeanCallback != null) {
                        synchronized (mHandler) {
                            // update UI in main thread
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGotLocationBeanCallback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }

                } else {
                    // if missing many detailed information
                    LocationBean locationBean = LocationBean.builder()
                            .latitude(locLatitude)
                            .longitude(locLongitude)
                            .coordinateSystemType(Constants.WGS84)
                            .textAddress("Address")
                            .gotDetailedAddressInfo(false)
                            .build();

                    if (onGotLocationBeanCallback != null) {
                        synchronized (mHandler) {
                            // update UI in main thread
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGotLocationBeanCallback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * set call back
     *
     * @param onGotLocationBeanCallback
     */
    public void setOnGotLocationBeanCallBack(OnGotLocationBeanCallback onGotLocationBeanCallback) {
        this.onGotLocationBeanCallback = onGotLocationBeanCallback;
    }
}
