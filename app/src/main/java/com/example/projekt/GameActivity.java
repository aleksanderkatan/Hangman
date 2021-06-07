package com.example.projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projekt.bluetooth.BluetoothConnectionService;
import com.example.projekt.database.GameEntry;
import com.example.projekt.database.ProjectDatabase;
import com.example.projekt.game_logic.GameInstance;
import com.example.projekt.game_logic.GameManager;
import com.example.projekt.game_logic.GameMessage;
import com.example.projekt.game_logic.GameMessageFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class GameActivity extends AppCompatActivity {
    public final static String TAG = "JoinedGameActivity";
    BluetoothConnectionService bcs;
    boolean host;
    long beginTimestamp;

    ConstraintLayout lGame;
    TextView txtPlayers, txtGuessing, txtPassword, txtFails, txtEstablishing,
            txtMyHints, txtYourHints, txtTargetScore;
    ImageView imHangman, imHint;
    GameKeyboard keyboard;

    GameManager gameManager;
    List<String> passwords;


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
                gameManager.initializeMe(getStringFromSharedPref("playerName"));
                gameManager.initializeYou(message.playerName);
                gameManager.isGuessing = true;
                GameMessage m = GameMessageFactory.produceInitManagerAnswerMessage(getStringFromSharedPref("playerName"));
                bcs.write(GameMessage.toBytes(m));
                beginTimestamp = System.currentTimeMillis();
                txtEstablishing.setText("Waiting for\nfirst password...");
                break;
            case INIT_MANAGER_ANSWER:
                gameManager.initializeYou(message.playerName);
                gameManager.isGuessing = false;
                beginTimestamp = System.currentTimeMillis();
                txtEstablishing.setText("Waiting for\nfirst password...");
                enterPassword();        // host is always second to guess
                break;
            case INIT_GAME:
                txtEstablishing.setVisibility(View.INVISIBLE);
                txtTargetScore.setText(String.valueOf(gameManager.getPointsToWin()));
                lGame.setVisibility(View.VISIBLE);
                gameManager.message(message);
                keyboard.resetButtons();
                break;
            case NORMAL:
            case HINT:
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

        // my favourite part of code
        switch (gameManager.getMaxFails()-gameManager.getFails()) {
            case 9:
                imHangman.setImageResource(R.drawable.hangman9);
                break;
            case 8:
                imHangman.setImageResource(R.drawable.hangman8);
                break;
            case 7:
                imHangman.setImageResource(R.drawable.hangman7);
                break;
            case 6:
                imHangman.setImageResource(R.drawable.hangman6);
                break;
            case 5:
                imHangman.setImageResource(R.drawable.hangman5);
                break;
            case 4:
                imHangman.setImageResource(R.drawable.hangman4);
                break;
            case 3:
                imHangman.setImageResource(R.drawable.hangman3);
                break;
            case 2:
                imHangman.setImageResource(R.drawable.hangman2);
                break;
            case 1:
                imHangman.setImageResource(R.drawable.hangman1);
                break;
            case 0:
                imHangman.setImageResource(R.drawable.hangman0);
                break;
            default:
                imHangman.setImageResource(R.drawable.hangman10);
                break;
        }

        keyboard.updateButtons(gameManager.getCurrentGame().getGuessedCharacters());

        if (gameManager.getMaxHints() > 0) {
            txtMyHints.setText("Hints: " + gameManager.getMe().getRemainingHints());
            txtYourHints.setText("Enemy's: " + gameManager.getYou().getRemainingHints());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // initialize passwords
        passwords = new ArrayList<>();
        Resources res = getResources();
        InputStream in = res.openRawResource(R.raw.filtered_words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            line = reader.readLine();
            while (line != null) {
                if (line.length() > 3)
                    passwords.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize view
        lGame = findViewById(R.id.lGame);
        txtPlayers = findViewById(R.id.txtPlayers);
        txtGuessing = findViewById(R.id.txtGuessing);
        txtPassword = findViewById(R.id.txtPassword);
        txtFails = findViewById(R.id.txtFails);
        imHangman = findViewById(R.id.imHangman);
        txtEstablishing = findViewById(R.id.txtEstablishingConnection);
        txtMyHints = findViewById(R.id.txtMyHints);
        txtYourHints = findViewById(R.id.txtYourHints);
        imHint = findViewById(R.id.imHint);
        txtTargetScore = findViewById(R.id.txtTargetScore);

        imHint.setOnClickListener(v -> btHintPressedAction());

        lGame.setVisibility(View.INVISIBLE);

        LinearLayout[] keyButtonsLayouts = new LinearLayout[]{findViewById(R.id.loButtons1), findViewById(R.id.loButtons2), findViewById(R.id.loButtons3)};
        keyboard = new GameKeyboard(this, keyButtonsLayouts, this::btKeyPressedAction);
        keyboard.prepareKeyButtons();

        //bcs
        host = "Host".equals(getStringFromSharedPref("StartedGameFrom"));

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
                for (int i = 0; i< 30; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bcs.isConnected()) {
                        Log.d(TAG, "connected after " + i + " tries");
                        break;
                    }
                }
                if (! bcs.isConnected()) {
                    Log.d(TAG, "Connection failed to establish");
                    finish();
                }
                bcs.write("Let's play!".getBytes());
            };
            Thread thread = new Thread(task);
            thread.start();
        } else {
            GameMessage m = GameMessageFactory.produceInitManagerMessage(
                    getIntFromSharedPref("safetyWords"),
                    getIntFromSharedPref("pointsToWin"),
                    getIntFromSharedPref("failsToHang"),
                    getIntFromSharedPref("hints"),
                    getStringFromSharedPref("playerName")
            );
            gameManager.initializeOptions(m);
            gameManager.initializeMe(m.playerName);
            bcs.write(GameMessage.toBytes(m));
        }
    }

    void saveToDatabase() {
        GameEntry entry = new GameEntry();

        entry.player1 = gameManager.getMe().name;
        entry.player2 = gameManager.getYou().name;
        entry.score1 = gameManager.getMe().getScore();
        entry.score2 = gameManager.getYou().getScore();
        entry.begin = beginTimestamp;
        entry.length = System.currentTimeMillis() - beginTimestamp;

        ProjectDatabase db = ProjectDatabase.getDatabaseInstance(this.getApplicationContext());
        db.gameEntryDao().insertGameEntry(entry);
    }

    public int getIntFromSharedPref(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        int ans = sharedPref.getInt(key, 10);

        Log.d(TAG, "Extracting from shared pref: " + key + ", " + ans);
        return ans;
    }

    public String getStringFromSharedPref(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        String ans = sharedPref.getString(key, null);

        Log.d(TAG, "Extracting from shared pref: " + key + ", " + ans);
        return ans;
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

    void btHintPressedAction() {
        Log.d(TAG, "Called for hint");
        if (gameManager.getCurrentGame() == null) return;
        if (gameManager.isGameFinished()) return;
        if (gameManager.getMe().getRemainingHints() == 0) return;
        if (! gameManager.isGuessing) return;
        Log.d(TAG, "Successfully");

        GameMessage m = GameMessageFactory.produceHintMessage();
        manageMessage(m);
        bcs.write(GameMessage.toBytes(m));
        updateView();
    }

    // POPUPS

    @SuppressLint("SetTextI18n")
    private void gameEnded() {
        Log.d(TAG, "Game ended popup");

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
            txtGuessing.setText("You're waiting!");
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
        txtThePassword.setText("The password was:\n" + gameManager.getCurrentGame().getPassword());

        updateView();

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
                } else {
                    txtGuessing.setText("You're waiting!");
                    keyboard.disableButtons();
                }
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

        LinearLayout lSafety = enterPasswordPopup.findViewById(R.id.loSafetyWords);
        EditText password = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_password);
        Button confirm = enterPasswordPopup.findViewById(R.id.enterPasswordPopup_confirm);

        if (gameManager.withSafetyWords()) {
            lSafety.setVisibility(View.VISIBLE);
            Collections.shuffle(passwords);
            List<Button> buttons = new ArrayList<>();
            buttons.add(enterPasswordPopup.findViewById(R.id.btSafety1));
            buttons.add(enterPasswordPopup.findViewById(R.id.btSafety2));
            buttons.add(enterPasswordPopup.findViewById(R.id.btSafety3));
            for (int i = 0; i< 3; i++) {
                Button button = buttons.get(i);
                button.setText(passwords.get(i));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        password.setText(button.getText());
                    }
                });
            }
        } else {
            lSafety.setVisibility(View.GONE);
        }





        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = password.getText().toString();
                if (word.length() < 1) return;

                GameMessage myMessage = GameMessageFactory.produceInitGameMessage(word);
                GameMessage yourMessage = GameMessageFactory.produceInitGameMessage(word);

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

        saveToDatabase();

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