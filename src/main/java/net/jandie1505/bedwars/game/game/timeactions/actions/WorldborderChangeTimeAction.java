package net.jandie1505.bedwars.game.game.timeactions.actions;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public final class WorldborderChangeTimeAction extends TimeAction {
    private final int radius;

    public WorldborderChangeTimeAction(@NotNull Game game, @NotNull String id, int time, int radius) {
        super(game, id, time);
        this.radius = radius;
    }

    // ----- RUN -----

    @Override
    protected void onRun() {
        WorldBorder worldBorder = this.getGame().getWorld().getWorldBorder();

        long moveTime = (long) ((worldBorder.getSize() - (this.radius * 2L)) / 2);

        if (moveTime < 0) {
            moveTime = moveTime * (-1);
        }

        worldBorder.setSize((this.radius * 2), moveTime);
    }

    // ----- MESSAGES -----

    @Override
    public @NotNull Component getChatMessage() {
        return Component.empty().appendNewline()
                .append(Component.text("Watch out!", NamedTextColor.LIGHT_PURPLE)).appendNewline()
                .append(Component.text("The worldborder is coming!", NamedTextColor.LIGHT_PURPLE)).appendNewline();
    }

    @Override
    public @NotNull Component getScoreboardText() {
        return Component.text("Border");
    }

    // ----- OTHER -----

    public int getRadius() {
        return this.radius;
    }

    // ----- DATA -----

    public record Data(@NotNull String id, int time, int radius) implements TimeAction.Data {

        @Override
        public @NotNull String type() {
            return "worldborder_change";
        }

        @Override
        public @NotNull TimeAction build(@NotNull Game game) {
            return new WorldborderChangeTimeAction(game, this.id(), this.time(), this.radius());
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("id", this.id);
            json.put("time", this.time);
            json.put("radius", this.radius);

            return json;
        }

        public static @NotNull Data fromJSON(@NotNull JSONObject json) {
            return new Data(json.getString("id"), json.getInt("time"), json.getInt("radius"));
        }

    }

}
