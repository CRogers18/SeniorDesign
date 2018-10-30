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
    TextView value1;
    TextView value2;
    TextView value3;


    ListView myListView;
    ArrayAdapter<String> adapter;
    long testme;
    float Val;
    int byteCount = 0;
    int[] byteBuffer = new int[4];
    byte[] byteBuffer2 = new byte[120];

    int counter2 = 0;


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
           final String[] strVals = { Float.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3)};
//final String[] strVals = {Integer.toString(dist_1)};
            runOnUiThread(() -> updateText(strVals) );
        }
    }

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



    void updateText(String[] a)
    {
        floatvalshow.setText("Ultrasonic 1: " + a[0] + "\nUltrasonic 2: " + a[1] + "\nUltrasonic 3: " + a[2] );
        if(!a[0].equals("0") || Integer.parseInt(a[0]) < 150 )
            value1.setText(a[0]);

        if(!a[1].equals("0") || Integer.parseInt(a[1]) < 150 )
            value2.setText(a[1]);

        if(!a[2].equals("0")|| Integer.parseInt(a[2]) < 150 )
            value3.setText(a[2]);


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
                boolean didI = false;

                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();

                        if(bytesAvailable > 0)
                        {

                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);



                            for(int i=0;i<bytesAvailable;i++)
                            {
                                System.out.println("packetBytes[" + i + "]:  "+ Integer.toHexString(packetBytes[i]));


                               // System.out.print("ByteCount: " + byteCount);
                               // System.out.print("I for packetBytes: " + i);
                               // System.out.println("bytesAvailable:  " + bytesAvailable);


                              //  byteBuffer[byteCount] = (packetBytes[i] & 0xff);
                                byteBuffer2[byteCount] = packetBytes[i];
                              //  System.out.println("val of packetBytes[i]: " + packetBytes + "i= " + i);
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
        String msg = myTextbox.getText().toString();
       // msg += "\n";
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
