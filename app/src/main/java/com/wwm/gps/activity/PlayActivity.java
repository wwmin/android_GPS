package com.wwm.gps.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.wwm.gps.R;
import com.yixia.camera.util.Log;

/**
 * Created by wwmin on 2017/5/29.
 */

public class PlayActivity extends Activity {
    private VideoView videoView;
    private String videoPath;
    private MediaController mediaController;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videoView = (VideoView) findViewById(R.id.videoview);
        type = getIntent().getIntExtra("type", 0);
        videoPath = getIntent().getStringExtra("path");
        Log.i("videoPath", videoPath);
        play(videoPath);
//        mVideoView.setVideoPath(videoPath);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void play(final String path) {

        mediaController = new MediaController(this);

        if (type == 2) {
            Uri uri = Uri.parse(path);
            videoView.setVideoURI(uri);
        } else {
            videoView.setVideoPath(path);
        }

        // 设置VideView与MediaController建立关联
        videoView.setMediaController(mediaController);
//        // 设置MediaController与VideView建立关联
        mediaController.setMediaPlayer(videoView);
        mediaController.setVisibility(View.INVISIBLE);
        // 让VideoView获取焦点
//        videoView.requestFocus();
        // 开始播放
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                mp.setLooping(true);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                videoView.setVideoPath(path);
//                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }


}
