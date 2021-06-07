package com.example.projekt.other;

public class InputSanitizer {
    public static boolean isValidCharacter(Character c) {
        if (c <= 'Z' && c >= 'A') return true; //big letter
        if (c <= 'z' && c >= 'a') return true; //lower letter
        if (c == '-' || c == '\'' || c == ' ') return true;
        return false;
    }

    public static boolean isValidString(String s, int minLength, int maxLength) {
        if (s.length() < minLength) return false;
        if (s.length() > maxLength) return false;
        for (int i = 0; i< s.length(); i++) {
            if (! isValidCharacter(s.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isValidInt(String s, int min, int max) {
        int val;
        try {
            val = Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        if (val < min) return false;
        if (val > max) return false;
        return true;
    }

}
