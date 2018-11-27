package com.group10.backupbuddy;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static Context appContext;

    private final String actID = "MainMenuActivity";
    public static UserConfig userSettings;

    ImageView wakeBtn, settings;
    TextView mainText;
    Button debug;
    Button arm;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    byte[] readBuffer;
    int readBufferPosition;
    Thread workerThread;
    volatile boolean stopWorker;
    TextView wakeing;
    Boolean armme = false;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("User settings loaded successfully.");
        System.out.println("Android using byte-order: " + ByteOrder.nativeOrder());
        arm = (Button) getView().findViewById(R.id.security);
        wakeBtn = (ImageView) getView().findViewById(R.id.wakeButton);
        settings = (ImageView) getView().findViewById(R.id.settingIcon);
        mainText = (TextView) getView().findViewById(R.id.mainText);
        debug = (Button) getView().findViewById(R.id.debugMe);
        userSettings = new UserConfig(getContext());
        wakeing = (TextView) getView().findViewById(R.id.waking);
//        wakeing.setVisibility(View.INVISIBLE);
        wakeing.setText("");

        wakeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getActivity().runOnUiThread(() -> {
                    System.out.println("myRunONUI");
                    wakeing.setText("Waking.......");

                });
                wakeBtn.setEnabled(false);
                new LongRunningTask().execute();

//                findBT();

//                try {
//                    TimeUnit.MILLISECONDS.sleep(500);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//                try {
//                    openBT();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                try {
//                    TimeUnit.MILLISECONDS.sleep(500);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//                try {
//                    sendData(0);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                try {
//                    TimeUnit.MILLISECONDS.sleep(500);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//
//                try {
//                    closeBT();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                wakeing.setText("");
//
//                Intent bluetoothFun = new Intent(getActivity(), DemoActivity.class);
//                getActivity().startActivity(bluetoothFun);
            }
        });


//        wakeBtn.setOnClickListener(e -> {
//
//
//            getActivity().runOnUiThread(() -> {
//                System.out.println("myRunONUI");
//
//
//
//
//            });
        // transmit wakeCmd w/ bluetooth here
        //Intent cameraFeedIntent = new Intent(MainMenuActivity.this, BackUpCamera.class);
        // MainMenuActivity.this.startActivity(cameraFeedIntent);


//            Intent demo = new Intent(HomeFragment.this, DemoActivity.class);
//            HomeFragment.this.startActivity(demo);


//        });
        arm.setOnClickListener((View e) -> {

            armme = !armme;
        new LongRunningTask2().execute();


        });

        settings.setOnClickListener(e -> {

            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);

            settingsIntent.putExtra("btEnabled", userSettings.isBluetoothEnabled());
            settingsIntent.putExtra("btIsDiscover", userSettings.isIsDiscoverable());
            settingsIntent.putExtra("gridEnabled", userSettings.isDistanceGridEnabled());
            settingsIntent.putExtra("gridOpacity", userSettings.getGridOpacity());

            getActivity().startActivity(settingsIntent);

        });

        // Switches to settings activity here
//        settings.setOnClickListener(e -> {
//
//            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
//
//            settingsIntent.putExtra("btEnabled", userSettings.isBluetoothEnabled());
//            settingsIntent.putExtra("btIsDiscover", userSettings.isIsDiscoverable());
//            settingsIntent.putExtra("gridEnabled", userSettings.isDistanceGridEnabled());
//            settingsIntent.putExtra("gridOpacity", userSettings.getGridOpacity());
//
//            getActivity().startActivity(settingsIntent);
//
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    void sendData(int test) throws IOException {
//        String msg = myTextbox.getText().toString();
        // msg += "\n";

        //byte test = 0xFC;
//        System.out.println("Coleman data: " + msg.getBytes());
        //mmOutputStream.write(msg.getBytes());
        if (test == 1) {
            mmOutputStream.write(0x1c);
            System.out.println("arm");
        }
        if (test == 0) {
            mmOutputStream.write(0x7f);
            System.out.println("notarm");
        }


//        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException {

        //stopWorker = true;
//        if(mmOutputStream!=null)
        mmOutputStream.close();
//        if(mmInputStream!=null)
        mmInputStream.close();
//        if(mmSocket!=null)
        mmSocket.close();
        //myLabel.setText("Bluetooth Closed");
        System.out.println("bluetooth closed");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
//        if(mmSocket==null) {
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
//        if(mmSocket == null)
//            System.out.println("socket still null");
//            if(mmOutputStream ==null)
        mmOutputStream = mmSocket.getOutputStream();
//            if(mmInputStream == null)
        mmInputStream = mmSocket.getInputStream();

//        beginListenForData();
//        }
        System.out.println("bluetooth ok?");
        //myLabel.setText("Bluetooth Opened");
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //myLabel.setText("No bluetooth adapter available");
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
        // myLabel.setText("Bluetooth Device Found");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        ArrayList<String> myStringArray1 = new ArrayList<String>();

        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable() {
            public void run() {
                boolean didI = false;

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();


                        if (bytesAvailable > 0) {
                            System.out.println(bytesAvailable);


                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);


                            for (int i = 0; i < bytesAvailable; i++) {
                                System.out.println("packetBytes[" + i + "]:  " + Integer.toHexString(packetBytes[i]));


                                // System.out.print("ByteCount: " + byteCount);
                                // System.out.print("I for packetBytes: " + i);
                                // System.out.println("bytesAvailable:  " + bytesAvailable);


                                //  byteBuffer[byteCount] = (packetBytes[i] & 0xff);
//                                byteBuffer2[byteCount] = packetBytes[i];
                                //  System.out.println("val of packetBytes[i]: " + packetBytes + "i= " + i);
//                                byteCount++;

                                //System.out.println("val: = " + Val);
                                if (packetBytes[i] == 0x1f) {
                                    didI = true;
                                    System.out.println("eat my whole ass");
//                                    byteCount = 0;
                                }


//                                if(byteCount > 11 && didI)
//                                {
                                didI = false;
//                                    byteCount = 0;
//                                    System.out.println("Dyaln is gai first MSB in hex "+ Integer.toHexString(byteBuffer2[0]));
//                                    System.out.println("Dyaln is gai first LSB in hex" + Integer.toHexString(byteBuffer2[1]));
//                                    System.out.println("X " + (byteBuffer2[0] << 4 | byteBuffer2[1] >> 4));
//                                    System.out.println("Y " + (byteBuffer2[2] << 4 | byteBuffer2[3] >> 4));
//                                    System.out.println("Z " + (byteBuffer2[4] << 4 | byteBuffer2[5] >> 4));


//                                    DebugFragment.TMFrame p = new DebugFragment.TMFrame(byteBuffer2);

//                                }

                            }


                        }
                    } catch (IOException ex) {
                        System.out.println("I am a failure");
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    private class LongRunningTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            findBT();
            try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    openBT();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    sendData(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                try {
                    closeBT();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

         return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

            //update ui here
//            System.out.println("do it make it here?");
            wakeing.setText("");

                Intent bluetoothFun = new Intent(getActivity(), DemoActivity.class);
                getActivity().startActivity(bluetoothFun);

            wakeBtn.setEnabled(true);
            //red color is #59ff0000
            // yellow color is #59FFC400
            // green color is #5900FF22
        }


    }
    private class LongRunningTask2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            findBT();
            try {
                openBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                sendData(1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            try {
                closeBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

            //update ui here
//            System.out.println("do it make it here?");

            if(armme)
                arm.setText("Disarm");
            else
                arm.setText("Arm Security");




            //red color is #59ff0000
            // yellow color is #59FFC400
            // green color is #5900FF22
        }


    }
}
