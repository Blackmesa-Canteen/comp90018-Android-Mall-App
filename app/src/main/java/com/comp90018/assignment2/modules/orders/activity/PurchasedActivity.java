package com.comp90018.assignment2.modules.orders.activity;

import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import me.leefeng.promptlibrary.PromptDialog;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPurchasedPdtListBinding;
import com.comp90018.assignment2.databinding.ItemPurchasedPdtListBinding;
import com.comp90018.assignment2.databinding.ItemSoldPdtListBinding;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.home.fragment.HomeFragment;
import com.comp90018.assignment2.modules.messages.activity.ChatActivity;
import com.comp90018.assignment2.modules.orders.adapter.PurchasedAdapter;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.modules.users.me.activity.EditProfileActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PurchasedActivity extends AppCompatActivity {

    private static final String TAG = "Purchased[dev]";
    private ActivityPurchasedPdtListBinding list_binding;
    private ItemPurchasedPdtListBinding item_binding;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private TextView textNoResult;
    private UserDTO currentUserDTO;
    private PurchasedAdapter adapter;
    private PromptDialog dialog;
    private UserDTO currentUserDto = null;
    private UserDTO buyerDTO = null;
    private List<OrderDTO> orderDTOList;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_binding = ActivityPurchasedPdtListBinding.inflate(getLayoutInflater());
        item_binding = ItemPurchasedPdtListBinding.inflate(getLayoutInflater());
        setContentView(list_binding.getRoot());

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) list_binding.purchasedSwipe;

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(PurchasedActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        recyclerView = list_binding.purchasesList;


        list_binding.purchasesBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // attach no result alert view

        Intent intent = getIntent();
        orderDTOList = intent.getParcelableArrayListExtra("orderDTOList");

        if(orderDTOList != null && orderDTOList.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            processData(orderDTOList);
            progressDialog.dismiss();
        } else if (orderDTOList != null && orderDTOList.size() == 0){
            // show empty result alert
            recyclerView.setVisibility(View.GONE);
            progressDialog.dismiss();
        } else {
            // if the incoming list is null, for debug use
            recyclerView.setVisibility(View.VISIBLE);
            db.collection(Constants.ORDERS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<OrderDTO> orderDTOList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderDTOList.add(document.toObject(OrderDTO.class));
                        }
                        processData(orderDTOList);
                        progressDialog.dismiss();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new RefreshTask().execute();
            }
        });
    }

    private class RefreshTask extends AsyncTask<Void, Void, String[]> {
        @SuppressLint("MissingPermission")
        @Override
        protected String[] doInBackground(Void... voids) {
            finish();
            //Intent intent = new Intent(getApplicationContext(), PurchasedActivity.class);
            startActivity(getIntent());
            return new String[0]; //can't convert the type to void, so have to return String[]
        }
        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }


    private void processData(List<OrderDTO> orderDTOList) {
        List<OrderDTO> publishedOrderDTOList = new ArrayList<>();

        for (OrderDTO orderDTO : orderDTOList) {
            DocumentReference buyerDocReference = orderDTO.getBuyer_ref();
            DocumentReference currentUserReference = db.collection(USERS_COLLECTION).document(firebaseAuth.getCurrentUser().getUid());
            String id1 = buyerDocReference.getId();
            String id2 = currentUserReference.getId();
            if (id1.equals(id2)) {
                publishedOrderDTOList.add(orderDTO);
            }
        }

        adapter = new PurchasedAdapter(this, publishedOrderDTOList);
        recyclerView.setAdapter(adapter);

        // 1 columns grid
        GridLayoutManager gvManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gvManager);

        for (int index = 0; index < publishedOrderDTOList.size(); index++) {
            OrderDTO orderDTO = publishedOrderDTOList.get(index);
            int finalIndex = index;
            orderDTO.getSeller_ref().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
            orderDTO.getBuyer_ref().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
            orderDTO.getProduct_ref().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        ProductDTO productDTO = document.toObject(ProductDTO.class);
                        DocumentReference reference = document.getReference();
                        //Log.d(TAG, "get product info: " + productDTO.getId());
                        // add to adapter and refresh it
                        adapter.addNewProductDtoInMap(reference, productDTO);
                        adapter.notifyItemChanged(finalIndex);
                    } else {
                        Log.w(TAG, "user info db connection failed");
                    }
                }
            });


        }
    }
}