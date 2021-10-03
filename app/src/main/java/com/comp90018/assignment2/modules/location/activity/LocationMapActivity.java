package com.comp90018.assignment2.modules.location.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.utils.LocationBeanConverter;
import com.comp90018.assignment2.modules.location.utils.LocationHelper;
import com.comp90018.assignment2.modules.location.utils.OnGotLocationBeanCallback;
import com.comp90018.assignment2.utils.Calculator;
import com.comp90018.assignment2.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

/**
 *
 * Activity to show target point on the map.
 *
 * should get DATA_A: LocationBean;
 * @author xiaotian li
 */
public class LocationMapActivity extends AppCompatActivity {

    private BaiduMap baiduMap;
    private MapView mapView;

    private LocationBean targetLocationBean;

    private PromptDialog dialog;

    private LocationHelper locationHelper;

    private MarkerOptions currentUserMarker;
    private List<Overlay> currentOverLays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        dialog = new PromptDialog(this);
        dialog.setViewAnimDuration(2000);
        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        locationHelper = new LocationHelper(this);
        currentOverLays = new ArrayList<>();

        // set up exit function
        ImageView backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get input coordinate
        targetLocationBean = (LocationBean) getIntent().getSerializableExtra(Constants.DATA_A);

        // get current user location
        locationHelper.getLiveLocating(new OnGotLocationBeanCallback() {
            @Override
            public void gotLocationCallback(LocationBean locationBean) {

                // there should be only one current user marker
                if (currentOverLays.size() != 0) {
                    baiduMap.removeOverLays(currentOverLays);
                    currentOverLays.clear();
                }

                // convert point
                LocationBeanConverter.convertLocationToBdMapLocation(locationBean);

                Log.d("MapActivity[dev]", locationBean.toString());

                LatLng center = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());
                // current user point to the map
                Bundle mBundle = new Bundle();
                mBundle.putString("desc", "This is Your location.");
                currentUserMarker = new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red))
                        .extraInfo(mBundle);

                // add this marker to map
                Overlay marker = baiduMap.addOverlay(currentUserMarker);
                if (marker != null) {
                    marker.setExtraInfo(mBundle);
                    currentOverLays.add(marker);
                }

                if (targetLocationBean == null) {
                    // debug: no targetLocation, zoom to current user location
                    MapStatus mapStatus = new MapStatus.Builder()
                            .target(center)
                            .zoom(18)
                            .build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                    baiduMap.setMapStatus(mMapStatusUpdate);
                }
            }
        });

        if (targetLocationBean != null) {
            // convert location for map showing
            LocationBeanConverter.convertLocationToBdMapLocation(targetLocationBean);
            LatLng center = new LatLng(targetLocationBean.getLatitude(), targetLocationBean.getLongitude());
            // set up marker
            Bundle mBundle = new Bundle();
            mBundle.putString("desc", "This is target location: " + targetLocationBean.getTextAddress());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue))
                    .extraInfo(mBundle);

            // add this marker to map
            Overlay marker = baiduMap.addOverlay(markerOptions);
            if (marker != null) {
                marker.setExtraInfo(mBundle);
            }

            // zoom here
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(center)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mMapStatusUpdate);
        }

        // show different info in different type of marker
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                String desc = bundle.getString("desc");
                dialog.showInfo(desc);
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}