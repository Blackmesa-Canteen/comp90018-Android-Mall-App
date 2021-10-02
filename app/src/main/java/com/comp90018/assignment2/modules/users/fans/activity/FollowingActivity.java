package com.comp90018.assignment2.modules.users.fans.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityFollowedBinding;
import com.comp90018.assignment2.databinding.ActivityFollowingBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.fans.adapter.RvFollowedAdapter;
import com.comp90018.assignment2.modules.users.fans.adapter.RvFollowingAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * following
 *
 * need to input Current User DTO with DATA_A
 *
 * @author xiaotian li
 */
public class FollowingActivity extends AppCompatActivity {

    private final static String TAG = "FollAct[dev]";
    private ActivityFollowingBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private UserDTO currentUserDto;
    private List<UserDTO> followingUserDtoList;
    private PromptDialog dialog;

    private RvFollowingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up view binding
        // init view binding
        binding = ActivityFollowingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        dialog = new PromptDialog(this);
        followingUserDtoList = new ArrayList<>();

        // get CurrentUserDTO from bundle
        currentUserDto = getIntent().getParcelableExtra(Constants.DATA_A);

        if (currentUserDto == null) {
            Log.w(TAG, "failed to load dto from bundle");
            finish();
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // set up back logic
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get current user Dto
        // get current user info
        if (auth.getCurrentUser() == null) {
            Log.w(TAG, "not login");
            Toast.makeText(App.getContext(), "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }

        adapter = new RvFollowingAdapter(R.layout.item_rv_following, followingUserDtoList, this, currentUserDto);
        binding.rvFollowing.setAdapter(adapter);
        binding.rvFollowing.setLayoutManager(new LinearLayoutManager(this));

        // query followings
        List<DocumentReference> following_refs = currentUserDto.getFollowing_refs();

        if (following_refs == null || following_refs.size() == 0) {
            binding.textNothing.setVisibility(View.VISIBLE);
            binding.rvFollowing.setVisibility(View.GONE);
        } else {
            for (DocumentReference ref : following_refs) {
                // query follower one by one, and add to the adapter one by one
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserDTO userDTO = task.getResult().toObject(UserDTO.class);

                            // add this one to adapter
                            if(userDTO != null) {
                                followingUserDtoList.add(userDTO);
                                int currentSize = followingUserDtoList.size();
                                adapter.notifyItemInserted(currentSize - 1);
                            }
                        } else {
                            Log.w(TAG, "db query failed, " + task.getException());
                        }
                    }
                });
            }
        }
    }
}