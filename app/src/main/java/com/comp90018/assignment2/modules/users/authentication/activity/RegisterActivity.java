package com.comp90018.assignment2.modules.users.authentication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityLoginBinding;
import com.comp90018.assignment2.databinding.ActivityRegisterBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.ClearWriteEditText;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity DEBUG";
    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // init view binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        // attach to layout file
        setContentView(view);

        // init firebase service
        firebaseAuth = FirebaseAuth.getInstance();

        // setup register listeners
        // back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RadioGroup genderGroup = binding.genderRadioGroup;
        final Integer[] gender = {Constants.GENDER_UNKNOWN};

        // get gender info
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == binding.radioGenderFemale.getId()) {
                    gender[0] = Constants.FEMALE;
                } else if (checkedId == binding.radioGenderMale.getId()) {
                    gender[0] = Constants.MALE;
                } else {
                    gender[0] = Constants.GENDER_UNKNOWN;
                }
            }
        });

        genderGroup.check(binding.radioGenderFemale.getId());

        // submit
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = binding.registerEmail.getText().toString();
                String loginPassword = binding.registerPassWord.getText().toString();
                String repeatPassword = binding.registerRepeatPassword.getText().toString();

                String nickName = binding.registerNickname.getText().toString();

                // get genderType
                Integer genderType = gender[0];

                // check size input
                // size of input will not greater than 80 characters
                if (usernameStr == null || loginPassword == null || repeatPassword == null || nickName == null
                        || usernameStr.length() == 0 || loginPassword.length() == 0
                        || usernameStr.length() >= 80 || loginPassword.length() >= 80
                        || repeatPassword.length() >= 80 || repeatPassword.length() == 0
                        || nickName.length() >= 20) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("Please input all information correctly.").setPositiveButton("ok", null).show();
                    return;
                }

                // validate inputs
                // check legal inputs
                // email regex
                String emailRegex = "^[a-z\\d]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$";
                // No password regex, because it is login
                if (!usernameStr.matches(emailRegex)) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("username should be email.").setPositiveButton("ok", null).show();
                    return;
                }

                //check nickname
                String nicknameRegex = "^[a-z0-9_-]{0,20}$";
                // No password regex, because it is login
                if (!nickName.matches(nicknameRegex)) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("Nickname should be letters, underscores and dashes.").setPositiveButton("ok", null).show();
                    return;
                }

                // check password strong
                String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\s\\S]{6,}$";
                if (!loginPassword.matches(passwordRegex)) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("Password contains at least 6 characters, at least 1 letter and 1 digit.").setPositiveButton("ok", null).show();
                    binding.registerPassWord.setText("");
                    binding.registerRepeatPassword.setText("");
                    return;
                }

                // check repeat equality
                if (!repeatPassword.equals(loginPassword)) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("Repeat password not matched.").setPositiveButton("ok", null).show();
                    binding.registerPassWord.setText("");
                    binding.registerRepeatPassword.setText("");
                    return;
                }

                // register user on Firebase




            }
        });
    }
}
