package net.jandie1505.bedwars.game.map;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapData {
    private final World world;
    private final List<TeamData> teams;
    private final int respawnCountdown;

    public MapData(World world, List<TeamData> teams, int respawnCountdown) {
        this.world = world;
        this.teams = Collections.synchronizedList(new ArrayList<>());
        this.teams.addAll(teams);
        this.respawnCountdown = respawnCountdown;
    }

    public World getWorld() {
        return world;
    }

    public List<TeamData> getTeams() {
        return teams;
    }

    public int getRespawnCountdown() {
        return this.respawnCountdown;
    }
}
