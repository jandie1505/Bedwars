package net.jandie1505.bedwars.lobby.map;

public class LobbyMapData {
    private final int respawnCountdown;

    public LobbyMapData(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    public int getRespawnCountdown() {
        return this.respawnCountdown;
    }
}
