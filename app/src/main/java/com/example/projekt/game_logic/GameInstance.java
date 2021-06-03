package com.example.projekt.game_logic;

public class GameInstance {
    private Boolean guessing; // ! napraw to
    private Password password;
    private int fails;

    public void initialize(boolean guessing, String password) {
        this.guessing = guessing;
        this.password = new Password(password);
    }

    public GameInstance() {}

    public void move(Character c) {
        password.guess(c);

        if (! password.isInPassword(c)) {
            fails += 1;
        }
    }

    public Boolean isGuessing() {
        return guessing;
    }

    public int getFails() {
        return fails;
    }

    public String getGuessedPassword() { return password.getGuessedPassword(); }
}
