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
import com.comp90018.assignment2.databinding.ActivitySearchProductBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.orders.activity.PublishedActivity;
import com.comp90018.assignment2.modules.users.fans.adapter.RvFollowedAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * followed activity
 *
 * need to input Current User DTO with DATA_A
 *
 * @author xiaotian li
 */
public class FollowedActivity extends AppCompatActivity {

    private final static String TAG = "FollAct[dev]";
    private ActivityFollowedBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private UserDTO currentUserDto;
    private List<UserDTO> followedUserDtoList;
    private PromptDialog dialog;

    private RvFollowedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up view binding
        // init view binding
        binding = ActivityFollowedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);
        dialog = new PromptDialog(this);
        followedUserDtoList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // get CurrentUserDTO from bundle
        currentUserDto = getIntent().getParcelableExtra(Constants.DATA_A);

        // set up back logic
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // setup adapter
        adapter = new RvFollowedAdapter(R.layout.item_rv_followed, followedUserDtoList, this);
        binding.rvFollowed.setAdapter(adapter);
        binding.rvFollowed.setLayoutManager(new LinearLayoutManager(this));

        // get current user info
        if (auth.getCurrentUser() == null) {
            Log.w(TAG, "not login");
            Toast.makeText(App.getContext(), "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }

        // query followers
        List<DocumentReference> follower_refs = currentUserDto.getFollower_refs();

        if (follower_refs.size() == 0) {
            binding.textNothing.setVisibility(View.VISIBLE);
            binding.rvFollowed.setVisibility(View.GONE);
        } else {
            for (DocumentReference ref : follower_refs) {
                // query follower one by one, and add to the adapter one by one
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserDTO userDTO = task.getResult().toObject(UserDTO.class);

                            if (userDTO != null) {
                                // add this one to adapter
                                followedUserDtoList.add(userDTO);
                                int currentSize = followedUserDtoList.size();
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