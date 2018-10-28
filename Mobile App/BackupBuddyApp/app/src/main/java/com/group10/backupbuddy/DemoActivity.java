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


    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }


    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
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
                                //System.out.println("packetBytes[" + i +"]:  "+ packetBytes[i]);


                                // System.out.print("ByteCount: " + byteCount);
                                // System.out.print("I for packetBytes: " + i);
                                // System.out.println("bytesAvailable:  " + bytesAvailable);
                                System.out.print("packet bytes: " + packetBytes[i]);
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

                                byteBuffer[byteCount] = (packetBytes[i] & 0xff);
                                byteCount++;

                                //System.out.println("val: = " + Val);
                                byte b = packetBytes[i];
                                if(byteCount > 3)
                                {
                                    byteCount = 0;


                                    Val = Float.intBitsToFloat(
                                            (byteBuffer[3])
                                                    | ((byteBuffer[2]) << 8)
                                                    | ((byteBuffer[1]) << 16)
                                                    | ((byteBuffer[0]) << 24));

                                    String myAss = Float.toString(Val);
                                    //System.out.println("myAss: " + myAss);
                                    //myStringArray1.add(myAss);
                                    // adapter = new ArrayAdapter<String>(this, android.R.layout., myStringArray1);
                                    ///  myListView.setAdapter(adapter);


                                    String old = "luca";
                                    old = floatvalshow.getText().toString();
                                    String combine = old +" , " + myAss;
                                    // System.out.println("combine: " + combine);
                                    //floatvalshow.setText(combine);

                                    if(center)
                                    {
                                        center = false;
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                updateTextcenter(combine);


                                            }
                                        });
                                    }
                                    if(left)
                                    {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                updateTextleft(combine);


                                            }
                                        });
                                        left = false;
                                        right = true;
                                    }
                                    if(right)
                                    {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                updateTextright(combine);


                                            }
                                        });

                                        right = false;
                                        center = true;
                                    }
                                    readBufferPosition = 0;
                                    //long timeend = testme - endtime;
                                    //System.out.println("last time value: " + endtime);

                                    //  System.out.print("the time it took: " + timeend );
                                    System.out.println("VAl: " + Val);

                                    if(!myAss.equals("12.345679"))
                                    {
                                        counter2++;
                                        System.out.println("Val of error: " + counter2);
                                    }

                                    if(Val == 0.0)
                                    {
                                        testme = System.currentTimeMillis();

                                    }
                                    if(Val == 59.0)
                                    {
                                        long endtime = System.currentTimeMillis();
                                        //System.out.println("time it took = " + (endtime - testme));

                                    }

                                }
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
                                    readBuffer[readBufferPosition++] = b;
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
        myLabel.setText("Bluetooth Closed");
    }

    private static final int TIMEOUT = 20;

    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;



    MjpegView mjpegView;
    TextView left;
    TextView right;
    TextView center;


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

        //distancetoback.setText("what");
        // distancetoback.setBackgroundColor()
        // distancetoback.setBackgroundColor(Color.parseColor("#55FF0000"));
//        MjpegView viewer = (MjpegView) findViewById(R.id.mjpegview);
//        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
//        viewer.setAdjustHeight(true);
//        viewer.setUrl("http://192.168.4.1:8080/?action=stream");
//        viewer.startStream();

        try
        {
            findBT();
            openBT();
        }
        catch (IOException ex) { }

//
//        try
//        {
//            sendData();
//        }
//        catch (IOException ex) { }
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
