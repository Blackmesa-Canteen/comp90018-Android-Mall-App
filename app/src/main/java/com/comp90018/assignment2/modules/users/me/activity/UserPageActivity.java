package com.comp90018.assignment2.modules.users.me.activity;

import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;

import com.comp90018.assignment2.databinding.ActivityUserPageBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.me.adapter.RvUserPageAdapter;

import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UserPage Activity
 *
 * @author Zhonghui Jiang
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
        binding.tvFollowerNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserPageActivity.this, "被关注数量", Toast.LENGTH_SHORT).show();
            }
        });
        binding.tvFollowingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserPageActivity.this, "关注数量", Toast.LENGTH_SHORT).show();
            }
        });
        binding.tvFavoriteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserPageActivity.this, "收藏数量", Toast.LENGTH_SHORT).show();
            }
        });



        productDTOList = new ArrayList<>();

        loadData();

    }


    private void loadData(){

        //get the current user's UID
        String currentUserId = auth.getCurrentUser().getUid();

        Intent intent = getIntent();
        //get the userDTO from the product ref
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");

        String userID = null;

        //if jumped from profile page, go to the user's own page
        if(userDTO==null) {
            userID=currentUserId;
            binding.button.setText("Edit Profile");
            binding.button.setOnClickListener(new View.OnClickListener() {
                Intent intent = null;
                public void onClick(View v) {
                    intent = new Intent(UserPageActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        //if jumped from product detail page
        else {
            userID = userDTO.getId();


            //go to other page from product page
            if (!userID.equals(currentUserId)) {

                //if not followed, click to follow
                if (!binding.button.getText().equals("Followed")){
                    binding.button.setText("Follow");
                    binding.button.setOnClickListener(new View.OnClickListener() {
                        Intent intent = null;

                    public void onClick(View v) {

                        binding.button.setText("Followed");
                        int background_color = Color.parseColor("#FFFFFF");
                        int text_color = Color.parseColor("#757575");

                        binding.button.setBackgroundColor(background_color);
                        binding.button.setTextColor(text_color);

                    }
                });}
            }
            //go to own page from product page
            else {
                binding.button.setText("Edit Profile");
                binding.button.setOnClickListener(new View.OnClickListener() {
                    Intent intent = null;

                    public void onClick(View v) {
                        intent = new Intent(UserPageActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

            DocumentReference userReference = db.collection(USERS_COLLECTION).document(userID);
            binding.tvUserID.setText("ID: " + userID);

            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);

                    String follower = String.valueOf(userDTO.getFollower_refs().size());
                    String following = String.valueOf(userDTO.getFollowing_refs().size());
                    String favorite = String.valueOf(userDTO.getFavorite_refs().size());

                    // show current user avatar
                    if (userDTO == null
                            || userDTO.getAvatar_address() == null
                            || userDTO.getAvatar_address().equals("")
                            || userDTO.getAvatar_address().equals("default")
                            || userDTO.getAvatar_address().equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
                        binding.icUser.setImageResource(R.drawable.default_avatar);
                    } else {
                        StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());
                        Glide.with(UserPageActivity.this)
                                .load(imgReference)
                                .into(binding.icUser);
                    }

                    //get current user nickname, number of followers and following,favorite info
                    binding.tvNickName.setText(userDTO.getNickname());
                    binding.tvFollowerNumber.setText(follower);
                    binding.tvFollowingNumber.setText(following);
                    binding.tvFavoriteNumber.setText(favorite);
                };



            });

            //query products belong to current user
            db.collection(Constants.PRODUCT_COLLECTION)
                    .whereIn("status", Arrays.asList(Constants.PUBLISHED, Constants.SOLD_OUT, Constants.UNDERCARRIAGE))
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

    }






    //update data
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

}
