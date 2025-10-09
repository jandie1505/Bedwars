package net.jandie1505.bedwars.game.game.timeactions.actions;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.entities.EndgameWither;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeActionData;
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

            int amount = team.getUpgrade(TeamUpgrades.ENDGAME_BUFF) + 1;

            for (int i = 0; i < amount; i++) {
                new EndgameWither(
                        this.getGame(),
                        this.getGame().getCenterLocation().mutableCopy(),
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
