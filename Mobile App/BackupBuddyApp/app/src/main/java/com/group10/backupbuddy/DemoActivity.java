package com.group10.backupbuddy;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Override;
import java.lang.Exception;
import com.github.niqdev.mjpeg.Mjpeg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Adapter;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import android.widget.AdapterView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.sql.SQLSyntaxErrorException;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;



public class DemoActivity extends AppCompatActivity {


    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Thread testing;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    TextView floatvalshow;
    ListView myListView;
    View leftBar;
    View rightBar;
    View centerBar;
    ArrayAdapter<String> adapter;
    long testme;
    float Val;
    int byteCount = 0;
    int[] byteBuffer = new int[4];
    int counter2 = 0;
    byte[] byteBuffer2 = new byte[120];
    String leftbarcolor = "#5900FF22";
    String rightbarcolor = "#5900FF22";
    String centerbarcolor = "#5900FF22";
    Button exit;




    public class TMFrame
    {
        int dist_1, dist_2, dist_3;
        float accel_x, accel_y, accel_z;

        // Instantiate the class with data and the text is automatically updated in the UI
        public TMFrame(byte[] packetData)
        {

            int[] dataConvert = new int[packetData.length];


            for(int i = 0; i < packetData.length; i++)
            {
                dataConvert[i] = (int)packetData[i] & 0xff;
            }


            //  System.out.println("Data1: " + dataConvert[1] + " Data0: " + dataConvert[0]);
            //  System.out.println("before divide: " + (((dataConvert[0] )<< 8) | dataConvert[1]));
            //System.out.println("Second before divide: " + (((dataConvert[1] )<< 8) | dataConvert[0]));

            dist_1 = ((dataConvert[0] << 8) | dataConvert[1]) / 58;
            //float whatever = (float)dist_1 / (float)58;
            //dist_1 = whatever;
            //System.out.println("whatever: " + whatever);

            // System.out.println("Data3: " + dataConvert[3] + " Data2: " + dataConvert[2]);
            // System.out.println("Val1: " + dist_1);

            dist_2 = ((dataConvert[2] << 8) | dataConvert[3]) / 58;
//            System.out.println("Data5: " + dataConvert[5] + " Data4: " + dataConvert[4]);
//            System.out.println("Val2: " + dist_2);
//
//
            dist_3 = ((dataConvert[4] << 8) | dataConvert[5]) / 58;
//            System.out.println("Val3: " + dist_3);

//            accel_x = (dataConvert[6] << 8) | dataConvert[7];
//            accel_y = (dataConvert[8] << 8) | dataConvert[9];
//            accel_z = (dataConvert[10] << 8) | dataConvert[11];

//            final String[] strVals = { Integer.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3),
//                    Float.toString(accel_x),  Float.toString(accel_y),  Float.toString(accel_z) };
            final String[] strVals = { Integer.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3)};
//final String[] strVals = {Integer.toString(dist_1)};
            //runOnUiThread(() -> updateText(strVals) );
        }
    }
    void updateText(String[] a)
    {
        //floatvalshow.setText("Ultrasonic 1: " + a[0] + "\nUltrasonic 2: " + a[1] + "\nUltrasonic 3: " + a[2] );

        if(a[0] != "0")
            left.setText(a[0]);
        if(a[1] != "0")
            right.setText(a[1]);
        if(a[2] != "0")
            center.setText(a[2]);

//        if(!a[0].equals("0") || Integer.parseInt(a[0]) < 150 )
//            left.setText(a[0]);
//
//        if(!a[1].equals("0") || Integer.parseInt(a[1]) < 150 )
//            right.setText(a[1]);
//
//        if(!a[2].equals("0")|| Integer.parseInt(a[2]) < 150 )
//            center.setText(a[2]);
//

    }
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

//    void startVideo()
//    {
//        testing = new Thread()
//        {
//            @Override
//            public void run() {
//                Mjpeg.newInstance()
//                        .open("http://192.168.4.1:8080/?action=stream", TIMEOUT)
//                        .subscribe(inputStream -> {
//                            mjpegView.setSource(inputStream);
//                            mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
//                            mjpegView.showFps(true);
//                            mjpegView.flipVertical(true);
//
//                        },throwable -> {
//                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
//                            //Toast.makeText(this, "Error Server is down", Toast.LENGTH_LONG).show();
//                        });
//
//            }
//        };
//        testing.start();
//    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            //myLabel.setText("No bluetooth adapter available");
            System.out.println("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        System.out.println("Bluetooth Device Found");
       // myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        System.out.println("Bluetooth Opened");
        //myLabel.setText("Bluetooth Opened");
    }



    void updateTextleft(String a)
    {
        left.setText(a);

    }
    void updateTextright(String a)
    {
        right.setText(a);

    }
    void updateTextcenter(String a)
    {
        center.setText(a);

    }
    void beginListenForData()
    {


        //final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        ArrayList<String> myStringArray1 = new ArrayList<String>();

        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            // This is used to update which view is changed
            boolean center = true;
            //boolean left = false;
            boolean right = false;
            int dist_1count = 0;
            public void run()
            {
                boolean didI = false;

                // System.out.println("you make it in here fam");
               // System.out.println("StopWorker Value: " + stopWorker);
                //System.out.println(" Thread.currentThread: " + Thread.currentThread().isInterrupted());
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    //System.out.println("While");
                    try
                    {
                        //System.out.println("yeah I tried and made it");
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            //System.out.println("yeah I tried and made it to bytesAvailable");
                            //System.out.println("First time value: " + testme);

                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            // bytesAvailable = 4
                            for(int i=0;i<bytesAvailable;i++)
                            {
//                               System.out.println("packetBytes[" + i +"]:  "+ packetBytes[i]);


                              //  byteBuffer[byteCount] = (packetBytes[i] & 0xff);
                                byteBuffer2[byteCount] = packetBytes[i];

                                byteCount++;
                                //System.out.println("val: = " + Val);
                                if(packetBytes[i] == 0x1f)
                                {
                                    didI = true;
                                    //System.out.println("eat my whole ass");
                                    byteCount = 0;
                                }


                                if(byteCount > 5 && didI)
                                {
                                    didI = false;
                                    byteCount = 0;
                                    int dist_1, dist_2, dist_3;
                                    float accel_x, accel_y, accel_z;



                                    int[] dataConvert = new int[byteBuffer2.length];


                                    for(int w = 0; w < byteBuffer2.length; w++)
                                    {
                                        dataConvert[w] = (int)byteBuffer2[w] & 0xff;
                                    }


                                    dist_1 = ((dataConvert[0] << 8) | dataConvert[1]) / 58;

                                    dist_2 = ((dataConvert[2] << 8) | dataConvert[3]) / 58;

                                    dist_3 = ((dataConvert[4] << 8) | dataConvert[5]) / 58;

                                    accel_x = (dataConvert[6] << 8) | dataConvert[7];
                                    accel_y = (dataConvert[8] << 8) | dataConvert[9];
                                    accel_z = (dataConvert[10] << 8) | dataConvert[11];


//
//
//
//
//                                                          //TMFrame p = new TMFrame(byteBuffer2);
//                                    });
                                }


                                if(false)
                                {
                                    System.out.println("yeah I tried and made it to delimiter");

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

//                                    handler.post(new Runnable()
//                                    {
//                                        public void run()
//                                        {
//                                            System.out.println("data is here: " + data);
//                                            myLabel.setText(data);
//                                        }
//                                    });
                                }
                                else
                                {
                                    //readBuffer[readBufferPosition++] = b;
                                }
                            }



                        }
                    }
                    catch (IOException ex)
                    {
                        System.out.println("I am a failure");
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        //String msg = myTextbox.getText().toString();
        String msg = "W";
        //msg += "\n";
        mmOutputStream.write(msg.getBytes());
        System.out.println("Data Sent");
       // myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        if(mmOutputStream!=null)
            mmOutputStream.close();
        if(mmInputStream!=null)
            mmInputStream.close();
        if(mmSocket!=null)
            mmSocket.close();
       // myLabel.setText("Bluetooth Closed");
        System.out.println("Bluetooth closed");
    }

    private static final int TIMEOUT = 20;

    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;



    MjpegView mjpegView;
    TextView left;
    TextView right;
    TextView center;
//    Button exit;
    Button bluetooth;
    WebView webView;


    //WebView webView;


    //String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";
    String videoUrl = "http://192.168.4.1:8080/?action=stream";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // your code

        setContentView(R.layout.activity_demo);
        getSupportActionBar().hide();
        leftBar = (View)findViewById(R.id.leftBar);
        rightBar = (View)findViewById(R.id.rightBar);
        centerBar = (View)findViewById(R.id.middleBar);
        exit = (Button)findViewById(R.id.closeing);


        //red color is #59ff0000
        // yellow color is #59FFC400
        // green color is #5900FF22


        //videoView = (VideoView)findViewById(R.id.videoView);
       //- btnPlayPause = (ImageButton)findViewById(R.id.btn_play_pause);
        //btnPlayPause.setOnClickListener(this);
        mjpegView = (MjpegView)findViewById(R.id.VIEW_NAME);
//        left = (TextView)findViewById(R.id.left);
//        right = (TextView)findViewById(R.id.right);
//        center = (TextView)findViewById(R.id.center);
//        exit = (Button)findViewById(R.id.Exit);
//        bluetooth = (Button)findViewById(R.id.bluetooth);
        webView = (WebView)findViewById(R.id.webview);

        //distancetoback.setText("what");
        // distancetoback.setBackgroundColor()
        // distancetoback.setBackgroundColor(Color.parseColor("#55FF0000"));
//       MjpegView viewer = (MjpegView) findViewById(R.id.mjpegview);
//        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
//        viewer.setAdjustHeight(true);
//        viewer.setUrl("http://192.168.4.1:8080/?action=stream");
//        viewer.startStream();

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {

                    closeBT();
                }
                catch (IOException ex) { }

                Intent Home = new Intent(DemoActivity.this, navigationFun.class);
                DemoActivity.this.startActivity(Home);
            }
        });
//
//        bluetooth.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
////                new Thread(new Runnable() {
////                    public void run() {
//                        // a potentially time consuming task
//                        try
//                        {
//                            findBT();
//                            openBT();
//                        }
//                        catch (IOException ex) { }
//
////                    }
////                }).start();
//
//
//
//
//
////                try
////                {
////                    if(mmOutputStream != null)
////                    {
////                        sendData();
////                    }
////
////                }
////                catch (IOException ex) { }
//
//
//            }
//        });



//        try
//        {
//            findBT();
//            openBT();
//        }
//        catch (IOException ex) { }
//        try
//        {
//            findBT();
//            openBT();
//        }
//        catch (IOException ex) { }

        // use this to make another view that counts down from 10 or so for the pi to boot
//        try {
//            TimeUnit.SECONDS.sleep(4);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


//        try
//        {
//            sendData();
//        }
//        catch (IOException ex) { }

       // startVideo();
//
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.loadUrl("http://192.168.4.1:8080/?action=stream");



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
      //  MjpegView viewer = (MjpegView) findViewById(R.id.mjpegview);
      //  mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
      //  mjpegView.setAdjustHeight(true);
       // mjpegView.setUrl("http://www.example.com/mjpeg.php?id=1234");
       // mjpegView.getSurfaceView()

       new LongRunningTask().execute();


    }

    private class LongRunningTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

//            findBT();
//            try {
//                openBT();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try
            {
                if(mBluetoothAdapter ==null) {
                    System.out.println("test2");
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        //myLabel.setText("No bluetooth adapter available");
                        System.out.println("No bluetooth adapter available");
                    }

                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBluetooth, 0);
                    }

                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().equals("HC-06")) {
                                mmDevice = device;
                                break;
                            }
                        }
                    }
                }
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
               if(mmSocket==null) {
                   System.out.println("test 1");
                   mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                   //if(mmSocket==null)
                   mmSocket.connect();
//                if(mmOutputStream==null)
                   mmOutputStream = mmSocket.getOutputStream();
                   mmInputStream = mmSocket.getInputStream();

               }
               // System.out.println("Bluetooth Opened");
             //  String msg = "0xBB";
               // mmOutputStream.write(msg.getBytes());

                stopWorker = false;

                readBufferPosition = 0;
                readBuffer = new byte[1024];
                boolean didI = false;
                while(!stopWorker)
                {
//                    System.out.println("test3");
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {

                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                System.out.println("packetBytes[" + i +"]:  "+ packetBytes[i]);

                                byteBuffer2[byteCount] = packetBytes[i];

                                byteCount++;
                                if(packetBytes[i] == 0x1f)
                                {
                                    didI = true;
                                    //System.out.println("eat my whole ass");
                                    byteCount = 0;
                                }


                                if(byteCount > 5 && didI)
                                {
                                    didI = false;
                                    int dist_1, dist_2, dist_3;
                                    float accel_x, accel_y, accel_z;



                                    int[] dataConvert = new int[byteBuffer2.length];


                                    for(int w = 0; w < byteBuffer2.length; w++)
                                    {
                                        dataConvert[w] = (int)byteBuffer2[w] & 0xff;
                                    }


                                    dist_1 = ((dataConvert[0] << 8) | dataConvert[1]) / 58;

                                    dist_2 = ((dataConvert[2] << 8) | dataConvert[3]) / 58;

                                    dist_3 = ((dataConvert[4] << 8) | dataConvert[5]) / 58;

                                    accel_x = (dataConvert[6] << 8) | dataConvert[7];
                                    accel_y = (dataConvert[8] << 8) | dataConvert[9];
                                    accel_z = (dataConvert[10] << 8) | dataConvert[11];

                                    //String leftbarcolor = "#5900FF22";
                                    //String rightbarcolor = "#5900FF22";
                                    //String centerbarcolor = "#5900FF22";

                                    //green
                                    if(dist_1 >= 120)
                                    {
                                        leftbarcolor = "#5900FF22";
                                    }

                                    if(dist_1 < 120 && dist_1 > 50)
                                    {
                                        leftbarcolor = "#59FFC400";
                                    }

                                    //red
                                    if(dist_1 < 50)
                                    {
                                        leftbarcolor = "#59ff0000";
                                    }

                                    if(dist_2 >= 120)
                                    {
                                        rightbarcolor = "#5900FF22";
                                    }

                                    if(dist_2 < 120 && dist_2 > 50)
                                    {
                                        rightbarcolor = "#59FFC400";
                                    }

                                    if(dist_2 < 50)
                                    {
                                        rightbarcolor = "#59ff0000";
                                    }

                                    if(dist_3 >= 120)
                                    {
                                        centerbarcolor = "#5900FF22";
                                    }

                                    if(dist_3 < 120 && dist_3 > 50)
                                    {
                                        centerbarcolor = "#59FFC400";
                                    }

                                    if(dist_3 < 50)
                                    {
                                        centerbarcolor = "#59ff0000";
                                    }
                                    //break;
                                    stopWorker = true;
//                                    try {
//                                        TimeUnit.SECONDS.sleep(1);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    byteCount = 0;
//                                    int dist_1, dist_2, dist_3;
//
//                                    int[] dataConvert = new int[byteBuffer2.length];
//
//
//                                    for(int w = 0; w < byteBuffer2.length; w++)
//                                    {
//                                        dataConvert[w] = (int)byteBuffer2[w] & 0xff;
//                                    }
//                                    dist_1 = ((dataConvert[0] << 8) | dataConvert[1]) / 58;
//                                    String test = Integer.toString(dist_1);


                                 // TMFrame p = new TMFrame(byteBuffer2);

                                }


                            }



                        }
                    }
                    catch (IOException ex)
                    {
                        System.out.println("I am a failure");
                        stopWorker = true;
                    }
                }

            }
            catch (IOException ex) { }
             return null;

//            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //update ui here
//            System.out.println("do it make it here?");
            leftBar.setBackgroundColor(Color.parseColor(leftbarcolor));
            rightBar.setBackgroundColor(Color.parseColor(rightbarcolor));
            centerBar.setBackgroundColor(Color.parseColor(centerbarcolor));



            new LongRunningTask().execute();

            //red color is #59ff0000
            // yellow color is #59FFC400
            // green color is #5900FF22
        }
    }


//    @Override
//    public void onClick(View v)
//    {
//        mDialog = new ProgressDialog(BackUpCamera.this);
//        mDialog.setMessage("Connecting to camera please wait...");
//        mDialog.setCanceledOnTouchOutside(false);
//        mDialog.show();
//
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
