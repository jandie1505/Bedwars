package net.jandie1505.bedwars;

import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.chaossquad.mclib.dynamicevents.ListenerOwner;
import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.chaossquad.mclib.world.DynamicWorldLoadingSystem;
import net.jandie1505.bedwars.base.GameBase;
import net.jandie1505.bedwars.base.GameInstance;
import net.jandie1505.bedwars.commands.ConfigSubcommand;
import net.jandie1505.bedwars.commands.StopSubcommand;
import net.jandie1505.bedwars.commands.TestSubcommand;
import net.jandie1505.bedwars.commands.WorldsSubcommand;
import net.jandie1505.bedwars.constants.Permissions;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.utilities.ConfigurationUtilities;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Bedwars {
    public static final String PLUGIN_NAME = "Bedwars";

    @NotNull private final BWPlugin plugin;
    @NotNull private final DataStorage config;
    @NotNull private final EventListenerManager listenerManager;
    @NotNull private final HashMap<Integer, GameInstance> gameInstances;
    @NotNull private final Set<UUID> bypassingPlayers;
    @NotNull private final SubcommandCommand command;
    @NotNull private final DynamicWorldLoadingSystem worldManager;

    private int nextGameId;
    private boolean paused;

    Bedwars(@NotNull BWPlugin plugin) {
        this.plugin = plugin;
        this.config = new DataStorage();
        this.listenerManager = new EventListenerManager(this.plugin);
        this.gameInstances = new HashMap<>();
        this.bypassingPlayers = new HashSet<>();
        this.command = new SubcommandCommand(this.plugin, Permissions::admin);
        this.worldManager = new DynamicWorldLoadingSystem(this.plugin);

        this.nextGameId = 0;
        this.paused = false;

        // COMMAND

        PluginCommand cmd = this.plugin.getCommand("bedwars");
        if (cmd != null) {
            cmd.setExecutor(this.command);
            cmd.setTabCompleter(this.command);
        } else {
            throw new NullPointerException("Plugin command not found");
        }

        this.command.addSubcommand("stop", SubcommandEntry.of(new StopSubcommand(this)));
        this.command.addSubcommand("config", SubcommandEntry.of(new ConfigSubcommand(this)));
        this.command.addSubcommand("test", SubcommandEntry.of(new TestSubcommand(this)));
        this.command.addSubcommand("worlds", SubcommandEntry.of(new WorldsSubcommand()));

        // LISTENER

        this.listenerManager.addSource(() ->this.gameInstances.values().stream()
                .filter(instance -> !instance.data().paused())
                .map(GameInstance::game)
                .filter(Objects::nonNull)
                .map(gameBase -> (ListenerOwner) gameBase)
                .toList()
        );

        this.createBukkitRunnable(id -> this.listenerManager.manageListeners()).runTaskTimer(this.plugin, 1, 200);
        this.createBukkitRunnable(this::gameTick).runTaskTimer(this.plugin, 1, 1);
        this.createBukkitRunnable(this::worldUnloadTask).runTaskTimer(this.plugin, 1, 20);
        this.createBukkitRunnable(id -> this.playerVisibilityTask()).runTaskTimer(this.plugin, 1, 30*20);
    }

    // ----- TASKS -----

    /**
     * Game tick.
     */
    private void gameTick(int taskIk) {
        if (this.paused) return;

        for (GameBase game : this.gameInstances.values().stream()
                .filter(instance -> !instance.data().paused())
                .map(GameInstance::game)
                .filter(Objects::nonNull)
                .toList()
        ) {
            game.tick();
        }

    }

    /**
     * Unloads worlds of non-existing games.
     */
    private void worldUnloadTask(int taskId) {

        for (World world : this.worldManager.getDynamicWorlds()) {

            if (world == null || !getServer().getWorlds().contains(world) || getServer().getWorlds().getFirst() == world) {
                continue;
            }

            boolean worldUsed = false;
            for (GameBase game : this.getRunningGames()) {
                if (game.getWorld() == world) {
                    worldUsed = true;
                    break;
                }
            }

            if (worldUsed) continue;

            WorldUtils.unloadWorld(world, false);
        }

    }

    /**
     * Handles player visibility.
     */
    public void playerVisibilityTask() {

        for (Player player : List.copyOf(this.getServer().getOnlinePlayers())) {
            for (Player otherPlayer : List.copyOf(getServer().getOnlinePlayers())) {
                if (player == otherPlayer) continue;

                GameInstance playerGameInstance = this.getGameByPlayer(player);
                GameInstance otherPlayerGameInstance = this.getGameByPlayer(otherPlayer);

                if (player.canSee(otherPlayer)) {

                    // Never hide players from bypassing players
                    if (this.isPlayerBypassing(player)) continue;

                    // Do not hide players from spectators
                    if (playerGameInstance == null) continue;

                    // Hide player if player is in another game instance
                    if (playerGameInstance != otherPlayerGameInstance) {
                        player.hidePlayer(this.plugin, player);
                        continue;
                    }

                } else {

                    // Show players for bypassing players
                    if (this.isPlayerBypassing(player)) {
                        player.showPlayer(this.plugin, otherPlayer);
                        continue;
                    }

                    // Show players to spectators
                    if (playerGameInstance == null) {
                        player.showPlayer(this.plugin, otherPlayer);
                        continue;
                    }

                    // Show players when they are in the same game instance
                    if (playerGameInstance == otherPlayerGameInstance) {
                        player.showPlayer(this.plugin, otherPlayer);
                        continue;
                    }

                }
            }
        }

    }

    // ----- GAME MANAGEMENT -----

    public @NotNull Map<Integer, GameInstance> getRunningGameInstances() {
        return this.gameInstances;
    }

    public @NotNull Set<GameBase> getRunningGames() {
        return this.gameInstances.values().stream().map(GameInstance::game).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public @Nullable GameInstance getGameInstance(int id) {
        return this.gameInstances.get(id);
    }

    public @Nullable GameBase getGame(int id) {
        GameInstance gameInstance = this.getGameInstance(id);
        if (gameInstance == null) return null;
        return gameInstance.game();
    }

    public void stopAllGames() {
        this.gameInstances.clear();
        this.listenerManager.manageListeners();
        this.getLogger().info("Stopped all games");
    }

    public void stopGame(int id) {
        this.gameInstances.remove(id);
        this.listenerManager.manageListeners();
        this.getLogger().info("Stopped game " + id);
    }

    public void startNewGame() {
        GameInstance instance = new GameInstance(this, ++nextGameId);

        World world = this.worldManager.createWorldFromTemplate("minimalist");

        Game game = new Game(instance, world);
        instance.setGame(game);
        this.gameInstances.put(instance.gameId(), instance);
    }

    // ----- GAME PLAYERS -----

    public @Nullable GameInstance getGameByPlayer(@NotNull UUID player) {

        for (GameInstance instance : this.gameInstances.values()) {
            if (instance.game() == null) continue;
            if (instance.game().isPlayerIngame(player)) return instance;
        }

        return null;
    }

    public @Nullable GameInstance getGameByPlayer(@NotNull OfflinePlayer player) {
        return this.getGameByPlayer(player.getUniqueId());
    }

    // ----- PLAYER BYPASS -----

    public void addBypassingPlayer(@NotNull UUID player) {
        this.bypassingPlayers.add(player);
    }

    public void removeBypassingPlayer(@NotNull UUID player) {
        this.bypassingPlayers.remove(player);
    }

    public boolean isBypassingPlayer(@NotNull UUID player) {
        return this.bypassingPlayers.contains(player);
    }

    public boolean isPlayerBypassing(@NotNull OfflinePlayer player) {
        return this.isBypassingPlayer(player.getUniqueId());
    }

    public Set<UUID> getLocallyBypassingPlayers() {
        return Set.copyOf(this.bypassingPlayers);
    }

    // ----- ENABLE / DISABLE -----

    void onDisable() {

    }

    // ----- LOAD CONFIG -----



    // ----- OTHER -----

    public @NotNull DataStorage getConfig() {
        return this.config;
    }

    public @NotNull EventListenerManager getListenerManager() {
        return this.listenerManager;
    }

    public @NotNull DynamicWorldLoadingSystem getWorldManager() {
        return this.worldManager;
    }

    // ----- BUKKIT -----

    public Server getServer() {
        return this.plugin.getServer();
    }

    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    // ----- UTILITIES -----

    private BukkitRunnable createBukkitRunnable(final @NotNull BukkitRunnableTask task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run(this.getTaskId());
            }
        };
    }

    private interface BukkitRunnableTask {
        void run(int taskId);
    }

}
