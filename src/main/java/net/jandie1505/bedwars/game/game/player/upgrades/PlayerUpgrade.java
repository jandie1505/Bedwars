package net.jandie1505.bedwars.game.game.player.upgrades;

import net.chaossquad.mclib.misc.Removable;
import net.chaossquad.mclib.scheduler.TaskScheduler;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;

/**
 * The base class for a player upgrade like armor, pickaxe, etc...
 */
public abstract class PlayerUpgrade implements Removable {
    @NotNull private final PlayerUpgradeManager manager;
    @NotNull private final String id;
    @NotNull private final TaskScheduler scheduler;
    @NotNull private final Map<UUID, Integer> playerCache;

    /**
     * Creates a new player upgrade.
     * @param manager manager
     * @param id id
     */
    public PlayerUpgrade(@NotNull PlayerUpgradeManager manager, @NotNull String id) {
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        this.manager = manager;
        this.id = id;
        this.scheduler = new TaskScheduler(this.manager.getGame().getPlugin().getLogger());
        this.playerCache = new HashMap<>();

        this.scheduler.scheduleRepeatingTask(this::onApplyAndRemoveTask, 1, 20, "apply_and_remove_task");
    }

    // ----- UTILITIES -----

    /**
     * Called when the upgrade is registered.
     */
    @ApiStatus.OverrideOnly
    protected void onRegister() {}

    /**
     * Called when the upgrade is unregistered/removed.
     */
    @ApiStatus.OverrideOnly
    protected void onUnregister() {}

    // ----- APPLY/REMOVE METHODS -----

    /**
     * This method is called when the upgrade (level) is applied to the player.<br/>
     * Please note that {@link #onRemove(Player, PlayerData, int)} is called before this method if the upgrade was applied before but the level has changed.
     * @param player player
     * @param playerData player data
     * @param level the upgrade level
     */
    @ApiStatus.OverrideOnly
    protected void onApply(@NotNull Player player, @NotNull PlayerData playerData, int level) {}

    /**
     * This method is called when the upgrade (level) is removed from the player.<br/>
     * This method is also called before calling {@link #onApply(Player, PlayerData, int)} when the upgrade level changes.
     * @param player player
     * @param playerData player data
     * @param level level
     */
    @ApiStatus.OverrideOnly
    protected void onRemove(@NotNull Player player, @NotNull PlayerData playerData, int level) {}

    @ApiStatus.OverrideOnly
    protected void onAffectedPlayerDeath(@NotNull Player player, @NotNull PlayerData playerData, int level) {}

    @ApiStatus.OverrideOnly
    protected void onAffectedPlayerRespawn(@NotNull Player player, @NotNull PlayerData playerData, int level) {}

    // ----- CHECKS -----

    /**
     * When this returns false, the upgrade will handle this player as they does not have the upgrade.
     * @param playerId player uuid
     * @param player player
     * @param playerData player data
     * @return enabled
     */
    @ApiStatus.OverrideOnly
    protected boolean isEnabled(@NotNull UUID playerId, @Nullable Player player, @NotNull PlayerData playerData) {
        return true;
    }

    /**
     * Returns the level after the negative and enabled checks.
     * @param player player
     * @param playerData playerData
     * @return level
     */
    protected final int getLevel(@NotNull UUID playerId, @Nullable Player player, @NotNull PlayerData playerData) {
        int level = playerData.getUpgrade(this.id);

        if (level < 0) return level;
        if (!this.isEnabled(playerId, player, playerData)) return 0;
        return level;
    }

    // ----- SCHEDULER -----

    /**
     * Schedules a task repeating task for this upgrade.
     * @param runnable upgrade task runnable
     * @param delay delay
     * @param interval interval
     * @param removeCondition remove condition
     * @param label label
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition, @Nullable String label) {
        return this.scheduler.scheduleRepeatingTask(task -> {

            for (Player player : this.getAffectedPlayers()) {

                PlayerData playerData = this.manager.getGame().getPlayerData(player);
                if (playerData == null) continue;

                int level = this.getLevel(player.getUniqueId(), player, playerData);

                try {
                    runnable.run(player, playerData, level);
                } catch (Exception e) {
                    this.manager.getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to run upgrade task for player " + player.getUniqueId() + "(" + player.getName() + ")", e);
                }
            }

        }, delay, interval, removeCondition, label);
    }

    /**
     * Schedules a task repeating task for this upgrade.
     * @param runnable upgrade task runnable
     * @param delay delay
     * @param interval interval
     * @param removeCondition remove condition
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, removeCondition, null);
    }

    /**
     * Schedules a task repeating task for this upgrade.
     * @param runnable upgrade task runnable
     * @param delay delay
     * @param interval interval
     * @param label label
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskRunnable runnable, long delay, long interval, @Nullable String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }

    /**
     * Schedules a task repeating task for this upgrade.
     * @param runnable upgrade task runnable
     * @param delay delay
     * @param interval interval
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskRunnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (Removable) null);
    }

    // ----- PLAYERS -----

    /**
     * Returns a set of player that have this upgrade on <b>any</b> upgrade level.
     * @return set of affected players
     */
    public final Set<Player> getAffectedPlayers() {
        Set<Player> affectedPlayers = new HashSet<>();

        for (Player player : this.manager.getGame().getOnlinePlayers()) {
            PlayerData playerData = this.manager.getGame().getPlayerData(player);
            if (playerData == null) continue;

            int upgradeLevel = this.getLevel(player.getUniqueId(), player, playerData);
            if (upgradeLevel <= 0) continue;

            affectedPlayers.add(player);
        }

        return affectedPlayers;
    }

    /**
     * Returns a set of players with the <b>specified</b> upgrade level.
     * @param level level
     * @return set of affected players
     */
    public final Set<Player> getAffectedPlayers(int level) {
        Set<Player> affectedPlayers = new HashSet<>();

        for (Player player : this.manager.getGame().getOnlinePlayers()) {

            PlayerData playerData = this.manager.getGame().getPlayerData(player);
            if (playerData == null) continue;

            int upgradeLevel = this.getLevel(player.getUniqueId(), player, playerData);

            if (upgradeLevel <= 0) continue;
            if (upgradeLevel != level) continue;

            affectedPlayers.add(player);
        }

        return affectedPlayers;
    }

    // ----- APPEARANCE -----

    /**
     * Returns the name of the upgrade.
     * @return name
     */
    @ApiStatus.OverrideOnly
    public @NotNull Component getName() {
        return Component.text(this.id);
    }

    /**
     * Returns the description of the upgrade.
     * @return name
     */
    @ApiStatus.OverrideOnly
    public @NotNull Component getDescription() {
        return Component.empty();
    }

    /**
     * Returns the icon list of the upgrade.
     * @return icon list
     */
    @ApiStatus.OverrideOnly
    public @NotNull List<ItemStack> getIcons() {
        return List.of();
    }

    /**
     * Returns the icon of the specified level.
     * @param level level
     * @return icon or null
     */
    public final @Nullable ItemStack getIcon(int level) {
        List<ItemStack> icons = this.getIcons();

        if (icons.isEmpty()) return null;
        if (level < 0) return icons.getFirst();
        if (level >= icons.size()) return icons.getLast();

        return icons.get(level);
    }

    // ----- TASKS -----

    private void onApplyAndRemoveTask() {

        for (Map.Entry<UUID, PlayerData> entry : this.getManager().getGame().getPlayerDataMap().entrySet()) {
            UUID playerId = entry.getKey();
            PlayerData playerData = entry.getValue();

            // Get current and cached levels
            int currentLevel = this.getLevel(playerId, Bukkit.getPlayer(playerId), playerData);
            int cachedLevel = this.playerCache.getOrDefault(playerId, 0);
            if (cachedLevel < 0) cachedLevel = 0;

            // Levels < 0 can only occur when an admin sets this value manually using the debug command.
            // Setting a < 0 value means that the upgrade is cleared from cache, and never applied or removed.
            // This is only for debug purposes, since it breaks how an upgrade normally behaves.
            if (currentLevel < 0) {
                this.playerCache.remove(playerId);
                continue;
            }

            // Check if cache and current levels match
            if (currentLevel == cachedLevel) continue;

            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            // Call onRemove for the old level
            if (cachedLevel > 0) {
                try {
                    this.onRemove(player, playerData, cachedLevel);
                } catch (Exception e) {
                    this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to remove upgrade " + this.id + " for player " + playerId, e);
                    continue;
                }
            }

            // Update cache
            if (currentLevel > 0) {
                this.playerCache.put(playerId, currentLevel);
            } else {
                this.playerCache.remove(playerId);
            }

            // Call onApply
            if (currentLevel > 0) {
                try {
                    this.onApply(player, playerData, currentLevel);
                } catch (Exception e) {
                    this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to apply upgrade " + this.id + " for player " + playerId, e);
                }
            }

        }

        // Clean up non-ingame players
        Iterator<UUID> i = this.playerCache.keySet().iterator();
        while (i.hasNext()) {
            UUID playerId = i.next();
            if (this.getManager().getGame().isPlayerIngame(playerId)) continue;
            i.remove();
        }

    }

    // ----- DATA -----

    public interface Data {
        @NotNull String id();
        @NotNull String type();
        @NotNull PlayerUpgrade buildUpgrade(@NotNull PlayerUpgradeManager manager);
        @NotNull JSONObject toJSON();
    }

    // ----- OTHER -----

    public final @NotNull PlayerUpgradeManager getManager() {
        return this.manager;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public final boolean isRegistered() {
        return this.manager.getUpgrade(this.id) == this;
    }

    public final void unregister() {
        this.manager.removeUpgrade(this.id);
    }

    final @NotNull TaskScheduler getTaskScheduler() {
        return this.scheduler;
    }

    @Override
    public final boolean toBeRemoved() {
        return !this.isRegistered();
    }

    /**
     * A task runnable for upgrades.
     */
    public interface UpgradeTaskRunnable {
        void run(@NotNull Player player, @NotNull PlayerData playerData, int level);
    }

}
