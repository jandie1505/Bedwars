package net.jandie1505.bedwars.game.player;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
    private boolean alive;
    private int respawnCountdown;
    private int team;
    private Scoreboard scoreboard;

    public PlayerData(int team) {
        this.alive = false;
        this.respawnCountdown = 0;
        this.team = team;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
}
