package com.comp90018.assignment2.modules.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.search.activity.SearchProductActivity;
import com.comp90018.assignment2.modules.search.activity.SearchResultActivity;
import com.comp90018.assignment2.modules.search.adapter.SearchResultRvAdapter;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

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
        binding.tvPdtDetailRate.setText("rate: " + productDTO.getStar_number().toString());
        binding.tvPdtUserLocation.setText("Pdt Loc: " + productDTO.getLocation_text());
        binding.tvPdtPublishTime.setText(productDTO.getPublish_time().toDate().toString());
        binding.tvPdtDetailPrice.setText("$" + productDTO.getPrice().toString());
        binding.tvPdtDetailDescription.setText(productDTO.getDescription());
        binding.tvPdtDetailBrand.setText("Brand: " + productDTO.getBrand());
        binding.tvPdtDetailStatus.setText("Status: " + productDTO.getStatus());
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
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Go Profile Page", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        /** add current product to my favourite */
        binding.llFavouriteBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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