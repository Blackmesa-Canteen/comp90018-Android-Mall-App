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
import com.comp90018.assignment2.databinding.ActivityUserListBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.fans.adapter.RvFollowedAdapter;
import com.comp90018.assignment2.modules.users.fans.adapter.RvUserListAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * Need input :
 *
 * UserDTO with key "DATA_A"
 *
 * Which one 1 - follower, 2- following, with "DATA_B"
 *
 */
public class UserListActivity extends AppCompatActivity {

    private final static String TAG = "UserList[dev]";
    private ActivityUserListBinding binding;

    private FirebaseFirestore db;

    private UserDTO currentUserDto;
    private List<UserDTO> userDTOList;

    private RvUserListAdapter adapter;

    private final static int GET_FOLLOWER_LIST = 1;
    private final static int GET_FOLLOWING_LIST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up view binding
        // init view binding
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // get CurrentUserDTO from bundle
        currentUserDto = getIntent().getParcelableExtra(Constants.DATA_A);
        int whichOne = getIntent().getIntExtra(Constants.DATA_B, 0);
        userDTOList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new RvUserListAdapter(R.layout.item_rv_user_list, userDTOList, this);
        binding.rvFollowed.setAdapter(adapter);
        binding.rvFollowed.setLayoutManager(new LinearLayoutManager(this));

        if (currentUserDto == null && whichOne != 0) {
            Toast.makeText(App.getContext(), "input data err", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        List<DocumentReference> refs = null;
        // query what
        if (whichOne == GET_FOLLOWER_LIST) {
            refs = currentUserDto.getFollower_refs();
        } else if (whichOne == GET_FOLLOWING_LIST) {
            refs = currentUserDto.getFollowing_refs();
        } else {
            // not defined
            ;
        }

        if (refs == null || refs.size() == 0) {
            binding.textNothing.setVisibility(View.VISIBLE);
            binding.rvFollowed.setVisibility(View.GONE);
        } else {
            for (DocumentReference ref : refs) {
                // query follower one by one, and add to the adapter one by one
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserDTO userDTO = task.getResult().toObject(UserDTO.class);

                            if (userDTO != null) {
                                // add this one to adapter
                                userDTOList.add(userDTO);
                                int currentSize = userDTOList.size();
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