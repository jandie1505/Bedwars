package net.jandie1505.bedwars.old.game.generators;

import net.jandie1505.bedwars.old.game.Game;
import org.bukkit.Material;

public class PublicGenerator extends Generator {

    public PublicGenerator(Game game, GeneratorData generatorData) {
        super(game, generatorData);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getLevel() {

        if (this.getData().item().getType() == Material.DIAMOND) {
            return this.getGame().getPublicDiamondGeneratorLevel();
        } else if (this.getData().item().getType() == Material.EMERALD) {
            return this.getGame().getPublicEmeraldGeneratorLevel();
        } else {
            return 0;
        }

    }
}
