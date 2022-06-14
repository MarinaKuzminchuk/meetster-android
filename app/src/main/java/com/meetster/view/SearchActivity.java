package com.meetster.view;

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

import com.example.meetster.R;
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

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

    private RecyclerView foundUsersRV;
    private ImageView btImage;
    private Button searchBtn;
    private Button stopSearchBtn;
    private BluetoothAdapter btAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        foundUsersRV = findViewById(R.id.foundUsers);
        btImage = findViewById(R.id.imageBt);
        searchBtn = findViewById(R.id.search);
        stopSearchBtn = findViewById(R.id.stopSearch);
        // initialize an adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // set image depending on status
        if (btAdapter.isEnabled()) {
            btImage.setImageResource(R.drawable.ic_action_on);
        } else {
            btImage.setImageResource(R.drawable.ic_action_off);
        }

        //create and set layout manager for each RecyclerView
        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(this);
        foundUsersRV.setLayoutManager(firstLayoutManager);
        // add divider (line) between items in recycler view
        foundUsersRV.addItemDecoration(
                new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));

        List<FoundUser> previouslyFoundUsers = new ArrayList<>();
        previouslyFoundUsers.add(new FoundUser(new User("Valera"), new Filters("HTW", "IMI", "GAME", "play")));
        previouslyFoundUsers.add(new FoundUser(new User("Sara"), new Filters("TU", "ECO", "WEB", "play")));
        //Initializing and set adapter for each RecyclerView
        FoundUsersRecyclerViewAdapter foundUsersAdapter = new FoundUsersRecyclerViewAdapter(this, previouslyFoundUsers);
        foundUsersRV.setAdapter(foundUsersAdapter);

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
                    foundUsersAdapter.addFoundUser(new FoundUser(new User(device.getName()), new Filters("", "", "", "")));
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
        searchBtn.setOnClickListener(new View.OnClickListener() {
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
        stopSearchBtn.setOnClickListener(new View.OnClickListener() {
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
