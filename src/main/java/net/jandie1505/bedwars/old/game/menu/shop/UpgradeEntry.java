package net.jandie1505.bedwars.old.game.menu.shop;

import net.chaossquad.mclib.JSONConfigUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public record UpgradeEntry(
        @NotNull List<UpgradeStep> upgradeSteps,
        @NotNull List<ShopGUIPosition> guiPositions,
        @NotNull ItemStack maxLevelItem
) {

    public UpgradeEntry(@NotNull List<UpgradeStep> upgradeSteps, @NotNull List<ShopGUIPosition> guiPositions, @NotNull ItemStack maxLevelItem) {
        this.upgradeSteps = List.copyOf(upgradeSteps);
        this.guiPositions = List.copyOf(guiPositions);
        this.maxLevelItem = maxLevelItem.clone();
    }

    @Override
    @NotNull
    public ItemStack maxLevelItem() {
        return maxLevelItem.clone();
    }

    // STATIC

    public static JSONObject serializeToJSON(@NotNull UpgradeEntry entry) {
        JSONObject data = new JSONObject();

        data.put("upgrade_steps", UpgradeStep.serializeToJSON(entry.upgradeSteps()));
        data.put("positions", ShopGUIPosition.convertToJSON(entry.guiPositions()));
        data.put("max_level_item", JSONConfigUtils.serializeItem(entry.maxLevelItem()));

        return data;
    }

    public static UpgradeEntry deserializeFromJSON(@NotNull JSONObject data) {
        return new UpgradeEntry(
                UpgradeStep.deserializeFromJSON(data.getJSONArray("upgrade_steps")),
                ShopGUIPosition.createFromJSON(data.getJSONArray("positions")),
                JSONConfigUtils.deserializeItem(data.getJSONObject("max_level_item"))
        );
    }

    // INNER CLASS

    public record UpgradeStep(
            @NotNull ItemStack displayItem,
            @NotNull Material currency,
            int price
    ) {

        public UpgradeStep(@NotNull ItemStack displayItem, @NotNull Material currency, int price) {
            this.displayItem = displayItem.clone();
            this.currency = currency;
            this.price = price;
        }

        @NotNull
        public ItemStack displayItem() {
            return displayItem.clone();
        }

        public static JSONObject serializeToJSON(UpgradeStep upgradeStep) {
            JSONObject data = new JSONObject();

            data.put("display_item", JSONConfigUtils.serializeItem(upgradeStep.displayItem()));
            data.put("currency", upgradeStep.currency().name());
            data.put("price", upgradeStep.price());

            return data;
        }

        public static JSONArray serializeToJSON(List<UpgradeStep> upgradeSteps) {
            JSONArray steps = new JSONArray();

            for (UpgradeStep step : upgradeSteps) {
                steps.put(serializeToJSON(step));
            }

            return steps;
        }

        public static UpgradeStep deserializeFromJSON(JSONObject data) {
            Material material = Material.getMaterial(data.getString("currency"));
            if (material == null) return null;

            return new UpgradeStep(
                    JSONConfigUtils.deserializeItem(data.getJSONObject("display_item")),
                    material,
                    data.optInt("price", 1)
            );
        }

        public static List<UpgradeStep> deserializeFromJSON(JSONArray data) {
            List<UpgradeStep> steps = new ArrayList<>();

            for (int i = 0; i < data.length(); i++) {
                steps.add(deserializeFromJSON(data.getJSONObject(i)));
            }

            return steps;
        }

    }

}
