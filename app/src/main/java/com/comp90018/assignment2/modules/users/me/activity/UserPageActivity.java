package com.comp90018.assignment2.modules.users.me.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;

import com.comp90018.assignment2.databinding.ActivityUserPageBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.fans.activity.UserListActivity;
import com.comp90018.assignment2.modules.users.me.adapter.RvUserPageAdapter;

import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * UserPage Activity
 *
 * Need input a userDTO in bundle with key "userDTO"
 *
 * @author Zhonghui Jiang
 * @author Xiaotian Li
 */
public class UserPageActivity extends AppCompatActivity {

    private final static String TAG = "UserPage[dev]";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ActivityUserPageBinding binding;
    private RvUserPageAdapter adapter;
    private RecyclerView recyclerView;


    private FirebaseStorage storage;
    List<ProductDTO> productDTOList;

    private UserDTO targetUserDTO;
    private UserDTO currentUserDTO;

    private final static int GET_FOLLOWER_LIST = 1;
    private final static int GET_FOLLOWING_LIST = 2;

    private PromptDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_page);
        dialog = new PromptDialog(this);
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

        Intent intent = getIntent();
        //get the userDTO from the product ref
        targetUserDTO = (UserDTO) intent.getParcelableExtra("userDTO");

        // attach update listener
        final DocumentReference docRef = db
                .collection(Constants.USERS_COLLECTION)
                .document(targetUserDTO.getId());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    targetUserDTO = snapshot.toObject(UserDTO.class);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        loadData();
    }


    private void loadData() {

        if (targetUserDTO == null) {
            Toast.makeText(App.getContext(), "User is not exist!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // get the current user's UID
        String currentUserId = null;

        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();

            if (currentUserId.equals(targetUserDTO.getId())) {
                // if is self, no need to query db again
                currentUserDTO = targetUserDTO;
            } else {
                // if not self, query current userDTO.
                db.collection(Constants.USERS_COLLECTION)
                        .document(currentUserId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    currentUserDTO = task.getResult().toObject(UserDTO.class);

                                    // handle following btn text
                                    DocumentReference targetUserRef =
                                            db.collection(Constants.USERS_COLLECTION)
                                                    .document(targetUserDTO.getId());

                                    List<DocumentReference> currentUserFollowingList = currentUserDTO.getFollowing_refs();
                                    if (currentUserFollowingList.contains(targetUserRef)) {
                                        binding.button.setText("Unfollow");
                                    } else {
                                        binding.button.setText("Follow");
                                    }

                                } else {
                                    Log.w("UserPage[dev]",
                                            "query current userDTO fail: " + task.getException());
                                }
                            }
                        });
            }
        }

        // render target User info
        int followerSize = targetUserDTO.getFollower_refs().size();
        int followingSize = targetUserDTO.getFollowing_refs().size();
        int favoriteSize = targetUserDTO.getFavorite_refs().size();
        String nickname = targetUserDTO.getNickname();
        String loginId = "Login ID: " + targetUserDTO.getEmail();


        String description = targetUserDTO.getDescription();
        if (description.equals("")){
            binding.tvDescription.setText("Hello, I'm new here!");
        }
        else {
            binding.tvDescription.setText(description);
        }

        binding.tvFollowerNumber.setText(String.valueOf(followerSize));
        binding.tvFollowingNumber.setText(String.valueOf(followingSize));
        binding.tvNickName.setText(nickname);
        binding.tvUserID.setText(loginId);


        StorageReference imgReference = storage.getReferenceFromUrl(targetUserDTO.getAvatar_address());
        // query image with the reference
        Glide.with(this)
                .load(imgReference)
                .into(binding.icUser);

        DocumentReference userReference = db
                .collection(Constants.USERS_COLLECTION)
                .document(targetUserDTO.getId());
        //query products belong to target user
        db.collection(Constants.PRODUCT_COLLECTION)
                .whereEqualTo("status", Constants.PUBLISHED)
                .whereEqualTo("owner_ref", userReference)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productDTOList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductDTO productDTO = document.toObject(ProductDTO.class);
                                productDTOList.add(productDTO);
                            }


                            //use grid view, set adapter
                            GridLayoutManager gvManager = new GridLayoutManager(UserPageActivity.this, 2);
                            adapter = new RvUserPageAdapter(productDTOList, UserPageActivity.this);


                            // query for userDTO
                            // then attach it to the adapter
                            userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot result = task.getResult();
                                        UserDTO userDTO = result.toObject(UserDTO.class);
                                        adapter.setUserDTO(userDTO);
                                    } else {
                                        Log.w(TAG, "get user dto failed: " + task.getException());
                                    }
                                }
                            });

                            // attach adapter to rv and set layout
                            binding.rvUserPage.setAdapter(adapter);
                            binding.rvUserPage.setHasFixedSize(true);
                            binding.rvUserPage.setLayoutManager(gvManager);
                        } else {
                            Log.w(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // show following / follower activity
        View.OnClickListener goFollowersEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showUserListIntent = new Intent(UserPageActivity.this, UserListActivity.class);
                showUserListIntent.putExtra(Constants.DATA_A, targetUserDTO);
                showUserListIntent.putExtra(Constants.DATA_B, GET_FOLLOWER_LIST);
                startActivity(showUserListIntent);
            }
        };

        View.OnClickListener goFollowingEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showUserListIntent = new Intent(UserPageActivity.this, UserListActivity.class);
                showUserListIntent.putExtra(Constants.DATA_A, targetUserDTO);
                showUserListIntent.putExtra(Constants.DATA_B, GET_FOLLOWING_LIST);
                startActivity(showUserListIntent);
            }
        };

        binding.tvFollowers.setOnClickListener(goFollowersEvent);
        binding.tvFollowerNumber.setOnClickListener(goFollowersEvent);

        binding.tvFollowing.setOnClickListener(goFollowingEvent);
        binding.tvFollowingNumber.setOnClickListener(goFollowingEvent);

        // handle button logic
        if (currentUserId == null) {
            // if is not logged in
            binding.button.setVisibility(View.GONE);

        } else if (currentUserId.equals(targetUserDTO.getId())){
            // if is self, show edit profile
            binding.button.setText("Edit Profile");
            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToEditProfileIntent = new Intent(UserPageActivity.this, EditProfileActivity.class);
                    startActivity(goToEditProfileIntent);
                }
            });

        } else {
            // if is not self, show follow/unfollow button
            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.showLoading("Please wait...");
                    if (currentUserDTO != null) {
                        // handle follow logic
                        DocumentReference currentUserRef =
                                db.collection(Constants.USERS_COLLECTION)
                                        .document(currentUserDTO.getId());

                        DocumentReference targetUserRef =
                                db.collection(Constants.USERS_COLLECTION)
                                        .document(targetUserDTO.getId());

                        List<DocumentReference> currentUserFollowingList = currentUserDTO.getFollowing_refs();
                        if (currentUserFollowingList.contains(targetUserRef)) {
                            // if already followed, unfollow logic
                            currentUserDTO.getFollowing_refs().remove(targetUserRef);
                            currentUserRef.set(currentUserDTO);
                            targetUserDTO.getFollower_refs().remove(currentUserRef);
                            targetUserRef.set(targetUserDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadData();
                                    dialog.dismiss();
                                }
                            });

                            binding.button.setText("Follow");
                        } else {
                            // if not follow, can follow
                            // if already followed, unfollow logic
                            currentUserDTO.getFollowing_refs().add(targetUserRef);
                            currentUserRef.set(currentUserDTO);
                            targetUserDTO.getFollower_refs().add(currentUserRef);
                            targetUserRef.set(targetUserDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadData();
                                    dialog.dismiss();
                                }
                            });

                            binding.button.setText("Unfollow");
                        }
                    }
                }
            });
        }
    }

    //update data
    @Override
    protected void onResume() {
        super.onResume();
        if (targetUserDTO != null) {
            loadData();
        }
    }
}
