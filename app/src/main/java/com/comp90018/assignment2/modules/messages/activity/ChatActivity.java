package com.comp90018.assignment2.modules.messages.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.messages.adapter.RvChatAdapter;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;
import com.comp90018.assignment2.modules.messages.fragment.KeyboardMoreFragment;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.DensityUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

/**
 * Chat window
 *
 * @author xiaotian
 */
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final static String TAG = "ChatActivity";

    private FrameLayout flKeyboardMore;

    /** default chat type */
    private int chatType = Constants.SINGLE_CHAT;

    private String userId = "";
    private String nickName = "";

    private Conversation conversation;

    private List<ChatMessageBean> chatMessageBeanList;
    private RvChatAdapter adapter;

    private KeyboardMoreFragment keyboardMoreFragment;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init view binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // init db
        db = FirebaseFirestore.getInstance();

        flKeyboardMore = binding.flKeyboardMore;

        // set conversation list and adapter
        chatMessageBeanList = new ArrayList<>();
        adapter = new RvChatAdapter(chatMessageBeanList, this);

        // init the fragemnt
        keyboardMoreFragment = new KeyboardMoreFragment();

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

        // get info from intent bundle
        chatType = getIntent().getIntExtra(Constants.TYPE, Constants.SINGLE_CHAT);
        keyboardMoreFragment.setChatType(chatType);

        if (chatType == Constants.SINGLE_CHAT) {
            userId = getIntent().getStringExtra(Constants.DATA_A);
            nickName = getIntent().getStringExtra(Constants.DATA_B);

            binding.textNickname.setText(nickName);
            keyboardMoreFragment.setUserId(userId);

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
                                    String email = userDTO.getEmail();
                                    if (email != null) {
                                        binding.textUserId.setText(email);
                                    } else {
                                        binding.textUserId.setText("");
                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }

        // get into the conversation
        if (chatType == Constants.SINGLE_CHAT) {
            conversation = JMessageClient.getSingleConversation(userId);
            // if there is no conversation record from server
            if (conversation == null) {
                conversation = Conversation.createSingleConversation(userId);
            }
        }

        // load chat history from server
        if (conversation.getAllMessage() != null) {
            for (cn.jpush.im.android.api.model.Message message : conversation.getAllMessage()) {
                addMessageFromServer(message);
            }


        }

    }

    /**
     * UI handler for chat activity
     *
     * Since Android only allows updates to the UI in the main thread,
     * the purpose of the Handler is to act as a bridge between threads
     * and then update the UI through the main thread
     */
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Constants.SCROLL_BOTTOM:
                    binding.rvChat.scrollToPosition(chatMessageBeanList.size() - 1);
                    break;

                // used for show/hide keyboard more buttons
                case Constants.HIDDEN_BOTTOM:
                    ViewGroup.LayoutParams params = binding.flKeyboardMore.getLayoutParams();
                    params.height = DensityUtil.INSTANCE.dp2px(ChatActivity.this, 0f);
                    binding.flKeyboardMore.setLayoutParams(params);
                    break;

                case Constants.SHOW_BOTTOM:
                    ViewGroup.LayoutParams params2 = binding.flKeyboardMore.getLayoutParams();
                    params2.height = DensityUtil.INSTANCE.dp2px(ChatActivity.this, 200f);
                    binding.flKeyboardMore.setLayoutParams(params2);
                    break;
            }
        }
    };

    /**
     * add new message in to adapter, refresh it, and roll to it
     * @param messageBean
     */
    public void addNewMessageBeanToAdapter(ChatMessageBean messageBean) {
        chatMessageBeanList.add(messageBean);
        adapter.notifyItemInserted(chatMessageBeanList.size() - 1);
        handler.sendEmptyMessageDelayed(Constants.SCROLL_BOTTOM, 200);
    }

    /**
     * add Message obj from server, change it to bean
     * and show it.
     * @param message
     */
    private void addMessageFromServer(cn.jpush.im.android.api.model.Message message) {
        if(message.getStatus() == MessageStatus.send_fail
                || message.getContentType() == ContentType.eventNotification){
            return;
        }

        // if the message is single chat message, but not belongs to target
        if (message.getTargetType() == ConversationType.single) {
            UserInfo targetInfo = (UserInfo) message.getTargetInfo();
            String targetId = targetInfo.getUserName();
            if (!targetId.equals(userId)) {
                return;
            }
        }

        // is recall message
        if (message.getContent() instanceof PromptContent) {
            chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.RETRACT));
            adapter.notifyItemInserted(chatMessageBeanList.size() - 1);
            handler.sendEmptyMessageDelayed(Constants.SCROLL_BOTTOM, 200);

            return;
        }

        switch (message.getContentType()) {
            case text:
                // TODO: zhe li!
        }



    }

    public Conversation getConversation() {
        return conversation;
    }

    public List<ChatMessageBean> getChatMessageBeanList() {
        return chatMessageBeanList;
    }

    public RvChatAdapter getAdapter() {
        return adapter;
    }
}