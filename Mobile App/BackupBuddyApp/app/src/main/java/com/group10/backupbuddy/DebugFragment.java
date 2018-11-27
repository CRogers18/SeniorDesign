package com.group10.backupbuddy;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFragment extends Fragment {

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
    TextView acc1;
    TextView acc2;
    TextView acc3;


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
        int accel_x, accel_y, accel_z;

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
//            dist_1 = ((dataConvert[1] << 8) | dataConvert[0]) / 58;

            //float whatever = (float)dist_1 / (float)58;
            //dist_1 = whatever;
            //System.out.println("whatever: " + whatever);

            // System.out.println("Data3: " + dataConvert[3] + " Data2: " + dataConvert[2]);
            // System.out.println("Val1: " + dist_1);

            dist_2 = ((dataConvert[2] << 8) | dataConvert[3]) / 58;
//            dist_2 = ((dataConvert[3] << 8) | dataConvert[2]) / 58;

//            System.out.println("Data5: " + dataConvert[5] + " Data4: " + dataConvert[4]);
//            System.out.println("Val2: " + dist_2);
//
//
            dist_3 = ((dataConvert[4] << 8) | dataConvert[5]) / 58;
//            dist_3 = ((dataConvert[5] << 8) | dataConvert[4]) / 58;

//            System.out.println("Val3: " + dist_3);

        //    accel_x = (dataConvert[6] << 8) | dataConvert[7];
         //   accel_y = (dataConvert[8] << 8) | dataConvert[9];
           // accel_z = (dataConvert[10] << 8) | dataConvert[11];

//                accel_x = (dataConvert[0] << 4) | dataConvert[1] >>4;
//               accel_y = (dataConvert[2] << 4) | dataConvert[3]>>4;
//             accel_z = (dataConvert[4] << 4) | dataConvert[5]>>4;

            //dylan slack code 1
           // 7 6
//            accel_x = (dataConvert[6]|(dataConvert[7]<<8))>>6;
////
//            if (accel_x>0x01FF)
//            {
//                accel_x=(((~accel_x)+1)-0xFC00);
//            }
//
//            //98
//            accel_y = (dataConvert[8]|(dataConvert[9]<<8))>>6;
//            if (accel_y>0x01FF)
//            {
//                accel_y=(((~accel_y)+1)-0xFC00);
//            }
//
//            //11 10
//            accel_z = (dataConvert[10]|(dataConvert[11]<<8))>>6;
//            if (accel_z>0x01FF)
//            {
//                accel_z=(((~accel_z)+1)-0xFC00);
//            }

// Colman code

            accel_x = (dataConvert[6] << 8) | dataConvert[7];
            accel_y = (dataConvert[8] << 8) | dataConvert[9];
            accel_z = (dataConvert[10] << 8) | dataConvert[11];

            double x , y , z;
            x = (short)accel_x / 128;
            y = (short)accel_y / 128;
            z = (short)accel_z / 128;

//
//            Short.toString((short) accel_x);
//            Short.toString((short) accel_y);
//            Short.toString((short) accel_z);

            //  accel_z = (byteBuffer2[0] << 4 | byteBuffer2[1] >> 4);
          //  accel_z = (byteBuffer2[2] << 4 | byteBuffer2[3] >> 4);
          //  accel_z =  (byteBuffer2[4] << 4 | byteBuffer2[5] >> 4);

            final String[] strVals = { Integer.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3),
                    Double.toString( x),  Double.toString( y),  Double.toString( z) };


//            final String[] strVals = { Integer.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3),
//                    Short.toString( (short)accel_x),  Short.toString( (short)accel_y),  Short.toString( (short)accel_z) };
//           final String[] strVals = { Integer.toString(dist_1), Integer.toString(dist_2), Integer.toString(dist_3)};
//final String[] strVals = {Integer.toString(dist_1)};
//              final String[] strVals = {
//                Integer.toString(accel_x),  Integer.toString(accel_y),  Integer.toString(accel_z) };

            getActivity().runOnUiThread(() -> updateText(strVals) );
        }
    }

    public DebugFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
//        System.out.println("I have left this view");
        System.out.println("this is mmsocket: "+ mmSocket);
        if(mmSocket !=null) {
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        Button openButton = (Button)getView().findViewById(R.id.open);
        Button sendButton = (Button)getView().findViewById(R.id.send);
        Button closeButton = (Button)getView().findViewById(R.id.close);
        myLabel = (TextView)getView().findViewById(R.id.label);
        myTextbox = (EditText)getView().findViewById(R.id.entry);
        floatvalshow = (TextView)getView().findViewById(R.id.label2);
        value1 = (TextView)getView().findViewById(R.id.value1);
        value2 = (TextView)getView().findViewById(R.id.value2);
        value3 = (TextView)getView().findViewById(R.id.value3);
        acc1 = (TextView)getView().findViewById(R.id.AccXData);
        acc2 = (TextView)getView().findViewById(R.id.accYData);
        acc3 = (TextView)getView().findViewById(R.id.accZData);
        floatvalshow.setText("");

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug, container, false);
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
//        if(mmSocket==null) {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
//            if(mmOutputStream ==null)
                mmOutputStream = mmSocket.getOutputStream();
//            if(mmInputStream == null)
                mmInputStream = mmSocket.getInputStream();

            beginListenForData();
//        }
        myLabel.setText("Bluetooth Opened");
    }



    void updateText(String[] a)
    {
//        floatvalshow.setText("Ultrasonic 1: " + a[0] + "\nUltrasonic 2: " + a[1] + "\nUltrasonic 3: " + a[2] );

//        System.out.println("A[0] = " + a[0]);
//        System.out.println("A[1] = " + a[1]);
//        System.out.println("A[2] = " + a[2]);
//        System.out.println("A[3] = " + a[3]);
//        System.out.println("A[4] = " + a[4]);
//        System.out.println("A[5] = " + a[5]);

        if(a[0] != "0")
            value1.setText(a[0]);
        if(a[1] != "0")
            value2.setText(a[1]);
        if(a[2] != "0")
            value3.setText(a[2]);

        acc1.setText(a[3]);
        acc2.setText(a[4]);
        acc3.setText(a[5]);

//        acc1.setText(a[3]);
//        acc2.setText(a[4]);
//        acc3.setText(a[5]);




//        if(!a[0].equals("0") ) // Integer.parseInt(a[0]) < 150
//            value1.setText(a[0]);
//
//        if(!a[1].equals("0")  )//|| Integer.parseInt(a[1]) < 150
//            value2.setText(a[1]);
//
//        if(!a[2].equals("0"))//| Integer.parseInt(a[2]) < 150
//            value3.setText(a[2]);


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
                            System.out.println(bytesAvailable);


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
                                if(packetBytes[i] == 0x33)
                                {
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");
                                    System.out.println("Dylan wants to know abouot you");

                                }

                                if(byteCount > 11 && didI)
                                {
                                    didI = false;
                                    byteCount = 0;
//                                    System.out.println("Dyaln is gai first MSB in hex "+ Integer.toHexString(byteBuffer2[0]));
//                                    System.out.println("Dyaln is gai first LSB in hex" + Integer.toHexString(byteBuffer2[1]));
//                                    System.out.println("X " + (byteBuffer2[0] << 4 | byteBuffer2[1] >> 4));
//                                    System.out.println("Y " + (byteBuffer2[2] << 4 | byteBuffer2[3] >> 4));
//                                    System.out.println("Z " + (byteBuffer2[4] << 4 | byteBuffer2[5] >> 4));


                                    DebugFragment.TMFrame p = new DebugFragment.TMFrame(byteBuffer2);

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
        byte test = 0x1a;
        System.out.println("Coleman data: " + msg.getBytes());
        //mmOutputStream.write(msg.getBytes());
        mmOutputStream.write(test);
        myLabel.setText("Data Sent");
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
        myLabel.setText("Bluetooth Closed");
    }

}
