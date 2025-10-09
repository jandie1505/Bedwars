package net.jandie1505.bedwars.config;

import net.chaossquad.mclib.storage.DSSerializer;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.game.game.builder.GameBuilder;
import net.jandie1505.datastorage.DataStorage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Utilities for configuration file setup.
 */
public final class ConfigSetup {

    private ConfigSetup() {}

    public static boolean reloadConfig(@NotNull Bedwars plugin, boolean clear, boolean mergeDefaults) {

        if (clear) plugin.config().clear();
        if (mergeDefaults) plugin.config().merge(DefaultConfigValues.getConfig());

        try {

            DataStorage loadedStorage = DSSerializer.loadConfig(new File(plugin.getDataFolder(), "config.yml"));
            if (loadedStorage != null) {
                plugin.config().merge(loadedStorage);
                plugin.getLogger().info("Config loaded successfully");
                return true;
            } else {
                DSSerializer.saveConfig(plugin.config(), new File(plugin.getDataFolder(), "config.yml"));
                plugin.getLogger().info("Config created successfully");
                return true;
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load config. Using default values.", e);
            return false;
        }
    }

    public static @NotNull DataStorage loadDataStorage(@NotNull Bedwars plugin, @NotNull String filename) {
        try {
            File file = new File(plugin.getDataFolder(), filename);
            if (!file.exists() || !file.isFile()) return new DataStorage();
            return DSSerializer.loadConfig(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load config. Using default values.", e);
            return new DataStorage();
        }
    }

    /**
     * Sets up the game config files.<br/>
     * They are normally loaded by {@link GameBuilder} on game start.
     * @param plugin the plugin
     * @throws IOException IOException
     */
    public static void setupGameConfigs(@NotNull Bedwars plugin) throws IOException {

        // LOBBY
        setupDefaultLobbyConfig(plugin.getDataFolder());

        // MAPS
        setupDefaultMapConfig(plugin);

        // SHOP

        File shopFile = new File(plugin.getDataFolder(), "shop.json");
        if (!shopFile.exists()) {
            shopFile.createNewFile();

            JSONObject shopFileContent = new JSONObject();
            shopFileContent.put("items", GameBuilder.createJSONFromShopEntries(DefaultConfigValues.getDefaultShopEntries(plugin)));
            shopFileContent.put("upgrade_entries", GameBuilder.createJSONFromUpgradeEntries(DefaultConfigValues.getDefaultUpgradeEntries(plugin)));
            shopFileContent.put("default_quick_buy_menu", GameBuilder.createJSONFromQuickBuyMenuEntries(DefaultConfigValues.getDefaultQuickBuyMenu()));
            JSONLoader.saveJSONToFile(shopFile, shopFileContent, 4);

        }

        // UPGRADES

        File playerUpgradesFile = new File(plugin.getDataFolder(), "player_upgrades.json");
        if (!playerUpgradesFile.exists()) {

            JSONObject playerUpgradesFileContent = new JSONObject();
            DefaultConfigValues.getPlayerUpgrades().forEach(upgrade -> playerUpgradesFileContent.put(upgrade.id(), upgrade.toJSON()));
            JSONLoader.saveJSONToFile(playerUpgradesFile, playerUpgradesFileContent, 4);

        }

        // TEAM UPGRADES

        File teamUpgradesFile = new File(plugin.getDataFolder(), "team_upgrades.json");
        if (!teamUpgradesFile.exists()) {
            JSONLoader.saveJSONToFile(teamUpgradesFile, DefaultConfigValues.getDefaultTeamUpgradesFile(), 4);
        }

        File teamGUIFile = new File(plugin.getDataFolder(), "team_gui.json");
        if (!teamGUIFile.exists()) {
            teamGUIFile.createNewFile();

            JSONObject teamGUIFileContent = new JSONObject();
            teamGUIFileContent.put("upgrade_entries", GameBuilder.createJSONFromUpgradeEntries(DefaultConfigValues.getDefaultTeamUpgradeEntries()));
            teamGUIFileContent.put("trap_entries", GameBuilder.createJSONFromTeamTrapEntries(DefaultConfigValues.getDefaultTeamTrapEntries()));
            JSONLoader.saveJSONToFile(teamGUIFile, teamGUIFileContent, 4);

        }

    }

    private static void setupDefaultLobbyConfig(@NotNull File dataFolder) throws IOException {

        File lobbyConfigFile = new File(dataFolder, "lobby.json");
        if (!lobbyConfigFile.exists()) {
            lobbyConfigFile.createNewFile();
            JSONLoader.saveJSONToFile(lobbyConfigFile, DefaultConfigValues.getLobbyConfig(), 4);
        }

    }

    private static void setupDefaultMapConfig(@NotNull Bedwars plugin) throws IOException {

        Path mapDir = plugin.getDataPath().toAbsolutePath().resolve("maps");
        if (Files.notExists(mapDir)) {
            Files.createDirectories(mapDir);
        }

        if (!Files.isDirectory(mapDir)) {
            plugin.getLogger().warning(mapDir + " is not a directory!");
            return;
        }

        try (Stream<Path> stream = Files.list(mapDir)) {
            if (stream.findAny().isPresent()) {
                return;
            }
        }

        JSONLoader.saveJSONToFile(new File(mapDir.toFile(), "minimalist.json"), DefaultConfigValues.getExampleMap(), 4);
    }

}
