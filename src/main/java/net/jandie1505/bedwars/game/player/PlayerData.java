package net.jandie1505.bedwars.game.player;

public class PlayerData {
    private int team;
    private boolean alive;
    private int respawnCountdown;

    public PlayerData(int team) {
        this.team = team;
        this.alive = true;
        this.respawnCountdown = 5;
    }

    public int team() {
        return this.team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean alive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int respawnCountdown() {
        return this.respawnCountdown;
    }

    public void setRespawnCountdown(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    public void decrementRespawnCountdown() {
        this.respawnCountdown--;
    }

}
