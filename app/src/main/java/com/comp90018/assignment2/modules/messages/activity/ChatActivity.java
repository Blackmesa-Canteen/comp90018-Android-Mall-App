package com.comp90018.assignment2.modules.messages.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityChatBinding;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.messages.adapter.RvChatAdapter;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;
import com.comp90018.assignment2.modules.messages.fragment.KeyboardMoreFragment;
import com.comp90018.assignment2.modules.messages.view.RecordButtonTextView;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.DensityUtil;
import com.comp90018.assignment2.utils.VoiceMessageUtil;
import com.comp90018.assignment2.utils.activity.ImagePreviewActivity;
import com.comp90018.assignment2.utils.activity.VideoPlayerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Chat window
 *
 * @author xiaotian
 */
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final static String TAG = "ChatActivity";

    private FrameLayout flKeyboardMore;

    /**
     * default chat type
     */
    private int chatType = Constants.SINGLE_CHAT;

    private String userId = "";
    private String nickName = "";

    private Conversation conversation;

    private List<ChatMessageBean> chatMessageBeanList;
    private RvChatAdapter adapter;

    private KeyboardMoreFragment keyboardMoreFragment;

    private UserDTO targetUserDTO;

    FirebaseFirestore db;

    boolean isShowingRecordingTextView = false;
    private VoiceMessageUtil voiceMessageUtil;

    private PromptDialog dialog;

    private EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JMessageClient.registerEventReceiver(this);

        // init view binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        //
        dialog = new PromptDialog(this);

        // init db
        db = FirebaseFirestore.getInstance();

        flKeyboardMore = binding.flKeyboardMore;

        // set conversation list and adapter
        chatMessageBeanList = new ArrayList<>();
        adapter = new RvChatAdapter(chatMessageBeanList, this);

        // init the fragemnt
        keyboardMoreFragment = new KeyboardMoreFragment();

        // set emoj
        emojIcon = new EmojIconActions(this, (View) binding.rlRoot, binding.etMessageInput, binding.ivEmoji);

        // get info from intent bundle
        chatType = getIntent().getIntExtra(Constants.TYPE, Constants.SINGLE_CHAT);
        keyboardMoreFragment.setChatType(chatType);

        if (chatType == Constants.SINGLE_CHAT) {
            userId = getIntent().getStringExtra(Constants.DATA_A);
            targetUserDTO = getIntent().getParcelableExtra("targetUserDTO");

            nickName = targetUserDTO.getNickname();
            binding.textNickname.setText(nickName);
            keyboardMoreFragment.setUserId(userId);

            String email = targetUserDTO.getEmail();
            if (email != null) {
                binding.textUserId.setText(email);
            } else {
                binding.textUserId.setText("");
            }

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
                addMessageBeanFromJMessage(message);
            }
        }

        // init activity title
        initTitle();

        // init chat message list
        initList();

        // init keyboard
        initInput();
        initMore();

        // init voice service
        initVoiceService();

    }


    boolean showOption = false;
    boolean showEmoji = false;

    /**
     * init more options behavior
     */
    private void initMore() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fl_keyboard_more, keyboardMoreFragment)
                .commit();

        fragmentManager.beginTransaction()
                .hide(keyboardMoreFragment)
                .commit();

        handler.sendEmptyMessage(Constants.HIDDEN_BOTTOM);

        // set click emoj btn behaviour
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.d(TAG, "emoji open");

                // if click emoj, hide normal keyboard
//                InputMethodManager inputSoftKeys =
//                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputSoftKeys.hideSoftInputFromWindow(binding.etMessageInput.getWindowToken(), 0);

                if (showOption) {
                    showOption = false;
                    hideOption();
                }

                showEmoji = !showEmoji;
            }

            @Override
            public void onKeyboardClose() {
                Log.d(TAG, "emoji close");
                showEmoji = false;
            }
        });

        // set click keyboard more behavior
        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the emoj is on, no show more
                if (!showEmoji) {
                    // hide normal keyboard
                    InputMethodManager inputSoftKeys =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputSoftKeys.hideSoftInputFromWindow(binding.etMessageInput.getWindowToken(), 0);

                    // click again to close
                    if (showOption) {
                        showOption = false;
                        hideOption();
                    } else {
                        showOption = true;
                        showOption();
                    }
                }
            }
        });

    }

    /**
     * hide keyboard more option
     */
    private void hideOption() {
        handler.sendEmptyMessage(Constants.HIDDEN_BOTTOM);
        getSupportFragmentManager()
                .beginTransaction()
                .hide(keyboardMoreFragment)
                .commit();
    }

    /**
     * show keyboard more option
     */
    private void showOption() {
        handler.sendEmptyMessage(Constants.SHOW_BOTTOM);
        getSupportFragmentManager()
                .beginTransaction()
                .show(keyboardMoreFragment)
                .commit();
    }

    /**
     * send plain text with send btn
     */
    private void initInput() {
        binding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.etMessageInput.getText().toString();
                sentTextMessage(text);
                binding.etMessageInput.setText("");
            }
        });
    }


    /**
     * init chat message bubble list
     */
    private void initList() {
        binding.rvChat.setAdapter(adapter);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (chatMessageBeanList.get(position).getMessage().getDirect() == MessageDirect.send) {
                    ;
                } else {
                    if (chatType == Constants.SINGLE_CHAT) {
                        if (targetUserDTO != null) {
                            // TODO activity jumping
//                        Intent goToUserPageActivity =
                            Toast.makeText(ChatActivity.this, "jump to user:" + targetUserDTO.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    // now there is only single chat type, no else.
                }
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                // click image bubble and preview the img
                if (chatMessageBeanList.get(position).getItemType() == ChatMessageBean.IMG_RECEIVE
                        || chatMessageBeanList.get(position).getItemType() == ChatMessageBean.IMG_SEND) {

                    int first = ((LinearLayoutManager) Objects.requireNonNull(binding.rvChat.getLayoutManager())).findFirstVisibleItemPosition();
                    int last = ((LinearLayoutManager) Objects.requireNonNull(binding.rvChat.getLayoutManager())).findLastVisibleItemPosition();

                    if (position < first || position > last) {
                        return;
                    }

                    ImageContent imageContent = (ImageContent) chatMessageBeanList.get(position).getMessage().getContent();

                    String path = "";
                    if (!TextUtils.isEmpty(imageContent.getLocalThumbnailPath())) {
                        path = imageContent.getLocalThumbnailPath();
                    }
                    dialog.showLoading("Downloading image...");
                    String finalPath = path;
                    imageContent.downloadOriginImage(chatMessageBeanList.get(position).getMessage(), new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {
                            dialog.dismiss();
                            // preview the image
                            Intent goToImagePreviewIntent = new Intent(ChatActivity.this, ImagePreviewActivity.class);
                            if (i == 0) {
                                goToImagePreviewIntent.putExtra("image_path", file.getPath());
                            } else {
                                goToImagePreviewIntent.putExtra("image_path", finalPath);
                            }
                            startActivity(goToImagePreviewIntent);
                        }
                    });
                }

                // if video
                if (chatMessageBeanList.get(position).getItemType() == ChatMessageBean.VIDEO_RECEIVE ||
                        chatMessageBeanList.get(position).getItemType() == ChatMessageBean.VIDEO_SEND) {
                    cn.jpush.im.android.api.model.Message message = chatMessageBeanList
                            .get(position).getMessage();

                    VideoContent videoContent = (VideoContent) message.getContent();

                    dialog.showLoading("Downloading video...");
                    videoContent.downloadVideoFile(message, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {
                            dialog.dismiss();

                            if (file == null || i != 0) {
                                Log.w(TAG, "Failed to download video.");
                                return;
                            }

                            Intent goToVideoPlayIntent = new Intent(ChatActivity.this, VideoPlayerActivity.class);
                            goToVideoPlayIntent.putExtra("video_url", file.getPath());
                            startActivity(goToVideoPlayIntent);
                        }
                    });
                }

                // if record
                if (chatMessageBeanList.get(position).getItemType() == ChatMessageBean.VOICE_SEND
                        || chatMessageBeanList.get(position).getItemType() == ChatMessageBean.VOICE_RECEIVE) {
                    if (voiceMessageUtil != null) {
                        voiceMessageUtil.playVoice(chatMessageBeanList, position);
                    }
                }
            }
        });

    }

    /**
     * set up voice message handling logic
     */
    private void initVoiceService() {
        voiceMessageUtil = new VoiceMessageUtil(this, adapter);

        binding.ivSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingRecordingTextView) {
                    binding.tvSound.setVisibility(View.INVISIBLE);
                    binding.etMessageInput.setVisibility(View.VISIBLE);
                    binding.ivSound.setImageResource(R.drawable.microphone);
                } else {
                    binding.tvSound.setVisibility(View.VISIBLE);
                    binding.etMessageInput.setVisibility(View.INVISIBLE);
                    binding.ivSound.setImageResource(R.drawable.ic_action_keyboard);
                }

                isShowingRecordingTextView = !isShowingRecordingTextView;
            }
        });

        binding.tvSound.setConversation(conversation);

        binding.tvSound.setOnNewMessage(new RecordButtonTextView.OnNewMessage() {
            @Override
            public void newMessage(cn.jpush.im.android.api.model.Message message) {
                if (message == null) {
                    return;
                }

                addMessageBeanFromJMessage(message);
                int nowSize = chatMessageBeanList.size();
                chatMessageBeanList.get(nowSize - 1).setUpload(false);
                adapter.notifyItemChanged(nowSize - 1);

                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            chatMessageBeanList.get(nowSize - 1).setUpload(true);
                            adapter.notifyItemChanged(nowSize - 1);
                        }
                    }
                });
            }
        });
    }

    /**
     * set up activity's title click event
     */
    private void initTitle() {
        binding.llUserTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent goToUserPageActivityIntent = new Intent();
                // TODO: jump to user detail
                if (targetUserDTO != null) {
                    Toast.makeText(ChatActivity.this, "去用户首页:" + targetUserDTO.getEmail(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * send plain text to Jmessage server
     *
     * @param text text
     */
    private void sentTextMessage(String text) {
        if (!TextUtils.isEmpty(text)) {
            TextContent textContent = new TextContent(text);
            if (conversation != null) {
                cn.jpush.im.android.api.model.Message message = conversation.createSendMessage(textContent);
                ChatMessageBean bean = new ChatMessageBean(message, ChatMessageBean.TEXT_SEND);
                bean.setUpload(false);
                chatMessageBeanList.add(bean);

                adapter.notifyItemInserted(chatMessageBeanList.size() - 1);

                int nowSize = chatMessageBeanList.size();

                handler.sendEmptyMessageDelayed(Constants.SCROLL_BOTTOM, 200);
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            chatMessageBeanList.get(nowSize - 1).setUpload(true);
                            adapter.notifyItemChanged(chatMessageBeanList.size() - 1);
                        }
                    }
                });

                JMessageClient.sendMessage(message);
            }

        }
    }

    /**
     * UI handler for chat activity
     * <p>
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
     *
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
     *
     * @param message
     */
    private void addMessageBeanFromJMessage(cn.jpush.im.android.api.model.Message message) {
        if (message.getStatus() == MessageStatus.send_fail
                || message.getContentType() == ContentType.eventNotification) {
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
                if (message.getDirect() == MessageDirect.send) {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.TEXT_SEND));
                } else {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.TEXT_RECEIVE));
                }
                break;

            case image:
                if (message.getDirect() == MessageDirect.send) {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.IMG_SEND));
                } else {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.IMG_RECEIVE));
                }
                break;

            case voice:
                if (message.getDirect() == MessageDirect.send) {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.VOICE_SEND));
                } else {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.VOICE_RECEIVE));
                }
                break;

            case video:
                if (message.getDirect() == MessageDirect.send) {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.VIDEO_SEND));
                } else {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.VIDEO_RECEIVE));
                }
                break;

            case location:
                if (message.getDirect() == MessageDirect.send) {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.ADDRESS_SEND));
                } else {
                    chatMessageBeanList.add(new ChatMessageBean(message, ChatMessageBean.ADDRESS_RECEIVE));
                }
                break;

            case custom:
                // no custom for now
                break;
        }

        adapter.notifyItemInserted(chatMessageBeanList.size() - 1);
        handler.sendEmptyMessageDelayed(Constants.SCROLL_BOTTOM, 200);
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

    /**
     * receive new incoming message
     *
     * @param event jmessage message event
     */
    public void onEventMainThread(MessageEvent event) {

        addMessageBeanFromJMessage(event.getMessage());
    }

    /**
     * receive offline message
     *
     * @param event
     */
    public void onEvent(OfflineMessageEvent event) {
        for (cn.jpush.im.android.api.model.Message message : event.getOfflineMessageList()) {
            addMessageBeanFromJMessage(message);
        }
    }

    /**
     * handle recalled message
     */
    public void onEvent(MessageRetractEvent event) {
        for (ChatMessageBean bean : chatMessageBeanList) {
            if (event.getRetractedMessage().getId() == bean.getMessage().getId()) {
                bean.setItemType(ChatMessageBean.RETRACT);
                adapter.notifyItemChanged(chatMessageBeanList.indexOf(bean));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // all callbacks and messages will be removed
        handler.removeCallbacksAndMessages(null);
        if (voiceMessageUtil != null) {
            // stop playing when exit
            voiceMessageUtil.getMediaPlayer().stop();
        }

        JMessageClient.unRegisterEventReceiver(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (chatType == Constants.SINGLE_CHAT) {
            JMessageClient.enterSingleConversation(userId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE_B:
                chatMessageBeanList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }
}