package com.comp90018.assignment2.app.entrance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.comp90018.assignment2.R;


/**
 * Welcome activity
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        // 1.5 seconds, then enter main layout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // run() is in main thread
                // start main page
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }
}