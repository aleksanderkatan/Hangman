package com.example.projekt.game_logic;

public class Player {
    public final String name;
    private int score;

    Player(String name) {
        this.name = name;
        score = 0;
    }

    public int getScore() { return score; }
    public void increaseScore() { score++; }
}
