package com.group10.backupbuddy;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button toggleBt, toggleDiscoverable, connect, toggleGrid, save;
    Boolean btEnabled, isDicoverable, gridActive;
    Double gridOpacity;

    private final String actID = "SettingsActivity";

    BluetoothAdapter btAdapter;

    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView connectionsList;

    // Broadcast receiver for logging bluetooth ON/OFF status
    private final BroadcastReceiver btBROnOff = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals(btAdapter.ACTION_STATE_CHANGED))
            {
                final int btState = intent.getIntExtra(btAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch(btState)
                {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(actID, "Bluetooth turning ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(actID, "Bluetooth ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(actID, "Bluetooth turning OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(actID, "Bluetooth OFF");
                        break;
                }
            }
        }
    };

    // Broadcast receiver for handling bluetooth device discoverability and connection status
    private final BroadcastReceiver btBRDiscoverable = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            // Listener for if device scan mode changes
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
            {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (scanMode)
                {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(actID, "Device is now discoverable and can receive connections.");
                        break;

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(actID, "Device is not discoverable but can receive connections.");
                        break;

                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(actID, "Device is not discoverable and cannot receive connections.");
                        break;

                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(actID, "Attempting to connect device...");
                        break;

                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(actID, "Connection was established successfully.");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver btBRDeviceDiscovery = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(d);
                Log.d(actID, "Found device: " + d.getName() + " (" + d.getAddress() + ")");
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, btDevices);
                connectionsList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver btBRDeviceConnect = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (d.getBondState())
                {
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(actID, "Device has bonded with target device.");
                        break;

                    case BluetoothDevice.BOND_BONDING:
                        Log.d(actID, "Device bonding in progress...");
                        break;

                    case BluetoothDevice.BOND_NONE:
                        Log.d(actID, "Device not bonded with target device.");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(btBROnOff.isOrderedBroadcast())
            unregisterReceiver(btBROnOff);

        if(btBRDiscoverable.isOrderedBroadcast())
            unregisterReceiver(btBRDiscoverable);

        if(btBRDeviceDiscovery.isOrderedBroadcast())
            unregisterReceiver(btBRDeviceDiscovery);

        unregisterReceiver(btBRDeviceConnect);

        Log.d(actID, "Bluetooth Broadcast Receivers closed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        toggleBt = (Button) findViewById(R.id.btToggleBtn);
        toggleDiscoverable = (Button) findViewById(R.id.discoverToggleBtn);
        connect = (Button) findViewById(R.id.btConnectBtn);
        toggleGrid = (Button) findViewById(R.id.gridToggleBtn);
        save = (Button) findViewById(R.id.saveBtn);
        connectionsList = (ListView) findViewById(R.id.lvNewDevices);

        btEnabled = UserConfig.isBluetoothEnabled();
        isDicoverable = UserConfig.isIsDiscoverable();
        gridActive = UserConfig.isDistanceGridEnabled();
        gridOpacity = UserConfig.getGridOpacity();

        connectionsList.setOnItemClickListener(SettingsActivity.this);

        IntentFilter connectFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btBRDeviceConnect, connectFilter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null)
        {
            toggleBt.setText("Bluetooth Not Found");
            toggleDiscoverable.setEnabled(false);
        }
        else if(btAdapter.isEnabled())
            toggleBt.setText("Disable Bluetooth");
        else if(!btAdapter.isEnabled())
            toggleBt.setText("Enable Bluetooth");

        toggleBt.setOnClickListener(e -> toggleBluetooth());

        if(isDicoverable)
            toggleDiscoverable.setText("Make Device Discoverable");

        if(gridActive)
            toggleGrid.setText("Turn Distance Grid ON");
        else
            toggleGrid.setText("Turn Distance Grid OFF");

        if(connectionsList.getChildCount() == 0)
        {
            if(btAdapter != null && btAdapter.isEnabled())
                connect.setText("Scan for Devices");
            else
                connect.setText("Bluetooth Not Found");
        }

        else
            connect.setText("Connect");

        // Exits the activity with modified values
        save.setOnClickListener(e -> finish());

        toggleDiscoverable.setOnClickListener(e -> {

            // If adapter is not already discoverable, make discoverable
            if(btAdapter != null && btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            {
                // Will make the device discoverable for 2 minutes to help with pairing
                Intent makeDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                makeDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivity(makeDiscoverable);

                IntentFilter scanModeChangeFilter = new IntentFilter(btAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(btBRDiscoverable, scanModeChangeFilter);

                Log.d(actID, "Device has been made discoverable for 2 minutes.");
                toggleDiscoverable.setText("Now Discoverable");
            }

            else
                Log.d(actID, "Making device discoverable failed. Perhaps the device does not support bluetooth.");

            if(btAdapter.isDiscovering())
            {
                btAdapter.cancelDiscovery();

                // Android required permission check for bluetooth on versions higher than lollipop
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                {
                    int permCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                    permCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

                    if(permCheck != 0)
                        this.requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
                }

                btAdapter.startDiscovery();
                IntentFilter discoverDevice = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(btBRDeviceDiscovery, discoverDevice);
            }

            else if(!btAdapter.isDiscovering())
            {
                // Android required permission check for bluetooth on versions higher than lollipop
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                {
                    int permCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                    permCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

                    if(permCheck != 0)
                        this.requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
                }

                btAdapter.startDiscovery();
                IntentFilter discoverDevice = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(btBRDeviceDiscovery, discoverDevice);
            }

        });
    }

    public void toggleBluetooth()
    {
        if(btAdapter == null)
            Log.d(actID, "Device does not support bluetooth");

        else if(!btAdapter.isEnabled())
        {
            Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(btEnable);

            Log.d(actID, "Enabling Bluetooth");

            if(!toggleDiscoverable.isEnabled())
                toggleDiscoverable.setEnabled(true);

            toggleBt.setText("Disable Bluetooth");

            // Listens for changes to the adapter and logs them via broadcast receivers
            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(btBROnOff, bluetoothIntent);
        }

        else if(btAdapter.isEnabled())
        {
            btAdapter.disable();

            Log.d(actID, "Disabling Bluetooth");

            if(toggleDiscoverable.isEnabled())
                toggleDiscoverable.setEnabled(false);

            // Clear device list on bluetooth disable
            connectionsList.removeAllViews();

            toggleBt.setText("Enable Bluetooth");

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(btBROnOff, bluetoothIntent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        btAdapter.cancelDiscovery();
        Log.d(actID, "Attempting to bond with: " + btDevices.get(i).getName() + ", " + btDevices.get(i).getAddress());
        btDevices.get(i).createBond();
        Log.d(actID, "Bond with target device established");
    }
}
