package net.jandie1505.bedwars.game.player;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

public class PlayerData {
    private boolean alive;
    private int respawnCountdown;
    private int team;
    private Scoreboard scoreboard;
    private int kills;
    private int deaths;
    private int bedsBroken;
    private int armorUpgrade;
    private int pickaxeUpgrade;
    private int shearsUpgrade;
    private int fireballCooldown;
    private int trapCooldown;
    private UUID trackingTarget;

    public PlayerData(int team) {
        this.alive = false;
        this.respawnCountdown = 0;
        this.team = team;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.kills = 0;
        this.deaths = 0;
        this.bedsBroken = 0;
        this.armorUpgrade = 0;
        this.pickaxeUpgrade = 0;
        this.shearsUpgrade = 0;
        this.fireballCooldown = 0;
        this.trapCooldown = 0;
        this.trackingTarget = null;
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
        return armorUpgrade;
    }

    public void setArmorUpgrade(int armorUpgrade) {
        this.armorUpgrade = armorUpgrade;
    }

    public int getPickaxeUpgrade() {
        return pickaxeUpgrade;
    }

    public void setPickaxeUpgrade(int pickaxeUpgrade) {
        this.pickaxeUpgrade = pickaxeUpgrade;
    }

    public int getShearsUpgrade() {
        return shearsUpgrade;
    }

    public void setShearsUpgrade(int shearsUpgrade) {
        this.shearsUpgrade = shearsUpgrade;
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
}
