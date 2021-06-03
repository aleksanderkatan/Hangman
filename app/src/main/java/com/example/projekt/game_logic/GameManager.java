package com.example.projekt.game_logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GameManager {
    public final static String TAG = "GameManager";
    GameInstance currentGame;

    int safetyWords;
    int pointsToWin;
    int maxFails;

    private Player me;
    private Player you;

    public void initialize(GameMessage initMessage, String me) {
        this.me = new Player(me);
        this.you = new Player(initMessage.playerName);
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
                currentGame.initialize(message.guessing, message.word);
                break;
            case NORMAL:
                currentGame.move(message.character);
        }
    }

    public Player getMe() {
        return me;
    }

    public Player getYou() {
        return you;
    }

    public GameInstance getCurrentGame() {
        return currentGame;
    }

    public int getSafetyWords() { return safetyWords; }
    public int getPointsToWin() { return pointsToWin; }
    public int getFails() { return currentGame.getFails(); }
    public int getMaxFails() { return maxFails; }
}
