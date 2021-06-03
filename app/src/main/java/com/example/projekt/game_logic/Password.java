package com.example.projekt.game_logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Password {
    private final String word;
    private final Set<Character> chars;

    public Password(String word) {
        this.word = word;
        this.chars = new HashSet<>();
    }

    public boolean isInPassword(Character c) {
        for (int i = 0; i< word.length(); i++) {
            if (c.equals(word.charAt(i))) return true;
        }
        return false;
    }

    public void guess(Character c) {
        chars.add(c);
    }

    public String getGuessedPassword() {
        StringBuilder answer = new StringBuilder();

        for (int i = 0; i< word.length(); i++) {
            Character c = word.charAt(i);
            if (c == ' ' || chars.contains(c)) {
                answer.append(c);
            } else {
                answer.append('_');
            }
        }

        return new String(answer);
    }

    public String getPassword() {
        return word;
    }
}
