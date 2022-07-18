package com.meetster.search.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.meetster.search.FoundUsersRecyclerViewAdapter;
import com.meetster.search.SearchController;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int DISCOVERABLE_DURATION_IN_SECONDS = 3600;
    private Activity activity;
    private BluetoothClient bluetoothClient;
    private FoundUsersRecyclerViewAdapter foundUsersAdapter;
    private SearchController searchController;

    public BluetoothBroadcastReceiver(Activity activity,
                                      BluetoothClient bluetoothClient,
                                      FoundUsersRecyclerViewAdapter foundUsersAdapter,
                                      SearchController searchController) {
        this.activity = activity;
        this.bluetoothClient = bluetoothClient;
        this.foundUsersAdapter = foundUsersAdapter;
        this.searchController = searchController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            handleFoundDevice(intent);
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            handleBtStateTransition();
        }
    }

    // Found a device during discovery
    private void handleFoundDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (device.getName() == null) {
            return;
        }
        searchController.addNewlyFoundUser(device.getName());
        foundUsersAdapter.updateData(
                searchController.getNewlyFoundUsers(), searchController.getPreviouslyFoundUsers());
    }

    // State of Bluetooth has changed
    private void handleBtStateTransition() {
        if (bluetoothClient.isEnabled() && !bluetoothClient.isDiscovering()) {
            // Start discovering other devices
            bluetoothClient.startDiscovery();
            // Make our device discoverable when bluetooth was turned on
            if (bluetoothClient.isOn()) {
                String btName = searchController.getBluetoothName();
                bluetoothClient.setName(btName);
            }
            bluetoothClient.makeDeviceDiscoverable();
        }
    }
}
