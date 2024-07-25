package net.jandie1505.bedwars.game;

import net.chaossquad.mclib.JSONConfigUtils;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.generators.GeneratorData;
import net.jandie1505.bedwars.game.team.TeamData;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    public static MapData deserializeFromJSON(@NotNull JSONObject map) {

        // General values

        String name = map.optString("name");
        if (name == null) throw new IllegalArgumentException("Map name missing");

        String world = map.optString("world");
        if (world == null) throw new IllegalArgumentException("Map world missing in map " + name);

        int respawnCountdown = map.optInt("respawnCooldown", -1);
        if (respawnCountdown < 0) throw new IllegalArgumentException("Respawn cooldown missing in map " + name);

        int maxTime = map.optInt("maxTime", -1);
        if (maxTime < 0) throw new IllegalArgumentException("Max time missing in map " + name);

        int spawnBlockPlaceProtection = map.optInt("spawnBlockPlaceProtectionRadius", -1);
        if (spawnBlockPlaceProtection < 0) throw new IllegalArgumentException("Spawn block place protection missing in map " + name);

        int villagerBlockPlaceProtection = map.optInt("villagerBlockPlaceProtectionRadius", -1);
        if (villagerBlockPlaceProtection < 0) throw new IllegalArgumentException("Villager block protection missing in map " + name);

        JSONObject centerLocationData = map.optJSONObject("center");
        if (centerLocationData == null) throw new IllegalArgumentException("Map center missing in map " + name);
        Location centerLocation = JSONConfigUtils.jsonObjectToLocation(centerLocationData);
        if (centerLocation == null) throw new IllegalArgumentException("Map center invalid in map " + name);

        int mapRadius = map.optInt("mapRadius", -1);
        if (mapRadius < 0) throw new IllegalArgumentException("Map radius missing in map " + name);

        // Teams

        JSONArray teamsArray = map.optJSONArray("teams");
        if (teamsArray == null) throw new IllegalArgumentException("Teams array missing in map " + name);
        List<TeamData> teams = new ArrayList<>();
        for (int i = 0; i < teamsArray.length(); i++) {

            JSONObject team = teamsArray.optJSONObject(i);
            if (team == null) throw new IllegalArgumentException("Team json " + i + " invalid in map " + name);
            TeamData teamData = TeamData.deserializeFromJSON(team);
            if (teamData == null) throw new IllegalArgumentException("Team data " + i + " in map " + name + " is invalid");

            teams.add(teamData);

        }

        // Global generators

        JSONArray generatorsArray = map.optJSONArray("globalGenerators");
        if (generatorsArray == null) throw new IllegalArgumentException("Generators array missing in map " + name);
        List<GeneratorData> globalGenerators = new ArrayList<>();
        for (int i = 0; i < generatorsArray.length(); i++) {

            JSONObject generator = generatorsArray.optJSONObject(i);
            if (generator == null) throw new IllegalArgumentException("Generator json " + i + " in map " + name + " is invalid");
            GeneratorData generatorData = GeneratorData.deserializeFromJSON(generator);
            if (generatorData == null) throw new IllegalArgumentException("Generator data " + i + " in map " + name + " is invalid");

            globalGenerators.add(generatorData);

        }

        // Time Actions

        JSONArray timeActionsArray = map.optJSONArray("timeActions");
        if (timeActionsArray == null) throw new IllegalArgumentException("TimeActions array missing in map " + name);
        List<TimeActionData> timeActions = new ArrayList<>();
        for (int i = 0; i < timeActionsArray.length(); i++) {

            JSONObject timeAction = timeActionsArray.optJSONObject(i);
            if (timeAction == null) throw new IllegalArgumentException("TimeAction json " + i + " in map " + name + " is invalid");
            TimeActionData timeActionData = TimeActionData.deserializeFromJSON(timeAction);
            if (timeActionData == null) throw new IllegalArgumentException("TimeAction data " + i + " in map " + name + " is invalid");

            timeActions.add(timeActionData);

        }

        // CREATE

        return new MapData(
                name,
                world,
                respawnCountdown,
                maxTime,
                spawnBlockPlaceProtection,
                villagerBlockPlaceProtection,
                teams,
                globalGenerators,
                timeActions,
                new ImmutableLocation(centerLocation),
                mapRadius
        );

    }

}
