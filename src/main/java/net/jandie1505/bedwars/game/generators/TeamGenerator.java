package net.jandie1505.bedwars.game.generators;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.map.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class TeamGenerator extends Generator {
    private final BedwarsTeam team;

    public TeamGenerator(Game game, ItemStack item, Location location, BedwarsTeam team, int startLevel, double baseSpeed, double perUpgradeDivisor) {
        super(game, item, location, 128, startLevel, baseSpeed, perUpgradeDivisor);
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
