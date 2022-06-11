package com.example.meetster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int DISCOVERABLE_DURATION_IN_SECONDS = 3600;

    // Permissions for Android < 12
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    // Permissions for Android >= 12
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private TextView btStatus;
    private RecyclerView rvNewlyFoundUsers;
    private RecyclerView rvPreviouslyFoundUsers;
    private ImageView btImage;
    private Button btnSearch;
    private Button btnStopSearch;
    private BluetoothAdapter btAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btStatus = findViewById(R.id.statusBt);
        rvNewlyFoundUsers = findViewById(R.id.newlyFoundUsers);
        rvPreviouslyFoundUsers = findViewById(R.id.previouslyFoundUsers);
        btImage = findViewById(R.id.imageBt);
        btnSearch = findViewById(R.id.search);
        btnStopSearch = findViewById(R.id.stopSearch);
        // initialize an adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // check if bluetooth is available or not
        if (btAdapter == null) {
            btStatus.setText("Bluetooth is not available");
        } else {
            btStatus.setText("Bluetooth is available");
        }

        // set image depending on status
        if (btAdapter.isEnabled()) {
            btImage.setImageResource(R.drawable.ic_action_on);
        } else {
            btImage.setImageResource(R.drawable.ic_action_off);
        }

        //create and set layout manager for each RecyclerView
        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager secondLayoutManager = new LinearLayoutManager(this);

        rvNewlyFoundUsers.setLayoutManager(firstLayoutManager);
        rvPreviouslyFoundUsers.setLayoutManager(secondLayoutManager);

        rvNewlyFoundUsers.addItemDecoration(
                new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));
        rvPreviouslyFoundUsers.addItemDecoration(
                new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));

        List<List<String>> newlyFoundUsers = new ArrayList<>();
        newlyFoundUsers.add(Arrays.asList("TEST1", "uni:HTW, specialty:IMI"));
        newlyFoundUsers.add(Arrays.asList("TEST2", "uni:HTW, specialty:IMI"));

        List<List<String>> previouslyFoundUsers = new ArrayList<>();
        previouslyFoundUsers.add(Arrays.asList("TEST3", "uni:HTW, specialty:IMI"));
        previouslyFoundUsers.add(Arrays.asList("TEST4", "uni:HTW, specialty:IMI"));
        //Initializing and set adapter for each RecyclerView
        FilterRecyclerViewAdapter newlyFoundUsersAdapter = new FilterRecyclerViewAdapter(this, newlyFoundUsers);
        FilterRecyclerViewAdapter previouslyFoundUsersAdapter = new FilterRecyclerViewAdapter(this, previouslyFoundUsers);

        rvNewlyFoundUsers.setAdapter(newlyFoundUsersAdapter);
        rvPreviouslyFoundUsers.setAdapter(previouslyFoundUsersAdapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    // Discovery has found a device
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (device.getName() == null) {
                        return;
                    }
                    newlyFoundUsersAdapter.addFoundUser(device.getName(), device.toString());
                } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (btAdapter.isEnabled() && !btAdapter.isDiscovering()) {
                        // Make our device discoverable when bluetooth was turned on
                        showToast("Making your device discoverable");
                        if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
                            btAdapter.setName("Marina");
                        }
                        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        btAdapter.startDiscovery();
                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_IN_SECONDS);
                        startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);

        // start search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Turn on Bluetooth if it's not enabled
                if (!btAdapter.isEnabled()) {
                    showToast("Turning on bluetooth");
                    requestBlePermissions(SearchActivity.this, REQUEST_ENABLE_BT);
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });
        // stop search
        btnStopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btAdapter.isEnabled()) {
                    showToast("Turning off bluetooth");
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    btAdapter.disable();
                    btImage.setImageResource(R.drawable.ic_action_off);
                } else {
                    showToast("Bluetooth is already off");
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                btImage.setImageResource(R.drawable.ic_action_on);
                showToast("Bluetooth is on");
            } else {
                // user denied to turn on bluetooth
                showToast("Could not turn on bluetooth");
            }
        } else if (requestCode == REQUEST_DISCOVER_BT) {
            if (resultCode == DISCOVERABLE_DURATION_IN_SECONDS) {
                showToast("You can be discovered now for " + DISCOVERABLE_DURATION_IN_SECONDS + " seconds");
            } else {
                // user denied to turn on discovery
                showToast("You cannot be discovered");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * opens the chat window when another user is selected
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
