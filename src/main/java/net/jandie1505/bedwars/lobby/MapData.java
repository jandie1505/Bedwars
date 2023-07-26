package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.lobby.setup.*;
import org.bukkit.Location;

import java.util.List;

public class MapData {
    private final String name;
    private final String world;
    private final int respawnCooldown;
    private final int maxTime;
    private final int spawnBlockPlaceProtection;
    private final int villagerBlockPlaceProtection;
    private final List<LobbyTeamData> teams;
    private final List<LobbyGeneratorData> globalGenerators;
    private final List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions;
    private final List<LobbyDestroyBedsTimeActionData> destroyBedsTimeActions;
    private final List<LobbyWorldborderChangeTimeActionData> worldBorderChangeTimeActions;
    private final List<LobbyEndgameWitherTimeActionData> endgameWitherTimeActions;
    private final Location centerLocation;
    private final int mapRadius;

    public MapData(String name, String world, int respawnCooldown, int maxTime, List<LobbyTeamData> teams, List<LobbyGeneratorData> globalGenerators, List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions, List<LobbyDestroyBedsTimeActionData> destroyBedsTimeActions, List<LobbyWorldborderChangeTimeActionData> worldBorderChangeTimeActions, List<LobbyEndgameWitherTimeActionData> endgameWitherTimeActions, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection, Location centerLocation, int mapRadius) {
        this.name = name;
        this.world = world;
        this.respawnCooldown = respawnCooldown;
        this.maxTime = maxTime;
        this.spawnBlockPlaceProtection = spawnBlockPlaceProtection;
        this.villagerBlockPlaceProtection = villagerBlockPlaceProtection;
        this.teams = teams;
        this.globalGenerators = globalGenerators;
        this.generatorUpgradeTimeActions = generatorUpgradeTimeActions;
        this.destroyBedsTimeActions = destroyBedsTimeActions;
        this.worldBorderChangeTimeActions = worldBorderChangeTimeActions;
        this.endgameWitherTimeActions = endgameWitherTimeActions;
        this.centerLocation = centerLocation;
        this.mapRadius = mapRadius;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int getRespawnCooldown() {
        return respawnCooldown;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getSpawnBlockPlaceProtection() {
        return spawnBlockPlaceProtection;
    }

    public int getVillagerBlockPlaceProtection() {
        return villagerBlockPlaceProtection;
    }

    public List<LobbyTeamData> getTeams() {
        return teams;
    }

    public List<LobbyGeneratorData> getGlobalGenerators() {
        return globalGenerators;
    }

    public List<LobbyGeneratorUpgradeTimeActionData> getGeneratorUpgradeTimeActions() {
        return generatorUpgradeTimeActions;
    }

    public List<LobbyDestroyBedsTimeActionData> getDestroyBedsTimeActions() {
        return destroyBedsTimeActions;
    }

    public List<LobbyWorldborderChangeTimeActionData> getWorldBorderChangeTimeActions() {
        return worldBorderChangeTimeActions;
    }

    public List<LobbyEndgameWitherTimeActionData> getEndgameWitherTimeActions() {
        return endgameWitherTimeActions;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public int getMapRadius() {
        return mapRadius;
    }
}
