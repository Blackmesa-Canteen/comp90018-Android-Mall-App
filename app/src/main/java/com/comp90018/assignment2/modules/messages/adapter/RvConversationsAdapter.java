package com.comp90018.assignment2.modules.messages.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.Calculator;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * adapter for message fragment
 *
 * @author xiaotian
 */
public class RvConversationsAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder> {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private final Context context;

    private final static String TAG = "RvConversationsAdapter";

    public RvConversationsAdapter(int layoutResId, @Nullable List<Conversation> data, Context context) {
        super(layoutResId, data);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Conversation item) {

        // get userDTO based on user id
        if (item.getTargetInfo() instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) item.getTargetInfo();

            // we stored firebase userId as userName in Jmessage server
            // need to query it
            String userId = userInfo.getUserName();
            db.collection(Constants.USERS_COLLECTION)
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    // get userDTO from firebase
                                    UserDTO userDTO = document.toObject(UserDTO.class);

                                    // storage Reference of firebase
                                    StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());

                                    // query image with the reference, then show avatar
                                    Glide.with(context)
                                            .load(imgReference)
                                            .into((CircleImageView) helper.getView(R.id.img_avatar));

                                    // nickname
                                    String nickname;
                                    if (userDTO.getNickname().length() > 10) {
                                        nickname = userDTO.getNickname().substring(0, 10) + "...";
                                    } else {
                                        nickname = userDTO.getNickname();
                                    }
                                    helper.setText(R.id.text_nickname_cut, nickname);

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

            // get LatestMessage, and show
            Message lastMessage = item.getLatestMessage();
            if (lastMessage != null) {
                String showStr = "";

                switch (lastMessage.getContentType()) {
                    case image:
                        showStr = "[Image]";
                        break;
                    case voice:
                        showStr = "[Voice]";
                        break;
                    case video:
                        showStr = "[Video]";
                        break;
                    case location:
                        showStr = "[Location]";
                        break;
                    case eventNotification:
                        showStr = "[Notification]";
                        break;
                    case custom:
                        CustomContent customContent = (CustomContent) lastMessage.getContent();
                        String customType = customContent.getStringValue(Constants.TYPE);

                        if (TextUtils.isEmpty(customType)) {
                            showStr = "";
                            helper.setText(R.id.emoji_text_newest_msg_cut, showStr);
                            return;
                        }

                        if (customType.equals(Constants.ADDRESS)) {
                            showStr = "[Address]";
                        }

                        break;

                    /*
                     * Suggestive message content. This MessageContent type is
                     * only created by the SDK actively. It is used by
                     * the upper layer for presentation and cannot be used as
                     * the body of the message to be sent.
                     */
                    case prompt:
                        showStr = ((PromptContent) lastMessage.getContent()).getPromptText();
                        break;

                    default:
                        showStr = ((TextContent) lastMessage.getContent()).getText();
                        break;
                }

                helper.setText(R.id.emoji_text_newest_msg_cut, showStr);

                // message time
                long createTime = lastMessage.getCreateTime();
                String dateStr = Calculator.fromLongToDate("MM-dd HH:mm", createTime);
                helper.setText(R.id.text_newest_msg_time, dateStr);

                // if new message has come, show a red spot
                if (item.getExtra().equals(Constants.NEW_MESSAGE)) {
                    helper.getView(R.id.v_new).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.v_new).setVisibility(View.INVISIBLE);
                }
            }


        }

    }
}
