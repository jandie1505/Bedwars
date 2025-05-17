package net.jandie1505.bedwars.game.generators;

import net.chaossquad.mclib.json.JSONConfigUtils;
import net.chaossquad.mclib.immutables.ImmutableLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record GeneratorData(
        ImmutableLocation location,
        ItemStack item,
        List<Double> upgradeSteps
) {

    public GeneratorData(ImmutableLocation location, ItemStack item, List<Double> upgradeSteps) {
        this.location = location;
        this.item = item.clone();
        this.upgradeSteps = List.copyOf(upgradeSteps);
    }

    @Override
    public ItemStack item() {
        return item.clone();
    }

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
