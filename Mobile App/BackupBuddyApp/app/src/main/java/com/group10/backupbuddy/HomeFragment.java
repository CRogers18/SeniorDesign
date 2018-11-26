package com.group10.backupbuddy;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Set;
import java.util.UUID;


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
    Thread workerThread;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("User settings loaded successfully.");
        System.out.println("Android using byte-order: " + ByteOrder.nativeOrder());
        arm = (Button)getView().findViewById(R.id.security);
        wakeBtn = (ImageView) getView().findViewById(R.id.wakeButton);
        settings = (ImageView) getView().findViewById(R.id.settingIcon);
        mainText = (TextView) getView().findViewById(R.id.mainText);
        debug = (Button) getView().findViewById(R.id.debugMe);
        userSettings = new UserConfig(getContext());

        wakeBtn.setOnClickListener(e -> {

            char[] wakeCmd = {'W', 'W', 'W', 'W', 'W'};

            // transmit wakeCmd w/ bluetooth here
            //Intent cameraFeedIntent = new Intent(MainMenuActivity.this, BackUpCamera.class);
            // MainMenuActivity.this.startActivity(cameraFeedIntent);
            findBT();

            try {
                openBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                sendData(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                closeBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            Intent bluetoothFun = new Intent(getActivity(), DemoActivity.class);
             getActivity().startActivity(bluetoothFun);

//            Intent demo = new Intent(HomeFragment.this, DemoActivity.class);
//            HomeFragment.this.startActivity(demo);


        });
        arm.setOnClickListener(e -> {

            findBT();
            try {
                openBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                sendData(1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                closeBT();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


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

    void sendData(int test) throws IOException
    {
//        String msg = myTextbox.getText().toString();
        // msg += "\n";

        //byte test = 0xFC;
//        System.out.println("Coleman data: " + msg.getBytes());
        //mmOutputStream.write(msg.getBytes());
        if(test == 1)
            mmOutputStream.write(0xFC);
        if(test == 0)
            mmOutputStream.write(0x7f);
        else
            System.out.println("shit");

//        myLabel.setText("Data Sent");
    }
    void closeBT() throws IOException
    {

        //stopWorker = true;
        if(mmOutputStream!=null)
            mmOutputStream.close();
        if(mmInputStream!=null)
            mmInputStream.close();
        if(mmSocket!=null)
            mmSocket.close();
        //myLabel.setText("Bluetooth Closed");
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

       // beginListenForData();
//        }
        //myLabel.setText("Bluetooth Opened");
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            //myLabel.setText("No bluetooth adapter available");
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
       // myLabel.setText("Bluetooth Device Found");
    }
}
