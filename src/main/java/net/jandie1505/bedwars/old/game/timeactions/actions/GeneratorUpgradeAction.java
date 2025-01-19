package net.jandie1505.bedwars.old.game.timeactions.actions;

import net.jandie1505.bedwars.old.game.Game;
import net.jandie1505.bedwars.old.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.old.game.timeactions.base.TimeActionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GeneratorUpgradeAction extends TimeAction {
    private static final TimeActionData.DataAccessor<String> DATA_GENERATOR_MATERIAL = new TimeActionData.DataAccessor<>("material");
    private static final TimeActionData.DataAccessor<Integer> DATA_UPGRADE_LEVEL = new TimeActionData.DataAccessor<>("upgrade_level");
    private final Material material;
    private final int upgradeLevel;

    public GeneratorUpgradeAction(Game game, TimeActionData data) {
        super(game, data);
        this.material = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getData().getDataField(DATA_GENERATOR_MATERIAL))));
        this.upgradeLevel = Objects.requireNonNull(this.getData().getDataField(DATA_UPGRADE_LEVEL));
    }

    @Override
    protected void onRun() {
        this.getGame().setPublicDiamondGeneratorLevel(upgradeLevel);
    }

    @Override
    public @Nullable BaseComponent[] getMessage() {
        return new BaseComponent[]{TextComponent.fromLegacy("§b" + this.material.name() + " generators have been successfully upgraded to level " + this.upgradeLevel)};
    }

    @Override
    public @Nullable String getScoreboardText() {
        return this.material.name() + "S Level " + this.upgradeLevel;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }
}
