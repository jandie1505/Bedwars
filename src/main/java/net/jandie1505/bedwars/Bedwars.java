package net.jandie1505.bedwars;

import de.myzelyam.api.vanish.VanishAPI;
import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.chaossquad.mclib.world.DynamicWorldLoadingSystem;
import net.jandie1505.bedwars.commands.BedwarsCommand;
import net.jandie1505.bedwars.game.lobby.commands.LobbyStartSubcommand;
import net.jandie1505.bedwars.config.ConfigManager;
import net.jandie1505.bedwars.config.DefaultConfigValues;
import net.jandie1505.bedwars.game.base.GamePart;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.lobby.commands.LobbyVotemapCommand;
import net.jandie1505.bedwars.global.listeners.EventListener;
import net.jandie1505.bedwars.items.ItemStorage;
import net.jandie1505.bedwars.game.lobby.Lobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;

public class Bedwars extends JavaPlugin {
    private ConfigManager configManager;
    private ConfigManager mapConfig;
    private ConfigManager itemConfig;
    private ConfigManager shopConfig;
    private Set<UUID> bypassingPlayers;
    private EventListenerManager listenerManager;
    private GamePart game;
    private DynamicWorldLoadingSystem dynamicWorldLoadingSystem;
    private ItemStorage itemStorage;
    private net.jandie1505.bedwars.global.listeners.EventListener eventListener;
    private boolean nextStatus;
    private boolean paused;
    private boolean cloudSystemMode;
    private boolean svLoaded;

    // ----- ENABLE/DISABLE -----

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.json");
        this.mapConfig = new ConfigManager(this, DefaultConfigValues.getMapConfig(), true, "maps.json");
        this.itemConfig = new ConfigManager(this, DefaultConfigValues.getItemConfig(), true, "items.json");
        this.shopConfig = new ConfigManager(this, DefaultConfigValues.getShopConfig(), true, "shop-old.json");
        this.bypassingPlayers = Collections.synchronizedSet(new HashSet<>());
        this.listenerManager = new EventListenerManager(this);
        this.dynamicWorldLoadingSystem = new DynamicWorldLoadingSystem(this);
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

        // Commands

        BedwarsCommand command = new BedwarsCommand(this);
        this.getCommand("bedwars").setExecutor(command);
        this.getCommand("bedwars").setTabCompleter(command);

        PluginCommand startCommand = this.getCommand("start");
        if (startCommand != null) {
            LobbyStartSubcommand cmd = new LobbyStartSubcommand(this);
            startCommand.setExecutor(cmd);
            startCommand.setTabCompleter(cmd);
        }

        PluginCommand votemapCommand = this.getCommand("votemap");
        if (votemapCommand != null) {
            LobbyVotemapCommand cmd = new LobbyVotemapCommand(this);
            votemapCommand.setExecutor(cmd);
            votemapCommand.setTabCompleter(cmd);
        }

        // Listeners

        this.eventListener = new EventListener(this);
        this.getServer().getPluginManager().registerEvents(this.eventListener, this);

        this.listenerManager.addExceptedListener(this.eventListener);
        this.listenerManager.addSource(() -> {
            if (this.game != null) {
                return this.game;
            } else {
                return null;
            }
        });

        // Cleanup listeners task

        new BukkitRunnable() {

            @Override
            public void run() {
                Bedwars.this.listenerManager.manageListeners();
            }

        }.runTaskTimer(this, 1, 200);

        // Game Task

        new BukkitRunnable() {

            @Override
            public void run() {

                if (Bedwars.this.game != null && !Bedwars.this.isPaused()) {

                    try {

                        if (Bedwars.this.game.tick()) {

                            if (Bedwars.this.nextStatus) {

                                Bedwars.this.nextStatus = false;
                                Bedwars.this.game = Bedwars.this.game.getNextStatus();
                                Bedwars.this.getLogger().info("Updated game part");

                            }

                        } else {

                            Bedwars.this.stopGame();
                            Bedwars.this.getLogger().warning("Game stopped because it was aborted by tick");

                        }

                    } catch (Exception e) {
                        Bedwars.this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                        Bedwars.this.game = null;
                    }

                }

            }

        }.runTaskTimer(this, 1, 1);

        // Manage worlds task

        new BukkitRunnable() {

            @Override
            public void run() {

                for (World world : Bedwars.this.dynamicWorldLoadingSystem.getDynamicWorlds()) {

                    if (world == null || !getServer().getWorlds().contains(world) || getServer().getWorlds().getFirst() == world) {
                        continue;
                    }

                    if (Bedwars.this.getGame() instanceof Lobby) continue;
                    if (Bedwars.this.getGame() instanceof Game g && g.getWorld() == world) continue;

                    Bedwars.this.unloadWorld(world);
                }

            }

        }.runTaskTimer(this, 1, 20);

        // No game running task

        new BukkitRunnable() {

            @Override
            public void run() {

                for (Player player : List.copyOf(Bedwars.this.getServer().getOnlinePlayers())) {

                    if (!(Bedwars.this.game instanceof Game)) {

                        for (Player otherPlayer : List.copyOf(Bedwars.this.getServer().getOnlinePlayers())) {

                            if (!player.canSee(otherPlayer)) {
                                player.showPlayer(Bedwars.this, otherPlayer);
                            }

                        }

                    }

                    if (Bedwars.this.game == null) {
                        if (player.getScoreboard() != Bedwars.this.getServer().getScoreboardManager().getMainScoreboard()) {
                            player.setScoreboard(Bedwars.this.getServer().getScoreboardManager().getMainScoreboard());
                        }
                    }

                    if (Bedwars.this.game != null && Bedwars.this.isPaused()) {
                        player.sendTitle("§b\u23F8", "§7§lGAME PAUSED", 0, 20, 0);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b\u23F8 GAME PAUSED"));
                    }

                }

            }

        }.runTaskTimer(this, 1, 20);

        // Cloudsystem Mode

        if (this.isCloudSystemMode()) {
            this.getLogger().info("Cloud System Mode enabled (autostart game + switch to ingame + shutdown on end)");
            this.startGame();
        }
    }

    public void onDisable() {
        this.getLogger().info("Disabling " + this.getName());

        this.stopGame();

        this.dynamicWorldLoadingSystem.remove();

        this.getLogger().info(this.getName() + " was successfully disabled");
    }

    /**
     * Get a list of all listeners (not registered listeners).
     * This can be used to clear listeners of the game.
     * @return List of listeners
     */
    public List<Listener> getListeners() {
        List<RegisteredListener> registeredListenerList = List.copyOf(HandlerList.getRegisteredListeners(this));
        List<Listener> listenerList = new ArrayList<>();

        for (RegisteredListener registeredListener : registeredListenerList) {

            if (!listenerList.contains(registeredListener.getListener())) {
                listenerList.add(registeredListener.getListener());
            }

        }

        return List.copyOf(listenerList);
    }

    // ----- LISTENERS -----

    /**
     * Register a game listener as event listener.
     * @param listener game listener
     * @deprecated Use {@link GamePart#registerListener(net.chaossquad.mclib.executable.ManagedListener)}
     */
    @Deprecated(forRemoval = true)
    public void registerListener(ManagedListener listener) {
        listener.getGame().registerListener(listener);
    }

    // ----- WORLD MANAGEMENT -----

    /**
     * Creates and loads a copy of the specified world.
     * @param name world name
     * @return created and loaded world
     */
    public World loadWorld(String name) {
        return this.dynamicWorldLoadingSystem.createWorldFromTemplate(name);
    }

    /**
     * Unloads (and deletes) a dynamic world.
     * @param world world
     * @return success
     */
    public boolean unloadWorld(World world) {

        // Prevent unloading null worlds, the default world, an unmanaged world or an not existing world

        if (world == null || this.getServer().getWorlds().get(0) == world || !this.dynamicWorldLoadingSystem.getDynamicWorlds().contains(world) || !this.getServer().getWorlds().contains(world)) {
            return false;
        }

        // Unload world

        return WorldUtils.unloadWorld(world, false);
    }

    /**
     * Returns a list of all managed worlds.
     * @return list of managed worlds
     */
    public List<World> getManagedWorlds() {
        return this.dynamicWorldLoadingSystem.getDynamicWorlds();
    }

    // ----- CONFIGS -----

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

    // ----- BYPASSING PLAYERS -----

    public boolean addBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.add(playerId);
    }

    public boolean removeBypassingPlayer(UUID playerId) {
        return this.bypassingPlayers.remove(playerId);
    }

    public Set<UUID> getBypassingPlayers() {
        return Set.copyOf(this.bypassingPlayers);
    }

    public void clearBypassingPlayers() {
        this.bypassingPlayers.clear();
    }

    public boolean isPlayerBypassing(@Nullable UUID playerId) {
        if (playerId == null) return false;

        if (this.bypassingPlayers.contains(playerId)) {
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

    public boolean isPlayerBypassing(@Nullable OfflinePlayer player) {
        if (player == null) return false;
        return this.isPlayerBypassing(player.getUniqueId());
    }

    // ----- OTHER -----

    public EventListenerManager getListenerManager() {
        return this.listenerManager;
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

    // ----- UTILITIES -----

    public void givePointsToPlayer(Player player, int amount, String message) {

        if (this.configManager.getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("playerpoints", false)) {

            try {
                Class.forName("org.black_ixx.playerpoints.PlayerPoints");
                Class.forName("org.black_ixx.playerpoints.PlayerPointsAPI");

                PlayerPointsAPI pointsAPI = PlayerPoints.getInstance().getAPI();

                if (amount <= 0) {
                    return;
                }

                if (amount > this.configManager.getConfig().optJSONObject("rewards", new JSONObject()).optInt("maxRewardsAmount", 5000)) {
                    amount = this.configManager.getConfig().optJSONObject("rewards", new JSONObject()).optInt("maxRewardsAmount", 5000);
                }

                pointsAPI.give(player.getUniqueId(), amount);

                if (message != null) {
                    player.sendMessage(message.replace("{points}", String.valueOf(amount)));
                }

            } catch (ClassNotFoundException e) {

            }

        }

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

    /**
     * Returns the chunk coordinates of the specified world coordinates.
     * @param locationX world X
     * @param locationZ world Z
     * @return int[]{chunkX,chunkY}
     */
    public static int[] getChunkCoordinates(int locationX, int locationZ) {
        return new int[]{locationX >> 4, locationZ >> 4};
    }

    /**
     * Returns if the chunk at the specified location is loaded without loading it.
     * @param location location of the chunk
     * @return result
     */
    public static boolean isChunkLoaded(@NotNull Location location) {
        int[] chunkCoordinates = getChunkCoordinates(location.getBlockX(), location.getBlockZ());
        return Objects.requireNonNull(location.getWorld()).isChunkLoaded(chunkCoordinates[0], chunkCoordinates[1]);
    }

}
