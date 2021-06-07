package com.example.projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPrefEditor;

    private Button btSingleplayer;
    private Button btMultiplayer;
    private Button btLog;
    private TextView txtGreet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSingleplayer = findViewById(R.id.btSingleplayer);
        btMultiplayer = findViewById(R.id.btMultiplayer);
        btLog = findViewById(R.id.btLog);
        txtGreet = findViewById(R.id.txtGreet);

        sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();

        btSingleplayer.setOnClickListener(v -> changeActivity(SingleplayerActivity.class));
        btMultiplayer.setOnClickListener(v -> multiplayerPopup());
        btLog.setOnClickListener(v -> changeActivity(GamesLogActivity.class));
        txtGreet.setOnClickListener(v -> enterName(true));

        updatePlayerName();
        if (sharedPref.getString("playerName", null) == null) {
            enterName(false);
        }
    }

    private void changeActivity(Class<? extends AppCompatActivity> activity) {
        Intent switchActivityIntent = new Intent(this, activity);
        startActivity(switchActivityIntent);
    }

    private void updatePlayerName() {
        StringBuilder text = new StringBuilder();
        String name = sharedPref.getString("playerName", null);
        if (name == null) {
            text.append("Hello!");
        } else {
            text.append("Hello, ");
            text.append(name);
            text.append("!");
        }
        txtGreet.setText(text);
    }

    private void enterName(boolean cancelable) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View enterNamePopup = getLayoutInflater().inflate(R.layout.popup_enter_name, null);
        EditText name = enterNamePopup.findViewById(R.id.enterNamePopup_name);
        Button save = enterNamePopup.findViewById(R.id.enterNamePopup_save);

        dialogBuilder.setView(enterNamePopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(cancelable);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = name.getText().toString();
                if (playerName.length() < 1) return;
                sharedPrefEditor.putString("playerName", playerName);
                sharedPrefEditor.apply();
                updatePlayerName();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void multiplayerPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View multiplayerPopup = getLayoutInflater().inflate(R.layout.popup_multiplayer, null);

        dialogBuilder.setView(multiplayerPopup);
        AlertDialog dialog = dialogBuilder.create();

        Button join = multiplayerPopup.findViewById(R.id.multiplayerPopup_join);
        Button host = multiplayerPopup.findViewById(R.id.multiplayerPopup_host);

        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(HostGameActivity.class);
                dialog.dismiss();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(JoinGameActivity.class);
                dialog.dismiss();
            }
        });



        dialog.show();
    }

}