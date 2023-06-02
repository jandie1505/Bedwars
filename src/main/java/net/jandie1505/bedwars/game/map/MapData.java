package net.jandie1505.bedwars.game.map;

import org.bukkit.World;

import java.util.List;

public class MapData {
    private final int respawnCountdown;

    public MapData(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    public int getRespawnCountdown() {
        return this.respawnCountdown;
    }
}
