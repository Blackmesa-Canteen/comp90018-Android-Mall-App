package com.comp90018.assignment2.modules.entrance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;


/**
 * Welcome activity
 * can be used to init permissions
 *
 * @author xiaotian
 */
public class WelcomeActivity extends AppCompatActivity {

    private PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        promptDialog = new PromptDialog(this);

        // request permission
        AndPermission.with(WelcomeActivity.this)
                .runtime()
                .permission(
                        Permission.CAMERA,
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.RECORD_AUDIO,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE
                )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 1.5 seconds, then enter main layout
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // run() is in main thread
                                // start main page
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                finish();
                            }
                        }, 1500);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        promptDialog.showWarn("Please grant permissions: camera, location, microphone and storage.");
                        if (AndPermission.hasAlwaysDeniedPermission(WelcomeActivity.this, data)) {
                            promptDialog.showWarn("Please grant permissions in settings.");
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // run() is in main thread
                                    // start main page
                                    finish();
                                }
                            }, 1000);
                        }
                    }
                })
                .start();

        // get firebase token for debugging.
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firebase Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("Firebase Token", token);
                    }
                });
    }
}