package com.comp90018.assignment2.modules.users.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.databinding.ActivityEditPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import me.leefeng.promptlibrary.PromptDialog;

/**
 *
 * update password activity
 *
 * @author Xiaotian li
 */
public class EditPasswordActivity extends AppCompatActivity {

    private static final String TAG = "EditPwd[dev]";
    private ActivityEditPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private PromptDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init view binding
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dialog = new PromptDialog(this);

        // check login status
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
        }

        // set exit logic
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set confirm logic

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check password input
                String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\s\\S]{6,}$";

                if (binding.newPassWord.getText() != null
                        && binding.newPassWordRepeat.getText() != null
                        && binding.oldPassWord.getText() != null) {

                    String newPassword = binding.newPassWord.getText().toString();
                    String repeatPassword = binding.newPassWordRepeat.getText().toString();
                    String oldPassword = binding.oldPassWord.getText().toString();

                    if (newPassword == null || repeatPassword == null || oldPassword == null
                            || newPassword.length() == 0 || repeatPassword.length() == 0 || oldPassword.length() == 0
                            || newPassword.length() > 80 || repeatPassword.length() > 80 || oldPassword.length() > 80) {
                        dialog.showWarn("Please input legal password within 6-80 letters");
                        return;
                    }

                    // won't check old password
                    if (!newPassword.matches(passwordRegex) || !repeatPassword.matches(passwordRegex) ) {
                        dialog.showWarn("Password contains at least 6 characters, at least 1 letter and 1 digit.");
                        binding.newPassWord.setText("");
                        binding.newPassWordRepeat.setText("");
                        return;
                    }

                    if (!newPassword.equals(repeatPassword)) {
                        dialog.showWarn("Repeat password not matched.");
                        binding.newPassWord.setText("");
                        binding.newPassWordRepeat.setText("");
                        return;
                    }

                    updatePassword(oldPassword, newPassword);

                } else {
                    dialog.showWarn("Please input password");
                }
            }
        });
    }

    /**
     * update password:
     * 1. fireAuth's.
     * 2. Jmessage server's.
     *
     * @param newPassword String new password
     */
    private void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        dialog.showLoading("Updating password.");

        // update Jmessage server's pwd
        JMessageClient.updateUserPassword(oldPassword, newPassword, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                dialog.dismiss();
                if (i == 0) {
                    if (user == null) {
                        dialog.showWarn("please login in.");

                        return;
                    }

                    dialog.showLoading("Updating password...");
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        dialog.showSuccess("update password success!");
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(EditPasswordActivity.this, "update pwd failed, please try again.", Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "firebase db error");
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(EditPasswordActivity.this, "update pwd failed: check your old password, and try again", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Jmessage update pwd error: " + s);
                }
            }
        });
    }
}