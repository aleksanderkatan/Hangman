package com.example.projekt.game_logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameMessage implements Serializable {
    public enum MessageType {
        INIT_MANAGER, INIT_MANAGER_ANSWER, INIT_GAME, NORMAL, HINT
    }

    public final MessageType type;
    public final String playerName;
    public final Character character;
    public final int safetyWords;
    public final int pointsToWin;
    public final int maxFails;
    public final int maxHints;
    public final String word;


    public GameMessage(MessageType type, String playerName, Character character,
                       int safetyWords, int pointsToWin, int maxFails, int maxHints,
                       String word) {
        this.type = type;
        this.playerName = playerName;
        this.character = character;
        this.safetyWords = safetyWords;
        this.pointsToWin = pointsToWin;
        this.maxFails = maxFails;
        this.maxHints = maxHints;
        this.word = word;
    }

    public static byte[] toBytes(GameMessage message) {
        byte[] res = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(message);
            out.flush();
            res = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static GameMessage fromBytes(byte[] bytes) {
        Object ans = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            ans = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (GameMessage)ans;
    }
}
