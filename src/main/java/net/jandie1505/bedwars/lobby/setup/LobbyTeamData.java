package net.jandie1505.bedwars.lobby.setup;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;

public class LobbyTeamData {
    private final String name;
    private final ChatColor chatColor;
    private final Color color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;
    private final List<LobbyGeneratorData> generators;
    private final List<Location> shopVillagerLocations;
    private final List<Location> upgradesVillagerLocations;

    public LobbyTeamData(String name, ChatColor chatColor, Color color, List<Location> spawnpoints, List<Location> bedLocations, List<LobbyGeneratorData> generators, List<Location> shopVillagerLocations, List<Location> upgradesVillagerLocations) {
        this.name = name;
        this.chatColor = chatColor;
        this.color = color;
        this.spawnpoints = List.copyOf(spawnpoints);
        this.bedLocations = List.copyOf(bedLocations);
        this.generators = List.copyOf(generators);
        this.shopVillagerLocations = List.copyOf(shopVillagerLocations);
        this.upgradesVillagerLocations = List.copyOf(upgradesVillagerLocations);
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
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

    public List<Location> getShopVillagerLocations() {
        return List.copyOf(this.shopVillagerLocations);
    }

    public List<Location> getUpgradesVillagerLocations() {
        return List.copyOf(this.upgradesVillagerLocations);
    }

}
