package net.jandie1505.bedwars.game.player;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final Game game;
    private final Inventory enderchest;
    private final Map<String, Integer> upgrades;
    private boolean alive;
    private int respawnCountdown;
    private int team;
    private Scoreboard scoreboard;
    private int kills;
    private int deaths;
    private int bedsBroken;
    private int fireballCooldown;
    private int trapCooldown;
    private UUID trackingTarget;
    private int milkTimer;
    private int ironGolemCooldown;
    private int rewardPoints;
    private int zapperCooldown;
    private int teleportToBaseCooldown;
    private int blackHoleCooldown;

    public PlayerData(Game game, int team) {
        this.game = game;
        this.enderchest = this.game.getPlugin().getServer().createInventory(null, 27, "Enderchest");
        this.upgrades = new HashMap<>();
        this.alive = false;
        this.respawnCountdown = 0;
        this.team = team;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.kills = 0;
        this.deaths = 0;
        this.bedsBroken = 0;
        this.fireballCooldown = 0;
        this.trapCooldown = 0;
        this.trackingTarget = null;
        this.milkTimer = 0;
        this.ironGolemCooldown = 0;
        this.rewardPoints = 0;
        this.zapperCooldown = 0;
        this.teleportToBaseCooldown = 0;
        this.blackHoleCooldown = 0;
    }

    public int getUpgrade(String id) {
        return this.upgrades.getOrDefault(id, 0);
    }

    public void setUpgrade(String id, int level) {
        if (level < 0) return;
        this.upgrades.put(id, level);
    }

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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void resetScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
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

    public int getFireballCooldown() {
        return fireballCooldown;
    }

    public void setFireballCooldown(int fireballCooldown) {
        this.fireballCooldown = fireballCooldown;
    }

    public int getTrapCooldown() {
        return trapCooldown;
    }

    public void setTrapCooldown(int trapCooldown) {
        this.trapCooldown = trapCooldown;
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

    public int getMilkTimer() {
        return milkTimer;
    }

    public void setMilkTimer(int milkTimer) {
        this.milkTimer = milkTimer;
    }

    public int getIronGolemCooldown() {
        return ironGolemCooldown;
    }

    public void setIronGolemCooldown(int ironGolemCooldown) {
        this.ironGolemCooldown = ironGolemCooldown;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public int getZapperCooldown() {
        return zapperCooldown;
    }

    public void setZapperCooldown(int zapperCooldown) {
        this.zapperCooldown = zapperCooldown;
    }

    public int getTeleportToBaseCooldown() {
        return teleportToBaseCooldown;
    }

    public void setTeleportToBaseCooldown(int teleportToBaseCooldown) {
        this.teleportToBaseCooldown = teleportToBaseCooldown;
    }

    public int getBlackHoleCooldown() {
        return blackHoleCooldown;
    }

    public void setBlackHoleCooldown(int blackHoleCooldown) {
        this.blackHoleCooldown = blackHoleCooldown;
    }
}
