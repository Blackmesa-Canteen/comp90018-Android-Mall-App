package com.comp90018.assignment2.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.comp90018.assignment2.modules.messages.adapter.RvChatAdapter;
import com.comp90018.assignment2.modules.messages.bean.ChatMessageBean;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;

/**
 * util for handle voice message playing
 * @author xiaotian
 */
public class VoiceMessageUtil {

    private Context context;
    private final MediaPlayer mediaPlayer;
    private RvChatAdapter chatAdapter;

    private FileInputStream fileInputStream;
    private FileDescriptor fileDescriptor;

    public VoiceMessageUtil(Context context, RvChatAdapter chatAdapter) {
        this.context = context;
        this.chatAdapter = chatAdapter;
        mediaPlayer = new MediaPlayer();

        // handling management of volume, ringer modes and audio routing
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // not ringing and no call established
        audioManager.setMode(AudioManager.MODE_NORMAL);

        // TODO: not sure
        if (!audioManager.isSpeakerphoneOn()) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);
        }

        // Used to identify the volume of audio streams for the phone ring
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    /**
     * play voice message in the message bean list
     * @param list message bean list
     * @param position message item position
     */
    public void playVoice(List<ChatMessageBean> list, int position) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            return;
        }

        Message message = list.get(position).getMessage();
        VoiceContent voiceContent = (VoiceContent) message.getContent();

        try {
            // Resets the MediaPlayer to its uninitialized state.
            mediaPlayer.reset();

            // put voice file into stream to play
            fileInputStream = new FileInputStream(voiceContent.getLocalPath());
            fileDescriptor = fileInputStream.getFD();
            mediaPlayer.setDataSource(fileDescriptor);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // Prepares the player for playback, synchronously.
            // After setting the datasource and the display surface,
            // we need to either call prepare() or prepareAsync().
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                    chatAdapter.setPlayVoiceIndex(position);
                    chatAdapter.notifyItemChanged(position);
                }
            });

            // if finished playing
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    chatAdapter.setPlayVoiceIndex(-1);
                    chatAdapter.notifyItemChanged(position);
                    mp.reset();

                    // auto play next one
                    // TODO not sure whether need it or not
                    if(list.size() - 1 > position && (list.get(position + 1).getItemType() == ChatMessageBean.VOICE_RECEIVE
                            ||list.get(position + 1).getItemType() == ChatMessageBean.VOICE_SEND)){
                        playVoice(list,position+1);
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(context, "Voice fetch failed, we are trying to reload it.", Toast.LENGTH_SHORT).show();
            voiceContent.downloadVoiceFile(message, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, File file) {
                    if (i == 0) {
                        Toast.makeText(context, "Voice fetch completed.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Voice fetch failed, check network status.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
