package com.group10.backupbuddy;


import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 *
 * in this activity
 * Intent intent = new Intent(getBaseContext(), SignoutActivity.class);
 intent.putExtra("EXTRA_SESSION_ID", sessionId);
 startActivity(intent);

 from the new activity
 String sessionId= getIntent().getStringExtra("EXTRA_SESSION_ID");

 */
public class VideoFragment extends Fragment implements View.OnClickListener {


    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;


    //will be based on click to change to which url is used
    String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        videoView = (VideoView)getView().findViewById(R.id.videoView);
        btnPlayPause = (ImageButton)getView().findViewById(R.id.btn_play_pause);
       btnPlayPause.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    private void GetVideos(){
        String url = "http://passwords.aisoftworks.net/fetchAll.php";
//        RequestQueue rq = Volley.newRequestQueue(this);
//        HashMap<String, String> parameters = new HashMap<String, String>();
//        Intent temp = getIntent(); // gets the previously created intent
//        String idString = temp.getStringExtra("ID");//retrieves ID passed from login activity
//        parameters.put("id", idString);
    }

    @Override
    public void onClick(View view) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
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
}
