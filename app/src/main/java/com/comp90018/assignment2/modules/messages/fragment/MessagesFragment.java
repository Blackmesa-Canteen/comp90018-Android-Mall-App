package com.comp90018.assignment2.modules.messages.fragment;

import android.view.View;
import android.widget.ImageView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import cn.jmessage.biz.httptask.task.GetEventNotificationTaskMng;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 *
 * @author you
 */
public class MessagesFragment extends BaseFragment {


    /**
     * 缺省占位
     */
    private ImageView imageView;

    private List<Conversation> conversationList;

    @Override
    public View inflateView() {

        conversationList = new ArrayList<>();


        /*
         * 当你实际写出这个fragment的layout文件后，用View view = View.inflate(mContext, R.layout.fragment_XXX, null); 绑定
         * 然后该各fragment的各种view可以通过view.findViewById()来绑定
         * 然后return view.
         * */

        imageView = new ImageView(activityContext);

        return imageView;
    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */
        imageView.setImageResource(R.drawable.message);
    }

    /**
     * watch for new message event
     * @param event message event
     */
    public void onEventMainThread(MessageEvent event){
        boolean handleable = false;
        Message message = event.getMessage();

        // single chat event
        if (message.getTargetType() == ConversationType.single) {
            // get the target INFO
            UserInfo targetInfo = (UserInfo) message.getTargetInfo();
        }



    }
}
