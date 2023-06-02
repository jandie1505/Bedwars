package net.jandie1505.bedwars.game.map;

import org.bukkit.World;

import java.util.List;

public class MapData {
    private final List<TeamData> teams;
    private final int respawnCountdown;

    public MapData(World world, List<TeamData> teams, int respawnCountdown) {
        this.teams = List.copyOf(teams);
        this.respawnCountdown = respawnCountdown;
    }

    public List<TeamData> getTeams() {
        return teams;
    }

    public int getRespawnCountdown() {
        return this.respawnCountdown;
    }
}
