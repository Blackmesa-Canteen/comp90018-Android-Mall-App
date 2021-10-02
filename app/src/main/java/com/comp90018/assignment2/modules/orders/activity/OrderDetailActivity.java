package com.comp90018.assignment2.modules.orders.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityOrderDetailBinding;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import me.leefeng.promptlibrary.PromptDialog;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "[dev]OrderDetail";
    private ActivityOrderDetailBinding binding;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private UserDTO currentUserDTO;

    private PromptDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new PromptDialog(this);


        //get current login user
        if (firebaseAuth.getCurrentUser() != null) {
            String currentUserId = firebaseAuth.getUid();
            db.collection(Constants.USERS_COLLECTION)
                    .document(currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                currentUserDTO = document.toObject(UserDTO.class);
                            } else {
                                Log.w(TAG, "current user info db connection failed");
                            }
                        }
                    });

            // handle update info
            db.collection(Constants.USERS_COLLECTION)
                    .document(currentUserId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                currentUserDTO = snapshot.toObject(UserDTO.class);
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });
        }

        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // retrieve bundle data from intent li xiaotian
        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");
        switch (productDTO.getStatus()) {
            case Constants.HEAVILY_USED:
                binding.orderDetailOrderStatus.setText("Status: Heavily used");
                break;
            case Constants.WELL_USED:
                binding.orderDetailOrderStatus.setText("Status: Well used");
                break;
            case Constants.AVERAGE_CONDITION:
                binding.orderDetailOrderStatus.setText("Status: Average");
                break;
            case Constants.SLIGHTLY_USED:
                binding.orderDetailOrderStatus.setText("Status: Slightly used");
                break;
            case Constants.EXCELLENT:
                binding.orderDetailOrderStatus.setText("Status: Excellent");
                break;
            default:
                binding.orderDetailOrderStatus.setText("Status: Average");
                break;
        }















        binding.orderDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}