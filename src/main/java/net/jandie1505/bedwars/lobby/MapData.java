package net.jandie1505.bedwars.lobby;

import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.generators.GeneratorData;
import net.jandie1505.bedwars.game.team.TeamData;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;

import java.util.List;

public record MapData(
        String name,
        String world,
        int respawnCountdown,
        int maxTime,
        int spawnBlockPlaceProtection,
        int villagerBlockPlaceProtection,
        List<TeamData> teams,
        List<GeneratorData> globalGenerators,
        List<TimeActionData> timeActions,
        ImmutableLocation centerLocation,
        int mapRadius
) {

    public MapData(String name, String world, int respawnCountdown, int maxTime, int spawnBlockPlaceProtection, int villagerBlockPlaceProtection, List<TeamData> teams, List<GeneratorData> globalGenerators, List<TimeActionData> timeActions, ImmutableLocation centerLocation, int mapRadius) {
        this.name = name;
        this.world = world;
        this.respawnCountdown = respawnCountdown;
        this.maxTime = maxTime;
        this.spawnBlockPlaceProtection = spawnBlockPlaceProtection;
        this.villagerBlockPlaceProtection = villagerBlockPlaceProtection;
        this.teams = List.copyOf(teams);
        this.globalGenerators = List.copyOf(globalGenerators);
        this.timeActions = List.copyOf(timeActions);
        this.centerLocation = centerLocation.clone();
        this.mapRadius = mapRadius;
    }

}
