package com.comp90018.assignment2.modules.orders.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityOrderDetailBinding;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;

public class OrderDetailActivity extends AppCompatActivity {
    private ActivityOrderDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_order_detail);

        binding.orderDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.orderDetailOrderStatus.setText("Retrieve Order Status");
        binding.orderDetailDeliveryStatus.setText("Retrieve delivery Status");
        binding.orderDetailDeliveryTrackingId.setText("Retrieve Tracking id, CAN be Null");
        binding.orderDetailPdtImg;
        binding.orderDetailPdtName.setText("retrieve pdt name");
        binding.orderDetailPdtPrice.setText("$ + retrieve pdt price");
        binding.orderDetailPdtQuantity.setText("retrieve quantity");

        binding.orderDetailTotalPrice.setText("calculate product of price and quantity");
        binding.orderDetailReceiverName.setText("retrieve receiver's name");
        binding.orderDetailReceiverPhone.setText("retrieve receiver's phone number");
        binding.orderDetailReceiverAddr.setText("retrieve receiver's address(buyer's addrss/????)");

        binding.orderDetailConnectSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = "go to connect seller page";
            }
        });

        binding.orderDetailSellerUsername.setText("retrieve seller username");
        binding.orderDetailId.setText("retrieve order id");
        binding.orderDetailCopyOrderIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = "click this button copy order id";
            }
        });
        binding.orderDetailTransactionDate.setText("retrieve order tranaction date");


    }
}