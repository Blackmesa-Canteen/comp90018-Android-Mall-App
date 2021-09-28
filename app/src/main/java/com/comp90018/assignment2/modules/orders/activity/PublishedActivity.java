package com.comp90018.assignment2.modules.orders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPublishedBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.orders.adapter.RvPublishedProductAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * published activity
 *
 * @author xiaotian li
 */
public class PublishedActivity extends AppCompatActivity {

    private final static String TAG = "Published[dev]";
    private ActivityPublishedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    List<ProductDTO> productDTOList;

    private PromptDialog dialog;
    private RvPublishedProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublishedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // set return back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set up db, auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new PromptDialog(this);

        // set up data structure
        productDTOList = new ArrayList<>();

        loadData();
    }

    private void loadData() {
        // get current User info, if not login, exit to prevent crash
        if (auth.getCurrentUser() == null) {
            Toast.makeText(App.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
            finish();
        }

        String currentUserId = auth.getCurrentUser().getUid();
        DocumentReference currentUserReference = db.collection(Constants.USERS_COLLECTION).document(currentUserId);

        dialog.showLoading("Loading");
        // init: query products belong to current user, put it in the List
        db.collection(Constants.PRODUCT_COLLECTION)
                .whereIn("status", Arrays.asList(Constants.PUBLISHED, Constants.SOLD_OUT, Constants.UNDERCARRIAGE))
                .whereEqualTo("owner_ref", currentUserReference)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productDTOList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductDTO productDTO = document.toObject(ProductDTO.class);
                                productDTOList.add(productDTO);
                            }

                            // init adapter
                            adapter = new RvPublishedProductAdapter(
                                    R.layout.item_published_rv_item,
                                    productDTOList,
                                    PublishedActivity.this);

                            // query for current userDTO
                            // then attach it to the adapter
                            currentUserReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot result = task.getResult();
                                        UserDTO currentUserDTO = result.toObject(UserDTO.class);
                                        adapter.setCurrentUserDTO(currentUserDTO);
                                    } else {
                                        Log.w(TAG, "get current user dto failed: " + task.getException());
                                    }
                                }
                            });

                            // attach adapter to rv
                            binding.rvPublished.setAdapter(adapter);
                            // set layout
                            binding.rvPublished.setLayoutManager(new LinearLayoutManager(PublishedActivity.this));
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Log.w(TAG, "Error getting documents: ", task.getException());
                            dialog.showWarn("Db connection failed, please try again later");
                        }
                    }
                });
    }

    /**
     * used for data updating
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}