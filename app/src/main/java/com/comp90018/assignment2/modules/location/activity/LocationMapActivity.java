package com.comp90018.assignment2.modules.location.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.utils.LocationBeanConverter;
import com.comp90018.assignment2.modules.location.utils.LocationHelper;
import com.comp90018.assignment2.modules.location.utils.OnGotLocationBeanCallback;
import com.comp90018.assignment2.utils.Calculator;
import com.comp90018.assignment2.utils.Constants;

import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

/**
 *
 * Activity to show target point on the map.
 *
 * @author xiaotian li
 */
public class LocationMapActivity extends AppCompatActivity {

    private BaiduMap baiduMap;
    private MapView mapView;

    private LocationBean targetLocationBean;

    private PromptDialog dialog;

    private LocationHelper locationHelper;

    private MarkerOptions currentUserMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        dialog = new PromptDialog(this);
        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        locationHelper = new LocationHelper(this);

        // get input coordinate
        targetLocationBean = (LocationBean) getIntent().getSerializableExtra(Constants.DATA_A);

        // get current user location
        locationHelper.getLiveLocating(new OnGotLocationBeanCallback() {
            @Override
            public void gotLocationCallback(LocationBean locationBean) {
                // convert point
                LocationBeanConverter.convertLocationToBdMapLocation(locationBean);

                LatLng center = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());
                // current user point to the map
                currentUserMarker = new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));

                // add this marker to map
                baiduMap.addOverlay(currentUserMarker);

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
            // current user point to the map
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

            // add this marker to map
            baiduMap.addOverlay(markerOptions);

            // zoom here
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(center)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mMapStatusUpdate);
        }
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