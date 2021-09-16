package com.comp90018.assignment2.modules.messages.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import cn.jpush.im.android.api.model.Message;

/**
 * bean for a single message
 *
 * @author xiaotian li
 * @author Jmessage project authority https://docs.jiguang.cn/jmessage/guideline/jmessage_guide/
 * @author BaseRecyclerViewAdapter https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class ChatMessageBean implements MultiItemEntity {

    // item type
    public static final int TEXT_SEND = 1;
    public static final int TEXT_RECEIVE = 2;
    public static final int IMG_SEND = 3;
    public static final int IMG_RECEIVE = 4;
    public static final int VOICE_SEND = 5;
    public static final int VOICE_RECEIVE = 6;
    public static final int VIDEO_SEND = 7;
    public static final int VIDEO_RECEIVE = 8;
    public static final int ADDRESS_SEND = 9;
    public static final int ADDRESS_RECEIVE = 10;


    public static final int RETRACT = 99;

    private int itemType = 1;
    private boolean upload = true;
    private Message message;

    public ChatMessageBean(Message message, int itemType) {
        this.itemType = itemType;
        this.message = message;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
