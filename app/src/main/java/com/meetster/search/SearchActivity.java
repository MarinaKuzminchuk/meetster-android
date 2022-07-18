package com.meetster.search;

import static com.meetster.IntentExtraKeys.EXTRA_AUTHENTICATED_USER;
import static com.meetster.IntentExtraKeys.EXTRA_FILTERS_SPECIALTY;
import static com.meetster.IntentExtraKeys.EXTRA_FILTERS_TAG;
import static com.meetster.PreferencesKeys.PREF_AUTHENTICATED_USER;
import static com.meetster.PreferencesKeys.PREF_FILTERS_SPECIALTY;
import static com.meetster.PreferencesKeys.PREF_FILTERS_TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetster.R;
import com.meetster.model.Filters;
import com.meetster.model.User;
import com.meetster.search.bluetooth.BluetoothBroadcastReceiver;
import com.meetster.search.bluetooth.BluetoothClient;

public class SearchActivity extends AppCompatActivity {

    private ImageView btImage;
    private Button searchBtn;
    private Button stopSearchBtn;
    private RecyclerView foundUsersRV;
    private FoundUsersRecyclerViewAdapter foundUsersAdapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        foundUsersRV = findViewById(R.id.foundUsers);
        btImage = findViewById(R.id.imageBt);
        searchBtn = findViewById(R.id.search);
        stopSearchBtn = findViewById(R.id.stopSearch);

        BluetoothClient bluetoothClient = new BluetoothClient(this);
        bluetoothClient.disableBluetooth();
        btImage.setImageResource(R.drawable.ic_action_off);
        stopSearchBtn.setVisibility(View.GONE);
        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);

        Intent intent = getIntent();
        User authenticatedUser;
        Filters filters;
        if (intent.getExtras() == null) {
            authenticatedUser = new User(sharedPref.getString(PREF_AUTHENTICATED_USER, ""));
            filters = new Filters(
                    sharedPref.getString(PREF_FILTERS_SPECIALTY, ""),
                    sharedPref.getString(PREF_FILTERS_TAG, ""));
        } else {
            authenticatedUser = new User(intent.getStringExtra(EXTRA_AUTHENTICATED_USER));
            filters = new Filters(intent.getStringExtra(EXTRA_FILTERS_SPECIALTY), intent.getStringExtra(EXTRA_FILTERS_TAG));
        }

        SearchController searchController = new SearchController(sharedPref, authenticatedUser, filters);

        foundUsersRV.setLayoutManager(new LinearLayoutManager(this));
        foundUsersAdapter = new FoundUsersRecyclerViewAdapter(
                this, authenticatedUser, searchController.getPreviouslyFoundUsers());
        foundUsersRV.setAdapter(foundUsersAdapter);

        receiver = new BluetoothBroadcastReceiver(
                bluetoothClient, foundUsersAdapter, searchController);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);

        // start search
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothClient.enableBluetooth();
                btImage.setImageResource(R.drawable.ic_action_on);
                searchBtn.setVisibility(View.GONE);
                stopSearchBtn.setVisibility(View.VISIBLE);
            }
        });
        // stop search
        stopSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothClient.disableBluetooth();
                btImage.setImageResource(R.drawable.ic_action_off);
                stopSearchBtn.setVisibility(View.GONE);
                searchBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothClient.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                btImage.setImageResource(R.drawable.ic_action_on);
            }
        } else if (requestCode == BluetoothClient.REQUEST_DISCOVER_BT) {
            if (resultCode != BluetoothClient.DISCOVERABLE_DURATION_IN_SECONDS) {
                Toast.makeText(this, "You cannot be discovered", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
