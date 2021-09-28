package com.comp90018.assignment2.modules.orders.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.databinding.ActivityPublishedBinding;

public class PublishedActivity extends AppCompatActivity {

    private ActivityPublishedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublishedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);
    }
}