package net.jandie1505.bedwars.lobby.setup;

public class LobbyGeneratorUpgradeTimeActionData {
    private final int generatorType;
    private final int level;
    private final int time;

    public LobbyGeneratorUpgradeTimeActionData(int generatorType, int level, int time) {
        this.generatorType = generatorType;
        this.level = level;
        this.time = time;
    }

    public int getGeneratorType() {
        return generatorType;
    }

    public int getLevel() {
        return level;
    }

    public int getTime() {
        return time;
    }
}
