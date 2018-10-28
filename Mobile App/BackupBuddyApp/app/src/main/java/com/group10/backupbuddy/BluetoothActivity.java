package com.group10.backupbuddy;

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





public class BluetoothActivity extends Activity
{
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button openButton = (Button)findViewById(R.id.open);
        Button sendButton = (Button)findViewById(R.id.send);
        Button closeButton = (Button)findViewById(R.id.close);
        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);
        floatvalshow = (TextView)findViewById(R.id.label2);
     //   myListView = (ListView)findViewById(R.id.floatList);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    sendData();
                }
                catch (IOException ex) { }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });
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
        myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        myLabel.setText("Bluetooth Opened");
    }



    void updateText(String a)
    {
        floatvalshow.setText(a);

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
            public void run()
            {
                System.out.println("you make it in here fam");
                System.out.println("StopWorker Value: " + stopWorker);
                System.out.println(" Thread.currentThread: " + Thread.currentThread().isInterrupted());
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
                                float solution = 0;
                                int decimalPoint = 0;

                                if(((packetBytes[i] >> 6) & 1) == 1)
                                {
                                    solution = 1;
                                }
                                if(((packetBytes[i] >> 5) & 1) == 1)
                                {
                                    decimalPoint += 5000;
                                }
                                if(((packetBytes[i] >> 4) & 1) == 1)
                                {
                                    decimalPoint += 2500;

                                }
                                if(((packetBytes[i] >> 3) & 1) == 1)
                                {
                                    decimalPoint += 1250;

                                }
                                if(((packetBytes[i] >> 2) & 1) == 1)
                                {
                                    decimalPoint += 625;

                                }
                                if(((packetBytes[i] >> 1) & 1) == 1)
                                {
                                    decimalPoint += 313;

                                }
                                if(((packetBytes[i] >> 0) & 1) == 1)
                                {
                                    decimalPoint += 156;

                                }
                                if(((packetBytes[i] >> 7) & 1) == 1)
                                {
                                    solution = solution * -1;

                                }
                                System.out.println("Solution: " + solution);
                                System.out.println("Decimal Point: " + decimalPoint);

                                byteBuffer[byteCount] = (packetBytes[i] & 0xff);
                                byteCount++;

                                //System.out.println("val: = " + Val);
                                byte b = packetBytes[i];
                                if(byteCount > 3)
                                {
                                    byteCount = 0;


                                    //Val = ( (unsignedToBytes(byteBuffer[0])) | (unsignedToBytes(byteBuffer[1]) << 8) | (unsignedToBytes(byteBuffer[2]) << 16) | (unsignedToBytes(byteBuffer[3]) << 24));

                                    //Val = Byte.toUnsignedInt(byteBuffer[0]) | Byte.toUnsignedInt(byteBuffer[1]) << 8 | Byte.toUnsignedInt(byteBuffer[2]) << 16|Byte.toUnsignedInt(byteBuffer[3]) <<24;
                                    //  Val = (int)byteBuffer[0] & 0xff | ((int)byteBuffer[1] & 0xff) << 8 | ((int)byteBuffer[2] & 0xff) << 16 | ((int)byteBuffer[3] & 0xff) << 24 ;
                                   // System.out.println("ByteBuffer[3: " + byteBuffer[3]);
                                   // System.out.println("ByteBuffer[2: " + byteBuffer[2]);
                                   // System.out.println("ByteBuffer[1: " + byteBuffer[1]);
                                   // System.out.println("ByteBuffer[0: " + byteBuffer[0]);


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

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            updateText(combine);


                                        }
                                    });
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
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }
}

//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class BluetoothActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth);
//    }
//}