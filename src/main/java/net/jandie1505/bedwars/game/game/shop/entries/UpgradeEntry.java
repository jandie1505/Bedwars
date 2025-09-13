package net.jandie1505.bedwars.game.game.shop.entries;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Represents a player upgrade in the item shop.
 * @param upgradeId ID of the upgrade that can be bought.
 * @param prices Map of price entries for each level.
 * @param positions Positions where the upgrade should be displayed in the shop menu.
 */
public record UpgradeEntry(
        @NotNull String upgradeId,
        @NotNull Map<Integer, PriceEntry> prices,
        @NotNull Set<ShopGUIPosition> positions
) {

    public static @NotNull UpgradeEntry fromJSON(JSONObject json) {

        String upgradeId = json.getString("upgradeId");

        Map<Integer, PriceEntry> priceEntries = new HashMap<>();
        JSONArray pricesJSON = json.getJSONArray("prices");
        for (Object key : pricesJSON) {
            if (!(key instanceof JSONObject priceEntryJSON)) throw new IllegalArgumentException("Invalid price entry");

            int level = priceEntryJSON.getInt("level");
            PriceEntry priceEntry = PriceEntry.fromJSON(priceEntryJSON);

            priceEntries.put(level, priceEntry);
        }

        Set<ShopGUIPosition> positions = new HashSet<>();
        JSONArray guiPositions = json.getJSONArray("positions");
        for (int i = 0; i < guiPositions.length(); i++) {
            JSONObject guiPosition = guiPositions.getJSONObject(i);
            ShopGUIPosition position = ShopGUIPosition.createFromJSON(guiPosition);
            positions.add(position);
        }

        return new UpgradeEntry(upgradeId, priceEntries, positions);
    }

    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("upgradeId", this.upgradeId);

        JSONArray pricesJSON = new JSONArray();
        for (Map.Entry<Integer, PriceEntry> e : this.prices.entrySet()) {
            JSONObject priceEntryJSON = e.getValue().toJSON();
            priceEntryJSON.put("level", e.getKey());
            pricesJSON.put(priceEntryJSON);
        }
        json.put("prices", pricesJSON);

        JSONArray positions = new JSONArray();
        for (ShopGUIPosition guiPosition : this.positions) {
            positions.put(ShopGUIPosition.convertToJSON(guiPosition));
        }
        json.put("positions", positions);

        return json;
    }

    // ----- INNER CLASSES -----

    /**
     * Specifies the price for an upgrade level.
     * @param currency currency item
     * @param amount amount of that currency required
     */
    public record PriceEntry(@NotNull Material currency, int amount) {

        public static @NotNull PriceEntry fromJSON(@NotNull JSONObject json) {

            Material currency = Material.getMaterial(json.getString("currency"));
            if (currency == null) {
                throw new IllegalArgumentException("Material not found: " + json.getString("currency"));
            }

            int amount = json.getInt("amount");

            return new PriceEntry(currency, amount);
        }

        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("currency", this.currency.name());
            json.put("amount", this.amount);

            return json;
        }

    }



}
