package com.group10.backupbuddy;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class bluetoothLearning extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth_learning);
//    }
//}

<<<<<<< Updated upstream

=======
<<<<<<< HEAD
//origin https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
=======

>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes

import android.annotation.SuppressLint;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothServerSocket;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.Set;
        import java.util.UUID;

public class bluetoothLearning extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT=1;
    ListView lv_paired_devices;
    Set<BluetoothDevice> set_pairedDevices;
    ArrayAdapter adapter_paired_devices;
    BluetoothAdapter bluetoothAdapter;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int MESSAGE_READ=0;
    public static final int MESSAGE_WRITE=1;
    public static final int CONNECTING=2;
    public static final int CONNECTED=3;
    public static final int NO_SOCKET_FOUND=4;


<<<<<<< Updated upstream
=======
<<<<<<< HEAD
    //private ConnectedThread connectedAny;
    private BluetoothSocket btSocket;

    String bluetooth_message = "Z";
=======
>>>>>>> Stashed changes
    String bluetooth_message="00";



<<<<<<< Updated upstream
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes

    @SuppressLint("HandlerLeak")
    Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg_type) {
            super.handleMessage(msg_type);

<<<<<<< Updated upstream
            switch (msg_type.what){
=======
<<<<<<< HEAD
            switch (msg_type.what)
            {
=======
            switch (msg_type.what){
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
                case MESSAGE_READ:

                    byte[] readbuf=(byte[])msg_type.obj;
                    String string_recieved=new String(readbuf);
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
                    System.out.println("RECEIEVED: " + string_recieved);
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes

                    //do some task based on recieved string

                    break;
<<<<<<< Updated upstream
=======
<<<<<<< HEAD

                case MESSAGE_WRITE:

                    if(msg_type.obj!=null)
                    {
                        ConnectedThread connectedThread=new ConnectedThread((BluetoothSocket)msg_type.obj);
                        connectedThread.write(bluetooth_message.getBytes());
                    }

=======
>>>>>>> Stashed changes
                case MESSAGE_WRITE:

                    if(msg_type.obj!=null){
                        ConnectedThread connectedThread=new ConnectedThread((BluetoothSocket)msg_type.obj);
                        connectedThread.write(bluetooth_message.getBytes());

                    }
<<<<<<< Updated upstream
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
                    break;

                case CONNECTED:
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    break;

                case CONNECTING:
                    Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_SHORT).show();
                    break;

                case NO_SOCKET_FOUND:
                    Toast.makeText(getApplicationContext(),"No socket found",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

<<<<<<< Updated upstream
=======
<<<<<<< HEAD
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
=======
>>>>>>> Stashed changes


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
<<<<<<< Updated upstream
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_learning);
        initialize_layout();
        initialize_bluetooth();
        start_accepting_connection();
        initialize_clicks();
<<<<<<< Updated upstream

=======
<<<<<<< HEAD
=======

>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
    }

    public void start_accepting_connection()
    {
        //call this on button click as suited by you

        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
        Toast.makeText(getApplicationContext(),"accepting",Toast.LENGTH_SHORT).show();
    }
<<<<<<< Updated upstream
=======
<<<<<<< HEAD

    public void initialize_clicks()
    {
        lv_paired_devices.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 4)
                {
                    System.out.println("Transmit shit fam");
                    Message fuckU = new Message();
                    fuckU.what = MESSAGE_WRITE;
                    fuckU.obj = btSocket;
                    bluetooth_message = "A";

                    mHandler.sendMessage(fuckU);
                }

                else
                {
                    Object[] objects = set_pairedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) objects[position];

                    ConnectThread connectThread = new ConnectThread(device);
                    connectThread.start();

                    Toast.makeText(getApplicationContext(),"device choosen " + device.getName(),Toast.LENGTH_SHORT).show();
                }
=======
>>>>>>> Stashed changes
    public void initialize_clicks()
    {
        lv_paired_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object[] objects = set_pairedDevices.toArray();
                BluetoothDevice device = (BluetoothDevice) objects[position];

                ConnectThread connectThread = new ConnectThread(device);
                connectThread.start();

                Toast.makeText(getApplicationContext(),"device choosen "+device.getName(),Toast.LENGTH_SHORT).show();
<<<<<<< Updated upstream
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
            }
        });
    }

    public void initialize_layout()
    {
        lv_paired_devices = (ListView)findViewById(R.id.lv_paired_devices);
        adapter_paired_devices = new ArrayAdapter(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item);
        lv_paired_devices.setAdapter(adapter_paired_devices);
    }

    public void initialize_bluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(),"Your Device doesn't support bluetooth. you can play as Single player",Toast.LENGTH_SHORT).show();
            finish();
        }

        //Add these permisions before
//        <uses-permission android:name="android.permission.BLUETOOTH" />
//        <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        else {
            set_pairedDevices = bluetoothAdapter.getBondedDevices();

            if (set_pairedDevices.size() > 0) {

                for (BluetoothDevice device : set_pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    adapter_paired_devices.add(device.getName() + "\n" + device.getAddress());
                }
<<<<<<< Updated upstream
=======
<<<<<<< HEAD

                adapter_paired_devices.add("Click to transmit\n");
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
            }
        }
    }


    public class AcceptThread extends Thread
    {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NAME",MY_UUID);
            } catch (IOException e) { }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null)
                {
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
                    System.out.println("Connected");
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
                    // Do work to manage the connection (in a separate thread)
                    mHandler.obtainMessage(CONNECTED).sendToTarget();
                }
            }
        }
    }


    private class ConnectThread extends Thread {
<<<<<<< Updated upstream
=======
<<<<<<< HEAD

=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
            btSocket = mmSocket;
=======
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mHandler.obtainMessage(CONNECTING).sendToTarget();

                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            bluetooth_message = "Initial message"
//            mHandler.obtainMessage(MESSAGE_WRITE,mmSocket).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
<<<<<<< Updated upstream
            } catch (IOException e) { }
        }
    }
=======
<<<<<<< HEAD
                System.out.println("Socket closed");
            } catch (IOException e) { }
        }
    }

=======
            } catch (IOException e) { }
        }
    }
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
<<<<<<< Updated upstream
            byte[] buffer = new byte[2];  // buffer store for the stream
=======
<<<<<<< HEAD

            byte[] buffer = new byte[200];  // buffer store for the stream
=======
            byte[] buffer = new byte[2];  // buffer store for the stream
>>>>>>> origin/Android-Dev
>>>>>>> Stashed changes
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}