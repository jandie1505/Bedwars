package net.jandie1505.bedwars.game.game.generators;

import net.jandie1505.bedwars.game.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LevelGenerator extends Generator {
    @NotNull private final List<Double> upgradeSteps;

    public LevelGenerator(@NotNull Game game, @NotNull GeneratorData data) {
        super(game, new Generator.Data(
                data.location(),
                data.item(),
                5
        ));

        this.upgradeSteps = List.copyOf(data.upgradeSteps());
    }

    // ----- ABSTRACT -----

    public abstract int getLevel();

    public double getSpeed() {
        int level = this.getLevel();

        if (this.upgradeSteps.isEmpty()) {
            return 0;
        }

        if (level >= this.upgradeSteps.size()) {
            level = (this.upgradeSteps.size() - 1);
        }

        return this.upgradeSteps.get(level);
    }

    // ----- OTHER -----

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public final int getSpawnRate() {
        double speed = this.getSpeed();

        if (speed <= 0.0) return 0;

        return speed >= 1.0 ? ((int) speed) : 1;
    }

    @Override
    public int getAmount() {
        double speed = this.getSpeed();

        if (speed < 1.0) {
            return (int) Math.round(1.0d / speed);
        } else {
            return 1;
        }

    }

}
