package net.jandie1505.bedwars.game.game.builder;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.config.JSONLoader;
import net.jandie1505.bedwars.constants.NamespacedKeys;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.ArmorUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.UpgradableItemUpgrade;
import net.jandie1505.bedwars.game.game.shop.entries.QuickBuyMenuEntry;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.jandie1505.bedwars.game.game.team.upgrades.types.EnchantmentTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.PermanentPotionEffectTeamUpgrade;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameBuilder {
    @NotNull private final Bedwars plugin;

    public GameBuilder(@NotNull final Bedwars plugin) {
        this.plugin = plugin;
    }

    // ----- BUILD -----

    public @NotNull Game build(@NotNull final MapData selectedMap) {

        // WORLD

        World world = this.plugin.loadWorld(selectedMap.world());
        if (world == null) throw new IllegalStateException("World could not be loaded");

        // SHOP

        JSONObject shopFile = JSONLoader.loadJSONFromFile(new File(this.plugin.getDataFolder(), "shop.json"));

        Map<String, ShopEntry> shopEntries = getShopEntriesFromJSON(shopFile.optJSONObject("items", new JSONObject()));
        if (shopEntries.isEmpty()) shopEntries.putAll(DefaultConfigValues.getDefaultShopEntries(this.plugin));

        Map<String, UpgradeEntry> upgradeEntries = getUpgradeEntriesFromJSON(shopFile.optJSONObject("upgrade_entries", new JSONObject()));
        if (upgradeEntries.isEmpty()) upgradeEntries.putAll(DefaultConfigValues.getDefaultUpgradeEntries(this.plugin));

        Map<Integer, QuickBuyMenuEntry> quickBuyMenuEntries = getQuickBuyMenuEntriesFromJSON(shopFile.optJSONArray("default_quick_buy_menu", new JSONArray()));
        if (quickBuyMenuEntries.isEmpty()) quickBuyMenuEntries.putAll(DefaultConfigValues.getDefaultQuickBuyMenu());

        // CREATE GAME

        Game game = new Game(
                plugin,
                world,
                selectedMap,
                shopEntries,
                upgradeEntries,
                quickBuyMenuEntries,
                this.loadTeamUpgradesConfig()
        );

        // UPGRADES

        this.setupPlayerUpgrades(game);
        this.setupTeamUpgrades(game);

        // RETURN

        return game;
    }

    // ----- TEAM UPGRADES -----

    /**
     * Creates the team upgrades config.
     * @return TeamUpgradesConfig
     * @deprecated Will be replaced by a new team upgrade system soon
     */
    @Deprecated
    private @NotNull TeamUpgradesConfig loadTeamUpgradesConfig() {
        JSONObject shopConfig = this.plugin.getShopConfig().getConfig();
        return new TeamUpgradesConfig(
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("sharpness", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("protection", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("haste", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("generators", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("healpool", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("endgamebuff", new JSONObject())),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("noTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("alarmTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("itsATrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("miningFatigueTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("countermeasuresTrap")
        );
    }

    private TeamUpgrade getErrorUpgrade() {
        return new TeamUpgrade(-1, List.of(), List.of(), List.of());
    }

    private TeamUpgrade buildTeamUpgrade(JSONObject teamUpgrade) {

        int itemId = teamUpgrade.optInt("item", -1);

        if (itemId < 0) {
            this.plugin.getLogger().warning("Shop Config: Missing/wrong item in team upgrade");
            return this.getErrorUpgrade();
        }

        JSONArray priceListArray = teamUpgrade.optJSONArray("prices");

        if (priceListArray == null) {
            this.plugin.getLogger().warning("Shop Config: Missing/Wrong prices in team upgrade");
            return this.getErrorUpgrade();
        }

        List<Integer> prices = new ArrayList<>();
        List<Material> currencies = new ArrayList<>();

        for (int i = 0; i < priceListArray.length(); i++) {

            int price = priceListArray.optInt(i, -1);

            if (price < 0) {
                this.plugin.getLogger().warning("Shop Config: Wrong price in prices in team upgrade");
                return this.getErrorUpgrade();
            }

            prices.add(price);
            currencies.add(Material.DIAMOND);

        }

        JSONArray levelArray = teamUpgrade.optJSONArray("levels");

        List<Integer> levels = new ArrayList<>();

        if (levelArray == null) {

            for (int i = 0; i < prices.size(); i++) {
                levels.add(-1);
            }

        } else {

            for (int i = 0; i < levelArray.length(); i++) {

                int level = levelArray.optInt(i);

                if (level < 0) {
                    level = -1;
                }

                levels.add(level);

            }

        }

        return new TeamUpgrade(itemId, List.copyOf(prices), List.copyOf(currencies), List.copyOf(levels));
    }

    // ----- SHOP -----

    public static @NotNull Map<String, ShopEntry> getShopEntriesFromJSON(@NotNull JSONObject json) {
        Map<String, ShopEntry> shopEntries = new HashMap<>();

        for (String key : json.keySet()) {
            shopEntries.put(key, ShopEntry.createFromJSON(json.getJSONObject(key)));
        }

        return shopEntries;
    }

    public static @NotNull JSONObject createJSONFromShopEntries(@NotNull Map<String, ShopEntry> shopEntries) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, ShopEntry> entry : shopEntries.entrySet()) {
            json.put(entry.getKey(), ShopEntry.convertToJSON(entry.getValue()));
        }

        return json;
    }

    public static @NotNull Map<String, UpgradeEntry> getUpgradeEntriesFromJSON(@NotNull JSONObject json) {
        Map<String, UpgradeEntry> upgradeEntries = new HashMap<>();

        for (String key : json.keySet()) {
            upgradeEntries.put(key, UpgradeEntry.fromJSON(json.getJSONObject(key)));
        }

        return upgradeEntries;
    }

    public static @NotNull JSONObject createJSONFromUpgradeEntries(@NotNull Map<String, UpgradeEntry> upgradeEntries) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, UpgradeEntry> entry : upgradeEntries.entrySet()) {
            json.put(entry.getKey(), entry.getValue().toJSON());
        }

        return json;
    }

    public static @NotNull Map<Integer, QuickBuyMenuEntry> getQuickBuyMenuEntriesFromJSON(@NotNull JSONArray json) {
        Map<Integer, QuickBuyMenuEntry> quickBuyMenuEntries = new HashMap<>();

        for (Object o : json) {
            if (!(o instanceof JSONObject entryJSON)) throw new IllegalArgumentException("Invalid quick buy menu entry");
            quickBuyMenuEntries.put(entryJSON.getInt("slot"), QuickBuyMenuEntry.fromJSON(entryJSON));
        }

        return quickBuyMenuEntries;
    }

    public static @NotNull JSONArray createJSONFromQuickBuyMenuEntries(@NotNull Map<Integer, QuickBuyMenuEntry> quickBuyMenuEntries) {
        JSONArray json = new JSONArray();

        for (Map.Entry<Integer, QuickBuyMenuEntry> entry : quickBuyMenuEntries.entrySet()) {
            JSONObject entryJSON = entry.getValue().toJSON();
            entryJSON.put("slot", entry.getKey());
            json.put(entryJSON);
        }

        return json;
    }

    // ----- PLAYER UPGRADES -----

    /**
     * Converts a json to PlayerUpgrade Data.
     * @param id id
     * @param json json
     * @return PlayerUpgrade Data
     */
    public static @Nullable PlayerUpgrade.Data deserializePlayerUpgradeDataFromJSON(@NotNull String id, @NotNull JSONObject json) {

        switch (json.getString("type")) {
            case "upgradable_item" -> {
                return UpgradableItemUpgrade.Data.fromJSON(id, json);
            }
            case "armor" -> {
                return ArmorUpgrade.Data.fromJSON(id, json);
            }
            default -> {
                return null;
            }
        }

    }

    /**
     * Converts a json to list of PlayerUpgrade Data.<br/>
     * The id is the key and the value the other values.
     * @param json json
     * @return list of PlayerUpgrade Data
     */
    public static @NotNull List<PlayerUpgrade.Data> getPlayerUpgradesFromJSON(@NotNull JSONObject json) {
        List<PlayerUpgrade.Data> playerUpgrades = new ArrayList<>();

        for (String key : json.keySet()) {
            JSONObject dataJson = json.getJSONObject(key);
            PlayerUpgrade.Data data = deserializePlayerUpgradeDataFromJSON(key, dataJson);
            if (data == null) throw new IllegalArgumentException("Invalid player upgrade type for " + key);
            playerUpgrades.add(data);
        }

        return playerUpgrades;
    }

    /**
     * Sets up the player upgrades.
     * @param game game
     */
    private void setupPlayerUpgrades(@NotNull Game game) {
        JSONObject playerUpgradesFile = JSONLoader.loadJSONFromFile(new File(game.getPlugin().getDataFolder(), "player_upgrades.json"));

        if (playerUpgradesFile.isEmpty()) {
            DefaultConfigValues.getPlayerUpgrades().forEach(upgrade -> game.getPlayerUpgradeManager().registerUpgrade(upgrade.buildUpgrade(game.getPlayerUpgradeManager())));
            return;
        }

        getPlayerUpgradesFromJSON(playerUpgradesFile).forEach(upgrade -> game.getPlayerUpgradeManager().registerUpgrade(upgrade.buildUpgrade(game.getPlayerUpgradeManager())));
    }

    // ----- TEAM UPGRADES -----

    private void setupTeamUpgrades(@NotNull Game game) {

        game.getTeamUpgradeManager().registerUpgrade(new EnchantmentTeamUpgrade(game.getTeamUpgradeManager(), TeamUpgrades.ATTACK_DAMAGE, NamespacedKeys.GAME_ITEM_SHARPNESS_AFFECTED, Enchantment.SHARPNESS));
        game.getTeamUpgradeManager().registerUpgrade(new EnchantmentTeamUpgrade(game.getTeamUpgradeManager(), TeamUpgrades.PROTECTION, NamespacedKeys.GAME_ITEM_PROTECTION_AFFECTED, Enchantment.PROTECTION));
        game.getTeamUpgradeManager().registerUpgrade(new PermanentPotionEffectTeamUpgrade(game.getTeamUpgradeManager(), TeamUpgrades.HASTE, PotionEffectType.HASTE, false, false, false));

    }

}
