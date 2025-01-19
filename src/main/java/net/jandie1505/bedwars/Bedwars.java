package net.jandie1505.bedwars;

import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.chaossquad.mclib.dynamicevents.ListenerOwner;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.bedwars.base.GameBase;
import net.jandie1505.bedwars.base.GameInstance;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Bedwars {
    public static final String PLUGIN_NAME = "Bedwars";

    @NotNull private final BWPlugin plugin;
    @NotNull private final DataStorage config;
    @NotNull private final EventListenerManager listenerManager;
    @NotNull private final Set<GameInstance> gameInstances;
    @NotNull private final Set<UUID> bypassingPlayers;

    private boolean paused;

    private Bedwars(@NotNull BWPlugin plugin) {
        this.plugin = plugin;
        this.config = new DataStorage();
        this.listenerManager = new EventListenerManager(this.plugin);
        this.gameInstances = new HashSet<>();
        this.bypassingPlayers = new HashSet<>();

        this.paused = false;

        this.listenerManager.addSource(() -> this.gameInstances.stream()
                .filter(instance -> !instance.data().paused())
                .map(GameInstance::game)
                .filter(Objects::nonNull)
                .map(gameBase -> (ListenerOwner) gameBase)
                .toList()
        );

        this.createBukkitRunnable(id -> this.listenerManager.manageListeners()).runTaskTimer(this.plugin, 1, 200);
        this.createBukkitRunnable(this::gameTick).runTaskTimer(this.plugin, 1, 1);
        this.createBukkitRunnable(id -> this.playerVisibilityTask()).runTaskTimer(this.plugin, 1, 30*20);
    }

    // ----- TASKS -----

    private void gameTick(int id) {
        if (this.paused) return;

        for (GameBase game : this.gameInstances.stream()
                .filter(instance -> !instance.data().paused())
                .map(GameInstance::game)
                .filter(Objects::nonNull)
                .toList()
        ) {
            game.tick();
        }

    }

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

    public @NotNull Set<GameInstance> getRunningGameInstances() {
        return this.gameInstances;
    }

    public @NotNull Set<GameBase> getRunningGames() {
        return this.gameInstances.stream().map(GameInstance::game).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    // ----- GAME PLAYERS -----

    public @Nullable GameInstance getGameByPlayer(@NotNull UUID player) {

        for (GameInstance instance : this.gameInstances) {
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

    private void onDisable() {

    }

    // ----- OTHER -----

    public @NotNull DataStorage config() {
        return this.config;
    }

    public EventListenerManager listenerManager() {
        return this.listenerManager;
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

    // ----- PLUGIN -----

    public static class BWPlugin extends JavaPlugin {
        @Nullable private Bedwars plugin;

        public BWPlugin() {
            this.plugin = null;
        }

        @Override
        public void onEnable() {
            this.plugin = new Bedwars(this);
        }

        @Override
        public void onDisable() {

            try {
                if (this.plugin != null) this.plugin.onDisable();
            } catch (Exception e) {
                this.getLogger().log(Level.WARNING, "Failed to call onDisable in Bedwars", e);
            }

            this.plugin = null;
        }

    }

}
