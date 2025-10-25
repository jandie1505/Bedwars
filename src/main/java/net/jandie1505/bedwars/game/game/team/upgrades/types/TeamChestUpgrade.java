package net.jandie1505.bedwars.game.game.team.upgrades.types;

import net.jandie1505.bedwars.game.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgrade;
import net.jandie1505.bedwars.game.game.team.upgrades.TeamUpgradeManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public final class TeamChestUpgrade extends TeamUpgrade {
    @NotNull public static final String TYPE = "team_chest";

    /**
     * Creates a new team upgrade.
     *
     * @param manager manager
     * @param id      id
     */
    public TeamChestUpgrade(@NotNull TeamUpgradeManager manager, @NotNull String id) {
        super(manager, id);
    }

    @Override
    protected void onApply(@NotNull BedwarsTeam team, int level) {
        team.setTeamChestLevel(level);
    }

    @Override
    protected void onRemove(@NotNull BedwarsTeam team, int level) {
        team.setTeamChestLevel(0);
    }

    // ----- DATA .....

    /**
     * Data of HealPoolTeamUpgrade.
     * @param id id
     */
    public record Data(@NotNull String id) implements TeamUpgrade.Data {

        @Override
        public @NotNull String type() {
            return TYPE;
        }

        @Override
        public @NotNull TeamUpgrade buildUpgrade(@NotNull TeamUpgradeManager manager) {
            return new TeamChestUpgrade(manager, this.id);
        }

        public static @NotNull Data fromJSON(@NotNull String id, @NotNull JSONObject jsonObject) {
            return new Data(id);
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("type", this.type());
            return json;
        }
    }

    public @NotNull Data getData() {
        return new Data(this.getId());
    }

}
