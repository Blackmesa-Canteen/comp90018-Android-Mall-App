package com.comp90018.assignment2.modules.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.home.adapter.HomePageAdapter;
import com.comp90018.assignment2.modules.search.activity.SearchProductActivity;
import com.comp90018.assignment2.utils.Constants;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * @author Ka Hou Hong
 */
public class HomeFragment extends BaseFragment {

    private String TAG = "HomeFragment";
    //private ActivityHomePageBinding binding; // inflate layout
    List<ProductDTO> productDTOList;
    private HomePageAdapter adapter; // adapter
    private TextView textNoResult;
    private RecyclerView recyclerView; // bind rv id
    private ImageView fakeSearchView;
    private NavigationTabStrip viewLabel;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    FirebaseFirestore db;
    FusedLocationProviderClient client;
    String latitude;
    String longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    String user_geohash;


    @SuppressLint("MissingPermission")
    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.home_fragment, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycle);
        fakeSearchView = (ImageView) view.findViewById(R.id.img_fake_search_view);
        viewLabel = (NavigationTabStrip) view.findViewById(R.id.view_label);
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.main_swipe);

        locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);



        // attach search jumping listener
        fakeSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump to real search
                Intent goToSearchActivityIntent = new Intent(activityContext, SearchProductActivity.class);
                startActivity(goToSearchActivityIntent);
            }
        });

        /* https://github.com/Devlight/NavigationTabStrip */
        // setup label view
        viewLabel.setTitles("Recommends", "Intra-city");
        viewLabel.setTabIndex(0, true);



        // setup refresh
        /* https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout */
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new RefreshTask().execute();
            }
        });
        return view;
    }


    /**
     * task for `pull to refresh`
     * https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout
     */
    private class RefreshTask extends AsyncTask<Void, Void, String[]> {
        @SuppressLint("MissingPermission")
        @Override

        protected String[] doInBackground(Void... voids) {
            loadData(); // shuffle

            return new String[0]; //can't convert the type to void, so have to return String[]
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }



    // Get User Location END
    @SuppressLint("MissingPermission")
    @Override
    public void loadData() {
        db = FirebaseFirestore.getInstance();
        // 从数据库获取全部商品信息


        db.collection(Constants.PRODUCT_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductDTO> productDTOList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    productDTOList.add(document.toObject(ProductDTO.class));
                }
                // shuffle the list
                Collections.shuffle(productDTOList);
                processData(productDTOList);
            } else {
                Log.d(TAG, "Error getting documents", task.getException());
            }
        });
    }

    private void processData(List<ProductDTO> productDTOList) {

        // 2 columns grid
        GridLayoutManager gvManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gvManager);
        recyclerView.setHasFixedSize(true);
        adapter = new HomePageAdapter(activityContext, productDTOList);
        recyclerView.setAdapter(adapter);


        for (int i = 0; i < productDTOList.size(); i++) {
            ProductDTO productDTO = productDTOList.get(i);
            int finalIndex = i;
            productDTO.getOwner_ref().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        UserDTO userDTO = document.toObject(UserDTO.class);
                        DocumentReference reference = document.getReference();
                        Log.d(TAG, "get user info: " + userDTO.getEmail());
                        // add to adapter and refresh it
                        adapter.addNewUserDtoInMap(reference, userDTO);
                        adapter.notifyItemChanged(finalIndex);
                    } else {
                        Log.w(TAG, "user info db connection failed");
                    }
                }
            });
        }

    }


    // Get User Location, from :https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            String myLongitude = "Longitude: " + loc.getLongitude();
            String myLatitude = "Latitude: " + loc.getLatitude();
            //user_geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(45, 144));

            final GeoLocation center = new GeoLocation(45, 144);
            final double radiusInM = 50 * 1000;

            // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
            // a separate query for each pair. There can be up to 9 pairs of bounds
            // depending on overlap, but in most cases there are 4.
            List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
            final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            for (GeoQueryBounds b : bounds) {
                Query q = db.collection(Constants.PRODUCT_COLLECTION)
                        .orderBy("geo_hash")
                        .startAt(b.startHash)
                        .endAt(b.endHash);
                tasks.add(q.get());
            }


            // Collect all the query results together into a single list
            Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Task<?>>> t) {
                            List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                            for (Task<QuerySnapshot> task : tasks) {
                                QuerySnapshot snap = task.getResult();
                                System.out.println("snap: "+snap.getDocuments());
                                for (DocumentSnapshot doc : snap.getDocuments()) {
                                    double lat = doc.getDouble("lat");
                                    double lng = doc.getDouble("lng");
                                    // We have to filter out a few false positives due to GeoHash
                                    // accuracy, but most will match
                                    GeoLocation docLocation = new GeoLocation(lat, lng);
                                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                    if (distanceInM <= radiusInM) {
                                        matchingDocs.add(doc);
                                    }
                                }
                            }
                            // matchingDocs contains the results
                            // ...
                            System.out.println("matchingDocs size: "+matchingDocs.size());
                            System.out.println("matchingDocs: "+matchingDocs);
                        }
                    });

        }
    }
}
