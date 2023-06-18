package net.jandie1505.bedwars.game.generators;

import net.jandie1505.bedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PublicGenerator extends Generator {

    public PublicGenerator(Game game, ItemStack item, Location location, List<Double> upgradeSteps) {
        super(game, item, location, 2, upgradeSteps);
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
