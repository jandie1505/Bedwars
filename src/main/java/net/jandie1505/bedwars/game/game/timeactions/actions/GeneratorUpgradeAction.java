package net.jandie1505.bedwars.game.game.timeactions.actions;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public final class GeneratorUpgradeAction extends TimeAction {
    private final int generatorType;
    private final int upgradeLevel;

    public GeneratorUpgradeAction(@NotNull Game game, @NotNull String id, int time, int generatorType, int upgradeLevel) {
        super(game, id, time);
        this.generatorType = generatorType;
        this.upgradeLevel = upgradeLevel;
    }

    @Override
    protected void onRun() {

        switch (this.generatorType) {
            case 1 -> this.getGame().setPublicDiamondGeneratorLevel(upgradeLevel);
            case 2 -> this.getGame().setPublicEmeraldGeneratorLevel(upgradeLevel);
        }

    }

    private String getMaterialName() {

        return switch (this.generatorType) {
            case 1 -> "Diamond";
            case 2 -> "Emerald";
            default -> "unknown";
        };

    }

    // ----- MESSAGES -----

    @Override
    public @NotNull Component getChatMessage() {
        return Component.text(this.getMaterialName() + " generators have been upgraded to level " + this.upgradeLevel, NamedTextColor.DARK_AQUA);
    }

    @Override
    public @NotNull Component getScoreboardText() {
        return Component.text(this.getMaterialName() + "s Level " + this.upgradeLevel);
    }

    // ----- OTHER -----

    public int getGeneratorType() {
        return generatorType;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    // ----- DATA -----

    public record Data(@NotNull String id, int time, int generatorType, int upgradeLevel) implements TimeAction.Data {

        @Override
        public @NotNull String type() {
            return "generator_upgrade";
        }

        @Override
        public @NotNull TimeAction build(@NotNull Game game) {
            return new GeneratorUpgradeAction(game, this.id(), this.time(), this.generatorType(), this.upgradeLevel());
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("id", this.id);
            json.put("type", this.type());
            json.put("time", this.time);
            json.put("generator_type", this.generatorType);
            json.put("upgrade_level", this.upgradeLevel);

            return json;
        }

        public static @NotNull Data fromJSON(@NotNull JSONObject json) {
            return new Data(json.getString("id"), json.getInt("time"), json.getInt("generator_type"), json.getInt("upgrade_level"));
        }

    }

}
