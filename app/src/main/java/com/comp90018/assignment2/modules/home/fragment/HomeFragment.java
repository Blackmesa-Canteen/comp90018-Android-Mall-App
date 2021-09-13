package com.comp90018.assignment2.modules.home.fragment;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.home.adapter.HomePageAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author you
 */
public class HomeFragment extends BaseFragment {

    private String TAG = "HomeFragment";
    //private ActivityHomePageBinding binding; // inflate layout
    List<ProductDTO> productDTOList;
    private HomePageAdapter adapter; // adapter
    private TextView textNoResult;
    private RecyclerView recyclerView; // bind rv id

    FirebaseFirestore db;




    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.home_fragment, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycle);

        return view;
    }

    @Override
    public void loadData() {
        db = FirebaseFirestore.getInstance();
        // 从数据库获取全部商品信息

        db.collection(Constants.PRODUCT_COLLECTION).get().addOnCompleteListener(task ->  {

            if (task.isSuccessful()) {
                List<ProductDTO> productDTOList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    productDTOList.add(document.toObject(ProductDTO.class));
                }
                processData(productDTOList);
            } else{
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

    }
}
