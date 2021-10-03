package com.comp90018.assignment2.modules.users.fans.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.UserDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RvUserListAdapter extends BaseQuickAdapter<UserDTO, BaseViewHolder> {

    private final Context context;
    private final static String TAG = "RvUserLiAda[dev]";
    private final FirebaseStorage storage;

    public RvUserListAdapter(int layoutResId, @Nullable List<UserDTO> data, Context context) {
        super(layoutResId, data);
        this.context = context;

        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserDTO userDTO) {
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
                // TODO: go to user details
                Toast.makeText(context, "Go to user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
