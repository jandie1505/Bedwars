package net.jandie1505.bedwars.game.timeactions.actions;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GeneratorUpgradeAction extends TimeAction {
    private static final TimeActionData.DataAccessor<Integer> DATA_GENERATOR_MATERIAL = new TimeActionData.DataAccessor<>("generatorType");
    private static final TimeActionData.DataAccessor<Integer> DATA_UPGRADE_LEVEL = new TimeActionData.DataAccessor<>("generatorLevel");
    private final int generatorType;
    private final int upgradeLevel;

    public GeneratorUpgradeAction(Game game, TimeActionData data) {
        super(game, data);
        this.generatorType = Objects.requireNonNull(this.getData().getDataField(DATA_GENERATOR_MATERIAL));
        this.upgradeLevel = Objects.requireNonNull(this.getData().getDataField(DATA_UPGRADE_LEVEL));
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

    @Override
    public @Nullable BaseComponent[] getMessage() {
        return new BaseComponent[]{TextComponent.fromLegacy("§b" + this.getMaterialName() + " generators have been successfully upgraded to level " + this.upgradeLevel)};
    }

    @Override
    public @Nullable String getScoreboardText() {
        return this.getMaterialName() + "s Level " + this.upgradeLevel;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }
}
