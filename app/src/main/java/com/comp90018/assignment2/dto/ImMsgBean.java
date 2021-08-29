package com.comp90018.assignment2.dto;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.im.android.api.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImMsgBean {

    public final static int CHAT_SENDER_OTHER= 0;
    public final static int CHAT_SENDER_ME = 1;

    public final static int CHAT_MSGTYPE_TEXT = 11;
    public final static int CHAT_MSGTYPE_IMG = 12;

    private String sender;
    private String recipient;
    private String id;
    private int msgType;
    private int senderType;
    private String time;
    private String image;
    private int imageWidth;
    private int imageHeight;
    private String name;
    private String content;

    private File file;
    private int fileLoadstye;
    private Message mMessage;

    public static class CommentRequestData {
        private int count;

        private int result;

        private ArrayList<ImMsgBean> data;

        public int getCount() {
            return count;
        }

        public int getResult() {
            return result;
        }

        public ArrayList<ImMsgBean> getData() {
            return data;
        }
    }
}
