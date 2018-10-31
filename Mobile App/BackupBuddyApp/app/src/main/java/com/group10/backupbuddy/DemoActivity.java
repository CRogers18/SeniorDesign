package com.group10.backupbuddy;

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
    ArrayAdapter<String> adapter;
    long testme;
    float Val;
    int byteCount = 0;
    int[] byteBuffer = new int[4];
    int counter2 = 0;
    byte[] byteBuffer2 = new byte[120];


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
            runOnUiThread(() -> updateText(strVals) );
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


        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        ArrayList<String> myStringArray1 = new ArrayList<String>();

        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            // This is used to update which view is changed
            boolean center = true;
            boolean left = false;
            boolean right = false;

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
                                System.out.println("packetBytes[" + i +"]:  "+ packetBytes[i]);
                                //System.out.println("value as Hex" + packetBytes[i]);


                                // System.out.print("ByteCount: " + byteCount);
                                // System.out.print("I for packetBytes: " + i);
                                // System.out.println("bytesAvailable:  " + bytesAvailable);
                               // System.out.print("packet bytes: " + packetBytes[i]);
//                                float solution = 0;
//                                int decimalPoint = 0;

//                                if(((packetBytes[i] >> 6) & 1) == 1)
//                                {
//                                    solution = 1;
//                                }
//                                if(((packetBytes[i] >> 5) & 1) == 1)
//                                {
//                                    decimalPoint += 5000;
//                                }
//                                if(((packetBytes[i] >> 4) & 1) == 1)
//                                {
//                                    decimalPoint += 2500;
//
//                                }
//                                if(((packetBytes[i] >> 3) & 1) == 1)
//                                {
//                                    decimalPoint += 1250;
//
//                                }
//                                if(((packetBytes[i] >> 2) & 1) == 1)
//                                {
//                                    decimalPoint += 625;
//
//                                }
//                                if(((packetBytes[i] >> 1) & 1) == 1)
//                                {
//                                    decimalPoint += 313;
//
//                                }
//                                if(((packetBytes[i] >> 0) & 1) == 1)
//                                {
//                                    decimalPoint += 156;
//
//                                }
//                                if(((packetBytes[i] >> 7) & 1) == 1)
//                                {
//                                    solution = solution * -1;
//
//                                }
//                                System.out.println("Solution: " + solution);
//                                System.out.println("Decimal Point: " + decimalPoint);

                              //  byteBuffer[byteCount] = (packetBytes[i] & 0xff);
                                byteBuffer2[byteCount] = packetBytes[i];

                                byteCount++;
                                //System.out.println("val: = " + Val);
                                if(packetBytes[i] == 0x1f)
                                {
                                    didI = true;
                                    System.out.println("eat my whole ass");
                                    byteCount = 0;
                                }


                                if(byteCount > 5 && didI)
                                {
                                    didI = false;
                                    byteCount = 0;

                                    TMFrame p = new TMFrame(byteBuffer2);

                                }

                                //System.out.println("val: = " + Val);
//                                byte b = packetBytes[i];
//                                if(byteCount > 3)
//                                {
//                                    byteCount = 0;
//
//
//                                    Val = Float.intBitsToFloat(
//                                            (byteBuffer[3])
//                                                    | ((byteBuffer[2]) << 8)
//                                                    | ((byteBuffer[1]) << 16)
//                                                    | ((byteBuffer[0]) << 24));
//
//                                    String myAss = Float.toString(Val);
//                                    //System.out.println("myAss: " + myAss);
//                                    //myStringArray1.add(myAss);
//                                    // adapter = new ArrayAdapter<String>(this, android.R.layout., myStringArray1);
//                                    ///  myListView.setAdapter(adapter);
//
//
//                                    String old = "luca";
//                                    old = floatvalshow.getText().toString();
//                                    String combine = old +" , " + myAss;
//                                    // System.out.println("combine: " + combine);
//                                    //floatvalshow.setText(combine);
//
//                                    if(center)
//                                    {
//                                        center = false;
//                                        runOnUiThread(new Runnable() {
//
//                                            @Override
//                                            public void run() {
//
//                                                updateTextcenter(combine);
//
//
//                                            }
//                                        });
//                                    }
//                                    if(left)
//                                    {
//                                        runOnUiThread(new Runnable() {
//
//                                            @Override
//                                            public void run() {
//
//                                                updateTextleft(combine);
//
//
//                                            }
//                                        });
//                                        left = false;
//                                        right = true;
//                                    }
//                                    if(right)
//                                    {
//                                        runOnUiThread(new Runnable() {
//
//                                            @Override
//                                            public void run() {
//
//                                                updateTextright(combine);
//
//
//                                            }
//                                        });
//
//                                        right = false;
//                                        center = true;
//                                    }
//                                    readBufferPosition = 0;
//                                    //long timeend = testme - endtime;
//                                    //System.out.println("last time value: " + endtime);
//
//                                    //  System.out.print("the time it took: " + timeend );
//                                    System.out.println("VAl: " + Val);
//
//                                    if(!myAss.equals("12.345679"))
//                                    {
//                                        counter2++;
//                                        System.out.println("Val of error: " + counter2);
//                                    }
//
//                                    if(Val == 0.0)
//                                    {
//                                        testme = System.currentTimeMillis();
//
//                                    }
//                                    if(Val == 59.0)
//                                    {
//                                        long endtime = System.currentTimeMillis();
//                                        //System.out.println("time it took = " + (endtime - testme));
//
//                                    }
//
//                                }
                                //if(b == delimiter)

                                if(false)
                                {
                                    System.out.println("yeah I tried and made it to delimiter");

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            System.out.println("data is here: " + data);
                                            myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    //readBuffer[readBufferPosition++] = b;
                                }
                            }

//                            if(byteCount > 2)
//                            {
//                                byteCount = 0;
//
//
//                                //Val = ( (unsignedToBytes(byteBuffer[0])) | (unsignedToBytes(byteBuffer[1]) << 8) | (unsignedToBytes(byteBuffer[2]) << 16) | (unsignedToBytes(byteBuffer[3]) << 24));
//
//                                //Val = Byte.toUnsignedInt(byteBuffer[0]) | Byte.toUnsignedInt(byteBuffer[1]) << 8 | Byte.toUnsignedInt(byteBuffer[2]) << 16|Byte.toUnsignedInt(byteBuffer[3]) <<24;
//                                      //  Val = (int)byteBuffer[0] & 0xff | ((int)byteBuffer[1] & 0xff) << 8 | ((int)byteBuffer[2] & 0xff) << 16 | ((int)byteBuffer[3] & 0xff) << 24 ;
//                                System.out.println("ByteBuffer[3: " + byteBuffer[3]);
//                                System.out.println("ByteBuffer[2: " + byteBuffer[2]);
//                                System.out.println("ByteBuffer[1: " + byteBuffer[1]);
//                                System.out.println("ByteBuffer[0: " + byteBuffer[0]);
//
//
//                                 Val = Float.intBitsToFloat(
//                                        (byteBuffer[3])
//                                                | ((byteBuffer[2]) << 8)
//                                                | ((byteBuffer[1]) << 16)
//                                                | ((byteBuffer[0]) << 24));
//
//                                 String myAss = Float.toString(Val);
//                                System.out.println("myAss: " + myAss);
//                                //myStringArray1.add(myAss);
//                               // adapter = new ArrayAdapter<String>(this, android.R.layout., myStringArray1);
//                              ///  myListView.setAdapter(adapter);
//
//
//                                String old = "luca";
//                                old = floatvalshow.getText().toString();
//                                String combine = old +" , " + myAss;
//                                System.out.println("combine: " + combine);
//                                //floatvalshow.setText(combine);
//
//                                runOnUiThread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//
//                                        updateText(combine);
//
//
//                                    }
//                                });
//                                readBufferPosition = 0;
//
//                                System.out.println("VAl: " + Val);
//
//                            }

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
        mmOutputStream.close();
        mmInputStream.close();
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
    Button exit;
    Button bluetooth;



    //WebView webView;


    //String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";
    String videoUrl = "http://192.168.4.1:8080/?action=stream";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // your code
        setContentView(R.layout.activity_demo);
        getSupportActionBar().hide();
        //videoView = (VideoView)findViewById(R.id.videoView);
       //- btnPlayPause = (ImageButton)findViewById(R.id.btn_play_pause);
        //btnPlayPause.setOnClickListener(this);
        mjpegView = (MjpegView)findViewById(R.id.VIEW_NAME);
        left = (TextView)findViewById(R.id.left);
        right = (TextView)findViewById(R.id.right);
        center = (TextView)findViewById(R.id.center);
        exit = (Button)findViewById(R.id.Exit);
        bluetooth = (Button)findViewById(R.id.bluetooth);

        //distancetoback.setText("what");
        // distancetoback.setBackgroundColor()
        // distancetoback.setBackgroundColor(Color.parseColor("#55FF0000"));
//        MjpegView viewer = (MjpegView) findViewById(R.id.mjpegview);
//        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
//        viewer.setAdjustHeight(true);
//        viewer.setUrl("http://192.168.4.1:8080/?action=stream");
//        viewer.startStream();

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    if(mmOutputStream != null && mmInputStream!=null && mmSocket!= null)
                    closeBT();
                }
                catch (IOException ex) { }

                Intent Home = new Intent(DemoActivity.this, MainMenuActivity.class);
                DemoActivity.this.startActivity(Home);
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }

//                try
//                {
//                    if(mmOutputStream != null)
//                    {
//                        sendData();
//                    }
//
//                }
//                catch (IOException ex) { }


            }
        });
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
