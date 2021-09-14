package com.comp90018.assignment2.modules.product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.modules.search.activity.SearchProductActivity;
import com.comp90018.assignment2.modules.search.activity.SearchResultActivity;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    /**
     * This list will be given by bundle extra from the other activity li xiaotian
     */
    List<ProductDTO> productDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // retrieve bundle data from intent li xiaotian
        Intent intent = getIntent();
        productDTOList = intent.getParcelableArrayListExtra("productDTOList");

//        ProductDTO productDTO = (ProductDTO);
        intent.getParcelableExtra("productDTO");

//        binding.pdtDetailBrand.setText(productDTO.getBrand());

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
        binding.llCommentBtnGroup.setOnClickListener(new View.OnClickListener() {
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