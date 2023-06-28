package net.jandie1505.bedwars;

public abstract class GamePart {
    private final Bedwars plugin;

    public GamePart(Bedwars plugin) {
        this.plugin = plugin;
    }

    public abstract boolean tick();
    public abstract GamePart getNextStatus();

    public Bedwars getPlugin() {
        return this.plugin;
    }
}
