package com.example.projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projekt.bluetooth.BluetoothConnectionService;
import com.example.projekt.game_logic.GameInstance;
import com.example.projekt.game_logic.GameManager;
import com.example.projekt.game_logic.GameMessage;
import com.example.projekt.game_logic.GameMessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JoinedGameActivity extends AppCompatActivity {
    public final static String TAG = "JoinedGameActivity";
    BluetoothConnectionService bcs;
    boolean host;

    Button btSendMessage;
    TextView txtPlayers, txtGuessing, txtPassword, txtData;
    EditText etData;
    GameKeyboard keyboard;

    GameManager gameManager;


    private final BroadcastReceiver broadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received message");
            byte[] mess = intent.getByteArrayExtra("TheMessage");
            txtData.setText(new String(mess));

            GameMessage message = GameMessage.fromBytes(mess);
            if (message == null) {
                Log.d(TAG, "Got non-game message bytes: " + new String(mess));
                return;
            }
            if (message.type == GameMessage.MessageType.INIT_MANAGER) {
                Log.d(TAG, "received init manager message");
                gameManager.initialize(message, getFromSharedPref("playerName"));
                if (host) {
                    enterPassword();
                } else {
                    GameMessage m = GameMessageFactory.produceInitManagerMessage(3, 3, getFromSharedPref("playerName"));
                    bcs.write(GameMessage.toBytes(m));
                }
                updateView();
                return;
            }
            gameManager.message(message);
            updateView();
        }
    };

    @SuppressLint("SetTextI18n")
    private void updateView() {
        StringBuilder s = new StringBuilder();
        s.append(gameManager.getMe().name);
        s.append(" ");
        s.append(gameManager.getMe().score);
        s.append(" : ");
        s.append(gameManager.getYou().score);
        s.append(" ");
        s.append(gameManager.getYou().name);
        txtPlayers.setText(new String(s));

        GameInstance currentGame = gameManager.getCurrentGame();
        if (currentGame == null)
            return;

        if (currentGame.isGuessing()) {
            txtGuessing.setText("You're guessing!");
        } else {
            txtGuessing.setText("You're spectating!");
        }
        txtPassword.setText(currentGame.getGuessedPassword());

        keyboard.updateButtons(gameManager.getCurrentGame().getGuessedCharacters());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btSendMessage = findViewById(R.id.btSendMessage);
        txtPlayers = findViewById(R.id.txtPlayers);
        txtGuessing = findViewById(R.id.txtGuessing);
        txtPassword = findViewById(R.id.txtPassword);
        txtData = findViewById(R.id.txtData);
        etData = findViewById(R.id.etData);

        LinearLayout[] keyButtonsLayouts = new LinearLayout[]{findViewById(R.id.loButtons1), findViewById(R.id.loButtons2), findViewById(R.id.loButtons3)};
        keyboard = new GameKeyboard(this, keyButtonsLayouts, this::btKeyPressedAction);
        keyboard.prepareKeyButtons();

        btSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btSendMessageAction();
            }
        });

        host = "Host".equals(getFromSharedPref("StartedGameFrom"));

        if (host) {       //memory leak handled
            bcs = HostWaitingActivity.bcs;
        } else {
            bcs = JoinGameActivity.bcs;
        }
        bcs.context = this;

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver5, new IntentFilter("IncomingMessage"));

        gameManager = new GameManager();

        if (!host) {            // ! change that
            Runnable task = () -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bcs.write("Let's play!".getBytes());
            };
            Thread thread = new Thread(task);
            thread.start();
        } else {
            GameMessage m = GameMessageFactory.produceInitManagerMessage(3, 3, getFromSharedPref("playerName"));
            bcs.write(GameMessage.toBytes(m));
        }
    }

    void btKeyPressedAction(Character c) {
        Log.d(TAG, "Key pressed: " + c);
        c = Character.toLowerCase(c);

        if (! gameManager.getCurrentGame().isGuessing()) return;

        GameMessage message = GameMessageFactory.produceNormalMessage(c);
        gameManager.message(message);
        updateView();
        bcs.write(GameMessage.toBytes(message));
    }

    void btSendMessageAction() {
        char a = etData.getText().toString().charAt(0);
        GameMessage m = GameMessageFactory.produceNormalMessage(a);
        bcs.write(GameMessage.toBytes(m));
        gameManager.message(m);
        updateView();
    }

    public String getFromSharedPref(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        String ans = sharedPref.getString(key, null);

        Log.d(TAG, "Extracting from shared pref: " + key + ", " + ans);
        return ans;
    }

    private void enterPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View enterPasswordPopup = getLayoutInflater().inflate(R.layout.popup_enter_password, null);
        EditText password = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_password);
        Button confirm = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_confirm);

        dialogBuilder.setView(enterPasswordPopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = password.getText().toString();
                if (word.length() < 1) return;

                GameMessage myMessage = GameMessageFactory.produceInitGameMessage(false, word);
                GameMessage yourMessage = GameMessageFactory.produceInitGameMessage(true, word);

                gameManager.message(myMessage);
                bcs.write(GameMessage.toBytes(yourMessage));

                dialog.hide();
                updateView();
            }
        });

        dialog.show();
    }
}