package com.example.projekt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HostGameActivity extends AppCompatActivity {
    private static final String TAG = "HostGameActivity";
    TextView numPointsToWin;
    CheckBox cbSafetyWords;
    TextView numSafetyWords;
    Button btPlay;


    private void changeActivity(Class<? extends AppCompatActivity> activity) {
        Intent switchActivityIntent = new Intent(this, activity);
        startActivity(switchActivityIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        numPointsToWin = findViewById(R.id.numPointsToWin);
        cbSafetyWords = findViewById(R.id.cbSafetyWords);
        numSafetyWords = findViewById(R.id.numSafetyWords);
        btPlay = findViewById(R.id.btPlay);
        System.out.println(cbSafetyWords.toString());
        System.out.println(btPlay.toString());


        cbSafetyWords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                numSafetyWords.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(HostWaitingActivity.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
    }




}