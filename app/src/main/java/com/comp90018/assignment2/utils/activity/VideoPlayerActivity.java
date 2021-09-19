package com.comp90018.assignment2.utils.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.comp90018.assignment2.R;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 *
 * video player
 *
 * need intent bundle: video_url
 *
 * @author  xiaotian li
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private ExoUserPlayer exoUserPlayer;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "VidePlayAct[dev]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoUserPlayer = new VideoPlayerManager
                .Builder(VideoPlayerManager.TYPE_PLAY_USER, videoPlayerView)
                .setTitle("playing")
                .setPlayUri(getIntent().getStringExtra("video_url"))
                .create()
                .startPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoUserPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoUserPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoUserPlayer.onDestroy();
    }
}