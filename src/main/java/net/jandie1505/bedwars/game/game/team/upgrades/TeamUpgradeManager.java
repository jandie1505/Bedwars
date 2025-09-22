package net.jandie1505.bedwars.game.game.team.upgrades;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.events.GamePlayerRespawnEvent;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.player.upgrades.PlayerUpgrade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TeamUpgradeManager implements ManagedListener {
    @NotNull private final Game game;
    @NotNull private final Removable removeCondition;
    @NotNull private final Map<String, TeamUpgrade> upgrades;

    public TeamUpgradeManager(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.upgrades = new HashMap<>();

        this.getGame().getTaskScheduler().scheduleRepeatingTask(this::upgradeTasksTask, 10*20, 1, removeCondition, "team_upgrade_manager_upgrade_schedulers");
        this.game.registerListener(this);
    }

    // ----- TASKS -----

    private void upgradeTasksTask() {

        for (TeamUpgrade upgrade : this.upgrades.values()) {

            try {
                upgrade.getTaskScheduler().tick();
            } catch (Exception e) {
                this.getGame().getPlugin().getLogger().log(Level.WARNING, "Exception in upgrade task " + upgrade.getId(), e);
            }

        }

    }

    // ----- EVENTS -----

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        if (event.isCancelled()) return;

        for (TeamUpgrade upgrade : this.upgrades.values()) {

            upgrade.getAffectedTeams().forEach(team -> {

                int level = team.getUpgrade(upgrade.getId());
                if (level < 0) return;

                team.getOnlineMembers().forEach(member -> {

                    PlayerData playerData = this.game.getPlayerData(member.getUniqueId());
                    if (playerData == null) return;

                    this.game.getTaskScheduler().runTaskLater(task -> upgrade.onAffectedPlayerDeath(team, member, playerData, level), 1);
                });
            });

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBedwarsPlayerRespawn(@NotNull GamePlayerRespawnEvent event) {
        if (event.isCancelled()) return;

        for (TeamUpgrade upgrade : this.upgrades.values()) {

            upgrade.getAffectedTeams().forEach(team -> {

                int level = team.getUpgrade(upgrade.getId());
                if (level < 0) return;

                team.getOnlineMembers().forEach(member -> {

                    PlayerData playerData = this.game.getPlayerData(member.getUniqueId());
                    if (playerData == null) return;

                    this.game.getTaskScheduler().runTaskLater(task -> upgrade.onAffectedPlayerRespawn(team, member, playerData, level), 1);
                });
            });

        }

    }

    // ----- UPGRADE MANAGEMENT -----

    /**
     * Returns the upgrade with the specified upgrade id
     * @param upgradeId upgrade id
     * @return upgrade
     */
    public @Nullable TeamUpgrade getUpgrade(@NotNull String upgradeId) {
        return this.upgrades.get(upgradeId);
    }

    /**
     * Registers the specified upgrade.
     * @param upgrade upgrade
     * @throws IllegalStateException if the upgrade already exists
     */
    public void registerUpgrade(@NotNull TeamUpgrade upgrade) {
        if (this.upgrades.containsKey(upgrade.getId())) throw new IllegalStateException("Upgrade already registered");
        if (upgrade.getManager() != this) throw new IllegalStateException("Upgrade manager must be the manager where the upgrade is registered to");
        upgrade.onRegister();
        this.upgrades.put(upgrade.getId(), upgrade);
    }

    /**
     * Removes the specified upgrade.
     * @param id upgrade id
     */
    public void removeUpgrade(@NotNull String id) {
        TeamUpgrade upgrade = this.upgrades.remove(id);
        upgrade.onUnregister();
    }

    /**
     * Returns an unmodifiable map of the currently registered upgrades.
     * @return map of upgrades
     */
    public @NotNull Map<String, TeamUpgrade> getUpgrades() {
        return Map.copyOf(this.upgrades);
    }

    // ----- OTHER -----

    /**
     * Returns the game.
     * @return game
     */
    public final @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }

}
