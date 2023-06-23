package net.jandie1505.bedwars.game.timeactions;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.WorldBorder;

public class WorldborderChangeTimeAction extends TimeAction {
    private final int radius;

    public WorldborderChangeTimeAction(Game game, int time, String message, String scoreboardText, int radius) {
        super(game, time, message, scoreboardText);
        this.radius = radius;
    }

    @Override
    protected void run() {
        WorldBorder worldBorder = this.getGame().getWorld().getWorldBorder();

        long moveTime = (long) ((worldBorder.getSize() - (this.radius * 2L)) / 2);

        if (moveTime < 0) {
            moveTime = moveTime * (-1);
        }

        worldBorder.setSize((this.radius * 2), moveTime);
    }

    public int getRadius() {
        return this.radius;
    }
}
