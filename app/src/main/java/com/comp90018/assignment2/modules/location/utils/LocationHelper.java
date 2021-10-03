package com.comp90018.assignment2.modules.location.utils;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.comp90018.assignment2.App;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.bean.OpenStreetMapResultBean;
import com.comp90018.assignment2.utils.Constants;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
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

    private final static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * static Tool:
     * <p>
     * Based on WGS84 coordinate, get text description
     *
     * @param latitude  WGS84
     * @param longitude WGS84
     * @param callback  callback: if finished query, locationBean can be called in the call back method.
     * @author xiaotian li
     */
    public static void getTextAddressWithCoordinate(double latitude, double longitude, OnGotLocationBeanCallback callback) {
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
                    // update UI in main thread
                    synchronized (mHandler) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.gotLocationCallback(locationBean);
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
                // get detailed information
                if (resultBean != null
                        && resultBean.getFeatures() != null
                        && resultBean.getFeatures().get(0) != null
                        && resultBean.getFeatures().get(0).getProperties() != null
                        && resultBean.getFeatures().get(0).getProperties().getAddress() != null) {

                    LocationBean locationBean = LocationBean.builder()
                            .latitude(latitude)
                            .longitude(longitude)
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

                    if (callback != null) {
                        // update UI in main thread
                        synchronized (mHandler) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }
                } else if (resultBean != null
                        && resultBean.getFeatures() != null
                        && resultBean.getFeatures().get(0) != null
                        && resultBean.getFeatures().get(0).getProperties() != null) {

                    // if missing some detailed information
                    LocationBean locationBean = LocationBean.builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .coordinateSystemType(Constants.WGS84)
                            .textAddress(resultBean.getFeatures().get(0).getProperties().getDisplayName())
                            .gotDetailedAddressInfo(false)
                            .build();

                    if (callback != null) {
                        // update UI in main thread
                        synchronized (mHandler) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }

                } else {
                    // if missing many detailed information
                    LocationBean locationBean = LocationBean.builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .coordinateSystemType(Constants.WGS84)
                            .textAddress("Address")
                            .gotDetailedAddressInfo(false)
                            .build();

                    if (callback != null) {
                        // update UI in main thread
                        synchronized (mHandler) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.gotLocationCallback(locationBean);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

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

        // request permission
        AndPermission.with(context)
                .runtime()
                .permission(
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION
                )
                .onGranted(new Action<List<String>>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onAction(List<String> data) {
                        locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                5000,
                                10, myLocationListener);

                        // if location changed, update new location
                        myLocationListener.setOnGotLocationBeanCallBack(callback);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(App.getContext(), "Please grand permission", Toast.LENGTH_SHORT).show();
                    }
                })
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                        List<String> permissionNames = Permission.transformText(context, data);
                        String message = "please grand following permissions:\n" + permissionNames;

                        new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setTitle("Reminder")
                                .setMessage(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        executor.execute();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .start();
    }

    /**
     * get current known location for once.
     *
     * @param callback
     */
    public void getCurrentKnownLocationBean(OnGotLocationBeanCallback callback) {

        // request permission
        AndPermission.with(context)
                .runtime()
                .permission(
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION
                )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Location lastKnownLocation = getLastKnownLocation();

                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();

                        Log.d(TAG, "current latitude:" + latitude);
                        Log.d(TAG, "current longitude:" + longitude);

                        getTextAddressWithCoordinate(latitude, longitude, callback);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(App.getContext(), "please grant permission", Toast.LENGTH_SHORT).show();
                    }
                })
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                        List<String> permissionNames = Permission.transformText(context, data);
                        String message = "please grand following permissions:\n" + permissionNames;

                        new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setTitle("Reminder")
                                .setMessage(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        executor.execute();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .start();


    }

    /**
     * robust getLastKnowLocation method to prevent null result
     *
     * @return Location
     */
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                Log.w(TAG, "No permission");
                AndPermission.with(context)
                        .runtime()
                        .permission(
                                Permission.ACCESS_COARSE_LOCATION,
                                Permission.ACCESS_FINE_LOCATION
                        )
                        .start();
                Toast.makeText(App.getContext(), "Please grant permission.", Toast.LENGTH_SHORT).show();
                return null;
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
