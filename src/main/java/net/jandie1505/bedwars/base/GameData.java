package net.jandie1505.bedwars.base;

public class GameData {
    private boolean paused;

    public GameData() {
        this.paused = false;
    }

    public boolean paused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
