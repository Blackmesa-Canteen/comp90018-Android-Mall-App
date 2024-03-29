package com.comp90018.assignment2.modules.messages.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.comp90018.assignment2.config.GlideEngine;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.utils.LocationHelper;
import com.comp90018.assignment2.modules.location.utils.OnGotLocationBeanCallback;
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
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * fragment when click on keyboard's more button.
 * Can be only used in ChatActivity
 *
 * @author xiaotian li
 */
public class KeyboardMoreFragment extends BaseFragment {

    private String userId;
    private final static String TAG = "[Dev]MoreFragment";
    private int chatType = Constants.SINGLE_CHAT;

    private List<KeyboardMoreItemBean> itemList;
    private KeyboardMoreItemAdapter adapter;

    private RecyclerView rv_root;
    private PromptDialog dialog;

    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.fragment_chat_keyboard_more, null);
        rv_root = view.findViewById(R.id.rv_chat_keyboard_more);

        itemList = new ArrayList<>();

        dialog = new PromptDialog((ChatActivity) activityContext);

        itemList.clear();
        itemList.add(new KeyboardMoreItemBean(R.drawable.media, "Gallery"));
        itemList.add(new KeyboardMoreItemBean(R.drawable.location_fill, "Location"));

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
                                .imageEngine(GlideEngine.createGlideEngine())
                                .maxSelectNum(1)
                                .minSelectNum(1)
                                .selectionMode(PictureConfig.SINGLE)
                                .isPreviewImage(true)
                                .isCompress(true)
                                .forResult(Constants.REQUEST_CODE_A);

                        break;

                    case 1:
                        // location button clicked

                        // 1. get current location
                        LocationHelper locationHelper = new LocationHelper(activityContext);
                        dialog.showLoading("Locating...");
                        locationHelper.getLiveLocating(new OnGotLocationBeanCallback() {
                            @Override
                            public void gotLocationCallback(LocationBean locationBean) {
                                Log.d(TAG, "location: " + locationBean.toString());
                                String jMessageType = Constants.ADDRESS;

                                String textAddress = null;
                                if (locationBean.getRoad() != null && locationBean.getCity() != null) {
                                    textAddress = locationBean.getCity() + ", " + locationBean.getRoad();
                                } else if (locationBean.getCity() != null) {
                                    textAddress = locationBean.getCity();
                                } else {
                                    textAddress = locationBean.getTextAddress();
                                }

                                double latitude = locationBean.getLatitude();
                                double longitude = locationBean.getLongitude();
                                // gen Jmessage location content
                                LocationContent locationContent = new LocationContent(
                                        latitude,
                                        longitude,
                                        18,
                                        textAddress);

                                locationContent.setStringExtra(Constants.TYPE, jMessageType);

                                Message messageToSend = ((ChatActivity) activityContext).getConversation()
                                        .createSendMessage(locationContent);

                                sendMessage(messageToSend, ChatMessageBean.ADDRESS_SEND);
                                dialog.dismiss();
                            }
                        });

                        break;

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
                if (selectList != null && selectList.size() == 1 && selectList.get(0) != null) {
                    Log.d(TAG, "img path: " + selectList.get(0).getRealPath());
                    String jMessageType = Constants.IMG_TYPE;
                    if (PictureMimeType.isHasImage(selectList.get(0).getMimeType())) {
                        jMessageType = Constants.IMG_TYPE;

                        String finalJMessageType = jMessageType;
                        ImageContent.createImageContentAsync(
                                new File(selectList.get(0).getRealPath()),
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
                                            Log.w(TAG, s);
                                        }
                                    }
                                }
                        );

                    } else if (PictureMimeType.isHasVideo(selectList.get(0).getMimeType())) {
                        jMessageType = Constants.VIDEO_TYPE;

                        int index = selectList.get(0).getRealPath().lastIndexOf('/');
                        String fileName = "";
                        if (index > 0) {
                            fileName = selectList.get(0).getRealPath().substring(index + 1);
                        }
                        MediaMetadataRetriever media = new MediaMetadataRetriever();
                        media.setDataSource(selectList.get(0).getRealPath());

                        // thumbImage
                        Bitmap bitmap = media.getFrameAtTime();

                        Message message = null;
                        if (chatType == Constants.SINGLE_CHAT) {
                            try {
                                message = JMessageClient.createSingleVideoMessage(userId,
                                        Constants.JPUSH_APPKEY,
                                        bitmap,
                                        "jpeg",
                                        new File(selectList.get(0).getRealPath()),
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
     *
     * @param messageToSend       Jmessage obj
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
