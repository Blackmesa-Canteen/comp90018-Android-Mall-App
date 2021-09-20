package com.comp90018.assignment2.modules.product.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.product.ProductDetailAdapter;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private FirebaseStorage storage;
    private ProductDetailAdapter productDetailAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();

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

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Go Previous Page", Toast.LENGTH_SHORT);
                toast.show();
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

                // check whther the item is liked or not
                

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Add to my favourite", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        /** comments on current product */
        binding.llChatBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "comments on current product", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        /** want current product */
        binding.pdtDetailBtnWantThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Click Want this", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}