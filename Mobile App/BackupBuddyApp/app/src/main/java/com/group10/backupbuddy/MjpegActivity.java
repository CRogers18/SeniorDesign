package com.group10.backupbuddy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

//import com.demo.mjpeg.MjpegView.MjpegInputStream;
//import com.demo.mjpeg.MjpegView.MjpegView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MjpegActivity extends Activity {
    private static final String TAG = "MjpegActivity";

    private MjpegView mv;
    private Bluetooth bt;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT,1000);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //sample public cam
        String URL = "http://192.168.4.1:8080/?action=stream";

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mv = new MjpegView(this);
        setContentView(mv);
//        bt = new Bluetooth(this, mHandler);
//        connectService();
        new DoRead().execute(URL);
    }

    public void connectService(){
        try {
//            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-06");
                Log.d(TAG, "Btservice started - listening");
//                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
//                status.setText("Bluetooth Not enabled");
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ",e);
//            status.setText("Unable to connect " +e);
        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] buffer;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = null;
            try {
                objStream = new ObjectOutputStream(byteStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                objStream.writeObject(msg.obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer = byteStream.toByteArray();
//            System.out.println(buffer[0]);

            if(msg.arg1>1)
            {
//                int dist_1 = ((buffer[0] << 8) | buffer[1]) / 58;
//                System.out.println("sensor 1: " + dist_1);
            }
//            System.out.println(buffer);
//            buffer = (byte[])msg.obj;
//            System.out.println("ya yeeet "+buffer.toString());
//            buffer = new ByteArrayInputStream(msg.obj);

//            buffer[0]
//            System.out.println;
            boolean didI = false;
            for(int i = 0; i < msg.arg1 -1; i++)
            {
                if(buffer[i] == 0x1f)
                {
                    didI = true;

                }
//                System.out.println("didI" + didI);
                //z                if(didI)
//                {
//                    if(i+1 < msg.arg1) {
//                        int dist_1 = ((buffer[i] << 8) | buffer[i + 1]) / 58;
//                        System.out.println("this be value: "+ dist_1);
//
//                    }
//                    didI = false;
//                }
//                System.out.println(buffer[i]);
            }
//            System.out.println(buffer[0]);
            System.out.println("this is the message "+msg.obj +" arg1: "+ msg.arg1);
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE 2");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
        }
    };
    public void onPause() {
        super.onPause();
        mv.stopPlayback();
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }

            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(true);
        }
    }
}
