package com.group10.backupbuddy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Coleman on 9/2/2018.
 */

public class UserConfig extends Application
{
    private Context appCont;

    private static boolean bluetoothEnabled;
    private static boolean isDiscoverable;

    // variable for connection settings here

    private static boolean distanceGridEnabled;
    private static float gridOpacity;

    public UserConfig()
    {

    }

    public UserConfig(Context appContext)
    {
        appCont = appContext;
        SharedPreferences userPrefs = appCont.getSharedPreferences("UserSettings", 0);

        // If the user settings have not been generated yet
        if(!userPrefs.contains("btEnable"))
        {
            System.out.println("Prefs don't exist");
            SharedPreferences.Editor edit = userPrefs.edit();
            edit.putBoolean("btEnable", false);
            edit.putBoolean("btIsDiscover", false);
            edit.putBoolean("distanceGridEnabled", false);
            edit.putFloat("gridOpacity", 0.4f);
            edit.apply();

            bluetoothEnabled = false;
            isDiscoverable = false;
            distanceGridEnabled = false;
            gridOpacity = 0.4f;
        }

        else
        {
            bluetoothEnabled = userPrefs.getBoolean("btEnable", false);
            isDiscoverable = userPrefs.getBoolean("btIsDiscover", false);
            distanceGridEnabled = userPrefs.getBoolean("distanceGridEnabled", false);
            gridOpacity = userPrefs.getFloat("gridOpacity", 0.4f);
        }
    }

    public static boolean isBluetoothEnabled() {
        return bluetoothEnabled;
    }

    public static void setBluetoothEnabled(boolean bluetoothEnabled) {
        UserConfig.bluetoothEnabled = bluetoothEnabled;
    }

    public static boolean isIsDiscoverable() {
        return isDiscoverable;
    }

    public static void setIsDiscoverable(boolean isDiscoverable) {
        UserConfig.isDiscoverable = isDiscoverable;
    }

    public static boolean isDistanceGridEnabled() {
        return distanceGridEnabled;
    }

    public static void setDistanceGridEnabled(boolean distanceGridEnabled) {
        UserConfig.distanceGridEnabled = distanceGridEnabled;
    }

    public static float getGridOpacity() {
        return gridOpacity;
    }

    public static void setGridOpacity(float gridOpacity) {
        UserConfig.gridOpacity = gridOpacity;
    }
}
