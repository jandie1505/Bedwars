package net.jandie1505.bedwars.game.game.player.data;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.constants.PlayerTimers;
import net.jandie1505.bedwars.game.game.player.upgrades.constants.PlayerUpgradeKeys;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final Game game;
    private final Inventory enderchest;
    private final Map<String, Integer> upgrades;
    private final Map<@NotNull String, @NotNull Integer> timers;
    private boolean alive;
    private int respawnCountdown;
    private int team;
    private int kills;
    private int deaths;
    private int bedsBroken;
    private UUID trackingTarget;
    private int milkTimer;
    private int rewardPoints;

    public PlayerData(Game game, int team) {
        this.game = game;
        this.enderchest = this.game.getPlugin().getServer().createInventory(null, 27, "Enderchest");
        this.upgrades = new HashMap<>();
        this.timers = new HashMap<>();
        this.alive = false;
        this.respawnCountdown = 0;
        this.team = team;
        this.kills = 0;
        this.deaths = 0;
        this.bedsBroken = 0;
        this.trackingTarget = null;
        this.milkTimer = 0;
        this.rewardPoints = 0;

        this.upgrades.put(PlayerUpgradeKeys.PICKAXE, 0);
        this.upgrades.put(PlayerUpgradeKeys.SHEARS, 0);
        this.upgrades.put(PlayerUpgradeKeys.ARMOR, 1);
    }

    // ----- UPGRADES -----

    public @NotNull Map<String, Integer> getUpgrades() {
        return Map.copyOf(upgrades);
    }

    public int getUpgrade(String id) {
        return this.upgrades.getOrDefault(id, 0);
    }

    public void setUpgrade(String id, int level) {

        if (level < 0) {
            this.upgrades.remove(id);
            return;
        }

        this.upgrades.put(id, level);
    }

    // ----- TIMERS -----

    /**
     * Returns the timer map.
     * @return timer map
     */
    public @NotNull Map<@NotNull String, @NotNull Integer> getTimers() {
        return this.timers;
    }

    /**
     * Returns a specific timer.
     * @param id id
     * @return timer value
     */
    public int getTimer(@NotNull String id) {
        return this.timers.getOrDefault(id, 0);
    }

    /**
     * Sets the specified timer.<br/>
     * Setting to negativ values will remove it.
     * @param id id
     * @param t amount
     */
    public void setTimer(@NotNull String id, int t) {

        if (t < 0) {
            this.timers.remove(id);
            return;
        }

        this.timers.put(id, t);
    }

    /**
     * Removes the specified timer
     * @param id id
     */
    public void removeTimer(@NotNull String id) {
        this.timers.remove(id);
    }

    // ----- OTHER -----

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getRespawnCountdown() {
        return respawnCountdown;
    }

    public void setRespawnCountdown(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getBedsBroken() {
        return bedsBroken;
    }

    public void setBedsBroken(int bedsBroken) {
        this.bedsBroken = bedsBroken;
    }

    public int getArmorUpgrade() {
        return this.getUpgrade("armor");
    }

    public void setArmorUpgrade(int armorUpgrade) {
        this.setUpgrade("armor", armorUpgrade);
    }

    public int getPickaxeUpgrade() {
        return this.getUpgrade("pickaxe");
    }

    public void setPickaxeUpgrade(int pickaxeUpgrade) {
        this.setUpgrade("pickaxe", pickaxeUpgrade);
    }

    public int getShearsUpgrade() {
        return this.getUpgrade("shears");
    }

    public void setShearsUpgrade(int shearsUpgrade) {
        this.setUpgrade("shears", shearsUpgrade);
    }

    @Deprecated
    public int getFireballCooldown() {
        return this.timers.getOrDefault(PlayerTimers.FIREBALL_COOLDOWN, 0);
    }

    @Deprecated
    public void setFireballCooldown(int fireballCooldown) {
        this.timers.put(PlayerTimers.FIREBALL_COOLDOWN, fireballCooldown);
    }

    @Deprecated(forRemoval = true)
    public int getTrapCooldown() {
        return this.timers.getOrDefault("player.trap_cooldown", 0);
    }

    @Deprecated(forRemoval = true)
    public void setTrapCooldown(int trapCooldown) {
        this.timers.put("player.trap_cooldown", trapCooldown);
    }

    public UUID getTrackingTarget() {
        return trackingTarget;
    }

    public void setTrackingTarget(UUID trackingTarget) {
        this.trackingTarget = trackingTarget;
    }

    public Game getGame() {
        return game;
    }

    public Inventory getEnderchest() {
        return enderchest;
    }

    @Deprecated
    public int getIronGolemCooldown() {
        return this.timers.getOrDefault(PlayerTimers.BASE_DEFENDER_COOLDOWN, 0);
    }

    @Deprecated
    public void setIronGolemCooldown(int ironGolemCooldown) {
        this.timers.put(PlayerTimers.BASE_DEFENDER_COOLDOWN, ironGolemCooldown);
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    @Deprecated
    public int getZapperCooldown() {
        return this.timers.getOrDefault(PlayerTimers.ZAPPER_COOLDOWN, 0);
    }

    @Deprecated
    public void setZapperCooldown(int zapperCooldown) {
        this.timers.put(PlayerTimers.ZAPPER_COOLDOWN, zapperCooldown);
    }

    @Deprecated
    public int getTeleportToBaseCooldown() {
        return this.timers.getOrDefault(PlayerTimers.SPAWN_DUST_COOLDOWN, 0);
    }

    @Deprecated
    public void setTeleportToBaseCooldown(int teleportToBaseCooldown) {
        this.timers.put(PlayerTimers.SPAWN_DUST_COOLDOWN, teleportToBaseCooldown);
    }

    @Deprecated
    public int getBlackHoleCooldown() {
        return this.timers.getOrDefault(PlayerTimers.BLACK_HOLE_COOLDOWN, 0);
    }

    @Deprecated
    public void setBlackHoleCooldown(int blackHoleCooldown) {
        this.timers.put(PlayerTimers.BLACK_HOLE_COOLDOWN, 0);
    }
}
