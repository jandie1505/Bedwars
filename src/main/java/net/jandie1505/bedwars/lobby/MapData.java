package net.jandie1505.bedwars.lobby;

import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.generators.GeneratorData;
import net.jandie1505.bedwars.game.team.TeamData;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
) implements ConfigurationSerializable {

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

    // SERIALIZATION

    /**
     * Serialize this MapData to a map of strings and objects.
     * @return map of strings and objects
     */
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", this.name);
        map.put("world", this.world);
        map.put("respawn_countdown", this.respawnCountdown);
        map.put("max_time", this.maxTime);
        map.put("spawn_block_place_protection", this.spawnBlockPlaceProtection);
        map.put("villager_block_place_protection", this.villagerBlockPlaceProtection);
        map.put("teams", this.teams.stream().map(TeamData::serialize).toList());
        map.put("global_generators", this.globalGenerators.stream().map(GeneratorData::serialize).toList());
        map.put("time_actions", this.timeActions.stream().map(TimeActionData::serialize).toList());
        map.put("center_location", this.centerLocation.serialize());
        map.put("map_radius", this.mapRadius);

        return map;
    }

    /**
     * Deserialize a map of strings and objects to a new MapData
     * @param map map of strings and objects
     * @return MapData
     */
    public static MapData deserialize(Map<String, Object> map) {
        try {
            String name = (String) Objects.requireNonNull(map.get("name"));
            String world = (String) Objects.requireNonNull(map.get("world"));
            int respawnCountdown = (int) Objects.requireNonNull(map.get("respawn_countdown"));
            int maxTime = (int) Objects.requireNonNull(map.get("max_time"));
            int spawnBlockPlaceProtection = (int) Objects.requireNonNull(map.get("spawn_block_place_protection"));
            int villagerBlockPlaceProtection = (int) map.get("villager_block_place_protection");
            List<TeamData> teams = ((List<Map<String, Object>>) Objects.requireNonNull(map.get("teams"))).stream().map(TeamData::deserialize).filter(Objects::nonNull).toList();
            List<GeneratorData> globalGenerators = ((List<Map<String, Object>>) Objects.requireNonNull(map.get("global_generators"))).stream().map(GeneratorData::deserialize).filter(Objects::nonNull).toList();
            List<TimeActionData> timeActions = ((List<Map<String, Object>>) Objects.requireNonNull(map.get("time_actions"))).stream().map(TimeActionData::deserialize).filter(Objects::nonNull).toList();
            ImmutableLocation location = new ImmutableLocation((Location) Objects.requireNonNull(Location.deserialize((Map<String, Object>) Objects.requireNonNull(map.get("center_location")))));
            int mapRadius = (int) map.get("map_radius");

            return new MapData(name, world, respawnCountdown, maxTime, spawnBlockPlaceProtection, villagerBlockPlaceProtection, teams, globalGenerators, timeActions, location, mapRadius);
        } catch (Exception e) {
            return null;
        }
    }

}
