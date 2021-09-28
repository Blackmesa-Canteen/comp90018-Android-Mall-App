package com.comp90018.assignment2.modules.orders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.databinding.ActivityPublishedBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
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
        DocumentReference userReference = db.collection(Constants.USERS_COLLECTION).document(currentUserId);

        dialog.showLoading("Loading");
        // init: query products belong to current user, put it in the List
        db.collection(Constants.PRODUCT_COLLECTION)
                .whereNotEqualTo("status", Constants.REMOVED)
                .whereEqualTo("owner_ref", userReference)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductDTO productDTO = document.toObject(ProductDTO.class);
                                productDTOList.add(productDTO);
                            }

                            // attach adapter


                            dialog.dismiss();

                            // TODO: refresh adapter;
                        } else {
                            dialog.dismiss();
                            Log.w(TAG, "Error getting documents: ", task.getException());
                            dialog.showWarn("Db connection failed, please try again later");
                        }
                    }
                });
    }

    /**
     * used for notify adapter.
     * because each action triggered by button will go to a new activity
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        // TODO notify adapter to update when return to this activity
    }
}