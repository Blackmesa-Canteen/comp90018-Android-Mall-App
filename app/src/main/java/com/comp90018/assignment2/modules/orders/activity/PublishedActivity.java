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
    private UserDTO currentUserDTO;
    List<ProductDTO> productDTOList;

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

        // get current User info
        if (auth.getCurrentUser() == null) {
            Toast.makeText(App.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // current user info
        String currentUserId = auth.getCurrentUser().getUid();
        DocumentReference userReference = db.collection(Constants.USERS_COLLECTION).document(currentUserId);

        // query current user, if it is needed
        db.collection(Constants.USERS_COLLECTION)
                .document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            currentUserDTO = snapshot.toObject(UserDTO.class);
                            Log.d(TAG, "db get current user: " + currentUserDTO.getEmail());
                        } else {
                            Log.w(TAG, "db get current user failed... " + task.getException() );
                            Toast.makeText(App.getContext(), "Can't get user information, please try again.", Toast.LENGTH_SHORT).show();
                            // to prevent crash, if db failed, exit this activity
                            finish();
                        }
                    }
                });

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

                            // TODO: refresh adapter;
                        } else {
                            Log.w(TAG, "Error getting documents: ", task.getException());
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