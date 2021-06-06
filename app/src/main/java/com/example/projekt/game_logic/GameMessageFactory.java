package com.example.projekt.game_logic;

public class GameMessageFactory {

    public static GameMessage produceNormalMessage(Character c) {
        return new GameMessage(GameMessage.MessageType.NORMAL, null,
                c, 0, 0, 0, 0, null);
    }

    public static GameMessage produceInitManagerMessage(
            int safetyWords, int pointsToWin, int maxFails, int maxHints, String playerName) {
        return new GameMessage(GameMessage.MessageType.INIT_MANAGER, playerName,
                null, safetyWords, pointsToWin, maxFails, maxHints, null);
    }

    public static GameMessage produceInitManagerAnswerMessage(String playerName) {
        return new GameMessage(GameMessage.MessageType.INIT_MANAGER_ANSWER, playerName,
                null, 0, 0, 0, 0, null);
    }

    public static GameMessage produceInitGameMessage(String word) {
        return new GameMessage(GameMessage.MessageType.INIT_GAME, null,
                null, 0, 0, 0, 0, word);
    }

    public static GameMessage produceHintMessage() {
        return new GameMessage(GameMessage.MessageType.HINT, null,
                null, 0, 0, 0, 0, null);
    }


}
