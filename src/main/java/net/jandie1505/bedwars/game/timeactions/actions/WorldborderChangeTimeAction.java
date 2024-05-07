package net.jandie1505.bedwars.game.timeactions.actions;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WorldborderChangeTimeAction extends TimeAction {
    private static final TimeActionData.DataAccessor<Integer> DATA_RADIUS = new TimeActionData.DataAccessor<>("radius");
    private final int radius;

    public WorldborderChangeTimeAction(Game game, TimeActionData data) {
        super(game, data);
        this.radius = Objects.requireNonNull(this.getData().getDataField(DATA_RADIUS));
    }

    @Override
    protected void onRun() {
        WorldBorder worldBorder = this.getGame().getWorld().getWorldBorder();

        long moveTime = (long) ((worldBorder.getSize() - (this.radius * 2L)) / 2);

        if (moveTime < 0) {
            moveTime = moveTime * (-1);
        }

        worldBorder.setSize((this.radius * 2), moveTime);
    }

    @Override
    public @Nullable BaseComponent[] getMessage() {
        return new BaseComponent[] {TextComponent.fromLegacy("§cWatch out! The worldborder is coming")};
    }

    @Override
    public @Nullable String getScoreboardText() {
        return "Border";
    }

    public int getRadius() {
        return this.radius;
    }
}
