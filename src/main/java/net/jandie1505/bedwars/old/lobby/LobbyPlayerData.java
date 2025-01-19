package net.jandie1505.bedwars.old.lobby;

import net.jandie1505.bedwars.old.game.MapData;

public class LobbyPlayerData {
    private MapData vote;
    private int team;

    public LobbyPlayerData() {
        this.vote = null;
        this.team = -1;
    }

    public MapData getVote() {
        return vote;
    }

    public void setVote(MapData vote) {
        this.vote = vote;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
