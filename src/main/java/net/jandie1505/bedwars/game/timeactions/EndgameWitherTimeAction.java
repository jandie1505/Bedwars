package net.jandie1505.bedwars.game.timeactions;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.entities.EndgameWither;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import org.bukkit.entity.Wither;

public class EndgameWitherTimeAction extends TimeAction {

    public EndgameWitherTimeAction(Game game, int time) {
        super(game, time, "§cEndgame withers spawned", "Withers");
    }

    @Override
    protected void run() {

        for (BedwarsTeam team : this.getGame().getTeams()) {

            int amount;
            TeamUpgrade endgameBuffUpgrade = this.getGame().getTeamUpgradesConfig().getEndgameBuffUpgrade();

            if (endgameBuffUpgrade.getUpgradeLevels().isEmpty()) {
                continue;
            }

            if (team.getEndgameBuffUpgrade() > 0) {
                if (team.getEndgameBuffUpgrade() >= endgameBuffUpgrade.getUpgradeLevels().size()) {
                    amount = endgameBuffUpgrade.getUpgradeLevels().get(endgameBuffUpgrade.getUpgradeLevels().size() - 1);
                } else {
                    amount = endgameBuffUpgrade.getUpgradeLevels().get(team.getEndgameBuffUpgrade());
                }
            } else {
                amount = 1;
            }

            for (int i = 0; i < amount; i++) {
                this.getGame().addEndgameWither(
                        new EndgameWither(
                                this.getGame(),
                                this.getGame().getWorld().spawn(this.getGame().getCenterLocation(), Wither.class),
                                team.getId()
                        )
                );
            }

        }

    }
}
