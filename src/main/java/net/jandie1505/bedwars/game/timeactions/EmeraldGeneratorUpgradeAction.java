package net.jandie1505.bedwars.game.timeactions;

import net.jandie1505.bedwars.game.Game;

public class EmeraldGeneratorUpgradeAction extends TimeAction {
    private final int upgradeLevel;

    public EmeraldGeneratorUpgradeAction(Game game, int time, String message, String scoreboardText, int upgradeLevel) {
        super(game, time, message, scoreboardText);
        this.upgradeLevel = upgradeLevel;
    }

    @Override
    protected void run() {
        this.getGame().setPublicEmeraldGeneratorLevel(upgradeLevel);
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }
}
