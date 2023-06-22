package net.jandie1505.bedwars.lobby.setup;

public class LobbyDestroyBedsTimeActionData {
    private final int time;
    private final boolean disableBeds;

    public LobbyDestroyBedsTimeActionData(int time, boolean disableBeds) {
        this.time = time;
        this.disableBeds = disableBeds;
    }

    public int getTime() {
        return time;
    }

    public boolean isDisableBeds() {
        return disableBeds;
    }
}
