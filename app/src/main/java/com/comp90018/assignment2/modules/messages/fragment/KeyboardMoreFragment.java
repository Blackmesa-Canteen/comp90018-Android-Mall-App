package com.comp90018.assignment2.modules.messages.fragment;

import android.app.Activity;
import android.content.Intent;
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
import java.util.List;

import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Message;

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
                                        if (resultCode == 0) {
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
        // not finish uploading
        bean.setUpload(false);
        // TODO: wait finishing
    }
}
