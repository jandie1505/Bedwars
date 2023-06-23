package net.jandie1505.bedwars.lobby.setup;

public class LobbyWorldborderChangeTimeActionData {
    private final int time;
    private final int radius;
    private final String chatMessage;
    private final String scoreboardText;

    public LobbyWorldborderChangeTimeActionData(int time, int radius, String chatMessage, String scoreboardText) {
        this.time = time;
        this.radius = radius;
        this.chatMessage = chatMessage;
        this.scoreboardText = scoreboardText;
    }

    public int getTime() {
        return time;
    }

    public int getRadius() {
        return radius;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getScoreboardText() {
        return scoreboardText;
    }
}
