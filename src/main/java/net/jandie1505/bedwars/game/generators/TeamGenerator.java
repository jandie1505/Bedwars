package net.jandie1505.bedwars.game.generators;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.map.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class TeamGenerator extends Generator {
    private final BedwarsTeam team;
    private final double baseSpeed;
    private final double perUpgradeMultiplier;

    public TeamGenerator(Game game, ItemStack item, Location location, BedwarsTeam team, double baseSpeed, double perUpgradeMultiplier) {
        super(game, item, location, 128);
        this.team = team;
        this.baseSpeed = baseSpeed;
        this.perUpgradeMultiplier = perUpgradeMultiplier;
    }

    @Override
    public double getSpeed() {
        return this.baseSpeed + ((this.team.getForgeUpgrade() + 1) * this.perUpgradeMultiplier);
    }
}
