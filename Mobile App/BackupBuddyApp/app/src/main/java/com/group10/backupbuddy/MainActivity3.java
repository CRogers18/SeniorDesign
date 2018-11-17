package com.group10.backupbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity3 extends AppCompatActivity {

    public final String TAG = "Main";

    private SeekBar elevation;
    private TextView debug;
    private TextView status;
    private Bluetooth bt;
    MjpegView mjpegView;
    WebView webView;
    int Bytecount = 0;

    private static final int TIMEOUT = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().hide();

        debug = (TextView) findViewById(R.id.textDebug);
        status = (TextView) findViewById(R.id.textStatus);
        mjpegView = (MjpegView)findViewById(R.id.VIEW_NAME);
        webView = (WebView)findViewById(R.id.webview);


//                webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.loadUrl("http://192.168.4.1:8080/?action=stream");
////

//
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

        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectService();
            }
        });

        elevation = (SeekBar) findViewById(R.id.seekBar);
        elevation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Seekbar","onStopTrackingTouch ");
                int progress = seekBar.getProgress();
                String p = String.valueOf(progress);
                debug.setText(p);
                bt.sendMessage(p);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("Seekbar","onStartTrackingTouch ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("Seekbar", "onProgressChanged " + progress);
            }
        });

        bt = new Bluetooth(this, mHandler);
        connectService();

    }

    public void connectService(){
        try {
            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-06");
                Log.d(TAG, "Btservice started - listening");
                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText("Bluetooth Not enabled");
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ",e);
            status.setText("Unable to connect " +e);
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

}
//public class MainActivity3 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main3);
//    }
//}
