package com.comp90018.assignment2.utils.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.comp90018.assignment2.R;

/**
 * Util activity shows ok,
 * Can be used for revoking onResume() method of the previous activity.
 *
 * @author Xiaotian li
 */
public class OkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}