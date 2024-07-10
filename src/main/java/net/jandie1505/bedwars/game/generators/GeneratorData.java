package net.jandie1505.bedwars.game.generators;

import net.chaossquad.mclib.JSONConfigUtils;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record GeneratorData(
        ImmutableLocation location,
        ItemStack item,
        List<Double> upgradeSteps
) implements ConfigurationSerializable {

    public GeneratorData(ImmutableLocation location, ItemStack item, List<Double> upgradeSteps) {
        this.location = location;
        this.item = item.clone();
        this.upgradeSteps = List.copyOf(upgradeSteps);
    }

    @Override
    public ItemStack item() {
        return item.clone();
    }

    // YAML

    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "location", this.location.serialize(),
                "material", this.item.getType().name(),
                "speed", List.of(this.upgradeSteps)
        );
    }

    @SuppressWarnings("unchecked")
    public static GeneratorData deserialize(Map<String, Object> data) {
        try {
            return new GeneratorData(
                    new ImmutableLocation(Objects.requireNonNull(Location.deserialize((Map<String, Object>) Objects.requireNonNull(data.get("location"))))),
                    new ItemStack(Objects.requireNonNull(Material.getMaterial((String) Objects.requireNonNull(data.get("material"))))),
                    Objects.requireNonNull((List<Double>) Objects.requireNonNull(data.get("speed")))
            );
        } catch (Exception e) {
            return null;
        }
    }

    // JSON

    public JSONObject serializeToJSON() {
        JSONObject json = new JSONObject();

        json.put("location", JSONConfigUtils.locationToJSONObject(this.location));
        json.put("material", this.item.getType().toString());
        json.put("speed", new JSONArray(this.upgradeSteps));

        return json;
    }

    public static GeneratorData deserializeFromJSON(JSONObject json) {

        try {
            ImmutableLocation location = new ImmutableLocation(Objects.requireNonNull(JSONConfigUtils.jsonObjectToLocation(json.getJSONObject("location"))));

            ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(json.getString("material")))));

            List<Double> upgradeSteps = new ArrayList<>();
            JSONArray upgradeStepsArray = json.getJSONArray("speed");
            for (int i = 0; i < upgradeStepsArray.length(); i++) {
                try {
                    upgradeSteps.add(upgradeStepsArray.getDouble(i));
                } catch (JSONException ignored) {}
            }

            return new GeneratorData(location, item, upgradeSteps);
        } catch (Exception e) {
            return null;
        }

    }

}
