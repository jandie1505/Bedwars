package net.jandie1505.bedwars.game.game.builder;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameBuilder {

    public GameBuilder(@NotNull Bedwars plugin) {}

    public static Map<String, ShopEntry> buildShopEntries(@NotNull JSONObject json) {
        Map<String, ShopEntry> shopEntries = new HashMap<>();

        for (String id : json.keySet()) {
            JSONObject data = json.getJSONObject(id);
            shopEntries.put(id, ShopEntry.createFromJSON(data));
        }

        return shopEntries;
    }

    public static Map<String, UpgradeEntry> buildUpgradeEntries(@NotNull JSONObject json) {
        Map<String, UpgradeEntry> upgradeEntries = new HashMap<>();

        for (String id : json.keySet()) {
            JSONObject data = json.getJSONObject(id);
            upgradeEntries.put(id, UpgradeEntry.fromJSON(data));
        }

        return upgradeEntries;
    }

}
