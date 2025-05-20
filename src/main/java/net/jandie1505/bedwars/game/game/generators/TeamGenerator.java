package net.jandie1505.bedwars.game.game.generators;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;

public class TeamGenerator extends Generator {
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
        return this.team.getForgeUpgrade();
    }

    public BedwarsTeam getTeam() {
        return this.team;
    }
}
