package net.jandie1505.bedwars.game.game.generators;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;

public class TeamGenerator extends LevelGenerator {
    private final BedwarsTeam team;

    public TeamGenerator(Game game, GeneratorData generatorData, BedwarsTeam team) {
        super(game, generatorData);
        this.team = team;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getLevel() {
        return this.team.getUpgrade(TeamUpgrades.GENERATORS);
    }

    public BedwarsTeam getTeam() {
        return this.team;
    }
}
