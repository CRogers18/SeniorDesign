package com.group10.backupbuddy;

/**
 * Created by Coleman on 9/2/2018.
 */

public class UserConfig
{
    private static boolean bluetoothEnabled = false;
    private static boolean isDiscoverable = false;

    // variable for connection settings here

    private static boolean distanceGridEnabled = true;
    private static double gridOpacity = 0.4;

    public UserConfig()
    {

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

    public static double getGridOpacity() {
        return gridOpacity;
    }

    public static void setGridOpacity(double gridOpacity) {
        UserConfig.gridOpacity = gridOpacity;
    }
}
