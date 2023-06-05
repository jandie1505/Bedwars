package net.jandie1505.bedwars.game.player;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

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
}
