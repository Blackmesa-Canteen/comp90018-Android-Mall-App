package com.comp90018.assignment2.modules.users.me.activity;

import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


import com.bumptech.glide.Glide;
import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.config.GlideEngine;
import com.comp90018.assignment2.databinding.ActivityEditUserProfileBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.authentication.activity.EditPasswordActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Activity for prodile editing.
 *
 * @author Gengchang Xu
 * @author Xiaotian Li
 */
public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfile[DEBUG]";
    private ActivityEditUserProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;

    private UserDTO currentUserDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        binding = ActivityEditUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // init db
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // check login status
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(App.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // get current user dto
        db.collection(USERS_COLLECTION)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();
                            currentUserDto = result.toObject(UserDTO.class);

                            // show current user's avatar
                            if (currentUserDto == null
                                    || currentUserDto.getAvatar_address() == null
                                    || currentUserDto.getAvatar_address().equals("")
                                    || currentUserDto.getAvatar_address().equals("default")
                                    || currentUserDto.getAvatar_address().equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
                                binding.ivAvatar.setImageResource(R.drawable.default_avatar);
                            } else {
                                StorageReference imgReference = firebaseStorage.getReferenceFromUrl(currentUserDto.getAvatar_address());
                                Glide.with(EditProfileActivity.this)
                                        .load(imgReference)
                                        .into(binding.ivAvatar);
                            }

                            // show current user's other info
                            binding.profileName.setText(currentUserDto.getNickname());
                            binding.profileDesc.setText(currentUserDto.getDescription());
                            binding.profileAddress.setText(currentUserDto.getLocation_text());

                            Integer gender = currentUserDto.getGender();
                            switch (gender) {
                                case Constants.MALE:
                                    binding.genderRadioGroup.check(R.id.profile_male);
                                    break;
                                case Constants.FEMALE:
                                    binding.genderRadioGroup.check(R.id.profile_female);
                                    break;

                                default:
                                    binding.genderRadioGroup.check(R.id.profile_other);
                                    break;
                            }

                        } else {
                            Log.e(TAG, "Get current user dto error");
                        }
                    }
                });

        // setup avatar editing
        binding.llEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserDto != null) {
                    PictureSelector.create(EditProfileActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .imageEngine(GlideEngine.createGlideEngine())
                            .maxSelectNum(1)
                            .minSelectNum(1)
                            .selectionMode(PictureConfig.SINGLE)
                            .isPreviewImage(true)
                            .isEnableCrop(true)
                            .isCompress(true)
                            .forResult(Constants.REQUEST_CODE_A);
                }
            }
        });

        //set edit profile listener

        //back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set update password jumping
        binding.btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserDto != null) {
                    Intent goToEditPasswordActivityIntent = new Intent(EditProfileActivity.this, EditPasswordActivity.class);
                    startActivity(goToEditPasswordActivityIntent);
                }
            }
        });

        RadioGroup genderGroup = binding.genderRadioGroup;
        final Integer[] gender = {Constants.FEMALE};

        // get gender info
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == binding.profileFemale.getId()) {
                    gender[0] = Constants.FEMALE;
                } else if (checkedId == binding.profileMale.getId()) {
                    gender[0] = Constants.MALE;
                } else {
                    gender[0] = Constants.GENDER_UNKNOWN;
                }
            }
        });

        genderGroup.check(binding.profileFemale.getId());

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String nickname = binding.profileName.getText().toString();
                String description = binding.profileDesc.getText().toString();
                String address = binding.profileAddress.getText().toString();
                // get genderType
                Integer genderType = gender[0];


                if (nickname == null || description == null || address == null || nickname.length() >= 20 || description.length() >= 60 || address.length() >= 100) {
                    new AlertDialog.Builder(EditProfileActivity.this).setMessage("Please input all information correctly.").setPositiveButton("ok", null).show();
                    return;
                }

                String nicknameRegex = "^[a-zA-Z0-9_-]{0,20}$";
                // No password regex, because it is login
                if (!nickname.matches(nicknameRegex)) {
                    new AlertDialog.Builder(EditProfileActivity.this).setMessage("Nickname should be letters, numbers, underscores and dashes, and no more then 20 letters.").setPositiveButton("ok", null).show();
                    return;
                }

                String addressRegex = "^[a-zA-Z0-9 !?,.;@\"'()-]{0,40}$";
                // No password regex, because it is login
                if (!address.matches(addressRegex)) {
                    new AlertDialog.Builder(EditProfileActivity.this).setMessage("address should be letters, numbers, common symbols and space").setPositiveButton("ok", null).show();
                    return;
                }

                String descRegex = "^[a-zA-Z0-9 !?,.;@\"'()-]{0,40}$";
                // No password regex, because it is login
                if (!description.matches(descRegex)) {
                    new AlertDialog.Builder(EditProfileActivity.this).setMessage("Description should be letters, numbers, common symbols and space").setPositiveButton("ok", null).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setTitle("Updating");
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(true);

                if (currentUserDto != null) {
                    currentUserDto.setNickname(nickname);
                    currentUserDto.setGender(genderType);
                    currentUserDto.setDescription(description);
                    currentUserDto.setLocation_text(address);

                    progressDialog.show();
                    db.collection(Constants.USERS_COLLECTION)
                            .document(currentUserDto.getId())
                            .set(currentUserDto)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // logout google account, then try to sign-in again
//                                        firebaseAuth.signOut();

                                        // finish the register activity
                                        progressDialog.dismiss();

                                        // update Jmessage profile
                                        UserInfo myInfo = JMessageClient.getMyInfo();
                                        myInfo.setNickname(nickname);

                                        progressDialog.setTitle("Updating Jmessage profile");
                                        progressDialog.setMessage("Please wait");
                                        progressDialog.show();
                                        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 0) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(App.getContext(), "profile update complete.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(EditProfileActivity.this, "Jmessage connection err, please try again", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        new AlertDialog.Builder(EditProfileActivity.this)
                                                .setTitle("Sorry")
                                                .setMessage("db update issue, please try again later.")
                                                .setPositiveButton("Ok", null).show();
                                    }
                                }
                            });
                } else {
                    // if currentUserDto has not loaded yet
                    Toast.makeText(EditProfileActivity.this, "db query error, try again please", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == Constants.REQUEST_CODE_A && currentUserDto != null) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() == 1 && selectList.get(0) != null) {
                Log.d(TAG, "img path: " + selectList.get(0).getRealPath());
                if (PictureMimeType.isHasImage(selectList.get(0).getMimeType())) {

                    ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                    progressDialog.setTitle("Updating avatar...");
                    progressDialog.setMessage("Please wait");
                    progressDialog.setCancelable(true);

                    String userId = currentUserDto.getId();

                    File imageFile = new File(selectList.get(0).getRealPath());
                    Uri fileUri = Uri.fromFile(imageFile);
                    StorageReference storageRef = firebaseStorage.getReference();
                    StorageReference newAvatarReference = storageRef.child("users/" + userId + "/" + fileUri.getLastPathSegment());

                    UploadTask uploadTask = newAvatarReference.putFile(fileUri);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Avatar updated Successfully", Toast.LENGTH_SHORT).show();
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                            // get image url for storage
                            String imageUrl = Constants.STORAGE_ROOT_PATH + taskSnapshot.getStorage().getPath();
                            Log.d(TAG, "## Stored path is " + imageUrl);

                            // update avatar
                            currentUserDto.setAvatar_address(imageUrl);
                            db.collection(Constants.USERS_COLLECTION)
                                    .document(currentUserDto.getId())
                                    .set(currentUserDto)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                StorageReference imgReference = firebaseStorage.getReferenceFromUrl(currentUserDto.getAvatar_address());
                                                Glide.with(EditProfileActivity.this)
                                                        .load(imgReference)
                                                        .into(binding.ivAvatar);
                                            } else {
                                                Log.e(TAG, "update user avatar failed");
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Image Uploaded failed.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });


                }
            }
        }
    }
}
