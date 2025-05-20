package net.jandie1505.bedwars.game.game.team;

import net.chaossquad.mclib.json.JSONConfigUtils;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.game.generators.GeneratorData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
) {

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
