package com.comp90018.assignment2.utils.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.databinding.ActivityImagePreviewBinding;

/**
 *
 * Image previewer
 *
 * need intent bundle: image_path
 *
 * @author  xiaotian li
 */
public class ImagePreviewActivity extends AppCompatActivity {

    private ActivityImagePreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        Glide.with(this).load(getIntent().getStringExtra("image_path")).into(binding.iv);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}