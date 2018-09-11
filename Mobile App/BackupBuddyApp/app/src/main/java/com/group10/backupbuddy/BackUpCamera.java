package com.group10.backupbuddy;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;
import java.lang.Override;
import java.lang.Exception;
import android.media.ToneGenerator;
import android.media.AudioManager;


public class BackUpCamera extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;


    String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        getSupportActionBar().hide();
        videoView = (VideoView)findViewById(R.id.videoView);
        btnPlayPause = (ImageButton)findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(this);

    }
    @Override
    public void onClick(View v)
    {
        mDialog = new ProgressDialog(BackUpCamera.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
      //  ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        try{
            if(!videoView.isPlaying()) {
                Uri uri = Uri.parse(videoUrl);
                videoView.setVideoURI(uri);
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                    }

                });
            }
            else
            {
                videoView.pause();
                mDialog.dismiss();
                btnPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
        catch(Exception ex)
        {

        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mDialog.dismiss();
                mp.setLooping(true);
                videoView.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
