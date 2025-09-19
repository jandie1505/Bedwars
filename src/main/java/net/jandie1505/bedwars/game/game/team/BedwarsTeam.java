package net.jandie1505.bedwars.game.game.team;

import net.chaossquad.mclib.immutables.ImmutableLocation;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.traps.BedwarsTrap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BedwarsTeam {
    @NotNull private final Game game;
    @NotNull private final TeamData data;
    @NotNull private final Map<String, Integer> upgrades;
    private final BedwarsTrap[] primaryTraps;
    private final BedwarsTrap[] secondaryTraps;
    private boolean disableBed;

    public BedwarsTeam(@NotNull Game game, @NotNull TeamData data) {
        this.game = game;
        this.data = data;
        this.upgrades = new HashMap<>();

        this.primaryTraps = new BedwarsTrap[2];
        this.secondaryTraps = new BedwarsTrap[2];

        this.upgrades.put(TeamUpgrades.ATTACK_DAMAGE, 0);
        this.upgrades.put(TeamUpgrades.PROTECTION, 0);
        this.upgrades.put(TeamUpgrades.HASTE, 0);
        this.upgrades.put(TeamUpgrades.FORGE, 0);
        this.upgrades.put(TeamUpgrades.HEAL_POOL, 0);
        this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, 0);

        this.disableBed = false;
    }

    public Location getRandomSpawnpoint() {
        List<ImmutableLocation> spawnpoints = this.data.spawnpoints();

        if (spawnpoints.isEmpty()) {
            return null;
        }

        return spawnpoints.get(new Random().nextInt(spawnpoints.size()));
    }

    public int getId() {
        return this.game.getTeams().indexOf(this);
    }

    public List<UUID> getPlayers() {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : this.game.getPlayers().keySet()) {
            PlayerData playerData = this.game.getPlayers().get(playerId);

            if (playerData.getTeam() == this.getId()) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    public int hasBed() {

        if (this.disableBed) {
            return 0;
        }

        int beds = 0;

        for (Location bedLocation : this.data.bedLocations()) {

            if (this.game.getWorld().getBlockAt(bedLocation).getBlockData() instanceof Bed) {
                beds++;
            }

        }

        return beds;
    }

    /**
     * Returns if the team is alive.
     * A team is alive, when it has one or more players, has a bed or at minimum one player that is still alive when the team has no bed anymore.
     * @return
     */
    public boolean isAlive() {
        List<UUID> players = this.getPlayers();
        if (players.isEmpty()) return false;

        if (this.hasBed() > 0) {
            return true;
        } else {

            for (UUID playerId : players) {
                PlayerData playerData = this.game.getPlayer(playerId);
                if (playerData == null) continue;
                if (playerData.isAlive()) return true;
            }

            return false;
        }

    }

    public Game getGame() {
        return game;
    }

    public TeamData getData() {
        return data;
    }

    /**
     * Returns an unmodifiable map of the current team upgrades.
     * @return map of team upgrades
     */
    public @NotNull Map<String, Integer> getUpgrades() {
        return Map.copyOf(this.upgrades);
    }

    /**
     * Gets the level of a specific team upgrade.<br/>
     * Returns 0 if the upgrade is not set.
     * @param upgradeId upgrade id
     * @return upgrade level
     */
    public int getUpgrade(@NotNull String upgradeId) {
        return this.upgrades.getOrDefault(upgradeId, 0);
    }

    /**
     * Sets the level of a specific team upgrade.<br/>
     * An upgrade can be removed by setting a negative value.
     * @param upgradeId upgrade id
     * @param upgrade new upgrade level (negative value to clear)
     */
    public void setUpgrade(@NotNull String upgradeId, int upgrade) {

        if (upgrade < 0) {
            this.upgrades.remove(upgradeId);
            return;
        }

        this.upgrades.put(upgradeId, upgrade);
    }

    public int getAttackDamageUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.ATTACK_DAMAGE, 0);
    }

    @Deprecated
    public void setAttackDamageUpgrade(int attackDamageUpgrade) {
        this.upgrades.put(TeamUpgrades.ATTACK_DAMAGE, attackDamageUpgrade);
    }

    @Deprecated
    public int getProtectionUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.PROTECTION, 0);
    }

    @Deprecated
    public void setProtectionUpgrade(int protectionUpgrade) {
        this.upgrades.put(TeamUpgrades.PROTECTION, protectionUpgrade);
    }

    @Deprecated
    public int getHasteUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.HASTE, 0);
    }

    @Deprecated
    public void setHasteUpgrade(int hasteUpgrade) {
        this.upgrades.put(TeamUpgrades.HASTE, hasteUpgrade);
    }

    @Deprecated
    public int getForgeUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.FORGE, 0);
    }

    @Deprecated
    public void setForgeUpgrade(int forgeUpgrade) {
        this.upgrades.put(TeamUpgrades.FORGE, forgeUpgrade);
    }

    @Deprecated
    public int getHealPoolUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.HEAL_POOL, 0);
    }

    @Deprecated
    public void setHealPoolUpgrade(int healPoolUpgrade) {
        this.upgrades.put(TeamUpgrades.HEAL_POOL, healPoolUpgrade);
    }

    @Deprecated
    public int getEndgameBuffUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.ENDGAME_BUFF, 0);
    }

    @Deprecated
    public void setEndgameBuffUpgrade(int endgameBuffUpgrade) {
        this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, endgameBuffUpgrade);
    }

    public int getTeamUpgrade(TeamUpgrade teamUpgrade) {

        if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getSharpnessUpgrade()) {
            return this.getAttackDamageUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            return this.getProtectionUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            return this.getHasteUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            return this.getForgeUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            return this.getHealPoolUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getEndgameBuffUpgrade()) {
            return this.getEndgameBuffUpgrade();
        } else {
            return -1;
        }

    }

    public void setTeamUpgrade(TeamUpgrade teamUpgrade, int value) {

        if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getSharpnessUpgrade()) {
            this.upgrades.put(TeamUpgrades.ATTACK_DAMAGE, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            this.upgrades.put(TeamUpgrades.PROTECTION, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            this.upgrades.put(TeamUpgrades.HASTE, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            this.upgrades.put(TeamUpgrades.FORGE, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            this.upgrades.put(TeamUpgrades.HEAL_POOL, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getEndgameBuffUpgrade()) {
            this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, value);
        }

    }

    public BedwarsTrap[] getPrimaryTraps() {
        return this.primaryTraps;
    }

    public BedwarsTrap[] getSecondaryTraps() {
        return this.secondaryTraps;
    }

    public boolean hasPrimaryTraps() {

        for (int i = 0; i < this.primaryTraps.length; i++) {

            if (this.primaryTraps[i] != null) {
                return true;
            }

        }

        return false;
    }

    public void shiftTraps() {

        if (this.primaryTraps.length < this.secondaryTraps.length) {
            return;
        }

        if (this.hasPrimaryTraps()) {
            return;
        }

        for (int i = 0; i < this.secondaryTraps.length; i++) {

            this.primaryTraps[i] = this.secondaryTraps[i];
            this.secondaryTraps[i] = null;

        }

    }

    public void destroyBeds() {
        for (Location location : this.data.bedLocations()) {
            this.game.getWorld().getBlockAt(location).setType(Material.AIR);
        }
    }

    /**
     * Returns if the bed is disabled (counts as destroyed even if it is there).
     * @return bed disabled
     */
    public boolean isBedDisabled() {
        return this.disableBed;
    }

    /**
     * Sets if the bed of the team should be disabled (counts as destroyed even if it is there).
     * @param bedDisabled bed disabled
     */
    public void setBedDisabled(boolean bedDisabled) {
        this.disableBed = bedDisabled;
    }

    @Deprecated
    public void disableBed() {
        this.setBedDisabled(true);
    }

    public static int getTrapsCount(BedwarsTrap[] bedwarsTraps) {

        int count = 0;

        for (int i = 0; i < bedwarsTraps.length; i++) {

            if (bedwarsTraps[i] != null) {
                count++;
            }

        }

        return count;
    }

    public static void addTrap(BedwarsTrap[] bedwarsTraps, BedwarsTrap trap) {

        for (int i = 0; i < bedwarsTraps.length; i++) {

            if (bedwarsTraps[i] == null) {
                bedwarsTraps[i] = trap;
                break;
            }

        }

    }
}
