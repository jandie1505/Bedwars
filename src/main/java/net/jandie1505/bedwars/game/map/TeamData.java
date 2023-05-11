package net.jandie1505.bedwars.game.map;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class TeamData {
    private final String name;
    private final ChatColor color;
    private final List<Location> spawnpoints;

    public TeamData(String name, ChatColor color, List<Location> spawnpoints) {
        this.name = name;
        this.color = color;
        this.spawnpoints = List.copyOf(spawnpoints);
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
}
