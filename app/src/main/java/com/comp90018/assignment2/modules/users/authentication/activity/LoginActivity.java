package com.comp90018.assignment2.modules.users.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityLoginBinding;
import com.comp90018.assignment2.modules.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * @author xiaotian
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity DEBUG";
    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        // attach to layout file
        setContentView(view);

        // init firebase service
        firebaseAuth = FirebaseAuth.getInstance();

        // setup register
        binding.loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        // setup login activity
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = binding.loginUserName.getText().toString();
                String loginPassword = binding.loginPassWord.getText().toString();

                // check size input
                // size of input will not greater than 80 characters
                if (usernameStr == null || loginPassword == null
                        || usernameStr.length() == 0 || loginPassword.length() == 0
                        || usernameStr.length() >= 80 || loginPassword.length() >= 80) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("Please input correct username/password").setPositiveButton("ok", null).show();
                    binding.loginUserName.setText("");
                    binding.loginPassWord.setText("");
                    return;
                }

                // check legal inputs
                // email regex
                String emailRegex = "^[a-z\\d]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$";
                // No password regex, because it is login
                if (!usernameStr.matches(emailRegex)) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("username should be email").setPositiveButton("ok", null).show();
                    return;
                }

                signIn(usernameStr, loginPassword);
            }
        });

        // forget password dummy
        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Reset mail sent. please check inbox", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if logged in, if so, go to me fragment
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && JMessageClient.getMyInfo() != null) {
            // 已经登陆了，退出activity.
            Log.d(TAG, "signInWithEmail:Already login");
            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivity);

            finish();
        }

        // clean login info
        if (currentUser != null) {
            firebaseAuth.signOut();
        }

        if (JMessageClient.getMyInfo() != null) {
            JMessageClient.logout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if logged in, if so, go to me fragment
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && JMessageClient.getMyInfo() != null) {
            // 已经登陆了，退出activity.
            Log.d(TAG, "signInWithEmail:Already login");
            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        }

        // clean login info
        if (currentUser != null) {
            firebaseAuth.signOut();
        }

        if (JMessageClient.getMyInfo() != null) {
            JMessageClient.logout();
        }
    }

    /**
     * login existing user， both login in
     * @param email login email
     * @param password password
     */
    private void signIn(String email, String password) {
        ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(dialog -> {
            // recreate this activity
            recreate();
        });
        progressDialog.show();

        FirebaseUser user = null;
        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Login 极光IM
                            if (user != null) {
                                String userId = user.getUid();
                                Toast.makeText(LoginActivity.this, "Almost done, please wait...", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "userId: " + userId);
                                JMessageClient.login(userId, password, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        // 0 表示正常。大于 0 表示异常，responseMessage 会有进一步的异常信息。
                                        if (i == 0) {
                                            Log.d(TAG, "Jmessage login:success");
                                            progressDialog.dismiss();
                                            // go back
                                            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(toMainActivity);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            new AlertDialog.Builder(LoginActivity.this).setMessage("IM service failed").setPositiveButton("ok", null).show();
                                            Log.w(TAG, "Jmessage login:failure:" + s);
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            new AlertDialog.Builder(LoginActivity.this).setMessage("username or password is not correct").setPositiveButton("ok", null).show();
                            binding.loginPassWord.setText("");
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}