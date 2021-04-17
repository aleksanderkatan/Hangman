package com.example.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HostGameActivity extends AppCompatActivity {
    TextView numPointsToWin;
    CheckBox cbSafetyWords;
    TextView numSafetyWords;
    Button btPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        System.out.println("aaa");

        numPointsToWin = findViewById(R.id.numPointsToWin);
        cbSafetyWords = findViewById(R.id.cbSafetyWords);
        numSafetyWords = findViewById(R.id.numSafetyWords);
        btPlay = findViewById(R.id.btPlay);

        cbSafetyWords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                numSafetyWords.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                System.out.println("elo");
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(HostWaitingActivity.class);
                System.out.println("olo");
            }
        });
    }

    private void changeActivity(Class<? extends AppCompatActivity> activity) {
        Intent switchActivityIntent = new Intent(this, activity);
        startActivity(switchActivityIntent);
    }
}