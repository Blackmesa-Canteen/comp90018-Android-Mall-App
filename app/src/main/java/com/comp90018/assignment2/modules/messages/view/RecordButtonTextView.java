package com.comp90018.assignment2.modules.messages.view;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.comp90018.assignment2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;


/**
 *
 * recording button, based on open source proj.
 *
 * @author xiaotian li
 * @author https://github.com/jpush/jchat-android
 */
public class RecordButtonTextView extends AppCompatTextView {
    private Context context;
    private Dialog recordingIndicator;
    private MediaRecorder recorder;
    private File recordAudioFile;

    private String fileDir;
    private Conversation conversation;

    private Chronometer voiceTime;
    private long startTime;
    // press to record
    private long time1;
    // release record button
    private long time2;

    private OnNewMessage onNewMessage;

    public RecordButtonTextView(@NonNull Context context) {
        super(context);
        this.context = context;
        initThis();
    }

    public RecordButtonTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initThis();
    }

    public RecordButtonTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initThis();
    }

    private void initThis() {
        recordingIndicator = new Dialog(context, R.style.jmui_record_voice_dialog);
        recordingIndicator.setContentView(R.layout.jmui_dialog_record_voice);
        voiceTime = (Chronometer) recordingIndicator.findViewById(R.id.voice_time);

        // setup temp voice store path
        File rootDir = context.getFilesDir();
        fileDir = rootDir.getAbsolutePath() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        recordAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance())
                        + ".amr");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            // pressed down
            case MotionEvent.ACTION_DOWN:
                time1 = System.currentTimeMillis();
                startRecording();
                recordingIndicator.show();
                return true;

            // released button
            case MotionEvent.ACTION_UP:
                time2 = System.currentTimeMillis();
                if (time2 - time1 < 300) {
                    Toast.makeText(context, "Please speak more.", Toast.LENGTH_SHORT).show();
                    if (recordingIndicator.isShowing()) {
                        recordingIndicator.dismiss();
                    }
                    return true;
                } else if (time2 - time1 < 1000) {
                    Toast.makeText(context, "Please speak more.", Toast.LENGTH_SHORT).show();
                    releaseRecorder();
                } else {
                    releaseRecorder();
                    finishRecord();
                }

                if(recordingIndicator.isShowing()){
                    recordingIndicator.dismiss();
                }

                break;

            case MotionEvent.ACTION_CANCEL:
                if(recordingIndicator.isShowing()){
                    recordingIndicator.dismiss();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * finished recording, send out
     */
    private void finishRecord() {
        if (recordAudioFile != null && recordAudioFile.exists()) {
            MediaPlayer mp = new MediaPlayer();
            try {
                FileInputStream fis = new FileInputStream(recordAudioFile);
                mp.setDataSource(fis.getFD());
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mp != null) {
                // sec
                int duration = mp.getDuration() / 1000;

                // range of allowed size to send
                if (duration < 1) {
                    duration = 1;
                } else if (duration > 60) {
                    duration = 60;
                }

                try {
                    VoiceContent content = new VoiceContent(recordAudioFile, duration);
                    if(conversation == null){
                        return;
                    }
                    Message msg = conversation.createSendMessage(content);
                    JMessageClient.sendMessage(msg);
                    if(onNewMessage!=null){
                        onNewMessage.newMessage(msg);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Please allow recording.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * stop recording
     */
    private void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception e) {
                Log.w("RecordVoice[dev]", "Stop recorder failed!");
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }

    /**
     * press down to record voice
     */
    private void startRecording() {
        recordAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance())
                        + ".amr");

        try {
            recorder = new MediaRecorder();
            // from microphone
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // amr
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(recordAudioFile.getAbsolutePath());
            recordAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.w("RecordTextView[dev]", "recorder prepare failed!");
                }
            });
            recorder.start();
            startTime = System.currentTimeMillis();

            /* Returns milliseconds since boot,
             * including time spent in sleep.
             * Returns:elapsed milliseconds since boot.
             *
             * I think it is a tradition of android to use it
             * to set timer's base time
             */
            voiceTime.setBase(SystemClock.elapsedRealtime());
            Log.d("RecordTv[dev]", "start timing");
            voiceTime.start();
        } catch (IOException e) {
            e.printStackTrace();

            // roll back
            if (recordAudioFile != null) {
                recordAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            // roll back
            if (recordAudioFile != null) {
                recordAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        }
    }

    /**
     * call back
     */
    public interface OnNewMessage{
        public void newMessage(Message message);
    }

    public void setOnNewMessage(OnNewMessage onNewMessage) {
        this.onNewMessage = onNewMessage;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
