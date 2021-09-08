package com.comp90018.assignment2.modules.search.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivitySearchResultBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.search.adapter.SearchResultRvAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * get List of productDTO from bundle and show results
 * @author xiaotian
 */
public class SearchResultActivity extends AppCompatActivity {

    private ActivitySearchResultBinding binding;

    /** This list will be given by bundle extra from the other activity */
    List<ProductDTO> productDTOList;

    private RecyclerView recyclerView;
    private SearchResultRvAdapter adapter;
    private TextView textNoResult;

    FirebaseFirestore db;
    private final static String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init db
        db = FirebaseFirestore.getInstance();

        // set up view binding
        // init view binding
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // setup loading dialog
        // show process dialog
        ProgressDialog progressDialog = new ProgressDialog(SearchResultActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");

        // show loading dialog
        progressDialog.show();

        // back logic
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // search logic
        binding.btnGoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSearchIntent = new Intent(SearchResultActivity.this, SearchProductActivity.class);
                startActivity(goSearchIntent);
            }
        });

        // attach recycler view
        recyclerView = binding.rvSearchResults;

        // attach no result alert view
        textNoResult = binding.textNoResult;

        // retrive bundle data from intent
        Intent intent = getIntent();
        productDTOList = intent.getParcelableArrayListExtra("productDTOList");

        if (productDTOList != null && productDTOList.size() > 0) {
            textNoResult.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            processData(productDTOList);

            // dismiss loding dialog
            progressDialog.dismiss();

        } else if (productDTOList != null && productDTOList.size() == 0) {
            // show empty result alert
            recyclerView.setVisibility(View.GONE);
            textNoResult.setVisibility(View.VISIBLE);

            // dismiss loding dialog
            progressDialog.dismiss();

        } else {
            // if the incoming list is null, for debug use
            textNoResult.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            db.collection(Constants.PRODUCT_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<ProductDTO> productDTOList = new ArrayList<>();
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            productDTOList.add(document.toObject(ProductDTO.class));
                        }

                        processData(productDTOList);
                        // dismiss loding dialog
                        progressDialog.dismiss();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                    }
                }
            });
        }


    }

    /**
     * setup adapter, attach data to view.
     *
     * @param productDTOList
     */
    private void processData(List<ProductDTO> productDTOList) {


        adapter = new SearchResultRvAdapter(this, productDTOList);
        recyclerView.setAdapter(adapter);

        // 2 columns grid
        GridLayoutManager gvManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gvManager);

        // get user info from these DTOs, to show user info in the items,
        // every time finished query, refresh adapter
        for (int index = 0; index < productDTOList.size(); index++) {

            ProductDTO productDTO =  productDTOList.get(index);
            int finalIndex = index;
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