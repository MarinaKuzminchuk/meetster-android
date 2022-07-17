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
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

import java.util.List;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int DISCOVERABLE_DURATION_IN_SECONDS = 3600;
    private Activity parentActivity;
    private User authenticatedUser;
    private Filters filters;
    private BluetoothAdapter btAdapter;
    private FoundUsersRecyclerViewAdapter foundUsersAdapter;
    private SearchController searchController;

    public BluetoothBroadcastReceiver(Activity parentActivity,
                                      User authenticatedUser,
                                      Filters filters,
                                      BluetoothAdapter btAdapter,
                                      FoundUsersRecyclerViewAdapter foundUsersAdapter,
                                      SearchController searchController) {
        this.parentActivity = parentActivity;
        this.authenticatedUser = authenticatedUser;
        this.filters = filters;
        this.btAdapter = btAdapter;
        this.foundUsersAdapter = foundUsersAdapter;
        this.searchController = searchController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            // Discovery has found a device
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (device.getName() == null) {
                return;
            }
            filterFoundUser(device.getName());
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (btAdapter.isEnabled() && !btAdapter.isDiscovering()) {
                // Make our device discoverable when bluetooth was turned on
                if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    String btName = "meetster/" + authenticatedUser.name + "/" + filters.specialty + "/" + filters.tag;
                    btAdapter.setName(btName);
                }
                if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                btAdapter.startDiscovery();
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_IN_SECONDS);
                parentActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
            }
        }
    }

    private void filterFoundUser(String btName) {
        if (btName.startsWith("meetster")){
            String[] parts = btName.split("/");
            String name = parts[1];
            String specialty = parts[2];
            String tag = parts[3];
            FoundUser foundUser = new FoundUser(new User(name), new Filters(specialty, tag));
            if (filters.specialty.equals(specialty) || filters.tag.equals(tag)){
                foundUsersAdapter.addFoundUser(foundUser);
                List<FoundUser> updatedFoundUsers = foundUsersAdapter.getFoundUsers();
                // save all found users after new user was found
                searchController.saveFoundUsers(updatedFoundUsers);
            }
        }
    }
}
