package com.group10.backupbuddy;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.lang.Override;
import java.lang.Exception;
import com.github.niqdev.mjpeg.Mjpeg;




public class BackUpCamera extends AppCompatActivity implements View.OnClickListener {

    private static final int TIMEOUT = 20;

    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;



    MjpegView mjpegView;
    TextView distancetoback;

    //WebView webView;


    //String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";
    String videoUrl = "http://192.168.4.1:8080/?action=stream";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // your code
        setContentView(R.layout.activity_main_camera);
        getSupportActionBar().hide();
        //videoView = (VideoView)findViewById(R.id.videoView);
        btnPlayPause = (ImageButton)findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(this);
        mjpegView = (MjpegView)findViewById(R.id.VIEW_NAME);
        distancetoback = (TextView)findViewById(R.id.label2);
        distancetoback.setText("what");
       // distancetoback.setBackgroundColor()
       // distancetoback.setBackgroundColor(Color.parseColor("#55FF0000"));
//        MjpegView viewer = (MjpegView) findViewById(R.id.mjpegview);
//        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
//        viewer.setAdjustHeight(true);
//        viewer.setUrl("http://192.168.4.1:8080/?action=stream");
//        viewer.startStream();

        if(mjpegView.isStreaming())
            System.out.println("testing");
        Mjpeg.newInstance()
                .open("http://192.168.4.1:8080/?action=stream", TIMEOUT)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                    mjpegView.flipVertical(true);

                },throwable -> {
                    Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                    Toast.makeText(this, "Error Server is down", Toast.LENGTH_LONG).show();
                });



    }
    @Override
    public void onClick(View v)
    {
        mDialog = new ProgressDialog(BackUpCamera.this);
        mDialog.setMessage("Connecting to camera please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

//        try{
//            if(!videoView.isPlaying()) {
//                Uri uri = Uri.parse(videoUrl);
//                videoView.setVideoURI(uri);
//                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        btnPlayPause.setImageResource(R.drawable.ic_play);
//                    }
//
//                });
//            }
//            else
//            {
//                videoView.pause();
//                mDialog.dismiss();
//                btnPlayPause.setImageResource(R.drawable.ic_play);
//            }
//        }
//        catch(Exception ex)
//        {
//
//        }
//        videoView.requestFocus();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
//        {
//            @Override
//            public void onPrepared(MediaPlayer mp)
//            {
//                mDialog.dismiss();
//                mp.setLooping(true);
//                videoView.start();
//                btnPlayPause.setImageResource(R.drawable.ic_pause);
//            }
//        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
