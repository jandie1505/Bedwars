package net.jandie1505.bedwars.game.game.builder;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.config.JSONLoader;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.game.player.upgrades.types.ArmorUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.UpgradableItemUpgrade;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.TeamUpgradesConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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

        Map<String, ShopEntry> shopEntries = getShopEntriesFromJSON(shopFile.getJSONObject("items"));
        if (shopEntries.isEmpty()) shopEntries.putAll(DefaultConfigValues.getDefaultShopEntries(this.plugin));

        Map<String, UpgradeEntry> upgradeEntries = getUpgradeEntriesFromJSON(shopFile.getJSONObject("upgrade_entries"));
        if (upgradeEntries.isEmpty()) upgradeEntries.putAll(DefaultConfigValues.getDefaultUpgradeEntries(this.plugin));

        // CREATE GAME

        Game game = new Game(
                plugin,
                world,
                selectedMap,
                shopEntries,
                upgradeEntries,
                this.loadTeamUpgradesConfig()
        );

        // UPGRADES

        this.setupPlayerUpgrades(game);

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

    // ----- UPGRADES -----

    /**
     * Sets up the player upgrades.
     * @param game game
     * @deprecated This needs to be replaced by loading a player-upgrades.json or yml file which contains the upgrades.
     */
    @Deprecated
    private void setupPlayerUpgrades(@NotNull Game game) {

        // TODO: This needs to be replaced by loading a player-upgrades.json or yml file which contains the upgrades.
        game.getPlayerUpgradeManager().registerUpgrade(new UpgradableItemUpgrade(game.getPlayerUpgradeManager(), "pickaxe", Component.text("Pickaxe"), Component.text("Pickaxe"), List.of(DefaultConfigValues.getUpgradePickaxe(1), DefaultConfigValues.getUpgradePickaxe(2), DefaultConfigValues.getUpgradePickaxe(3), DefaultConfigValues.getUpgradePickaxe(4), DefaultConfigValues.getUpgradePickaxe(5)), true, true));
        game.getPlayerUpgradeManager().registerUpgrade(new UpgradableItemUpgrade(game.getPlayerUpgradeManager(), "shears", Component.text("Shears"), Component.text("Shears"), List.of(new ItemStack(Material.SHEARS)), true, true));
        game.getPlayerUpgradeManager().registerUpgrade(new ArmorUpgrade(
                game.getPlayerUpgradeManager(),
                "armor",
                List.of(
                        new ArmorUpgrade.ArmorSet(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)),
                        new ArmorUpgrade.ArmorSet(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS)),
                        new ArmorUpgrade.ArmorSet(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS),  new ItemStack(Material.IRON_BOOTS)),
                        new ArmorUpgrade.ArmorSet(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS)),
                        new ArmorUpgrade.ArmorSet(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.NETHERITE_LEGGINGS), new ItemStack(Material.NETHERITE_BOOTS))
                )
        ));

    }

}
