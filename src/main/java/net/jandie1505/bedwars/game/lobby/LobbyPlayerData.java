package net.jandie1505.bedwars.game.lobby;

import org.jetbrains.annotations.Nullable;

public class LobbyPlayerData {
    @Nullable private String vote;
    private int team;

    public LobbyPlayerData() {
        this.vote = null;
        this.team = -1;
    }

    public @Nullable String getVote() {
        return vote;
    }

    public void setVote(@Nullable String vote) {
        this.vote = vote;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
