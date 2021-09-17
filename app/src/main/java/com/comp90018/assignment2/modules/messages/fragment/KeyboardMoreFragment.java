package com.comp90018.assignment2.modules.messages.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.modules.messages.activity.ChatActivity;
import com.comp90018.assignment2.modules.messages.adapter.KeyboardMoreItemAdapter;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;
import com.comp90018.assignment2.modules.messages.bean.KeyboardMoreItemBean;
import com.comp90018.assignment2.utils.Constants;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

/**
 *
 * fragment when click on keyboard's more button
 *
 * @author xiaotian li
 *
 */
public class KeyboardMoreFragment extends BaseFragment {

    private String userId;
    private final static String TAG = "[Dev]MoreFragment";
    private int chatType = Constants.SINGLE_CHAT;

    private List<KeyboardMoreItemBean> itemList;
    private KeyboardMoreItemAdapter adapter;

    private RecyclerView rv_root;

    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.fragment_chat_keyboard_more, null);
        rv_root = view.findViewById(R.id.rv_chat_keyboard_more);

        itemList.clear();
        itemList.add(new KeyboardMoreItemBean(R.drawable.media, "Gallery"));

        adapter = new KeyboardMoreItemAdapter(R.layout.item_chat_keyboard_more_rv, itemList);
        rv_root.setAdapter(adapter);
        rv_root.setLayoutManager(new GridLayoutManager(activityContext, 4));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position) {
                    case 0:
                        // gallery button clicked
                        PictureSelector.create(KeyboardMoreFragment.this)
                                .openGallery(PictureMimeType.ofAll())
                                .maxSelectNum(1)
                                .minSelectNum(1)
                                .selectionMode(PictureConfig.SINGLE)
                                .isPreviewImage(true)
                                .isCompress(true)
                                .forResult(Constants.REQUEST_CODE_A);

                    default:
                        Log.d(TAG, "Selected nothing");
                }
            }
        });

        return view;
    }

    @Override
    public void loadData() {
        // no need to query database
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE_A:
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

                // if has image
                if ( selectList != null && selectList.size() == 1 && selectList.get(0) != null) {

                    String jMessageType = Constants.IMG_TYPE;
                    if (PictureMimeType.isHasImage(selectList.get(0).getMimeType())) {
                        jMessageType = Constants.IMG_TYPE;

                        String finalJMessageType = jMessageType;
                        ImageContent.createImageContentAsync(
                                new File(selectList.get(0).getPath()),
                                new ImageContent.CreateImageContentCallback() {
                                    @Override
                                    public void gotResult(int i, String s, ImageContent imageContent) {
                                        if (i == 0) {
                                            imageContent.setStringExtra(Constants.TYPE, finalJMessageType);
                                            Message messageToSend = ((ChatActivity) activityContext).getConversation()
                                                    .createSendMessage(imageContent);

                                            sendMessage(messageToSend, ChatMessageBean.IMG_SEND);
                                        } else {
                                            Log.w(TAG, "Create image message failed!");
                                        }
                                    }
                                }
                        );

                    } else if (PictureMimeType.isHasVideo(selectList.get(0).getMimeType())) {
                        jMessageType = Constants.VIDEO_TYPE;

                        int index = selectList.get(0).getPath().lastIndexOf('/');
                        String fileName = "";
                        if (index > 0) {
                            fileName = selectList.get(0).getPath().substring(index + 1);
                        }
                        MediaMetadataRetriever media = new MediaMetadataRetriever();
                        media.setDataSource(selectList.get(0).getPath());

                        // thumbImage
                        Bitmap bitmap = media.getFrameAtTime();

                        Message message = null;

                        if (chatType == Constants.SINGLE_CHAT) {
                            try {
                                message = JMessageClient.createSingleVideoMessage(userId,
                                        Constants.JPUSH_APPKEY,
                                        bitmap,
                                        "jpeg",
                                        new File(selectList.get(0).getPath()),
                                        fileName,
                                        1000);
                            } catch (IOException e) {
                                Log.e(TAG, "Video pack to the message error");
                                e.printStackTrace();
                            }
                        }

                        if (message != null) {
                            sendMessage(message, ChatMessageBean.VIDEO_SEND);
                        }
                    }
                }
        }
    }

    /**
     * create different message bean type
     * @param messageToSend Jmessage obj
     * @param chatMessageBeanType my chat message bean obj
     */
    private void sendMessage(Message messageToSend, int chatMessageBeanType) {
        ChatMessageBean bean = new ChatMessageBean(messageToSend, chatMessageBeanType);
        // not finish uploading， show progress bar
        bean.setUpload(false);

        ((ChatActivity) activityContext).addNewMessageBeanToAdapter(bean);

        int nowListSize = ((ChatActivity) activityContext).getChatMessageBeanList().size();

        // if send to the server, erase progress bar
        messageToSend.setOnSendCompleteCallback(new BasicCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    ((ChatActivity) activityContext).getChatMessageBeanList().get(nowListSize - 1).setUpload(true);
                    ((ChatActivity) activityContext).getAdapter().notifyDataSetChanged();
                }
            }
        });

        // send msg to Jmessage server
        JMessageClient.sendMessage(messageToSend);
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
