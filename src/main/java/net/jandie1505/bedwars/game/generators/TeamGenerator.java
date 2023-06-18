package net.jandie1505.bedwars.game.generators;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TeamGenerator extends Generator {
    private final BedwarsTeam team;

    public TeamGenerator(Game game, ItemStack item, Location location, BedwarsTeam team, List<Double> upgradeSteps) {
        super(game, item, location, 5, upgradeSteps);
        this.team = team;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getLevel() {
        return this.team.getForgeUpgrade();
    }
}
