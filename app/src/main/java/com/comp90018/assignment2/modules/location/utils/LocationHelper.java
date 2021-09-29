package com.comp90018.assignment2.modules.location.utils;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.bean.OpenStreetMapResultBean;
import com.comp90018.assignment2.utils.Constants;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * My location helper
 *
 * @author xiaotian li
 */
public class LocationHelper {

    private static final String TAG = "LocHelper[dev]";

    private final Context context;

    private LocationManager locationManager;

    public LocationHelper(Context context) {
        this.context = context;
        locationManager = (LocationManager)
                context.getSystemService(LOCATION_SERVICE);
    }

    /**
     * get live location listener, don't forget to set up call back
     */
    public void getLiveLocating(OnGotLocationBeanCallback callback) {
        MyLocationListener myLocationListener = new MyLocationListener(context);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            AndPermission.with(context)
                    .runtime()
                    .permission(
                            Permission.ACCESS_COARSE_LOCATION,
                            Permission.ACCESS_FINE_LOCATION,
                            Permission.RECORD_AUDIO,
                            Permission.READ_EXTERNAL_STORAGE,
                            Permission.WRITE_EXTERNAL_STORAGE
                    )
                    .start();
        }
        locationManager.requestLocationUpdates(
                GPS_PROVIDER,
                5000,
                10, myLocationListener);

        // if location changed, update new location
        myLocationListener.setOnGotLocationBeanCallBack(callback);
    }

    /**
     * get current known location for once.
     * @param callback
     */
    public void getCurrentKnownLocationBean(OnGotLocationBeanCallback callback) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            AndPermission.with(context)
                    .runtime()
                    .permission(
                            Permission.ACCESS_COARSE_LOCATION,
                            Permission.ACCESS_FINE_LOCATION,
                            Permission.RECORD_AUDIO,
                            Permission.READ_EXTERNAL_STORAGE,
                            Permission.WRITE_EXTERNAL_STORAGE
                    )
                    .start();
        }
        Location lastKnownLocation = getLastKnownLocation();

        double latitude = lastKnownLocation.getLatitude();
        double longitude = lastKnownLocation.getLongitude();

        Log.d(TAG, "current latitude:" + latitude);
        Log.d(TAG, "current longitude:" + longitude);

        getTextAddressWithCoordinate(callback, latitude, longitude);
    }

    /**
     *
     * @param callback callback
     * @param latitude WGS84
     * @param longitude WGS84
     */
    private void getTextAddressWithCoordinate(OnGotLocationBeanCallback callback, double latitude, double longitude) {
        // set up query URL
        String queryURL = Constants.OPEN_STREET_MAP_APT_ROOT
                + Constants.REVERSE_PARSE_PATH
                + "&lat="
                + latitude
                + "&lon="
                + longitude;

        Log.d(TAG, "URL: " + queryURL);

        // query to OpenStreetMap api to get text address
        Request request = new Request.Builder()
                .url(queryURL)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.w(TAG, "query open street map api failed");
                e.printStackTrace();

                // put result bean to callback
                LocationBean locationBean = LocationBean.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .coordinateSystemType(Constants.WGS84)
                        .textAddress("Target Address").build();

                if (callback != null) {
                    callback.gotLocationCallback(locationBean);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // seal the response into object
                String responseBody = response.body().string();
                OpenStreetMapResultBean resultBean = JSON.parseObject(responseBody, OpenStreetMapResultBean.class);

                // set text address to result bean
                if (resultBean != null
                        && resultBean.getFeatures() != null
                        && resultBean.getFeatures().get(0) != null
                        && resultBean.getFeatures().get(0).getProperties() != null
                        && resultBean.getFeatures().get(0).getProperties().getAddress() != null) {

                    String roadName = resultBean.getFeatures().get(0).getProperties().getAddress().getRoad();
                    String displayName = resultBean.getFeatures().get(0).getProperties().getDisplayName();

                    if (roadName == null || roadName.length() == 0) {
                        // if road name is null, show displayName
                        // put result bean to callback
                        LocationBean locationBean = LocationBean.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .coordinateSystemType(Constants.WGS84)
                                .textAddress(displayName).build();

                        if (callback != null) {
                            callback.gotLocationCallback(locationBean);
                        }
                    } else {
                        // if has road name, show road name
                        // put result bean to callback
                        LocationBean locationBean = LocationBean.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .coordinateSystemType(Constants.WGS84)
                                .textAddress(roadName).build();

                        if (callback != null) {
                            callback.gotLocationCallback(locationBean);
                        }
                    }
                }
            }
        });
    }


    /**
     * robust getLastKnowLocation method to prevent null result
     * @return Location
     */
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                Log.w(TAG,"No permission");
                AndPermission.with(context)
                        .runtime()
                        .permission(
                                Permission.ACCESS_COARSE_LOCATION,
                                Permission.ACCESS_FINE_LOCATION,
                                Permission.RECORD_AUDIO,
                                Permission.READ_EXTERNAL_STORAGE,
                                Permission.WRITE_EXTERNAL_STORAGE
                        )
                        .start();
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        return bestLocation;
    }
}
