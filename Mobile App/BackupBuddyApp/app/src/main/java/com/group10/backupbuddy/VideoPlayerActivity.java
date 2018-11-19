package com.group10.backupbuddy;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {


    VideoView videoView;
    MediaController mController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoView = (VideoView)findViewById(R.id.videoView2);
        mController = new MediaController(this);
        mController.setAnchorView(videoView);
        videoView.setMediaController(mController);
        String VideoUrl= getIntent().getStringExtra("VideoUrl");

        Uri uri = Uri.parse(VideoUrl);
        videoView.setVideoURI(uri);

        videoView.start();



    }
}
