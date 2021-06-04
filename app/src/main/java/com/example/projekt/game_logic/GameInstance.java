package com.example.projekt.game_logic;

import android.util.Pair;

import java.util.Map;
import java.util.Set;

public class GameInstance {
    private Password password;
    private int fails;

    public void initialize(String password) {
        this.password = new Password(password);
    }

    public GameInstance() {}

    public void move(Character c) {
        password.guess(c);

        if (! password.isInPassword(c)) {
            fails += 1;
        }
    }

    public int getFails() {
        return fails;
    }

    public String getGuessedPassword() { return password.getGuessedPassword(); }

    public String getPassword() { return password.getPassword(); }

    public boolean isEntirelyGuessed() {
        return password.getPassword().equals(password.getGuessedPassword());
    }

    public Map<Character, Boolean> getGuessedCharacters() { return password.getGuessedCharacters(); }
}
