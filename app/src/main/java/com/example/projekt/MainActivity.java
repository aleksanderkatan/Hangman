package com.example.projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private EditText name;
    private Button save;


    private Button btSingleplayer;
    private Button btMultiplayer;
    private Button btLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSingleplayer = findViewById(R.id.btSingleplayer);
        btMultiplayer = findViewById(R.id.btMultiplayer);

        btSingleplayer.setOnClickListener(v -> changeActivity(GameActivity.class));

        enterName();
    }

    private void changeActivity(Class<? extends AppCompatActivity> activity) {
        Intent switchActivityIntent = new Intent(this, activity);
        startActivity(switchActivityIntent);
    }


    public void enterName() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View enterNamePopup = getLayoutInflater().inflate(R.layout.popup, null);
        name = enterNamePopup.findViewById(R.id.enterNamePopup_name);
        save = enterNamePopup.findViewById(R.id.enterNamePopup_save);

        dialogBuilder.setView(enterNamePopup);
        dialog = dialogBuilder.create();
        dialog.show();

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}