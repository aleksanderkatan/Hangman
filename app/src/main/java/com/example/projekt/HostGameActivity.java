package com.example.projekt;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class HostGameActivity extends AppCompatActivity {
    private static final String TAG = "HostGameActivity";
    TextView numPointsToWin, numFailsToHang, numHints;
    CheckBox cbSafetyWords, cbHints;
    Button btPlay;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        numPointsToWin = findViewById(R.id.numPointsToWin);
        numFailsToHang = findViewById(R.id.numFailsToHang);
        cbSafetyWords = findViewById(R.id.cbSafetyWords);
        numHints = findViewById(R.id.numHints);
        btPlay = findViewById(R.id.btPlay);
        cbHints = findViewById(R.id.cbHints);

        numPointsToWin.setText("3");
        numFailsToHang.setText("6");
        numHints.setText("3");

        cbHints.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                numHints.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pointsToWin = Integer.parseInt(numPointsToWin.getText().toString());
                int failsToHang = Integer.parseInt(numFailsToHang.getText().toString());
                int safetyWords = 0;
                if (cbSafetyWords.isChecked()) safetyWords=1;   //boolean
                int hints = Integer.parseInt(numHints.getText().toString());
                if (! cbHints.isChecked()) hints = 0;

                Log.d(TAG, new String(
                        new StringBuilder("Options: ").append(pointsToWin)
                                .append(" ").append(failsToHang)
                                .append(" ").append(safetyWords)
                                .append(" ").append(hints)
                        ));

                putIntoSharedPref("pointsToWin", pointsToWin);
                putIntoSharedPref("failsToHang", failsToHang);
                putIntoSharedPref("safetyWords", safetyWords);
                putIntoSharedPref("hints", hints);

                Intent intent = new Intent(getBaseContext(), HostWaitingActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
    }

    private void putIntoSharedPref(String key, int val) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        sharedPrefEditor.putInt(key, val);
        sharedPrefEditor.apply();
    }




}