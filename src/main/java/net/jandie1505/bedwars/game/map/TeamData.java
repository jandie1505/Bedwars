package net.jandie1505.bedwars.game.map;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class TeamData {
    private final String name;
    private final ChatColor color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;

    public TeamData(String name, ChatColor color, List<Location> spawnpoints, List<Location> bedLocations) {
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

    public Location getRandomSpawnpoint() {

        if (this.spawnpoints.isEmpty()) {
            return null;
        }

        return this.spawnpoints.get(new Random().nextInt(this.spawnpoints.size()));
    }

    public List<Location> getBedLocations() {
        return List.copyOf(this.bedLocations);
    }
}
