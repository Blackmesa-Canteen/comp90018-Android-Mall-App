package com.comp90018.assignment2.modules.product.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.product.ProductDetailAdapter;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "[dev]ProductDetail";
    private ActivityProductDetailBinding binding;
    private FirebaseStorage storage;
    private ProductDetailAdapter productDetailAdapter;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private UserDTO currentUserDTO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // query current login user
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
        }

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // retrieve bundle data from intent li xiaotian
        Intent intent = getIntent();

        recyclerView = binding.lvPdtDetailImages;
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");
        productDetailAdapter = new ProductDetailAdapter(this, productDTO.getImage_address());

        recyclerView.setAdapter(productDetailAdapter);
        GridLayoutManager gvManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gvManager);

        binding.pdtDetailUserNickname.setText(userDTO.getNickname());
        binding.itemRating.setRating(userDTO.getStar_number().floatValue());
        binding.tvPdtUserLocation.setText("From: " + productDTO.getLocation_text());
        binding.tvPdtPublishTime.setText(productDTO.getPublish_time().toDate().toString());
        binding.tvPdtDetailPrice.setText("$" + productDTO.getPrice().toString());
        binding.tvPdtDetailDescription.setText(productDTO.getDescription());
        binding.tvPdtDetailBrand.setText("Brand: " + productDTO.getBrand());
        switch (productDTO.getStatus()) {
            case Constants.HEAVILY_USED:
                binding.tvPdtDetailStatus.setText("Status: Heavily used");
                break;
            case Constants.WELL_USED:
                binding.tvPdtDetailStatus.setText("Status: Well used");
                break;
            case Constants.AVERAGE_CONDITION:
                binding.tvPdtDetailStatus.setText("Status: Average");
                break;
            case Constants.SLIGHTLY_USED:
                binding.tvPdtDetailStatus.setText("Status: Slightly used");
                break;
            case Constants.EXCELLENT:
                binding.tvPdtDetailStatus.setText("Status: Excellent");
                break;
            default:
                binding.tvPdtDetailStatus.setText("Status: Average");
                break;
        }

        StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());

        // query user avatar with the reference
        Glide.with(ProductDetailActivity.this)
                .load(imgReference)
                .into(binding.ivPdtDetailAvatar);


        /** go previous page */
        binding.pdtBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /** go profile page */
        binding.pdtDetailBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Go Profile Page", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        /** add current product to my favourite */
        binding.llFavouriteBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ProductDetailActivity.this,
                            "Please login in.", Toast.LENGTH_SHORT).show();

                } else if (currentUserDTO == null){
                    Toast.makeText(ProductDetailActivity.this,
                            "Handling login status, try again later.", Toast.LENGTH_SHORT).show();
                }else {
                    // check whther the item is liked or not
                    boolean isThisProductLiked = false;
                    List<DocumentReference> favorite_refs = currentUserDTO.getFavorite_refs();
                    for (DocumentReference ref : favorite_refs) {
                        if (ref.getId().equals(productDTO.getId())) {
                            isThisProductLiked = true;
                            break;
                        }
                    }

                    if (!isThisProductLiked) {
                        // if the user have not liked the product
                        Toast.makeText(ProductDetailActivity.this,
                                "Added to favourite.", Toast.LENGTH_SHORT).show();

                        // product's like number + 1
                        int prevFavoriteNumber = productDTO.getFavorite_number();
                        productDTO.setFavorite_number(prevFavoriteNumber + 1);
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId())
                                .set(productDTO);
                    } else {
                        Toast.makeText(ProductDetailActivity.this,
                                "You've already liked this product.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        /** comments on current product */
        binding.llChatBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ProductDetailActivity.this,
                            "Please login in.", Toast.LENGTH_SHORT).show();

                } else if (currentUserDTO == null){
                    Toast.makeText(ProductDetailActivity.this,
                            "Handling login status, try again later.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "comments on current product", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        /** want current product */
        binding.pdtDetailBtnWantThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ProductDetailActivity.this,
                            "Please login in.", Toast.LENGTH_SHORT).show();

                } else if (currentUserDTO == null){
                    Toast.makeText(ProductDetailActivity.this,
                            "Handling login status, try again later.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Click Want this", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}