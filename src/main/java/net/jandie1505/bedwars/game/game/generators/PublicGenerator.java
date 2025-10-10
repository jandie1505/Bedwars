package net.jandie1505.bedwars.game.game.generators;

import net.jandie1505.bedwars.game.game.Game;
import org.bukkit.Material;

public class PublicGenerator extends LevelGenerator {

    public PublicGenerator(Game game, GeneratorData generatorData) {
        super(game, generatorData);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getLevel() {

        if (this.getItem().getType() == Material.DIAMOND) {
            return this.getGame().getPublicDiamondGeneratorLevel();
        } else if (this.getItem().getType() == Material.EMERALD) {
            return this.getGame().getPublicEmeraldGeneratorLevel();
        } else {
            return 0;
        }

    }
}
