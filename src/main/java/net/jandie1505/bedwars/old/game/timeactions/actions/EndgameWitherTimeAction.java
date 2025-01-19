package net.jandie1505.bedwars.old.game.timeactions.actions;

import net.jandie1505.bedwars.old.game.Game;
import net.jandie1505.bedwars.old.game.entities.entities.EndgameWither;
import net.jandie1505.bedwars.old.game.team.BedwarsTeam;
import net.jandie1505.bedwars.old.game.team.TeamUpgrade;
import net.jandie1505.bedwars.old.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.old.game.timeactions.base.TimeActionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

public class EndgameWitherTimeAction extends TimeAction {

    public EndgameWitherTimeAction(Game game, TimeActionData data) {
        super(game, data);
    }

    @Override
    protected void onRun() {

        for (BedwarsTeam team : this.getGame().getTeams()) {

            if (!team.isAlive()) {
                continue;
            }

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
                new EndgameWither(
                        this.getGame(),
                        this.getGame().getData().centerLocation(),
                        team.getId()
                );
            }

        }

    }

    @Override
    public @Nullable BaseComponent[] getMessage() {
        return new BaseComponent[]{TextComponent.fromLegacy("§bThe endgame withers spawned!")};
    }

    @Override
    public @Nullable String getScoreboardText() {
        return "Withers";
    }

}
