package net.jandie1505.bedwars.game.game.team.traps;

import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TeamTrapManager {
    @NotNull private final Game game;
    @NotNull private final Removable removable;
    @NotNull private final Map<String, TeamTrap> traps;

    public TeamTrapManager(@NotNull Game game, @Nullable Removable removable) {
        this.game = game;
        this.removable = removable != null ? removable : () -> false;
        this.traps = new HashMap<>();

        this.game.getTaskScheduler().scheduleRepeatingTask(this::trapTask, 1, 10*20, "trap_manager_traps");
    }

    // ----- TASK -----

    private void trapTask() {

        for (BedwarsTeam team : this.game.getTeams()) {
            if (!team.hasTraps()) continue;

            this.game.getWorld().getNearbyEntitiesByType(Player.class, team.getBaseCenter(), team.getBaseRadius(), team.getBaseRadius(), team.getBaseRadius(), this.game::isPlayerIngame).forEach(player -> {
                if (player == null) return;
                this.handleNearPlayer(team, player);
            });

        }

    }

    /**
     * Handler for the for loop in {@link #trapTask()}.
     * @param team team that owns the base that is currently checked for (team the player would trigger a trap from)
     * @param player player
     */
    private void handleNearPlayer(@NotNull BedwarsTeam team, @NotNull Player player) {
        if (!this.isEligible(team, player)) return;

        @Nullable BedwarsTeam.TrapSlot trapSlot = team.pullTrap();
        if (trapSlot == null) return;

        this.triggerTrapSlot(team, player, trapSlot);
    }

    // ----- CHECKS -----

    /**
     * Checks if the specified player is eligible for triggering traps of the specified team.
     * @param team team the player would trigger a trap from
     * @param player player that is triggering the trap
     * @return eligible
     */
    public boolean isEligible(@NotNull BedwarsTeam team, @NotNull Player player) {

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return false;
        if (playerData.getTeam() == team.getId()) return false;

        return playerData.getTimer(PlayerTimers.TRAP_IMMUNITY) <= 0;
    }

    // ----- TRIGGER -----

    /**
     * Triggers the traps in this trap slot for the specified player.
     * @param team team the traps belong to
     * @param player player that triggers the trap
     * @param trapSlot trap slot containing the trap ids
     */
    public void triggerTrapSlot(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull BedwarsTeam.TrapSlot trapSlot) {

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        trapSlot.values().forEach(trapId -> this.triggerTrap(team, player, trapId));
    }

    /**
     * Triggers the trap with the specified id.
     * @param team the team the trap belongs to
     * @param player player that triggers the trap
     * @param trapId trap id of the trap that should be triggered
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean triggerTrap(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull String trapId) {

        TeamTrap trap = this.traps.get(trapId);
        if (trap == null) return false;

        return this.triggerTrap(team, player, trap);
    }

    /**
     * Triggers the specified trap.
     * @param team the team the trap belongs to
     * @param player player that triggers the trap
     * @param trap trap that should be triggered
     * @return success
     */
    public boolean triggerTrap(@NotNull BedwarsTeam team, @NotNull Player player, @NotNull TeamTrap trap) {

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return false;

        trap.trigger(team, player, playerData);

        playerData.setTimer(PlayerTimers.TRAP_IMMUNITY, 30*20);

        return true;
    }

    // ----- TRAPS MANAGEMENT -----

    /**
     * Returns an unmodifiable version of the traps map.
     * @return unmodifiable traps map
     */
    public @NotNull Map<String, TeamTrap> getTraps() {
        return Map.copyOf(this.traps);
    }

    /**
     * Returns the trap with the specified id.
     * @param id trap id
     * @return trap or null if it does not exist
     */
    public @Nullable TeamTrap getTrap(@NotNull String id) {
        return this.traps.get(id);
    }

    /**
     * Removes the trap with the specified id
     * @param id trap id
     */
    public void removeTrap(@NotNull String id) {
        this.traps.remove(id);
    }

    /**
     * Registers the specified trap.
     * @param trap trap
     * @throws IllegalStateException trap has already been registered
     * @throws IllegalStateException manager is different from the specified manager in trap
     */
    public void registerTrap(@NotNull TeamTrap trap) {
        if (this.traps.containsKey(trap.getId())) throw new IllegalStateException("Trap already registered");
        if (trap.getManager() != this) throw new IllegalStateException("Trap manager must be the manager where the trap is registered to");
        this.traps.put(trap.getId(), trap);
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    public @NotNull Removable getRemovable() {
        return removable;
    }

}
