package com.group10.backupbuddy;


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

import java.nio.ByteOrder;


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


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("User settings loaded successfully.");
        System.out.println("Android using byte-order: " + ByteOrder.nativeOrder());

        wakeBtn = (ImageView) getView().findViewById(R.id.wakeButton);
//        settings = (ImageView) getView().findViewById(R.id.settingIcon);
        mainText = (TextView) getView().findViewById(R.id.mainText);
        debug = (Button) getView().findViewById(R.id.debugMe);
        wakeBtn.setOnClickListener(e -> {

            char[] wakeCmd = {'W', 'W', 'W', 'W', 'W'};

            // transmit wakeCmd w/ bluetooth here
            //Intent cameraFeedIntent = new Intent(MainMenuActivity.this, BackUpCamera.class);
            // MainMenuActivity.this.startActivity(cameraFeedIntent);

             Intent bluetoothFun = new Intent(getActivity(), DemoActivity.class);
             getActivity().startActivity(bluetoothFun);

//            Intent demo = new Intent(HomeFragment.this, DemoActivity.class);
//            HomeFragment.this.startActivity(demo);


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

}
