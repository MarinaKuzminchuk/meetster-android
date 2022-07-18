package com.meetster.search.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class BluetoothClient {

    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_DISCOVER_BT = 1;
    public static final int DISCOVERABLE_DURATION_IN_SECONDS = 3600;
    // Permissions for Android < 12
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    // Permissions for Android >= 12
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
    };
    private BluetoothAdapter btAdapter;
    private Activity activity;

    public BluetoothClient(Activity activity) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
    }

    // Turn on Bluetooth if it's not enabled
    public void enableBluetooth() {
        if (!btAdapter.isEnabled()) {
            requestBlePermissions(activity);
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    // Turn off Bluetooth if it's enabled
    public void disableBluetooth() {
        if (btAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btAdapter.disable();
        }
    }

    public boolean isEnabled() {
        return btAdapter.isEnabled();
    }

    public boolean isDiscovering() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return btAdapter.isDiscovering();
    }

    private static void requestBlePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, BluetoothClient.REQUEST_ENABLE_BT);
        } else {
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, BluetoothClient.REQUEST_ENABLE_BT);
        }
    }

    public void startDiscovery() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btAdapter.startDiscovery();
    }

    public boolean isOn() {
        return btAdapter.getState() == BluetoothAdapter.STATE_ON;
    }

    public void setName(String btName) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btAdapter.setName(btName);
    }

    public void makeDeviceDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_IN_SECONDS);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
    }
}
