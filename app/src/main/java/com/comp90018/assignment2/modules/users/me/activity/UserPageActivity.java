package com.comp90018.assignment2.modules.users.me.activity;

import static android.content.ContentValues.TAG;
import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPublishedBinding;
import com.comp90018.assignment2.databinding.ActivityUserPageBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.BabushkaText;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ActivityUserPageBinding binding;


    private FirebaseStorage storage;
    List<ProductDTO> productDTOList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        productDTOList = new ArrayList<>();

        loadData();

    }


    private void loadData(){
        String currentUserId = auth.getCurrentUser().getUid();
        binding.tvUserID.setText("ID: "+currentUserId);
        DocumentReference currentUserReference = db.collection(Constants.USERS_COLLECTION).document(currentUserId);
        DocumentReference userReference = db.collection(USERS_COLLECTION).document(currentUserId);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDTO currentUserDto = documentSnapshot.toObject(UserDTO.class);
                String follower = String.valueOf(currentUserDto.getFollower_refs().size());
                String following = String.valueOf(currentUserDto.getFollowing_refs().size());
                String favorite=String.valueOf(currentUserDto.getFavorite_refs().size());

                // show current user's avatar
                if (currentUserDto == null
                        || currentUserDto.getAvatar_address() == null
                        || currentUserDto.getAvatar_address().equals("")
                        || currentUserDto.getAvatar_address().equals("default")
                        || currentUserDto.getAvatar_address().equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
                    binding.icUser.setImageResource(R.drawable.default_avatar);
                } else {
                    StorageReference imgReference = storage.getReferenceFromUrl(currentUserDto.getAvatar_address());
                    Glide.with(UserPageActivity.this)
                            .load(imgReference)
                            .into(binding.icUser);
                }
                binding.tvNickName.setText(currentUserDto.getNickname());
                binding.tvFollowerNumber.setText(follower);
                binding.tvFollowingNumber.setText(following);
                binding.tvFavoriteNumber.setText(favorite);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        loadData();
    }

}
