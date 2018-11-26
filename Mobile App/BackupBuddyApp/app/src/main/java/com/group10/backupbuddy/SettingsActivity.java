package com.group10.backupbuddy;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
    Float gridOpacity;

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

        Context c = MainMenuActivity.getAppContext();

        // Gets application context and saves settings
        SharedPreferences userPrefs = getApplicationContext().getSharedPreferences("UserSettings", 0);
        SharedPreferences.Editor edit = userPrefs.edit();
        edit.putBoolean("btEnable", btEnabled);
        edit.putBoolean("btIsDiscover", isDicoverable);
        edit.putBoolean("distanceGridEnabled", gridActive);
        edit.putFloat("gridOpacity", gridOpacity);
        edit.apply();

        Log.d(actID, "Bluetooth Broadcast Receivers closed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

//        Context c = MainMenuActivity.getAppContext();
//        navigationFun.getAppContext();
//        navigationFun.getActivity();
//        c = HomeFragment.getContext();


        // Gets application context and saves settings
        SharedPreferences userPrefs = getApplicationContext().getSharedPreferences("UserSettings", 0);

        btEnabled = userPrefs.getBoolean("btEnable", false);
        isDicoverable = userPrefs.getBoolean("btIsDiscover", false);
        gridActive = userPrefs.getBoolean("distanceGridEnabled", false);
        gridOpacity = userPrefs.getFloat("gridOpacity", 0.4f);

        toggleBt = (Button) findViewById(R.id.btToggleBtn);
        toggleDiscoverable = (Button) findViewById(R.id.discoverToggleBtn);
        connect = (Button) findViewById(R.id.btConnectBtn);
        toggleGrid = (Button) findViewById(R.id.gridToggleBtn);
        save = (Button) findViewById(R.id.saveBtn);
        connectionsList = (ListView) findViewById(R.id.lvNewDevices);

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
            toggleGrid.setText("Turn Distance Grid OFF");
        else
            toggleGrid.setText("Turn Distance Grid ON");

        if(connectionsList.getChildCount() == 0)
        {
            if(btAdapter != null && btAdapter.isEnabled())
                connect.setText("Scan for Devices");
            else
                connect.setText("Bluetooth Not Found");
        }
        else
            connect.setText("Connect");

        toggleGrid.setOnClickListener(e -> {

            gridActive = !gridActive;

            if(gridActive)
                toggleGrid.setText("Turn Distance Grid OFF");
            else
                toggleGrid.setText("Turn Distance Grid ON");
        });

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
                isDicoverable = true;
            }

            else
                Log.d(actID, "Making device discoverable failed. Perhaps the device does not support bluetooth.");

            if(btAdapter.isDiscovering())
            {
                btAdapter.cancelDiscovery();
                isDicoverable = false;
                toggleDiscoverable.setText("Make Device Discoverable");
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
                isDicoverable = true;
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
            btEnabled = true;

            // Listens for changes to the adapter and logs them via broadcast receivers
            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(btBROnOff, bluetoothIntent);
        }

        else if(btAdapter.isEnabled())
        {
            btAdapter.disable();
            btEnabled = false;

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


//
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.Spinner;
//import android.widget.TextView.BufferType;
//
//public class SettingsActivity extends Activity {
//
//    Button settings_done;
//
//    Spinner resolution_spinner;
//    EditText width_input;
//    EditText height_input;
//
//    EditText address1_input;
//    EditText address2_input;
//    EditText address3_input;
//    EditText address4_input;
//    EditText port_input;
//    EditText command_input;
//
//    Button address1_increment;
//    Button address2_increment;
//    Button address3_increment;
//    Button address4_increment;
//
//    Button address1_decrement;
//    Button address2_decrement;
//    Button address3_decrement;
//    Button address4_decrement;
//
//    RadioGroup port_group;
//    RadioGroup command_group;
//
//    int width = 640;
//    int height = 480;
//
//    int ip_ad1 = 192;
//    int ip_ad2 = 168;
//    int ip_ad3 = 2;
//    int ip_ad4 = 1;
//    int ip_port = 80;
//    String ip_command = "?action=stream";
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        Bundle extras = getIntent().getExtras();
//
//        ArrayAdapter<CharSequence> adapter =
//                ArrayAdapter.createFromResource(this, R.array.resolution_array,
//                        android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        resolution_spinner = (Spinner) findViewById(R.id.resolution_spinner);
//        resolution_spinner.setAdapter(adapter);
//
//        width_input = (EditText) findViewById(R.id.width_input);
//        height_input = (EditText) findViewById(R.id.height_input);
//
//        address1_input = (EditText) findViewById(R.id.address1_input);
//        address2_input = (EditText) findViewById(R.id.address2_input);
//        address3_input = (EditText) findViewById(R.id.address3_input);
//        address4_input = (EditText) findViewById(R.id.address4_input);
//        port_input = (EditText) findViewById(R.id.port_input);
//        command_input = (EditText) findViewById(R.id.command_input);
//
//        port_group = (RadioGroup) findViewById(R.id.port_radiogroup);
//        command_group = (RadioGroup) findViewById(R.id.command_radiogroup);
//
//        if (extras != null) {
//            width = extras.getInt("width", width);
//            height = extras.getInt("height", height);
//
//            ip_ad1 = extras.getInt("ip_ad1", ip_ad1);
//            ip_ad2 = extras.getInt("ip_ad2", ip_ad2);
//            ip_ad3 = extras.getInt("ip_ad3", ip_ad3);
//            ip_ad4 = extras.getInt("ip_ad4", ip_ad4);
//            ip_port = extras.getInt("ip_port", ip_port);
//            ip_command = extras.getString("ip_command");
//
//            width_input.setText(String.valueOf(width));
//            height_input.setText(String.valueOf(height));
//            resolution_spinner.setSelection(adapter.getCount() - 1);
//
//            address1_input.setText(String.valueOf(ip_ad1));
//            address2_input.setText(String.valueOf(ip_ad2));
//            address3_input.setText(String.valueOf(ip_ad3));
//            address4_input.setText(String.valueOf(ip_ad4));
//            port_input.setText(String.valueOf(ip_port));
//            command_input.setText(ip_command);
//        }
//
//        resolution_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {
//                Spinner spinner = (Spinner) parent;
//                String item = (String) spinner.getSelectedItem();
//                if (item.equals("640x480")) {
//                    width = 640;
//                    height = 480;
//                } else if (item.equals("480x640")) {
//                    width = 480;
//                    height = 640;
//                } else if (item.equals("320x240")) {
//                    width = 320;
//                    height = 240;
//                } else if (item.equals("240x320")) {
//                    width = 240;
//                    height = 320;
//                } else if (item.equals("176x144")) {
//                    width = 176;
//                    height = 144;
//                } else if (item.equals("144x176")) {
//                    width = 144;
//                    height = 176;
//                }
//                width_input.setText(String.valueOf(width));
//                height_input.setText(String.valueOf(height));
//            }
//
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        address1_increment = (Button) findViewById(R.id.address1_increment);
//        address1_increment.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address1_input.getText().toString();
//                        int val = ip_ad1;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val >= 0 && val < 255) {
//                            val += 1;
//                        } else if (val < 0) {
//                            val = 0;
//                        } else if (val >= 255) {
//                            val = 255;
//                        }
//
//                        ip_ad1 = val;
//                        address1_input.setText(String.valueOf(ip_ad1), BufferType.NORMAL);
//
//                    }
//                }
//        );
//        address2_increment = (Button) findViewById(R.id.address2_increment);
//        address2_increment.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address2_input.getText().toString();
//                        int val = ip_ad2;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val >= 0 && val < 255) {
//                            val += 1;
//                        } else if (val < 0) {
//                            val = 0;
//                        } else if (val >= 255) {
//                            val = 255;
//                        }
//
//                        ip_ad2 = val;
//                        address2_input.setText(String.valueOf(ip_ad2), BufferType.NORMAL);
//
//                    }
//                }
//        );
//        address3_increment = (Button) findViewById(R.id.address3_increment);
//        address3_increment.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address3_input.getText().toString();
//                        int val = ip_ad3;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val >= 0 && val < 255) {
//                            val += 1;
//                        } else if (val < 0) {
//                            val = 0;
//                        } else if (val >= 255) {
//                            val = 255;
//                        }
//
//                        ip_ad3 = val;
//                        address3_input.setText(String.valueOf(ip_ad3), BufferType.NORMAL);
//
//                    }
//                }
//        );
//        address4_increment = (Button) findViewById(R.id.address4_increment);
//        address4_increment.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address4_input.getText().toString();
//                        int val = ip_ad4;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val >= 0 && val < 255) {
//                            val += 1;
//                        } else if (val < 0) {
//                            val = 0;
//                        } else if (val >= 255) {
//                            val = 255;
//                        }
//
//                        ip_ad4 = val;
//                        address4_input.setText(String.valueOf(ip_ad4), BufferType.NORMAL);
//
//                    }
//                }
//        );
//
//        address1_decrement = (Button) findViewById(R.id.address1_decrement);
//        address1_decrement.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address1_input.getText().toString();
//                        int val = ip_ad1;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val > 0 && val <= 255) {
//                            val -= 1;
//                        } else if (val <= 0) {
//                            val = 0;
//                        } else if (val > 255) {
//                            val = 255;
//                        }
//
//                        ip_ad1 = val;
//                        address1_input.setText(String.valueOf(ip_ad1), BufferType.NORMAL);
//
//                    }
//                }
//        );
//
//        address2_decrement = (Button) findViewById(R.id.address2_decrement);
//        address2_decrement.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address2_input.getText().toString();
//                        int val = ip_ad2;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val > 0 && val <= 255) {
//                            val -= 1;
//                        } else if (val <= 0) {
//                            val = 0;
//                        } else if (val > 255) {
//                            val = 255;
//                        }
//
//                        ip_ad2 = val;
//                        address2_input.setText(String.valueOf(ip_ad2), BufferType.NORMAL);
//
//                    }
//                }
//        );
//        address3_decrement = (Button) findViewById(R.id.address3_decrement);
//        address3_decrement.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address3_input.getText().toString();
//                        int val = ip_ad3;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val > 0 && val <= 255) {
//                            val -= 1;
//                        } else if (val <= 0) {
//                            val = 0;
//                        } else if (val > 255) {
//                            val = 255;
//                        }
//
//                        ip_ad3 = val;
//                        address3_input.setText(String.valueOf(ip_ad3), BufferType.NORMAL);
//
//                    }
//                }
//        );
//        address4_decrement = (Button) findViewById(R.id.address4_decrement);
//        address4_decrement.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//                        String s = address4_input.getText().toString();
//                        int val = ip_ad4;
//                        if (!"".equals(s)) {
//                            val = Integer.parseInt(s);
//                        }
//                        if (val > 0 && val <= 255) {
//                            val -= 1;
//                        } else if (val <= 0) {
//                            val = 0;
//                        } else if (val > 255) {
//                            val = 255;
//                        }
//
//                        ip_ad4 = val;
//                        address4_input.setText(String.valueOf(ip_ad4), BufferType.NORMAL);
//
//                    }
//                }
//        );
//
//        port_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.port_80) {
//                    port_input.setText(getString(R.string.port_80));
//                } else if (checkedId == R.id.port_8080) {
//                    port_input.setText(getString(R.string.port_8080));
//                }
//            }
//        });
//
//        command_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.command_streaming) {
//                    command_input.setText(getString(R.string.command_streaming));
//                } else if (checkedId == R.id.command_videofeed) {
//                    command_input.setText(getString(R.string.command_videofeed));
//                }
//            }
//        });
//
//        settings_done = (Button) findViewById(R.id.settings_done);
//        settings_done.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View view) {
//
//                        String s;
//
//                        s = width_input.getText().toString();
//                        if (!"".equals(s)) {
//                            width = Integer.parseInt(s);
//                        }
//                        s = height_input.getText().toString();
//                        if (!"".equals(s)) {
//                            height = Integer.parseInt(s);
//                        }
//                        s = address1_input.getText().toString();
//                        if (!"".equals(s)) {
//                            ip_ad1 = Integer.parseInt(s);
//                        }
//                        s = address2_input.getText().toString();
//                        if (!"".equals(s)) {
//                            ip_ad2 = Integer.parseInt(s);
//                        }
//                        s = address3_input.getText().toString();
//                        if (!"".equals(s)) {
//                            ip_ad3 = Integer.parseInt(s);
//                        }
//                        s = address4_input.getText().toString();
//                        if (!"".equals(s)) {
//                            ip_ad4 = Integer.parseInt(s);
//                        }
//
//                        s = port_input.getText().toString();
//                        if (!"".equals(s)) {
//                            ip_port = Integer.parseInt(s);
//                        }
//
//                        s = command_input.getText().toString();
//                        ip_command = s;
//
//                        Intent intent = new Intent();
//                        intent.putExtra("width", width);
//                        intent.putExtra("height", height);
//                        intent.putExtra("ip_ad1", ip_ad1);
//                        intent.putExtra("ip_ad2", ip_ad2);
//                        intent.putExtra("ip_ad3", ip_ad3);
//                        intent.putExtra("ip_ad4", ip_ad4);
//                        intent.putExtra("ip_port", ip_port);
//                        intent.putExtra("ip_command", ip_command);
//
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//                }
//        );
//    }
//}
