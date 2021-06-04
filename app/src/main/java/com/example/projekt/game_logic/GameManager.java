package com.example.projekt.game_logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GameManager {
    public final static String TAG = "GameManager";
    GameInstance currentGame;

    private int safetyWords;
    private int pointsToWin;
    private int maxFails;
    public boolean isGuessing;

    private Player me;
    private Player you;

    public void initializeOptions(GameMessage initMessage) {
        safetyWords = initMessage.safetyWords;
        pointsToWin = initMessage.pointsToWin;
        maxFails = initMessage.maxFails;
    }

    public GameManager() {
        me = new Player("Me");
        you = new Player("You");
        safetyWords = 0;
        pointsToWin = 10000;
        maxFails = 10000;
    }

    public void message(GameMessage message) {
        switch (message.type) {
            case INIT_MANAGER:
                break;
            case INIT_GAME:
                currentGame = new GameInstance();
                currentGame.initialize(message.word);
                break;
            case NORMAL:
                currentGame.move(message.character);
        }
    }

    public boolean isGameFinished() {
        if (currentGame == null) return false;
        if (currentGame.getFails() >= maxFails) return true;
        if (currentGame.isEntirelyGuessed()) return true;
        return false;
    }

    public boolean guesserWon() {
        if (! isGameFinished()) {
            Log.d(TAG, "invalid call");
        }
        return currentGame.isEntirelyGuessed();
    }

    public void initializeMe(String name) {
        me = new Player(name);
    }

    public Player getMe() {
        return me;
    }

    public void initializeYou(String name) {
        you = new Player(name);
    }

    public Player getYou() {
        return you;
    }

    public GameInstance getCurrentGame() {
        return currentGame;
    }

    public int getSafetyWords() { return safetyWords; }
    public int getPointsToWin() { return pointsToWin; }
    public int getMaxFails() { return maxFails; }
    public int getFails() { return currentGame.getFails(); }
}
