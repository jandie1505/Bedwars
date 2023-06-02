package net.jandie1505.bedwars.game.map;

public class MapConfig {
    private final int respawnCountdown;

    public MapConfig(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    public int getRespawnCountdown() {
        return respawnCountdown;
    }
}
