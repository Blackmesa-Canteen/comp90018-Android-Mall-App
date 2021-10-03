package com.comp90018.assignment2.modules.product.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.comp90018.assignment2.modules.messages.activity.ChatActivity;
import com.comp90018.assignment2.modules.product.adapter.ProductDetailAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "[dev]ProductDetail";
    private ActivityProductDetailBinding binding;
    private FirebaseStorage storage;
    private ProductDetailAdapter productDetailAdapter;
    private RecyclerView recyclerView;

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
                    dialog.showWarn("Please login in.");

                } else if (currentUserDTO == null){
                    dialog.showWarn("Handling login status, try again later.");
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
                        dialog.showSuccess("Added to favourite.");

                        // product's like number + 1
                        int prevFavoriteNumber = productDTO.getFavorite_number();
                        productDTO.setFavorite_number(prevFavoriteNumber + 1);

                        DocumentReference productDocumentRef = db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId());
                        productDocumentRef.set(productDTO);

                        // add product ref to user's favorate
                        currentUserDTO.getFavorite_refs().add(productDocumentRef);
                        db.collection(Constants.USERS_COLLECTION)
                                .document(currentUserDTO.getId())
                                .set(currentUserDTO);
                    } else {
                        dialog.showInfo("You've already liked this product.");
                    }
                }
            }
        });

        /** chat with product owner */
        binding.llChatBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() == null) {
                    dialog.showWarn("Please login in.");

                } else if (currentUserDTO == null || userDTO == null){
                    dialog.showWarn("Handling login status, try again later.");
                }else {

                    // can't talk with your self
                    if (!currentUserDTO.equals(userDTO)) {
                        Intent goToChatActivityIntent = new Intent(ProductDetailActivity.this, ChatActivity.class);
                        goToChatActivityIntent.putExtra("targetUserDTO", userDTO);
                        goToChatActivityIntent.putExtra(Constants.DATA_A, userDTO.getId());
                        goToChatActivityIntent.putExtra(Constants.TYPE, Constants.SINGLE_CHAT);
                        startActivity(goToChatActivityIntent);
                    } else {
                        dialog.showInfo("This is your own product!");
                    }

                }
            }
        });
        /** want current product */
        binding.pdtDetailBtnWantThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() == null) {
                    dialog.showWarn("Please login in.");

                } else if (currentUserDTO == null){
                    dialog.showWarn("Handling login status, try again later.");
                }else {
                    // can't buy self
                    if (currentUserDTO.equals(userDTO)) {
                        dialog.showInfo("This is your own product!");
                        return;
                    }

                    // can't buy sold out item
                    if (productDTO.getStatus() == Constants.PUBLISHED) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Click Want this", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        dialog.showWarn("This item is not on sale.");
                    }

                }
            }
        });

    }
}