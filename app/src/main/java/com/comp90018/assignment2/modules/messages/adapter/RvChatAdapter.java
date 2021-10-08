package com.comp90018.assignment2.modules.messages.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.location.bean.LocationBean;
import com.comp90018.assignment2.modules.location.utils.LocationBeanConverter;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.TimeFormater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * adapter for chat bubble in rv_chat
 *
 * @author xiaotian li
 * @author BaseRecyclerViewAdapter https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class RvChatAdapter extends BaseMultiItemQuickAdapter<ChatMessageBean, BaseViewHolder> {

    private final Context mContext;

    private final static String TAG = "RvChatAdapter";

    // variavle for voice animation controll
    public int playVoiceIndex = -1;

    // if exceed 3 minutes, show time in the chat bubble
    private long timeInterval = 180000;

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public RvChatAdapter(@Nullable List<ChatMessageBean> data, Context context) {
        super(data);
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // attach different views with different type of item
        addItemType(ChatMessageBean.TEXT_SEND, R.layout.item_chat_text_send);
        addItemType(ChatMessageBean.TEXT_RECEIVE, R.layout.item_chat_text_receive);

        addItemType(ChatMessageBean.VOICE_SEND, R.layout.item_chat_voice_send);
        addItemType(ChatMessageBean.VOICE_RECEIVE, R.layout.item_chat_voice_receive);

        addItemType(ChatMessageBean.IMG_SEND, R.layout.item_chat_img_send);
        addItemType(ChatMessageBean.IMG_RECEIVE, R.layout.item_chat_img_receive);

        addItemType(ChatMessageBean.VIDEO_SEND, R.layout.item_chat_img_send);
        addItemType(ChatMessageBean.VIDEO_RECEIVE, R.layout.item_chat_img_receive);

        addItemType(ChatMessageBean.ADDRESS_SEND, R.layout.item_chat_address_send);
        addItemType(ChatMessageBean.ADDRESS_RECEIVE, R.layout.item_chat_address_receive);

        addItemType(ChatMessageBean.RETRACT, R.layout.item_chat_retract);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChatMessageBean item) {
        if (item.getItemType() == ChatMessageBean.RETRACT || item.getMessage() == null) {
            // don't need to do special things
            return;
        }

        // show time at the top of a lot of messages
        if (helper.getAdapterPosition() == 0) {
            TimeFormater timeFormater = new TimeFormater(mContext, item.getMessage().getCreateTime());
            helper.setText(R.id.tv_time, timeFormater.getDetailTime());
            helper.getView(R.id.tv_time).setVisibility(View.VISIBLE);

        } else {
            ChatMessageBean prevBean = getData().get(helper.getAdapterPosition() - 1);
            ChatMessageBean nowBean = item;

            if (prevBean != null && nowBean != null) {
                if (prevBean.getMessage() != null && nowBean.getMessage() != null) {
                    long prevTime = prevBean.getMessage().getCreateTime();
                    long nowTime = nowBean.getMessage().getCreateTime();

                    // if exceed time interval, show time in the buble
                    if (nowTime - prevTime > timeInterval) {
                        TimeFormater timeFormater2 = new TimeFormater(mContext, nowBean.getMessage().getCreateTime());
                        helper.setText(R.id.tv_time, timeFormater2.getDetailTime());
                        helper.getView(R.id.tv_time).setVisibility(View.VISIBLE);
                    } else {
                        // GONE: erase the view
                        // INVISIBLE: view is not displaying but consume the space
                        helper.getView(R.id.tv_time).setVisibility(View.GONE);
                    }

                } else {
                    // no prev bean, no need to show time
                    helper.getView(R.id.tv_time).setVisibility(View.GONE);
                }

            } else {
                // other exception condition, no need to show time
                helper.getView(R.id.tv_time).setVisibility(View.GONE);
            }
        }

        // set avatar
        String userId = item.getMessage().getFromUser().getUserName();
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

                                // storage Reference of firebase
                                StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());

                                // query image with the reference, then show avatar
                                Glide.with(mContext)
                                        .load(imgReference)
                                        .into((CircleImageView) helper.getView(R.id.iv_head));
                            } else {
                                Log.d(TAG, "###" + userId + "###" + ", No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        // handle special chat bubble types
        switch (helper.getItemViewType()) {
            case ChatMessageBean.TEXT_SEND:
            case ChatMessageBean.TEXT_RECEIVE:
                // text: only set text view will be good
                helper.setText(R.id.tv, ((TextContent) item.getMessage().getContent()).getText());
                break;

            case ChatMessageBean.IMG_SEND:
            case ChatMessageBean.IMG_RECEIVE:
                // image: use glide to retrive image and and attach it to the view
                // Caches remote data with both DATA and RESOURCE, and local data with RESOURCE only.
                RequestOptions options = new RequestOptions();
                options.centerInside()
                        .placeholder(R.drawable.default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                ImageContent imageContent = ((ImageContent) item.getMessage().getContent());
                // Gets the local path of the corresponding thumbnail
                Glide.with(mContext)
                        .load(imageContent.getLocalThumbnailPath())
                        .apply(options)
                        .into((ImageView) helper.getView(R.id.iv));
                break;

            case ChatMessageBean.VOICE_SEND:
            case ChatMessageBean.VOICE_RECEIVE:
                // voice: show duration, play the voice and animation of lines
                // show voice duration sec
                helper.setText(R.id.tv, ((VoiceContent) item.getMessage().getContent()).getDuration() + "'");

                // play animation
                ImageView imageVoiceLines = helper.getView(R.id.iv_voice);
                // read animation list
                AnimationDrawable voiceAnimation = (AnimationDrawable) imageVoiceLines.getDrawable();

                voiceAnimation.start();

                // only one bubble play the animation
                if (playVoiceIndex == helper.getAdapterPosition()) {
                    if (!voiceAnimation.isRunning()) {
                        voiceAnimation.start();
                    }
                } else {
                    if (voiceAnimation.isRunning()) {
                        voiceAnimation.stop();
                    }
                }

                break;

            case ChatMessageBean.VIDEO_SEND:
            case ChatMessageBean.VIDEO_RECEIVE:
                // handle video: reuse image
                VideoContent videoContent = (VideoContent) item.getMessage().getContent();
                RequestOptions optionsVideo = new RequestOptions();
                optionsVideo.centerInside()
                        .placeholder(R.color.white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                // video thumb image is kinda different
                // https://docs.jiguang.cn/jmessage/client/im_android_api_docs/
                videoContent.downloadThumbImage(item.getMessage(), new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        Glide.with(mContext)
                                .load(file.getPath())
                                .apply(optionsVideo)
                                .into((ImageView) helper.getView(R.id.iv));
                    }
                });

                break;

            case ChatMessageBean.ADDRESS_SEND:
            case ChatMessageBean.ADDRESS_RECEIVE:
                LocationContent addressContent = (LocationContent) item.getMessage().getContent();
                String textAddress = addressContent.getAddress();
                double latitude = addressContent.getLatitude().doubleValue();
                double longitude = addressContent.getLongitude().doubleValue();

                helper.setText(R.id.tv_detail, textAddress);

                LocationBean locationBean = LocationBean.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .textAddress(textAddress)
                        .build();

                // decrypt coordinate if in China
                LocationBeanConverter.convertLocationToBdMapLocation(locationBean);
                LatLng center = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());

                // handle map display
                MapView mapView = (MapView) helper.getView(R.id.map_view);
                mapView.showZoomControls(false);
                mapView.showScaleControl(false);
                BaiduMap baiduMap = mapView.getMap();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue));

                Overlay marker = baiduMap.addOverlay(markerOptions);

                // zoom the map
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(center)
                        .zoom(18)
                        .build();

                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                baiduMap.setMapStatus(mapStatusUpdate);
                break;

            default:
                return;
        }

        // progress indicator
        if (item.isUpload()) {
            helper.getView(R.id.pb).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.pb).setVisibility(View.VISIBLE);
        }

        // TODO: attach avatar click event
    }

    public int getPlayVoiceIndex() {
        return playVoiceIndex;
    }

    public void setPlayVoiceIndex(int playVoiceIndex) {
        this.playVoiceIndex = playVoiceIndex;
    }
}