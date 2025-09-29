package net.jandie1505.bedwars.game.game.builder;

import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.config.JSONLoader;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.MapData;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.ArmorUpgrade;
import net.jandie1505.bedwars.game.game.player.upgrades.types.UpgradableItemUpgrade;
import net.jandie1505.bedwars.game.game.shop.entries.QuickBuyMenuEntry;
import net.jandie1505.bedwars.game.game.shop.entries.ShopEntry;
import net.jandie1505.bedwars.game.game.shop.entries.UpgradeEntry;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.traps.TeamTrap;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.EnchantmentTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.HealPoolTeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.types.PermanentPotionEffectTeamUpgrade;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

        // TEAM

        JSONObject teamGUIFile = JSONLoader.loadJSONFromFile(new File(this.plugin.getDataFolder(), "team_gui.json"));

        Map<String, UpgradeEntry> teamUpgradeEntries = getUpgradeEntriesFromJSON(teamGUIFile.optJSONObject("upgrade_entries", new JSONObject()));
        if (teamUpgradeEntries.isEmpty()) teamUpgradeEntries.putAll(DefaultConfigValues.getDefaultTeamUpgradeEntries());

        // CREATE GAME

        Game game = new Game(
                plugin,
                world,
                selectedMap,
                shopEntries,
                upgradeEntries,
                quickBuyMenuEntries,
                teamUpgradeEntries
        );

        // UPGRADES

        this.setupPlayerUpgrades(game);
        this.setupTeamUpgrades(game);
        this.setupTeamTraps(game);

        // RETURN

        return game;
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

    /**
     * Converts a json to TeamUpgrade Data.
     * @param id id
     * @param json json
     * @return PlayerUpgrade Data
     */
    public static @Nullable TeamUpgrade.Data deserializeTeamUpgradeDataFromJSON(@NotNull String id, @NotNull JSONObject json) {

        switch (json.getString("type")) {
            case EnchantmentTeamUpgrade.TYPE -> {
                return EnchantmentTeamUpgrade.Data.fromJSON(id, json);
            }
            case HealPoolTeamUpgrade.TYPE -> {
                return HealPoolTeamUpgrade.Data.fromJSON(id, json);
            }
            case PermanentPotionEffectTeamUpgrade.TYPE -> {
                return PermanentPotionEffectTeamUpgrade.Data.fromJSON(id, json);
            }
            default -> {
                return null;
            }
        }

    }

    /**
     * Converts a json to list of TeamUpgrade Data.<br/>
     * The id is the key and the value the other values.
     * @param json json
     * @return list of TeamUpgrade Data
     */
    public static @NotNull List<TeamUpgrade.Data> getTeamUpgradesFromJSON(@NotNull JSONObject json) {
        List<TeamUpgrade.Data> teamUpgrades = new ArrayList<>();

        for (String key : json.keySet()) {
            JSONObject dataJson = json.getJSONObject(key);
            TeamUpgrade.Data data = deserializeTeamUpgradeDataFromJSON(key, dataJson);
            if (data == null) throw new IllegalArgumentException("Invalid team upgrade type for " + key);
            teamUpgrades.add(data);
        }

        return teamUpgrades;
    }

    /**
     * Sets up the team upgrades.
     * @param game game
     */
    private void setupTeamUpgrades(@NotNull Game game) {
        JSONObject teamUpgradesFile = JSONLoader.loadJSONFromFile(new File(game.getPlugin().getDataFolder(), "team_upgrades.json"));

        if (teamUpgradesFile.isEmpty()) {
            DefaultConfigValues.getTeamUpgrades().forEach(upgrade -> game.getTeamUpgradeManager().registerUpgrade(upgrade.buildUpgrade(game.getTeamUpgradeManager())));
            return;
        }

        getTeamUpgradesFromJSON(teamUpgradesFile.getJSONObject("team_upgrades")).forEach(upgrade -> game.getTeamUpgradeManager().registerUpgrade(upgrade.buildUpgrade(game.getTeamUpgradeManager())));
    }

    /**
     * Sets up team traps.
     * @param game game
     */
    private void setupTeamTraps(@NotNull Game game) {

        game.getTeamTrapManager().registerTrap(new TeamTrap(game.getTeamTrapManager(), "test") {
            @Override
            public void onTrigger(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData) {
                Bukkit.broadcast(Component.text("Trap " + this.getId() + " has been triggered by " + player.getName()));
            }
        });

    }

}
