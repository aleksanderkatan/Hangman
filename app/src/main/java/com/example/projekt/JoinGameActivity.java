package com.example.projekt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt.adapters.BluetoothDeviceListAdapter;
import com.example.projekt.bluetooth.BluetoothConnectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinGameActivity extends AppCompatActivity {
    static public final String TAG = "JoinGameActivity";
    RecyclerView rv;
    Button btDiscover;
    Button btConnect;
    BluetoothDeviceListAdapter adapter;
    public ArrayList<BluetoothDevice> bluetoothDevices;
    BluetoothAdapter bluetoothAdapter;

    List<BluetoothDevice> connectionDevice;
    public static BluetoothConnectionService bcs;
    static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    private BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (this.checkSelfPermission("Manifest.permission.BLUETOOTH") != PackageManager.PERMISSION_GRANTED
            || this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED
            || this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "requesting for permissions");
                this.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH
                }, 1002);
            }

        } else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }

    }

    public void discover(View v) {
        Log.d(TAG, "discover: Looking for unpaired devices.");

        bluetoothDevices.clear();
        adapter.notifyDataSetChanged();

        checkBTPermissions();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "discover: Canceling discovery.");
        }
        boolean res = bluetoothAdapter.startDiscovery();
        Log.d(TAG, "discovery start result: " + res);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        bluetoothDevices = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver3, discoverDevicesIntent);

        btConnect = findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnectAction();
            }
        });


        btDiscover = findViewById(R.id.btDiscover);
        btDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "discovering...");
                discover(v);
            }
        });

        bcs = new BluetoothConnectionService();
        connectionDevice = new ArrayList<>();

        rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new BluetoothDeviceListAdapter(this, bluetoothAdapter, connectionDevice);
        adapter.setBluetoothDeviceListAdapter(bluetoothDevices);
        rv.setAdapter(adapter);
    }

//    private int resumeCalls = 0;
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume called");
//        if (resumeCalls >= 1)
//            finish();
//        else
//            resumeCalls += 1;
//    }

    void btConnectAction() {
        if (connectionDevice == null || connectionDevice.size() == 0) {
            Log.d(TAG, "no paired device selected");
            return;
        }

        BluetoothDevice dev = connectionDevice.get(0);
        if (dev.getBondState() != BluetoothDevice.BOND_BONDED) {
            Log.d(TAG, "device not paired");
            return;
        }
        Log.d(TAG, "starting bt connection");

        bcs = new BluetoothConnectionService();
        bcs.startClient(dev, MY_UUID_INSECURE);


        switchActivity();
    }

    private void switchActivity() {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString("StartedGameFrom", "Join");
        sharedPrefEditor.apply();

        Intent switchActivityIntent = new Intent(this, GameActivity.class);
        startActivity(switchActivityIntent);
    }

}