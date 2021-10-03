package com.comp90018.assignment2.modules.users.fans.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.fans.activity.FollowingActivity;
import com.comp90018.assignment2.modules.users.me.activity.UserPageActivity;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * @author xiaotian li
 */
public class RvFollowingAdapter extends BaseQuickAdapter<UserDTO, BaseViewHolder> {
    private final Context context;
    private final static String TAG = "RvFollingAda[dev]";
    private final FirebaseStorage storage;
    private final FirebaseFirestore db;
    private final UserDTO currentUserDTO;

    private final PromptDialog dialog;

    public RvFollowingAdapter(int layoutResId, @Nullable List<UserDTO> data, Context context, UserDTO currentUserDTO) {
        super(layoutResId, data);
        this.context = context;
        this.currentUserDTO = currentUserDTO;

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        dialog = new PromptDialog((FollowingActivity) context);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserDTO userDTO) {

        int position = getItemPosition(userDTO);
        // set up user info
        // load avatar
        StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());
        // query image with the reference, then show avatar
        Glide.with(context)
                .load(imgReference)
                .into((CircleImageView) helper.getView(R.id.iv_avatar));

        // nick name
        helper.setText(R.id.text_nickname, userDTO.getNickname());

        // user desc
        helper.setText(R.id.text_user_desc, userDTO.getDescription());

        // attach jump to user details event
        RelativeLayout rlRoot = (RelativeLayout) helper.getView(R.id.rl_root);
        rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToUserPageIntent = new Intent(context, UserPageActivity.class);
                goToUserPageIntent.putExtra("userDTO", userDTO);
                context.startActivity(goToUserPageIntent);
            }
        });

        // unfollowing btn logic
        Button btnUnfollow = helper.getView(R.id.btn_unfollow);

        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showLoading("Please wait");
                String currentUid = currentUserDTO.getId();
                String targetUserId = userDTO.getId();

                DocumentReference currentUserRef =
                        db.collection(Constants.USERS_COLLECTION)
                        .document(currentUid);

                DocumentReference targetUserRef =
                        db.collection(Constants.USERS_COLLECTION)
                                .document(targetUserId);

                // remove following in current user
                currentUserDTO.getFollowing_refs().remove(targetUserRef);
                currentUserRef.set(currentUserDTO);

                // remove follower from that user
                userDTO.getFollower_refs().remove(currentUserRef);
                targetUserRef.set(userDTO);

                dialog.dismiss();
                dialog.showSuccess("Ok");

                btnUnfollow.setText("UnFollowed");
                btnUnfollow.setEnabled(false);
            }
        });
    }
}
