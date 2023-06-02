package net.jandie1505.bedwars.lobby.map;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class LobbyTeamData {
    private final String name;
    private final ChatColor color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;

    public LobbyTeamData(String name, ChatColor color, List<Location> spawnpoints, List<Location> bedLocations) {
        this.name = name;
        this.color = color;
        this.spawnpoints = List.copyOf(spawnpoints);
        this.bedLocations = List.copyOf(bedLocations);
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public List<Location> getSpawnpoints() {
        return List.copyOf(this.spawnpoints);
    }

    public List<Location> getBedLocations() {
        return List.copyOf(this.bedLocations);
    }

}
