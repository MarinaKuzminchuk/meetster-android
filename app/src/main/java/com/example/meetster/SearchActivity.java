package com.example.meetster;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    TextView btStatus, foundUsers;
    ImageView btImage;
    Button btOn, btOff, btDiscover, btFound;
    BluetoothAdapter btAdapter;

    ActivityResultLauncher<Intent> turnOnBluetoothActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        btImage.setImageResource(R.drawable.ic_action_on);
                        showToast("Bluetooth is on");
                        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    } else {
                        // user denied to turn on bluetooth
                        showToast("Could not turn on bluetooth");
                    }
                }
            });

    // Create a BroadcastReceiver for ACTION_FOUND.
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btStatus = findViewById(R.id.statusBt);
        foundUsers = findViewById(R.id.listOfUsers);
        btImage = findViewById(R.id.imageBt);
        btOn = findViewById(R.id.turnOnBt);
        btOff = findViewById(R.id.turnOffBt);
        btDiscover = findViewById(R.id.discoverableBt);
        btFound = findViewById(R.id.allFoundBt);
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

        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }
        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 4);
        }
        btAdapter.startDiscovery();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (device.getName() == null) {
                        return;
                    }
                    foundUsers.append(device.getName() + "\n");
                } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    if (btAdapter.isEnabled()) {
                        // Make our device discoverable
                        showToast("Making your device discoverable");

                        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        if (!btAdapter.isDiscovering()) {
                            if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 7);
                            }
                            if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 8);
                            }
                            showToast("BT State" + btAdapter.getState());
                            if (btAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                btAdapter.setName("Marina");
                            }

                            int requestCode = 1;
                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                            startActivityForResult(discoverableIntent, requestCode);
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);

        // start search
        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Turn on Bluetooth if it's not enabled
                if (!btAdapter.isEnabled()) {
                    showToast("Turning on bluetooth");
                    requestBlePermissions(SearchActivity.this, REQUEST_ENABLE_BT);
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    turnOnBluetoothActivityResultLauncher.launch(intent);
                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });
        // turns off bluetooth on click
        btOff.setOnClickListener(new View.OnClickListener() {
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
        // discover bluetooth button click
//        btDiscover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                if (!btAdapter.isDiscovering()) {
//                    showToast("Making your device discoverable");
//
//                    if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 7);
//                    }
//                    if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 8);
//                    }
//                    if (btAdapter.getState() == BluetoothAdapter.STATE_ON){
//                        btAdapter.setName("Marina");
//                    }
//
//                    int requestCode = 1;
//                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
//                    startActivityForResult(discoverableIntent, requestCode);
//                }
//            }
//        });
        // get all find users button click
//        btFound.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (btAdapter.isEnabled()) {
////                    foundUsers.setText("Paired devices");
////                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
////                        return;
////                    }
////                    Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
////                    for(BluetoothDevice device: devices){
////                        foundUsers.append("\nDevice" + device.getName() + "," + device);
////                    }
//                }
//            }
//        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    btImage.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                } else {
                    // user denied to turn on bluetooth
                    showToast("Could not turn on bluetooth");
                }
                break;
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
