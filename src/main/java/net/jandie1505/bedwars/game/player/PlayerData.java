package net.jandie1505.bedwars.game.player;

public class PlayerData {
    private int team;

    public PlayerData(int team) {
        this.team = team;
    }

    public int team() {
        return this.team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

}
