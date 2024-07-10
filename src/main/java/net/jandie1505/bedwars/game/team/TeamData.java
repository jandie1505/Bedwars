package net.jandie1505.bedwars.game.team;

import net.chaossquad.mclib.JSONConfigUtils;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.generators.GeneratorData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record TeamData(
        String name,
        ChatColor chatColor,
        Color color,
        ImmutableLocation baseCenter,
        int baseRadius,
        List<ImmutableLocation> spawnpoints,
        List<ImmutableLocation> bedLocations,
        List<GeneratorData> generators,
        List<ImmutableLocation> shopVillagerLocations,
        List<ImmutableLocation> upgradeVillagerLocations
) implements ConfigurationSerializable {

    public TeamData(
            String name,
            ChatColor chatColor,
            Color color,
            ImmutableLocation baseCenter,
            int baseRadius,
            List<ImmutableLocation> spawnpoints,
            List<ImmutableLocation> bedLocations,
            List<GeneratorData> generators,
            List<ImmutableLocation> shopVillagerLocations,
            List<ImmutableLocation> upgradeVillagerLocations
    ) {
        this.name = name;
        this.chatColor = chatColor;
        this.color = color;
        this.baseCenter = baseCenter;
        this.baseRadius = baseRadius;
        this.spawnpoints = List.copyOf(spawnpoints);
        this.bedLocations = List.copyOf(bedLocations);
        this.generators = List.copyOf(generators);
        this.shopVillagerLocations = List.copyOf(shopVillagerLocations);
        this.upgradeVillagerLocations = List.copyOf(upgradeVillagerLocations);
    }

    // YAML

    /**
     * Serializes this object from to a map of strings and objects.
     * @return map of strings and objects
     */
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "name", this.name,
                "chat_color", this.chatColor,
                "color", this.color,
                "base_center", this.baseCenter.serialize(),
                "base_radius", this.baseRadius,
                "spawnpoints", this.spawnpoints.stream().map(Location::serialize).toList(),
                "bed_locations", this.bedLocations.stream().map(Location::serialize).toList(),
                "generators", this.generators.stream().map(GeneratorData::serialize).toList(),
                "shop_villager_locations", this.shopVillagerLocations.stream().map(Location::serialize).toList(),
                "upgrade_villager_locations", this.upgradeVillagerLocations.stream().map(Location::serialize).toList()
        );
    }

    /**
     * Deserializes this object from a map of strings and objects.
     * @param data data
     * @return TeamData
     */
    public static TeamData deserialize(Map<String, Object> data) {
        try {
            String name = (String) Objects.requireNonNull(data.get("name"));
            ChatColor chatColor = Objects.requireNonNull(ChatColor.valueOf(Objects.requireNonNull(data.get("chat_color")).toString()));
            Color color = Objects.requireNonNull(Color.fromRGB((Integer) Objects.requireNonNull(data.get("color"))));
            ImmutableLocation baseCenter = new ImmutableLocation(Objects.requireNonNull(Location.deserialize((Map<String, Object>) Objects.requireNonNull(data.get("base_center")))));
            int baseRadius = (Integer) Objects.requireNonNull(data.get("base_radius"));
            List<ImmutableLocation> spawnpoints = Objects.requireNonNull(((List<Map<String, Object>>) Objects.requireNonNull(data.get("spawnpoints")))).stream().filter(Objects::nonNull).map(Location::deserialize).map(ImmutableLocation::new).toList();
            List<ImmutableLocation> bedLocations = Objects.requireNonNull(((List<Map<String, Object>>) Objects.requireNonNull(data.get("bed_locations")))).stream().filter(Objects::nonNull).map(Location::deserialize).map(ImmutableLocation::new).toList();
            List<GeneratorData> generators = Objects.requireNonNull(((List<Map<String, Object>>) Objects.requireNonNull(data.get("generators")))).stream().filter(Objects::nonNull).map(GeneratorData::deserialize).toList();
            List<ImmutableLocation> shopVillagerLocations = Objects.requireNonNull(((List<Map<String, Object>>) Objects.requireNonNull(data.get("shop_villager_locations")))).stream().filter(Objects::nonNull).map(Location::deserialize).map(ImmutableLocation::new).toList();
            List<ImmutableLocation> upgradeVillagerLocations = Objects.requireNonNull(((List<Map<String, Object>>) Objects.requireNonNull(data.get("upgrade_villager_locations")))).stream().filter(Objects::nonNull).map(Location::deserialize).map(ImmutableLocation::new).toList();

            return new TeamData(name, chatColor, color, baseCenter, baseRadius, spawnpoints, bedLocations, generators, shopVillagerLocations, upgradeVillagerLocations);
        } catch (Exception e) {
            return null;
        }
    }

    // JSON

    public JSONObject serializeToJSON() {
        JSONObject json = new JSONObject();

        json.put("name", this.name);
        json.put("color", this.color.asRGB());
        json.put("chatColor", this.chatColor.name());
        json.put("baseCenter", JSONConfigUtils.locationToJSONObject(this.baseCenter));
        json.put("baseRadius", this.baseRadius);
        json.put("spawnpoints", JSONConfigUtils.locationListToJSONArray(this.spawnpoints.stream().map(immutableLocation -> (Location) immutableLocation).toList()));
        json.put("bedLocations", JSONConfigUtils.locationListToJSONArray(this.bedLocations.stream().map(immutableLocation -> (Location) immutableLocation).toList()));
        json.put("generators", new JSONArray(this.generators.stream()
                        .map(GeneratorData::serializeToJSON)
                        .toList()
        ));
        json.put("shopVillagers", JSONConfigUtils.locationListToJSONArray(this.shopVillagerLocations.stream().map(immutableLocation -> (Location) immutableLocation).toList()));
        json.put("upgradeVillagers", JSONConfigUtils.locationListToJSONArray(this.upgradeVillagerLocations.stream().map(immutableLocation -> (Location) immutableLocation).toList()));

        return json;
    }

    /**
     * Creates a new instance of this object by json.
     * @param team json data
     * @return newly created LobbyTeamData
     */
    public static TeamData deserializeFromJSON(JSONObject team) {

        try {
            String name = Objects.requireNonNull(team.getString("name"));
            Color color = Objects.requireNonNull(Color.fromRGB(team.getInt("color")));
            ChatColor chatColor = Objects.requireNonNull(ChatColor.valueOf(team.getString("chatColor")));
            ImmutableLocation baseCenter = new ImmutableLocation(Objects.requireNonNull(JSONConfigUtils.jsonObjectToLocation(team.getJSONObject("baseCenter"))));
            int baseRadius = team.getInt("baseRadius");
            List<ImmutableLocation> spawnpoints = Objects.requireNonNull(JSONConfigUtils.jsonLocationArrayToLocationList(team.getJSONArray("spawnpoints")).stream().map(ImmutableLocation::new).toList());
            List<ImmutableLocation> bedLocations = Objects.requireNonNull(JSONConfigUtils.jsonLocationArrayToLocationList(team.getJSONArray("bedLocations")).stream().map(ImmutableLocation::new).toList());

            List<GeneratorData> teamGenerators = new ArrayList<>();
            for (Object o : team.getJSONArray("generators")) {
                if (!(o instanceof JSONObject json)) continue;
                GeneratorData data = GeneratorData.deserializeFromJSON(json);
                if (data == null) continue;
                teamGenerators.add(data);
            }

            List<ImmutableLocation> shopVillagers = Objects.requireNonNull(JSONConfigUtils.jsonLocationArrayToLocationList(team.getJSONArray("shopVillagers")).stream().map(ImmutableLocation::new).toList());
            List<ImmutableLocation> upgradeVillagers = Objects.requireNonNull(JSONConfigUtils.jsonLocationArrayToLocationList(team.getJSONArray("upgradeVillagers")).stream().map(ImmutableLocation::new).toList());

            return new TeamData(name, chatColor, color, baseCenter, baseRadius, spawnpoints, bedLocations, teamGenerators, shopVillagers, upgradeVillagers);
        } catch (Exception e) {
            return null;
        }

    }

}
