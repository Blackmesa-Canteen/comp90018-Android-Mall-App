package com.comp90018.assignment2.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.im.android.api.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImMsgBean implements Parcelable {

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

    protected ImMsgBean(Parcel in) {
        sender = in.readString();
        recipient = in.readString();
        id = in.readString();
        msgType = in.readInt();
        senderType = in.readInt();
        time = in.readString();
        image = in.readString();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        name = in.readString();
        content = in.readString();
        fileLoadstye = in.readInt();
    }

    public static final Creator<ImMsgBean> CREATOR = new Creator<ImMsgBean>() {
        @Override
        public ImMsgBean createFromParcel(Parcel in) {
            return new ImMsgBean(in);
        }

        @Override
        public ImMsgBean[] newArray(int size) {
            return new ImMsgBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(recipient);
        dest.writeString(id);
        dest.writeInt(msgType);
        dest.writeInt(senderType);
        dest.writeString(time);
        dest.writeString(image);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeString(name);
        dest.writeString(content);
        dest.writeInt(fileLoadstye);
    }
}
