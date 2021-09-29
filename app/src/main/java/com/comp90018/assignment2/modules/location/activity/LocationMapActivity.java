package com.comp90018.assignment2.modules.location.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baidu.mapapi.map.MapView;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.utils.LocationHelper;
import com.comp90018.assignment2.modules.location.utils.OnGotLocationCallback;
import com.comp90018.assignment2.utils.Calculator;
import com.comp90018.assignment2.utils.Constants;

import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

/**
 *
 * Activity to show target point on the map.
 *
 * @author xiaotian li
 */
public class LocationMapActivity extends AppCompatActivity {

    private MapView mapView;

    private LocationBean targetLocationBean;

    private PromptDialog dialog;

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        dialog = new PromptDialog(this);
        mapView = (MapView) findViewById(R.id.map_view);
        locationHelper = new LocationHelper(this);

        // get current user location
        locationHelper.getCurrentLocation(new OnGotLocationCallback() {
            @Override
            public void gotLocationCallback(LocationBean locationBean) {
                // convert point
                convertLocationToBdMapLocation(locationBean);

                // point point to the map


            }
        });

        // get input coordinate
        targetLocationBean = (LocationBean) getIntent().getSerializableExtra(Constants.DATA_A);
        // convert location for map showing
        convertLocationToBdMapLocation(targetLocationBean);
    }

    private void convertLocationToBdMapLocation(LocationBean targetLocationBean) {
        double lat = 0, lon = 0;
        if (targetLocationBean != null) {
            if (targetLocationBean.getCoordinateSystemType() == Constants.WGS84) {
                // change WGS84 default coordinate to GCJ02
                // try to maintain high accuracy in China.
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
                        dialog.showWarn("Can't find target location");
                    }
                } else {
                    // show warn
                    dialog.showWarn("Can't find target location");
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
                    dialog.showWarn("Can't find target location");
                }
            }
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