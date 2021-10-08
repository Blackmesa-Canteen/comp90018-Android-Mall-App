package com.comp90018.assignment2.modules.orders.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPlaceOrderBinding;

public class PlaceOrderActivity extends AppCompatActivity {
    private ActivityPlaceOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.placeOrderBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.placeOrderPdtImgOvalImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pass;
                    }
                }
        );
        binding.placeOrderPdtName.setText("product name");
        binding.placeOrderPdtPrice.setText("product's original price");
        binding.placeOrderReceiverName.setText("receiver's name");
        binding.placeOrderReceiverEmail.setText("receiver's email");
        binding.placeOrderReceiverAddr.setHint("insert receiver's address");
        binding.placeOrderCheckoutPrice.setText("finial check out price, may have multiple products in one order");
        binding.placeOrderBtnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass;
            }
        });

    }
}