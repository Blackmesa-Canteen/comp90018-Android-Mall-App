package com.comp90018.assignment2.modules.home.fragment;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.modules.home.adapter.HomePageAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * @author you
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


    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.home_fragment, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycle);
        fakeSearchView = (ImageView) view.findViewById(R.id.img_fake_search_view);
        viewLabel = (NavigationTabStrip) view.findViewById(R.id.view_label);
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.main_swipe);

        // attach search jumping listener

        /* https://github.com/Devlight/NavigationTabStrip */
        // setup label view
        viewLabel.setTitles("Recommends", "Intra-city");
        viewLabel.setTabIndex(0, true);

        // setup refresh
        /* https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout */
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                new Task().execute();
            }
        });

        return view;
    }

    /**
     * task for `pull to refresh`
     * https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout
     */
    private class Task extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }

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
                processData(productDTOList);
            } else {
                Log.d(TAG, "Error getting documents", task.getException());
            }
        });
        // get user info from these DTOs, to show user info in the items,
        // every time finished query, refresh adapter
    }

    private void processData(List<ProductDTO> productDTOList) {


        // 2 columns grid
        GridLayoutManager gvManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gvManager);
        recyclerView.setHasFixedSize(true);
        adapter = new HomePageAdapter(activityContext, productDTOList);
        recyclerView.setAdapter(adapter);

        //recyclerView.setVisibility(View.VISIBLE);

        // get user info from these DTOs, to show user info in the items,
        // every time finished query, refresh adapter
        /*
        for (int i=0; i<productDTOList.size(); i++){
            ProductDTO productDTO = productDTOList.get(i);
            int finalIndex = i;
            productDTO.getOwner_ref().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
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

         */
    }
}
