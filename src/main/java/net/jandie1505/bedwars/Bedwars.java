package net.jandie1505.bedwars;

import de.myzelyam.api.vanish.VanishAPI;
import net.jandie1505.bedwars.commands.BedwarsCommand;
import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.items.ItemStorage;
import net.jandie1505.bedwars.lobby.Lobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private ConfigManager mapConfig;
    private ConfigManager itemConfig;
    private ConfigManager shopConfig;
    private List<UUID> bypassingPlayers;
    private GamePart game;
    private int exceptionCount;
    private List<World> managedWorlds;
    private ItemStorage itemStorage;
    private boolean nextStatus;
    private boolean paused;
    private boolean cloudSystemMode;
    private boolean svLoaded;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.json");
        this.mapConfig = new ConfigManager(this, DefaultConfigValues.getMapConfig(), true, "maps.json");
        this.itemConfig = new ConfigManager(this, DefaultConfigValues.getItemConfig(), true, "items.json");
        this.shopConfig = new ConfigManager(this, DefaultConfigValues.getShopConfig(), true, "shop.json");
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.exceptionCount = 0;
        this.managedWorlds = Collections.synchronizedList(new ArrayList<>());
        this.itemStorage = new ItemStorage(this);
        this.nextStatus = false;
        this.paused = false;
        this.cloudSystemMode = false;

        this.configManager.reloadConfig();
        this.mapConfig.reloadConfig();
        this.itemConfig.reloadConfig();
        this.shopConfig.reloadConfig();

        this.cloudSystemMode = this.configManager.getConfig().optJSONObject("cloudSystemMode", new JSONObject()).optBoolean("enable", false);

        this.itemStorage.initItems();

        try {
            Class.forName("de.myzelyam.api.vanish.VanishAPI");
            this.svLoaded = true;
            this.getLogger().info("SuperVanish/PremiumVanish integration enabled (auto-bypass when vanished)");
        } catch (ClassNotFoundException ignored) {
            this.svLoaded = false;
        }

        this.getCommand("bedwars").setExecutor(new BedwarsCommand(this));
        this.getCommand("bedwars").setTabCompleter(new BedwarsCommand(this));

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

            try {

                // Manage game

                if (this.game != null && !this.isPaused()) {

                    try {

                        if (this.game.tick()) {

                            if (this.nextStatus) {

                                this.nextStatus = false;
                                this.game = this.game.getNextStatus();
                                this.getLogger().info("Updated game part");

                            }

                        } else {

                            this.stopGame();
                            this.getLogger().warning("Game stopped because it was aborted by tick");

                        }

                    } catch (Exception e) {
                        this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                        this.game = null;
                    }

                }

                // Manage worlds

                for (World world : List.copyOf(this.managedWorlds)) {

                    if (world == null || !this.getServer().getWorlds().contains(world) || this.getServer().getWorlds().get(0) == world) {
                        this.managedWorlds.remove(world);
                        continue;
                    }

                    if (!(this.game instanceof Lobby || (this.game instanceof Game && ((Game) this.game).getWorld() == world))) {
                        this.unloadWorld(world);
                    }

                }

                // Manage player visibility when not ingame


                for (Player player : List.copyOf(this.getServer().getOnlinePlayers())) {

                    if (!(this.game instanceof Game)) {

                        for (Player otherPlayer : List.copyOf(this.getServer().getOnlinePlayers())) {

                            if (!player.canSee(otherPlayer)) {
                                player.showPlayer(this, otherPlayer);
                            }

                        }

                    }

                    if (this.game == null) {
                        if (player.getScoreboard() != this.getServer().getScoreboardManager().getMainScoreboard()) {
                            player.setScoreboard(this.getServer().getScoreboardManager().getMainScoreboard());
                        }
                    }

                    if (this.game != null && this.isPaused()) {
                        player.sendTitle("§b\u23F8", "§7§lGAME PAUSED", 0, 20, 0);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b\u23F8 GAME PAUSED"));
                    }

                }

                // Exception Count

                if (this.exceptionCount > 0) {
                    this.exceptionCount--;
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.exceptionCount++;
            }

            if (this.exceptionCount >= 3) {
                this.getServer().getPluginManager().disablePlugin(this);
            }

        }, 0, 1);

        if (this.isCloudSystemMode()) {
            this.getLogger().info("Cloud System Mode enabled (autostart game + switch to ingame + shutdown on end)");
            this.startGame();
        }
    }

    public void onDisable() {
        this.getLogger().info("Disabling " + this.getName());

        this.stopGame();

        for (World world : List.copyOf(this.managedWorlds)) {
            this.unloadWorld(world);
        }

        this.getLogger().info(this.getName() + " was successfully disabled");
    }

    public World loadWorld(String name) {

        World world = this.getServer().getWorld(name);

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("World [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ") is already loaded and was added to managed worlds");
            return world;
        }

        world = this.getServer().createWorld(new WorldCreator(name));

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("Loaded world [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ")");
        } else {
            this.getLogger().warning("Error while loading world " + name);
        }

        return world;

    }

    public boolean unloadWorld(World world) {

        if (world == null || this.getServer().getWorlds().get(0) == world || !this.managedWorlds.contains(world) || !this.getServer().getWorlds().contains(world)) {
            return false;
        }

        UUID uid = world.getUID();
        int index = this.getServer().getWorlds().indexOf(world);
        String name = world.getName();

        for (Player player : world.getPlayers()) {
            player.teleport(new Location(this.getServer().getWorlds().get(0), 0, 0, 0));
        }

        boolean success = this.getServer().unloadWorld(world, false);

        if (success) {
            this.managedWorlds.remove(world);
            this.getLogger().info("Unloaded world [" + index + "] " + uid + " (" + name + ")");
        } else {
            this.getLogger().warning("Error white unloading world [" + index + "] " + uid + " (" + name + ")");
        }

        return success;

    }

    public void reloadPlugin() {
        this.getLogger().info("Reloading plugin...");

        this.configManager.reloadConfig();
        this.mapConfig.reloadConfig();
        this.itemConfig.reloadConfig();
        this.shopConfig.reloadConfig();

        this.itemStorage.clearItems();
        this.itemStorage.initItems();

        this.getLogger().info("Plugin successfully reloaded");
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ConfigManager getMapConfig() {
        return this.mapConfig;
    }

    public ConfigManager getItemConfig() {
        return this.itemConfig;
    }

    public ConfigManager getShopConfig() {
        return this.shopConfig;
    }

    public boolean addBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.add(playerId);
    }

    public boolean removeBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.remove(playerId);
    }

    public List<UUID> getBypassingPlayers() {
        return List.copyOf(this.bypassingPlayers);
    }

    public void clearBypassingPlayers() {
        this.bypassingPlayers.clear();
    }

    public boolean isPlayerBypassing(UUID playerId) {

        if (this.getBypassingPlayers().contains(playerId)) {
            return true;
        }

        if (this.getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("supervanish-premiumvanish", false) && this.svLoaded) {

            Player player = this.getServer().getPlayer(playerId);

            if (player == null) {
                return false;
            }

            return VanishAPI.isInvisible(player);

        }

        return false;
    }

    public void stopGame() {
        this.game = null;
        this.getLogger().info("Stopped game");
    }

    public void startGame() {
        if (this.game == null) {
            this.game = new Lobby(this);
            this.getLogger().info("Started game");
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void nextStatus() {
        this.nextStatus = true;
    }

    public GamePart getGame() {
        return this.game;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public ItemStorage getItemStorage() {
        return this.itemStorage;
    }

    public boolean isCloudSystemMode() {
        return this.cloudSystemMode;
    }

    public void setCloudSystemMode(boolean cloudSystemMode) {
        this.cloudSystemMode = cloudSystemMode;
    }

    public static String getDurationFormat(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;

        if (duration.toHours() > 0) {
            formattedTime = String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        } else {
            formattedTime = String.format("%d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
        }

        return formattedTime;
    }

    public static void removeSpecificAmountOfItems(Inventory inventory, Material type, int amount) {

        if (amount <= 0) {
            return;
        }

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {

            ItemStack is = inventory.getItem(slot);

            if (is == null) {
                continue;
            }

            if (type == is.getType()) {

                int newAmount = is.getAmount() - amount;

                if (newAmount > 0) {

                    is.setAmount(newAmount);
                    break;

                } else {

                    inventory.clear(slot);
                    amount = -newAmount;

                    if (amount == 0) {
                        break;
                    }

                }
            }
        }
    }

    public static void removeItemCompletely(Inventory inventory, ItemStack item) {

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack slotItem = inventory.getItem(slot);

            if (item.isSimilar(slotItem)) {
                inventory.clear(slot);
            }

        }

    }

    public static int getBlockDistance(Location location1, Location location2) {
        int dx = Math.abs(location1.getBlockX() - location2.getBlockX());
        int dy = Math.abs(location1.getBlockY() - location2.getBlockY());
        int dz = Math.abs(location1.getBlockZ() - location2.getBlockZ());

        double distanceSquared = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(distanceSquared);

        return (int) Math.round(distance);
    }

    public static String getBlockColorString(ChatColor color) {

        switch (color) {
            case BLACK:
                return "BLACK";
            case DARK_BLUE:
                return "BLUE";
            case DARK_GREEN:
                return "GREEN";
            case DARK_AQUA:
                return "CYAN";
            case DARK_RED:
                return "RED";
            case DARK_PURPLE:
                return "PURPLE";
            case GOLD:
                return "ORANGE";
            case GRAY:
                return "LIGHT_GRAY";
            case DARK_GRAY:
                return "GRAY";
            case BLUE:
                return "LIGHT_BLUE";
            case GREEN:
                return "LIME";
            case AQUA:
                return "CYAN";
            case RED:
                return "RED";
            case LIGHT_PURPLE:
                return "MAGENTA";
            case YELLOW:
                return "YELLOW";
            case WHITE:
                return "WHITE";
            default:
                return null;
        }

    }
}
