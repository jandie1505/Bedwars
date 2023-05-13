package net.jandie1505.bedwars.game.player;

import net.jandie1505.bedwars.game.Game;

public class PlayerData {
    private boolean alive;
    private int respawnCountdown;
    private int team;

    public PlayerData(int team) {
        this.alive = false;
        this.respawnCountdown = 0;
        this.team = team;
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
}
