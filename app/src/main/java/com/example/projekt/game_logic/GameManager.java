package com.example.projekt.game_logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GameManager {
    public final static String TAG = "GameManager";
    GameInstance currentGame;

    private boolean safetyWords;
    private int pointsToWin;
    private int maxFails;
    private int maxHints;
    public boolean isGuessing;

    private Player me;
    private Player you;

    public void initializeOptions(GameMessage initMessage) {
        if (initMessage.safetyWords == 0) {
            safetyWords = false;
        } else {
            safetyWords = true;
        }
        pointsToWin = initMessage.pointsToWin;
        maxFails = initMessage.maxFails;
        maxHints = initMessage.maxHints;
        me.setRemainingHints(maxHints);
        you.setRemainingHints(maxHints);
    }

    public GameManager() {
        me = new Player("Me", maxHints);
        you = new Player("You", maxHints);
        safetyWords = false;
        pointsToWin = 10000;
        maxFails = 10000;
        maxHints = 0;
    }

    public void message(GameMessage message) {
        switch (message.type) {
            case INIT_MANAGER:
            case INIT_MANAGER_ANSWER:
                break;
            case INIT_GAME:
                currentGame = new GameInstance();
                currentGame.initialize(message.word);
                break;
            case NORMAL:
                currentGame.move(message.character);
                break;
            case HINT:
                if (isGuessing) me.decreaseRemainingHints();
                else you.decreaseRemainingHints();
                currentGame.hint();
                break;
        }
    }

    public boolean isGameFinished() {
        if (currentGame == null) return false;
        if (currentGame.getFails() >= maxFails) return true;
        return currentGame.isEntirelyGuessed();
    }

    public boolean guesserWon() {
        if (! isGameFinished()) {
            Log.d(TAG, "invalid call");
        }
        return currentGame.isEntirelyGuessed();
    }

    public void initializeMe(String name) {
        me = new Player(name, maxHints);
    }

    public Player getMe() {
        return me;
    }

    public void initializeYou(String name) {
        you = new Player(name, maxHints);
    }

    public Player getYou() {
        return you;
    }

    public GameInstance getCurrentGame() {
        return currentGame;
    }

    public boolean isSessionFinished() {
        if (me.getScore() >= pointsToWin) return true;
        return you.getScore() >= pointsToWin;
    }

    public boolean withSafetyWords() { return safetyWords; }
    public int getPointsToWin() { return pointsToWin; }
    public int getMaxFails() { return maxFails; }
    public int getFails() { return currentGame.getFails(); }
    public int getMaxHints() { return maxHints; }
}
