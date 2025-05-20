package net.jandie1505.bedwars.game.game.team;

public class TeamUpgradesConfig {
    private final TeamUpgrade sharpnessUpgrade;
    private final TeamUpgrade protectionUpgrade;
    private final TeamUpgrade hasteUpgrade;
    private final TeamUpgrade forgeUpgrade;
    private final TeamUpgrade healPoolUpgrade;
    private final TeamUpgrade endgameBuffUpgrade;
    private final int noTrap;
    private final int alarmTrap;
    private final int itsATrap;
    private final int miningFatigueTrap;
    private final int countermeasuresTrap;

    public TeamUpgradesConfig(TeamUpgrade sharpnessUpgrade, TeamUpgrade protectionUpgrade, TeamUpgrade hasteUpgrade, TeamUpgrade forgeUpgrade, TeamUpgrade healPoolUpgrade, TeamUpgrade endgameBuffUpgrade, int noTrap, int alarmTrap, int itsATrap, int miningFatigueTrap, int countermeasuresTrap) {
        this.sharpnessUpgrade = sharpnessUpgrade;
        this.protectionUpgrade = protectionUpgrade;
        this.hasteUpgrade = hasteUpgrade;
        this.forgeUpgrade = forgeUpgrade;
        this.healPoolUpgrade = healPoolUpgrade;
        this.endgameBuffUpgrade = endgameBuffUpgrade;
        this.noTrap = noTrap;
        this.alarmTrap = alarmTrap;
        this.itsATrap = itsATrap;
        this.miningFatigueTrap = miningFatigueTrap;
        this.countermeasuresTrap = countermeasuresTrap;
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

    public TeamUpgrade getEndgameBuffUpgrade() {
        return endgameBuffUpgrade;
    }

    public int getNoTrap() {
        return noTrap;
    }

    public int getAlarmTrap() {
        return alarmTrap;
    }

    public int getItsATrap() {
        return itsATrap;
    }

    public int getMiningFatigueTrap() {
        return miningFatigueTrap;
    }

    public int getCountermeasuresTrap() {
        return countermeasuresTrap;
    }
}
