package com.group10.backupbuddy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

public class VideoPlayerActivity extends AppCompatActivity {


    VideoView videoView;
    MediaController mController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoView = (VideoView)findViewById(R.id.videoView2);
        mController = new MediaController(this);
        Button closeme = (Button)findViewById(R.id.button);
        mController.setAnchorView(videoView);
        videoView.setMediaController(mController);
        String VideoUrl= getIntent().getStringExtra("VideoUrl");
        getSupportActionBar().hide();

        Uri uri = Uri.parse(VideoUrl);
        videoView.setVideoURI(uri);
        videoView.start();

        closeme.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent Home = new Intent(VideoPlayerActivity.this, navigationFun.class);
                Home.putExtra("frgToLoad", 1);
                VideoPlayerActivity.this.startActivity(Home);
            }
        });


    }
}
