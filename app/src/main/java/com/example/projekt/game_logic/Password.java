package com.example.projekt.game_logic;

import android.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Password {
    private final String word;
    private final Map<Character, Boolean> chars;

    public Password(String word) {
        this.word = word.toLowerCase();
        this.chars = new HashMap<>();
    }

    public boolean isInPassword(Character c) {
        for (int i = 0; i< word.length(); i++) {
            if (c.equals(word.charAt(i))) return true;
        }
        return false;
    }

    public void guess(Character c) {
        chars.put(c, isInPassword(c));
    }

    public String getGuessedPassword() {
        StringBuilder answer = new StringBuilder();

        for (int i = 0; i< word.length(); i++) {
            char c = word.charAt(i);
            if (c == ' ' || c == '-' || c == '\'' || chars.get(c) != null) {
                answer.append(c);
            } else {
                answer.append('_');
            }
        }

        return new String(answer);
    }

    public Character getHint() {
        for (int i = 0; i< word.length(); i++) {
            char c = word.charAt(i);
            if (! (c == ' ' || c == '-' || chars.get(c) != null)) {
                return c;
            }
        }
        return null;
    }

    public String getPassword() {
        return word;
    }

    public Map<Character, Boolean> getGuessedCharacters() { return chars; }
}
