package com.comp90018.assignment2.modules.orders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPurchasedPdtListBinding;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.orders.adapter.PurchasedPdtListAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PurchasedPdtListActivity extends AppCompatActivity {

    private static final String TAG = "[dev]ProductDetail";
    private ActivityPurchasedPdtListBinding binding;
    private PurchasedPdtListAdapter purchasedPdtListAdapter;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private UserDTO currentUserDTO;
    private List<OrderDTO> orderDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchasedPdtListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(PurchasedPdtListActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        recyclerView = binding.purchasesList;
        recyclerView.setVisibility(View.VISIBLE);

        binding.purchasesBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db.collection(Constants.ORDERS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<OrderDTO> orderDTOList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        orderDTOList.add(document.toObject(OrderDTO.class));
                    }
                    purchasedPdtListAdapter = new PurchasedPdtListAdapter(PurchasedPdtListActivity.this, orderDTOList);
                    recyclerView.setAdapter(purchasedPdtListAdapter);
                    GridLayoutManager gvManager = new GridLayoutManager(PurchasedPdtListActivity.this, 1);
                    recyclerView.setLayoutManager(gvManager);
//                    processData(orderDTOList);
                    // dismiss loading dialog
                    progressDialog.dismiss();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }
            }
        });


    }
}