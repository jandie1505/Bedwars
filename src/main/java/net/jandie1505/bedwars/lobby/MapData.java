package net.jandie1505.bedwars.lobby;

import net.jandie1505.bedwars.lobby.setup.LobbyDestroyBedsTimeActionData;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorData;
import net.jandie1505.bedwars.lobby.setup.LobbyGeneratorUpgradeTimeActionData;
import net.jandie1505.bedwars.lobby.setup.LobbyTeamData;

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

    public MapData(String name, String world, int respawnCooldown, int maxTime, List<LobbyTeamData> teams, List<LobbyGeneratorData> globalGenerators, List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions, List<LobbyDestroyBedsTimeActionData> destroyBedsTimeActions, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection) {
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
}
