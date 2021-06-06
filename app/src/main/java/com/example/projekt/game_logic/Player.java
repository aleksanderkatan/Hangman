package com.example.projekt.game_logic;

import static java.lang.Math.max;

public class Player {
    public final String name;
    private int score;
    private int remainingHints;

    Player(String name, int remainingHints) {
        this.name = name;
        this.remainingHints = remainingHints;
        score = 0;
    }

    public int getScore() { return score; }
    public void increaseScore() { score++; }
    public int getRemainingHints() { return remainingHints; }
    public void decreaseRemainingHints() { remainingHints = max(remainingHints-1, 0); }
    public void setRemainingHints(int amount) { remainingHints = amount; }
}
