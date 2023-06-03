package net.jandie1505.bedwars.lobby.setup;

import net.jandie1505.bedwars.game.generators.Generator;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;

public class LobbyTeamData {
    private final String name;
    private final ChatColor color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;
    private final List<LobbyGeneratorData> generators;

    public LobbyTeamData(String name, ChatColor color, List<Location> spawnpoints, List<Location> bedLocations, List<LobbyGeneratorData> generators) {
        this.name = name;
        this.color = color;
        this.spawnpoints = List.copyOf(spawnpoints);
        this.bedLocations = List.copyOf(bedLocations);
        this.generators = List.copyOf(generators);
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

    public List<LobbyGeneratorData> getGenerators() {
        return List.copyOf(this.generators);
    }

}