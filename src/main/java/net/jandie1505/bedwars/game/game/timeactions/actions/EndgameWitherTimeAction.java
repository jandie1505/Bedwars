package net.jandie1505.bedwars.game.game.timeactions.actions;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.entities.entities.EndgameWither;
import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Spawns the endgame withers.
 * @deprecated The endgame withers should be replaced completely in an update in the future.
 */
@Deprecated
public class EndgameWitherTimeAction extends TimeAction {

    public EndgameWitherTimeAction(@NotNull Game game, @NotNull String id, int time) {
        super(game, id, time);
    }

    // ----- RUN -----

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

    // ----- MESSAGES -----

    @Override
    public @NotNull Component getChatMessage() {
        return Component.empty().appendNewline()
                .append(Component.text("The endgame withers have been spawned!", NamedTextColor.LIGHT_PURPLE)).appendNewline();
    }

    @Override
    public @NotNull Component getScoreboardText() {
        return Component.text("Withers");
    }

    // ----- DATA -----

    public record Data(@NotNull String id, int time) implements TimeAction.Data {

        public @NotNull String type() {
            return "endgame_withers";
        }

        @Override
        public @NotNull TimeAction build(@NotNull Game game) {
            return new EndgameWitherTimeAction(game, this.id(), this.time());
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("id", this.id());
            json.put("type", this.type());
            json.put("time", this.time());

            return json;
        }

        public static @NotNull Data fromJSON(@NotNull JSONObject json) {
            return new Data(json.getString("id"), json.getInt("time"));
        }

    }

}
