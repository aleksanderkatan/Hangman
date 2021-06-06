package com.example.projekt.adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt.R;

import java.util.List;
import java.util.UUID;

public class BluetoothDeviceListAdapter extends RecyclerView.Adapter<BluetoothDeviceListAdapter.MyViewHolder> {
    public static final String TAG = "BTDeviceListAdapter";
    private final Context context;
    private List<BluetoothDevice> bluetoothDevices;
    private static BluetoothAdapter bluetoothAdapter;
    private static List<BluetoothDevice> deviceReference;
    private static Button btConnect;


    public BluetoothDeviceListAdapter(Context context, BluetoothAdapter bluetoothAdapter,
                                      List<BluetoothDevice> device, Button btConnect) {
        this.context = context;
        BluetoothDeviceListAdapter.bluetoothAdapter = bluetoothAdapter;
        deviceReference = device;
        BluetoothDeviceListAdapter.btConnect = btConnect;
    }

    public void setBluetoothDeviceListAdapter(List<BluetoothDevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;
    }

    @NonNull
    @Override
    public BluetoothDeviceListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_bluetooth_device, parent, false);

        return new BluetoothDeviceListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceListAdapter.MyViewHolder holder, int position) {
        BluetoothDevice device = this.bluetoothDevices.get(position);
        String text = device.getName() + "\n(" + device.getAddress() + ")";
        holder.txtBluetoothDevice.setText(text);
        holder.device = bluetoothDevices.get(position);
    }

    @Override
    public int getItemCount() {
        return this.bluetoothDevices.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtBluetoothDevice;
        Button btPair;
        BluetoothDevice device;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBluetoothDevice = itemView.findViewById(R.id.txtBluetoothDevice);
            btPair = itemView.findViewById(R.id.btPair);
            btPair.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    bluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "onItemClick: You Clicked on a device.");
                    deviceReference.clear();
                    deviceReference.add(device);
                    btConnect.setText("Play with " + device.getName());
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "Already bonded!");
                        btConnect.setVisibility(View.VISIBLE);
                    }
                    device.createBond();
                }
            });

        }
    }


}