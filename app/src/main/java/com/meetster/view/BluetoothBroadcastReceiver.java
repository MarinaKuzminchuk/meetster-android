package com.meetster.view;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.meetster.controller.SearchController;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int DISCOVERABLE_DURATION_IN_SECONDS = 3600;
    private Activity parentActivity;
    private BluetoothAdapter btAdapter;
    private FoundUsersRecyclerViewAdapter foundUsersAdapter;
    private SearchController searchController;

    public BluetoothBroadcastReceiver(Activity parentActivity,
                                      BluetoothAdapter btAdapter,
                                      FoundUsersRecyclerViewAdapter foundUsersAdapter,
                                      SearchController searchController) {
        this.parentActivity = parentActivity;
        this.btAdapter = btAdapter;
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

    private void handleFoundDevice(Intent intent) {
        // Discovery has found a device
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (device.getName() == null) {
            return;
        }
        searchController.addNewlyFoundUser(device.getName());
        foundUsersAdapter.updateData(
                searchController.getNewlyFoundUsers(), searchController.getPreviouslyFoundUsers());
    }

    private void handleBtStateTransition() {
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (btAdapter.isEnabled() && !btAdapter.isDiscovering()) {
            // Start discovering other devices
            btAdapter.startDiscovery();
            // Make our device discoverable when bluetooth was turned on
            if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
                String btName = searchController.getBluetoothName();
                btAdapter.setName(btName);
            }
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_IN_SECONDS);
            parentActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
        }
    }
}
