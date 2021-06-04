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

    TextView txtPlayers, txtGuessing, txtPassword, txtFails;
    GameKeyboard keyboard;

    GameManager gameManager;


    private final BroadcastReceiver broadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received message");
            byte[] mess = intent.getByteArrayExtra("TheMessage");

            GameMessage message = GameMessage.fromBytes(mess);
            if (message == null) {
                Log.d(TAG, "Got non-game message bytes: " + new String(mess));
                return;
            }
            manageMessage(message);
        }
    };

    private void manageMessage(GameMessage message) {
        switch (message.type) {
            case INIT_MANAGER:
                Log.d(TAG, "received init manager message");
                gameManager.initializeOptions(message);
                gameManager.initializeMe(getFromSharedPref("playerName"));
                gameManager.initializeYou(message.playerName);
                gameManager.isGuessing = true;
                GameMessage m = GameMessageFactory.produceInitManagerAnswerMessage(getFromSharedPref("playerName"));
                bcs.write(GameMessage.toBytes(m));
                break;
            case INIT_MANAGER_ANSWER:
                gameManager.initializeYou(message.playerName);
                gameManager.isGuessing = false;
                enterPassword();        // host is always second to guess
                break;
            case INIT_GAME:
                gameManager.message(message);
                keyboard.resetButtons();
                break;
            case NORMAL:
                gameManager.message(message);
                break;
        }
        updateView();
        if (gameManager.isGameFinished())
            gameEnded();
    }

    @SuppressLint("SetTextI18n")
    private void updateView() {
        GameInstance currentGame = gameManager.getCurrentGame();
        if (currentGame == null)
            return;

        StringBuilder s = new StringBuilder();
        s.append(gameManager.getMe().name);
        s.append(" ");
        s.append(gameManager.getMe().getScore());
        s.append(" : ");
        s.append(gameManager.getYou().getScore());
        s.append(" ");
        s.append(gameManager.getYou().name);
        txtPlayers.setText(new String(s));


        if (gameManager.isGuessing) {
            txtGuessing.setText("You're guessing!");
        } else {
            txtGuessing.setText("You're spectating!");
        }
        txtPassword.setText(currentGame.getGuessedPassword());

        s = new StringBuilder();
        s.append("Fails: ");
        s.append(gameManager.getFails());
        s.append("/");
        s.append(gameManager.getMaxFails());
        txtFails.setText(new String(s));

        keyboard.updateButtons(gameManager.getCurrentGame().getGuessedCharacters());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        txtPlayers = findViewById(R.id.txtPlayers);
        txtGuessing = findViewById(R.id.txtGuessing);
        txtPassword = findViewById(R.id.txtPassword);
        txtFails = findViewById(R.id.txtFails);

        LinearLayout[] keyButtonsLayouts = new LinearLayout[]{findViewById(R.id.loButtons1), findViewById(R.id.loButtons2), findViewById(R.id.loButtons3)};
        keyboard = new GameKeyboard(this, keyButtonsLayouts, this::btKeyPressedAction);
        keyboard.prepareKeyButtons();

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
            GameMessage m = GameMessageFactory.produceInitManagerMessage(3, 3, 6, getFromSharedPref("playerName"));
            gameManager.initializeOptions(m);
            gameManager.initializeMe(m.playerName);
            bcs.write(GameMessage.toBytes(m));
        }
    }

    // BUTTON ACTIONS

    void btKeyPressedAction(Character c) {
        Log.d(TAG, "Key pressed: " + c);
        c = Character.toLowerCase(c);

        if (! gameManager.isGuessing) return;

        GameMessage message = GameMessageFactory.produceNormalMessage(c);
        manageMessage(message);
        updateView();
        bcs.write(GameMessage.toBytes(message));
    }

    public String getFromSharedPref(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        String ans = sharedPref.getString(key, null);

        Log.d(TAG, "Extracting from shared pref: " + key + ", " + ans);
        return ans;
    }

    // POPUPS

    @SuppressLint("SetTextI18n")
    private void gameEnded() {
        Log.d(TAG, "Game ended popup");
        updateView();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(bcs.context);
        final View gameEndedPopup = getLayoutInflater().inflate(R.layout.popup_game_ended, null);

        dialogBuilder.setView(gameEndedPopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);

        TextView txtResult = gameEndedPopup.findViewById(R.id.txtResult);
        TextView txtComment = gameEndedPopup.findViewById(R.id.txtComment);
        TextView txtThePassword = gameEndedPopup.findViewById(R.id.txtThePassword);
        Button btProceed = gameEndedPopup.findViewById(R.id.btProceed);

        if (gameManager.isGuessing) {
            if (gameManager.guesserWon()) {
                txtResult.setText("You won this round!");
                txtComment.setText("You guessed correctly!");
                gameManager.getMe().increaseScore();
            } else {
                txtResult.setText("You lost this round");
                txtComment.setText("You didn't guess in time");
                gameManager.getYou().increaseScore();
            }
        } else {
            if (gameManager.guesserWon()) {
                txtResult.setText("You lost this round");
                txtComment.setText("Your opponent guessed correctly");
                gameManager.getYou().increaseScore();
            } else {
                txtResult.setText("You won this round!");
                txtComment.setText("Your opponent didn't guess in time!");
                gameManager.getMe().increaseScore();
            }
        }
        txtThePassword.setText("The password was: " + gameManager.getCurrentGame().getPassword());


        btProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (gameManager.isSessionFinished()) {
                    sessionEnded();
                    return;
                }

                gameManager.isGuessing = ! gameManager.isGuessing;
                if (! gameManager.isGuessing) {
                    enterPassword();
                }
                updateView();
            }
        });
        dialog.show();
    }

    private void enterPassword() {
        Log.d(TAG, "enter password popup");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View enterPasswordPopup = getLayoutInflater().inflate(R.layout.popup_enter_password, null);

        dialogBuilder.setView(enterPasswordPopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);

        EditText password = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_password);
        Button confirm = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = password.getText().toString();
                if (word.length() < 1) return;

                GameMessage myMessage = GameMessageFactory.produceInitGameMessage(false, word);
                GameMessage yourMessage = GameMessageFactory.produceInitGameMessage(true, word);

                manageMessage(myMessage);
                bcs.write(GameMessage.toBytes(yourMessage));

                dialog.dismiss();
                updateView();
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void sessionEnded() {
        Log.d(TAG, "Session ended popup");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View sessionEndedPopup = getLayoutInflater().inflate(R.layout.popup_session_ended, null);

        dialogBuilder.setView(sessionEndedPopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);

        TextView txtSessionResult = sessionEndedPopup.findViewById(R.id.txtSessionResult);
        TextView txtFinalScore = sessionEndedPopup.findViewById(R.id.txtFinalScore);
        Button btFine = sessionEndedPopup.findViewById(R.id.btFine);

        if (gameManager.getMe().getScore() > gameManager.getYou().getScore()) {
            txtSessionResult.setText("You won!");
        } else {
            txtSessionResult.setText("You lost");
        }
        txtFinalScore.setText(txtPlayers.getText());

        btFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what to do?
                dialog.dismiss();
                startActivity(new Intent(bcs.context, MainActivity.class));
            }
        });
        dialog.show();
    }
}