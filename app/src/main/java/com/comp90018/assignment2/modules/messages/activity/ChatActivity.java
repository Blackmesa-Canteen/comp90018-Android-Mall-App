package com.comp90018.assignment2.modules.messages.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;
import com.comp90018.assignment2.utils.Constants;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

/**
 * Chat window
 *
 * @author xiaotian
 */
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final static String TAG = "ChatActivity";

    /** default chat type */
    private int type = Constants.SINGLE_CHAT;

    private String userName = "";
    private String nickName = "";

    private Conversation conversation;

    private List<ChatMessageBean> chatMessageBeanList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init view binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // set emoj
        EmojIconActions emojIcon = new EmojIconActions(this, (View) binding.rlRoot, binding.etMessageInput, binding.ivEmoji);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "emoji open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "emoji close");
            }
        });
    }
}