package com.comp90018.assignment2.modules.users.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityLoginBinding;
import com.comp90018.assignment2.databinding.ActivityMainBinding;
import com.comp90018.assignment2.db.repository.UserRepository;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.ClearWriteEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import cn.jpush.im.android.api.JMessageClient;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
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

        // init database
        firebaseAuth = FirebaseAuth.getInstance();

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if logged in, if so, go to me fragment
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // 已经登陆了，退出activity.
            Log.d(TAG, "signInWithEmail:Already login");
            finish();
        }
    }

    /**
     * login existing user， both login in
     * @param email login email
     * @param password password
     */
    private void signIn(String email, String password) {
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
                                Toast.makeText(LoginActivity.this, "userid:"+userId, Toast.LENGTH_LONG).show();
                                JMessageClient.login(userId, password, null);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            new AlertDialog.Builder(LoginActivity.this).setMessage("username or password is not correct").setPositiveButton("ok", null).show();
                            binding.loginPassWord.setText("");
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}