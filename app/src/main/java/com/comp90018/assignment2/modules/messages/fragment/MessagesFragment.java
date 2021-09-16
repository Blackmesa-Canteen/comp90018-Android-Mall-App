package com.comp90018.assignment2.modules.messages.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.MainActivity;
import com.comp90018.assignment2.modules.messages.activity.ChatActivity;
import com.comp90018.assignment2.modules.messages.adapter.RvConversationsAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * message fragment
 * @author xiaotian li
 */
public class MessagesFragment extends BaseFragment {

    private final static String TAG = "MessagesFragment";

    private List<Conversation> conversationList;

    private CircleImageView imgAvatar;
    private RelativeLayout rlmParent;
    private TextView textNewNotificationTime;
    private TextView textNewNotification;
    private RecyclerView rvFragmentMessaging;

    private RvConversationsAdapter adapter;

    FirebaseFirestore db;

    @Override
    public View inflateView() {

        View view = View.inflate(activityContext, R.layout.fragment_messages, null);
        imgAvatar = (CircleImageView) view.findViewById( R.id.img_avatar );
        rlmParent = (RelativeLayout) view.findViewById( R.id.rlm_parent );
        textNewNotificationTime = (TextView) view.findViewById( R.id.text_new_notification_time );
        textNewNotification = (TextView) view.findViewById( R.id.text_new_notification );
        rvFragmentMessaging = (RecyclerView) view.findViewById( R.id.rv_fragment_messaging );

        // init db
        db = FirebaseFirestore.getInstance();
        conversationList = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // attach event listener
        JMessageClient.registerEventReceiver(this);

        // set chat messages items
        rvFragmentMessaging.setLayoutManager(new LinearLayoutManager(activityContext));

        adapter = new RvConversationsAdapter(R.layout.item_list_view_messages, conversationList, activityContext);
        rvFragmentMessaging.setAdapter(adapter);
        rvFragmentMessaging.setHasFixedSize(true);

        // jump to chat activity
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                conversationList.get(position).updateConversationExtra("");
                adapter.notifyItemChanged(position);

                if (conversationList.get(position).getType() == ConversationType.single) {
                    UserInfo jMessageUserInfo = (UserInfo) conversationList.get(position).getTargetInfo();
                    String userId = jMessageUserInfo.getUserName();

                    // show process dialog
                    ProgressDialog progressDialog=new ProgressDialog(activityContext);
                    progressDialog.setTitle("Loading");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    db.collection(Constants.USERS_COLLECTION)
                            .document(userId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        UserDTO targetUserDTO = task.getResult().toObject(UserDTO.class);

                                        Intent goToChatActivityIntent = new Intent(activityContext, ChatActivity.class);
                                        goToChatActivityIntent.putExtra("targetUserDTO", targetUserDTO);
                                        goToChatActivityIntent.putExtra(Constants.TYPE, Constants.SINGLE_CHAT);
                                        progressDialog.dismiss();
                                        startActivity(goToChatActivityIntent);

                                    } else {
                                        progressDialog.dismiss();
                                        new AlertDialog.Builder(activityContext).setMessage("query db failed, please try again later.").setPositiveButton("ok", null).show();
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }

            }
        });
    }

    @Override
    public void loadData() {
        // message fragment is special from the other fragments
        // no need to query db here
        ;
    }


    @Override
    public void onResume() {
        super.onResume();

//        if (needReRegister) {
//            JMessageClient.registerEventReceiver(this);
//            needReRegister = false;
//        }

        refreshConversationList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode){
            case Constants.REQUEST_CODE_A:
                refreshConversationList();
                break;

            default:
                return;
        }
    }


    /**
     * Refresh conversation list from IM server
     */
    public void refreshConversationList() {
        if (JMessageClient.getMyInfo() != null) {
            conversationList.clear();
            conversationList.addAll(JMessageClient.getConversationList());
            adapter.notifyDataSetChanged();
        } else {
            Log.w(TAG, "没有登录，我不愿意刷新");
        }

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

            // find matched item to refresh
            for (Conversation conversation : conversationList) {
                if (conversation.getType() == ConversationType.single) {
                    UserInfo targetInfo1 = (UserInfo) conversation.getTargetInfo();

                    if (targetInfo1.getUserName().equals(targetInfo.getUserName())) {
                        conversation.updateConversationExtra(Constants.NEW_MESSAGE);

                        handleable = true;

                        adapter.notifyItemChanged(conversationList.indexOf(conversation));
                    }
                }
            }

            // if there is no such new coming conversation
            if (!handleable) {
                Conversation conversation = JMessageClient.getSingleConversation(targetInfo.getUserName());

                if (conversation.getTargetInfo() instanceof UserInfo) {
                    Conversation newConversation = conversation;
                    newConversation.updateConversationExtra(Constants.NEW_MESSAGE);
                    conversationList.add(newConversation);
                }
                adapter.notifyItemChanged(conversationList.size() - 1);
            }

            handleRedSpot();

        }
    }

    /**
     * show red spot in the mainActivities message Fragement button
     */
    private void handleRedSpot() {
        if (activityContext == null) {
            return;
        }

        boolean hasNewMessage = false;

        for (Conversation conversation : conversationList) {
            if (conversation.getExtra().equals(Constants.NEW_MESSAGE)) {
                setNewRedSpot(true);
                hasNewMessage = true;
            }
        }

        if (!hasNewMessage) {

            setNewRedSpot(false);
        }
    }

    /**
     * switch red spot to show whether there is new message or not
     * @param b turn on or turn off
     */
    private void setNewRedSpot(boolean b) {
        if (activityContext == null) {
            return;
        }

        ((MainActivity) activityContext).switchRedSpotOnMessageBtn(b);
    }
}
