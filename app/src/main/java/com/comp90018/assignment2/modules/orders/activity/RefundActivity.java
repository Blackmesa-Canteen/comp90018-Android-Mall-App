package com.comp90018.assignment2.modules.orders.activity;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.comp90018.assignment2.databinding.ActivityRefundBinding;

public class RefundActivity extends AppCompatActivity {
    private static final String TAG = "Refund[dev]";
    private ActivityRefundBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefundBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
