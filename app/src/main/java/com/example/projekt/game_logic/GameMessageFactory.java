package com.example.projekt.game_logic;

public class GameMessageFactory {

    public static GameMessage produceNormalMessage(Character c) {
        GameMessage message = new GameMessage(GameMessage.MessageType.NORMAL, null,
                c, 0, 0, 0, false, null);
        return message;
    }

    public static GameMessage produceInitManagerMessage(
            int safetyWords, int pointsToWin, int maxFails, String playerName) {
        GameMessage message = new GameMessage(GameMessage.MessageType.INIT_MANAGER, playerName,
                '.', safetyWords, pointsToWin, maxFails, false, "");
        return message;
    }

    public static GameMessage produceInitGameMessage(boolean guessing, String word) {
        GameMessage message = new GameMessage(GameMessage.MessageType.INIT_GAME, null,
                null, 0, 0, 0, guessing, word);
        return message;
    }


}
