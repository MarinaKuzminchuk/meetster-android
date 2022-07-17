package com.meetster.view;

import static com.meetster.view.IntentExtraKeys.AUTHENTICATED_USER;
import static com.meetster.view.IntentExtraKeys.FILTERS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meetster.R;
import com.meetster.controller.SearchController;
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

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
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    private RecyclerView foundUsersRV;
    private ImageView btImage;
    private Button searchBtn;
    private Button stopSearchBtn;
    private BluetoothAdapter btAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private BroadcastReceiver receiver;
    private SearchController searchController;

    private FoundUsersRecyclerViewAdapter foundUsersAdapter;
    private User authenticatedUser;
    private Filters filters;

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
        User authenticatedUser = (User) intent.getSerializableExtra(AUTHENTICATED_USER);
        Filters filters = (Filters) intent.getSerializableExtra(FILTERS);

        // set image
        btImage.setImageResource(R.drawable.ic_action_off);

        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        searchController = new SearchController(sharedPref, authenticatedUser, filters);

        //create and set layout manager for each RecyclerView
        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(this);
        foundUsersRV.setLayoutManager(firstLayoutManager);

        List<FoundUser> previouslyFoundUsers = searchController.getPreviouslyFoundUsers();

        //Initializing and set adapter for each RecyclerView
        foundUsersAdapter = new FoundUsersRecyclerViewAdapter(this, authenticatedUser, previouslyFoundUsers);
        foundUsersRV.setAdapter(foundUsersAdapter);

        receiver = new BluetoothBroadcastReceiver(
                this, btAdapter, foundUsersAdapter, searchController);
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
                    requestBlePermissions(SearchActivity.this, REQUEST_ENABLE_BT);
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            }
        });
        // stop search
        stopSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    btAdapter.disable();
                    btImage.setImageResource(R.drawable.ic_action_off);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                btImage.setImageResource(R.drawable.ic_action_on);
            }
        } else if (requestCode == REQUEST_DISCOVER_BT) {
            if (resultCode != DISCOVERABLE_DURATION_IN_SECONDS) {
                Toast.makeText(this, "You cannot be discovered", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
