package net.jandie1505.bedwars.game.team;

public class TeamUpgradesConfig {
    private final TeamUpgrade sharpnessUpgrade;
    private final TeamUpgrade protectionUpgrade;
    private final TeamUpgrade hasteUpgrade;
    private final TeamUpgrade forgeUpgrade;
    private final TeamUpgrade healPoolUpgrade;
    private final TeamUpgrade dragonBuffUpgrade;

    public TeamUpgradesConfig(TeamUpgrade sharpnessUpgrade, TeamUpgrade protectionUpgrade, TeamUpgrade hasteUpgrade, TeamUpgrade forgeUpgrade, TeamUpgrade healPoolUpgrade, TeamUpgrade dragonBuffUpgrade) {
        this.sharpnessUpgrade = sharpnessUpgrade;
        this.protectionUpgrade = protectionUpgrade;
        this.hasteUpgrade = hasteUpgrade;
        this.forgeUpgrade = forgeUpgrade;
        this.healPoolUpgrade = healPoolUpgrade;
        this.dragonBuffUpgrade = dragonBuffUpgrade;
    }

    public TeamUpgrade getSharpnessUpgrade() {
        return sharpnessUpgrade;
    }

    public TeamUpgrade getProtectionUpgrade() {
        return protectionUpgrade;
    }

    public TeamUpgrade getHasteUpgrade() {
        return hasteUpgrade;
    }

    public TeamUpgrade getForgeUpgrade() {
        return forgeUpgrade;
    }

    public TeamUpgrade getHealPoolUpgrade() {
        return healPoolUpgrade;
    }

    public TeamUpgrade getDragonBuffUpgrade() {
        return dragonBuffUpgrade;
    }
}
