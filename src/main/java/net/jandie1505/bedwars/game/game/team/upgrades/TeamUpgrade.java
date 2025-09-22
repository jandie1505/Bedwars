package net.jandie1505.bedwars.game.game.team.upgrades;

import net.chaossquad.mclib.misc.Removable;
import net.chaossquad.mclib.scheduler.TaskScheduler;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;

/**
 * The base class for a team upgrade like sharpness, protection, forge, heal pool, etc...
 */
public abstract class TeamUpgrade implements Removable {
    @NotNull private final TeamUpgradeManager manager;
    @NotNull private final String id;
    @NotNull private final TaskScheduler scheduler;
    @NotNull private final Map<Integer, Integer> teamCache;
    @NotNull private final Map<Integer, Map<UUID, Integer>> playerCache;

    /**
     * Creates a new team upgrade.
     * @param manager manager
     * @param id id
     */
    public TeamUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id) {
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        this.manager = manager;
        this.id = id;
        this.scheduler = new TaskScheduler(this.manager.getGame().getPlugin().getLogger());
        this.teamCache = new HashMap<>();
        this.playerCache = new HashMap<>();

        this.scheduler.scheduleRepeatingTask(this::onApplyAndRemoveTaskForTeams, 1, 20, "apply_and_remove_task");
        this.scheduler.scheduleRepeatingTask(this::onApplyAndRemoveTaskForPlayers, 1, 20, "apply_and_remove_task_players");
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
     * This method is called when the upgrade (level) is applied to the team.<br/>
     * Please note that {@link #onRemove(BedwarsTeam, int)} is called before this method if the upgrade was applied before but the level has changed.
     * @param team team
     * @param level the upgrade level
     */
    @ApiStatus.OverrideOnly
    protected void onApply(@NotNull BedwarsTeam team, int level) {}

    /**
     * This method is called when the upgrade (level) is removed from the team.<br/>
     * This method is also called before calling {@link #onApply(BedwarsTeam, int)} when the upgrade level changes.
     * @param team team
     * @param level level
     */
    @ApiStatus.OverrideOnly
    protected void onRemove(@NotNull BedwarsTeam team, int level) {}

    /**
     * This method is called when the upgrade (level) is applied to the player of a team.<br/>
     * Please note that {@link #onRemove(BedwarsTeam, Player, PlayerData, int)} is called before this method if the upgrade was applied before but the level has changed.
     * @param team team
     * @param player player
     * @param playerData player data
     * @param level the upgrade level
     */
    @ApiStatus.OverrideOnly
    protected void onApply(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {}

    /**
     * This method is called when the upgrade (level) is removed from the player of a team.<br/>
     * This method is also called before calling {@link #onApply(BedwarsTeam, Player, PlayerData, int)} when the upgrade level changes.
     * @param team team
     * @param player player
     * @param playerData player data
     * @param level level
     */
    @ApiStatus.OverrideOnly
    protected void onRemove(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {}

    @ApiStatus.OverrideOnly
    protected void onAffectedPlayerDeath(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {}

    @ApiStatus.OverrideOnly
    protected void onAffectedPlayerRespawn(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level) {}

    // ----- CHECKS -----

    /**
     * When this returns false, the upgrade will handle this team as they do not have the upgrade.
     * @param team team
     * @return enabled
     */
    @ApiStatus.OverrideOnly
    protected boolean isEnabled(@Nullable BedwarsTeam team) {
        return true;
    }

    /**
     * Returns the level after the negative and enabled checks.
     * @param team team
     * @return level
     */
    protected final int getLevel(@Nullable BedwarsTeam team) {

        if (team == null) return 0;
        int level = team.getUpgrade(this.id);

        if (level < 0) return level;
        if (!this.isEnabled(team)) return 0;

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

            for (BedwarsTeam team : this.getAffectedTeams()) {
                int level = this.getLevel(team);

                try {
                    runnable.run(team, level);
                } catch (Exception e) {
                    this.manager.getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to run upgrade task for team " + team.getId() + "(" + team.getName() + ")", e);
                }
            }

        }, delay, interval, removeCondition, label);
    }

    /**
     * Schedules a repeating task for this upgrade.
     * @param runnable runnable
     * @param delay delay
     * @param interval interval
     * @param removeCondition remove condition
     * @param label label
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskPlayerRunnable runnable, long delay, long interval, @Nullable Removable removeCondition, @Nullable String label) {
        return this.scheduleRepeatingTask((team, level) -> {

            for (Player player : team.getOnlineMembers()) {

                PlayerData playerData = this.manager.getGame().getPlayerData(player);
                if (playerData == null) continue;

                try {
                    runnable.run(team, player, playerData, level);
                } catch (Exception e) {
                    this.manager.getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to run upgrade task for team " + team.getId() + "(" + team.getName() + ") for player " + player.getUniqueId(), e);
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
     * @param removeCondition remove condition
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskPlayerRunnable runnable, long delay, long interval, @Nullable Removable removeCondition) {
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
     * @param label label
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskPlayerRunnable runnable, long delay, long interval, @Nullable String label) {
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

    /**
     * Schedules a task repeating task for this upgrade.
     * @param runnable upgrade task runnable
     * @param delay delay
     * @param interval interval
     * @return task id
     */
    protected final long scheduleRepeatingTask(@NotNull UpgradeTaskPlayerRunnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (Removable) null);
    }

    // ----- TEAMS -----

    /**
     * Returns a set of teams that have this upgrade on <b>any</b> upgrade level.
     * @return set of affected teams
     */
    public final Set<BedwarsTeam> getAffectedTeams() {
        Set<BedwarsTeam> affectedTeams = new HashSet<>();

        for (BedwarsTeam team : this.manager.getGame().getTeams()) {

            int upgradeLevel = this.getLevel(team);
            if (upgradeLevel <= 0) continue;

            affectedTeams.add(team);
        }

        return affectedTeams;
    }

    /**
     * Returns a set of teams with the <b>specified</b> upgrade level.
     * @param level level
     * @return set of affected teams
     */
    public final Set<BedwarsTeam> getAffectedTeams(int level) {
        Set<BedwarsTeam> affectedTeams = new HashSet<>();

        for (BedwarsTeam team : this.manager.getGame().getTeams()) {
            int upgradeLevel = this.getLevel(team);

            if (upgradeLevel <= 0) continue;
            if (upgradeLevel != level) continue;

            affectedTeams.add(team);
        }

        return affectedTeams;
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

    private void onApplyAndRemoveTaskForTeams() {

        for (BedwarsTeam team : this.getManager().getGame().getTeams()) {

            // Get current and cached levels
            int currentLevel = this.getLevel(team);
            int cachedLevel = this.teamCache.getOrDefault(team.getId(), 0);
            if (cachedLevel < 0) cachedLevel = 0;

            // Levels < 0 can only occur when an admin sets this value manually using the debug command.
            // Setting a < 0 value means that the upgrade is cleared from cache, and never applied or removed.
            // This is only for debug purposes, since it breaks how an upgrade normally behaves.
            if (currentLevel < 0) {
                this.teamCache.remove(team.getId());
                continue;
            }

            // Check if cache and current levels match
            if (currentLevel == cachedLevel) continue;

            // Call onRemove for the old level
            if (cachedLevel > 0) {
                try {
                    this.onRemove(team, cachedLevel);
                } catch (Exception e) {
                    this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to remove upgrade " + this.id + " for team " + team.getId(), e);
                    continue;
                }
            }

            // Update cache
            if (currentLevel > 0) {
                this.teamCache.put(team.getId(), currentLevel);
            } else {
                this.teamCache.remove(team.getId());
            }

            // Call onApply
            if (currentLevel > 0) {
                try {
                    this.onApply(team, currentLevel);
                } catch (Exception e) {
                    this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to apply upgrade " + this.id + " for team " + team.getId(), e);
                }
            }

        }

        // Clean up non-existing teams
        Iterator<Integer> i = this.teamCache.keySet().iterator();
        while (i.hasNext()) {
            int teamId = i.next();
            if (this.getManager().getGame().getTeam(teamId) != null) continue;
            i.remove();
        }

    }

    private void onApplyAndRemoveTaskForPlayers() {

        for (BedwarsTeam team : this.getManager().getGame().getTeams()) {

            // Get current and cached levels
            int currentLevel = this.getLevel(team);

            // Levels < 0 can only occur when an admin sets this value manually using the debug command.
            // Setting a < 0 value means that the upgrade is cleared from cache, and never applied or removed.
            // This is only for debug purposes, since it breaks how an upgrade normally behaves.
            if (currentLevel < 0) {
                this.playerCache.remove(team.getId());
                continue;
            }

            // Get player map or create a new one
            Map<UUID, Integer> cache = this.playerCache.computeIfAbsent(team.getId(), k -> new HashMap<>());

            // Iterate through team members
            for (Player player : team.getOnlineMembers()) { // Unlike PlayerUpgrade#onApplyAndRemoveTask, we can iterate through the online players directly here because there is no "debug remove case" here.
                PlayerData playerData = this.getManager().getGame().getPlayerData(player);
                if (playerData == null) continue;

                int cachedLevel = cache.getOrDefault(player.getUniqueId(), 0);
                if (cachedLevel < 0) cachedLevel = 0;

                // Check if cache and current levels match
                if (currentLevel == cachedLevel) continue;

                // Call onRemove for the old level
                if (cachedLevel > 0) {
                    try {
                        this.onRemove(team, player, playerData, cachedLevel);
                    } catch (Exception e) {
                        this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to remove upgrade " + this.id + " for team " + team.getId() + " for player " + player.getUniqueId(), e);
                        continue;
                    }
                }

                // Update cache
                if (currentLevel > 0) {
                    cache.put(player.getUniqueId(), currentLevel);
                } else {
                    cache.remove(player.getUniqueId());
                }

                // Call onApply
                if (currentLevel > 0) {
                    try {
                        this.onApply(team, player, playerData, currentLevel);
                    } catch (Exception e) {
                        this.getManager().getGame().getPlugin().getLogger().log(Level.WARNING, "Failed to apply upgrade " + this.id + " for team " + team.getId() + " for player " + player.getUniqueId(), e);
                    }
                }

            }

            // Clean up non-ingame players
            Iterator<UUID> i = cache.keySet().iterator();
            while (i.hasNext()) {
                UUID playerId = i.next();
                PlayerData playerData = this.getManager().getGame().getPlayerData(playerId);
                if (playerData != null && playerData.getTeam() == team.getId()) continue;
                i.remove();
            }

        }

        // Clean up non-existing teams
        Iterator<Integer> i = this.playerCache.keySet().iterator();
        while (i.hasNext()) {
            int teamId = i.next();
            if (this.getManager().getGame().getTeam(teamId) != null) continue;
            i.remove();
        }

    }

    // ----- DATA -----

    public interface Data {
        @NotNull String id();
        @NotNull String type();
        @NotNull TeamUpgrade buildUpgrade(@NotNull TeamUpgrade manager);
        @NotNull JSONObject toJSON();
    }

    // ----- OTHER -----

    public final @NotNull TeamUpgradeManager getManager() {
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
        void run(@NotNull BedwarsTeam team, int level);
    }

    /**
     * A task runnable for upgrades.
     */
    public interface UpgradeTaskPlayerRunnable {
        void run(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull PlayerData playerData, int level);
    }

}
