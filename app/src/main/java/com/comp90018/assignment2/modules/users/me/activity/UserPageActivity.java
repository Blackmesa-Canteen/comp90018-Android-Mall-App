package com.comp90018.assignment2.modules.users.me.activity;

import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        productDTOList = new ArrayList<>();

        loadData();

    }


    private void loadData(){

        //get the current user's UID
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

                // show current user avatar
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

                //get current user nickname, number of followers and following,favorite info
                binding.tvNickName.setText(currentUserDto.getNickname());
                binding.tvFollowerNumber.setText(follower);
                binding.tvFollowingNumber.setText(following);
                binding.tvFavoriteNumber.setText(favorite);
            }
        });
//query products belong to current user
db.collection(Constants.PRODUCT_COLLECTION)
                .whereIn("status", Arrays.asList(Constants.PUBLISHED, Constants.SOLD_OUT, Constants.UNDERCARRIAGE))
                .whereEqualTo("owner_ref", currentUserReference)
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





                            // query for current userDTO
                            // then attach it to the adapter
                            currentUserReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot result = task.getResult();
                                        UserDTO currentUserDTO = result.toObject(UserDTO.class);
                                        adapter.setCurrentUserDTO(currentUserDTO);
                                    } else {
                                        Log.w(TAG, "get current user dto failed: " + task.getException());
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
