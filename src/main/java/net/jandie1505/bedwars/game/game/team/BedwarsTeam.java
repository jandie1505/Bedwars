package net.jandie1505.bedwars.game.game.team;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.traps.BedwarsTrap;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BedwarsTeam {
    @NotNull private final Game game;

    @NotNull private final String name;
    @NotNull private final NamedTextColor chatColor;
    @NotNull private final Color color;
    @NotNull private final Location baseCenter;
    private final int baseRadius;
    @NotNull private final List<Location> spawnpoints;
    @NotNull private final List<Location> bedLocations;

    @NotNull private final Map<String, Integer> upgrades;
    private final BedwarsTrap[] primaryTraps;
    private final BedwarsTrap[] secondaryTraps;
    private boolean disableBed;

    public BedwarsTeam(@NotNull Game game, @NotNull TeamData data) {
        this.game = game;

        this.name = data.name();
        NamedTextColor chatColor = ChatCompatibilityUtils.getTextColorFromChatColor(data.chatColor());
        this.chatColor = chatColor != null ? chatColor : NamedTextColor.BLACK;
        this.color = data.color();
        this.baseCenter = WorldUtils.locationWithWorld(data.baseCenter(), this.game.getWorld());
        this.baseRadius = data.baseRadius();
        this.spawnpoints = data.spawnpoints().stream().map(location -> WorldUtils.locationWithWorld(location, this.game.getWorld())).toList();
        this.bedLocations = data.bedLocations().stream().map(location -> WorldUtils.locationWithWorld(location, this.game.getWorld())).toList();

        this.upgrades = new HashMap<>();

        this.primaryTraps = new BedwarsTrap[2];
        this.secondaryTraps = new BedwarsTrap[2];

        this.upgrades.put(TeamUpgrades.SHARPNESS, 0);
        this.upgrades.put(TeamUpgrades.PROTECTION, 0);
        this.upgrades.put(TeamUpgrades.HASTE, 0);
        this.upgrades.put(TeamUpgrades.GENERATORS, 0);
        this.upgrades.put(TeamUpgrades.HEAL_POOL, 0);
        this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, 0);

        this.disableBed = false;
    }

    // ----- SPAWNING -----

    /**
     * Returns a random spawnpoint of the team.
     * @return random spawnpoint
     */
    public Location getRandomSpawnpoint() {
        List<Location> spawnpoints = this.getSpawnpoints();

        if (spawnpoints.isEmpty()) {
            return null;
        }

        return spawnpoints.get(new Random().nextInt(spawnpoints.size()));
    }

    public int getId() {
        return this.game.getTeams().indexOf(this);
    }

    // ----- PLAYERS -----

    public boolean isMember(@Nullable OfflinePlayer player) {
        if (player == null) return false;
        return this.isMember(player.getUniqueId());
    }

    /**
     * Returns true if the specified player is member of this team.
     * @param playerId player uuid
     * @return is member
     */
    public boolean isMember(@Nullable UUID playerId) {
        if (playerId == null) return false;
        PlayerData playerData = this.game.getPlayerData(playerId);
        if (playerData == null) return false;
        return playerData.getTeam() == this.getId();
    }

    /**
     * Returns a set of all online team members.
     * @return set of online team members
     */
    public @NotNull Set<Player> getOnlineMembers() {
        return this.getMemberUUIDs().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a set of the uuids of all members.
     * @return uuids of all members of the team
     */
    public @NotNull Set<UUID> getMemberUUIDs() {
        return this.game.getPlayerDataMap().entrySet().stream()
                .filter(entry -> entry.getValue().getTeam() == this.getId())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Returns an immutable list of the player uuids of the players which are in this team.
     * @return immutable list of player uuids
     * @deprecated Use {@link #getMemberUUIDs()} and {@link #getOnlineMembers()}
     */
    @Deprecated(forRemoval = true)
    public List<UUID> getPlayers() {
        return List.copyOf(this.getMemberUUIDs());
    }

    // ----- BED MANAGEMENT -----

    /**
     * Returns a list of all locations of currently available beds of the team.
     * @return list of available beds of the team
     */
    public @NotNull List<Location> getAvailableBeds() {
        if (this.disableBed) return new ArrayList<>();

        List<Location> beds = new ArrayList<>();
        for (Location bedLocation : this.getBedLocations()) {

            if (this.game.getWorld().getBlockAt(bedLocation).getBlockData() instanceof Bed) {
                beds.add(bedLocation);
            }

        }

        return beds;
    }

    /**
     * Returns the amount of currently available beds of the team.
     * @return available beds
     */
    public int getAvailableBedsCount() {
        return this.getAvailableBeds().size();
    }

    /**
     * Returns the amount of currently available beds of the team.
     * @return available beds
     * @deprecated Use {@link #getAvailableBedsCount()}
     */
    @Deprecated(forRemoval = true)
    public int hasBed() {
        return this.getAvailableBedsCount();
    }

    /**
     * Shortcut to disable all available beds of the team.<br/>
     * You can also loop over all {@link #getAvailableBeds()} and set air there.
     */
    public void destroyBeds() {
        for (Location location : this.getAvailableBeds()) {
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

    /**
     * Disables the bed.
     * @deprecated Use {@link #setBedDisabled(boolean)}
     */
    @Deprecated(forRemoval = true)
    public void disableBed() {
        this.setBedDisabled(true);
    }

    // ----- ALIVE STATUS -----

    /**
     * Returns if the team is alive.
     * A team is alive, when it has one or more players, has a bed or at minimum one player that is still alive when the team has no bed anymore.
     * @return alive
     */
    public boolean isAlive() {
        Set<UUID> players = this.getMemberUUIDs();
        if (players.isEmpty()) return false;

        if (this.getAvailableBedsCount() > 0) return true;

        for (UUID playerId : players) {
            PlayerData playerData = this.game.getPlayerData(playerId);
            if (playerData == null) continue;
            if (playerData.isAlive()) return true;
        }

        return false;
    }

    // ----- UPGRADES -----

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
        return this.upgrades.getOrDefault(TeamUpgrades.SHARPNESS, 0);
    }

    @Deprecated(forRemoval = true)
    public void setAttackDamageUpgrade(int attackDamageUpgrade) {
        this.upgrades.put(TeamUpgrades.SHARPNESS, attackDamageUpgrade);
    }

    @Deprecated(forRemoval = true)
    public int getProtectionUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.PROTECTION, 0);
    }

    @Deprecated(forRemoval = true)
    public void setProtectionUpgrade(int protectionUpgrade) {
        this.upgrades.put(TeamUpgrades.PROTECTION, protectionUpgrade);
    }

    @Deprecated(forRemoval = true)
    public int getHasteUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.HASTE, 0);
    }

    @Deprecated(forRemoval = true)
    public void setHasteUpgrade(int hasteUpgrade) {
        this.upgrades.put(TeamUpgrades.HASTE, hasteUpgrade);
    }

    @Deprecated(forRemoval = true)
    public int getForgeUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.GENERATORS, 0);
    }

    @Deprecated(forRemoval = true)
    public void setForgeUpgrade(int forgeUpgrade) {
        this.upgrades.put(TeamUpgrades.GENERATORS, forgeUpgrade);
    }

    @Deprecated(forRemoval = true)
    public int getHealPoolUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.HEAL_POOL, 0);
    }

    @Deprecated(forRemoval = true)
    public void setHealPoolUpgrade(int healPoolUpgrade) {
        this.upgrades.put(TeamUpgrades.HEAL_POOL, healPoolUpgrade);
    }

    @Deprecated(forRemoval = true)
    public int getEndgameBuffUpgrade() {
        return this.upgrades.getOrDefault(TeamUpgrades.ENDGAME_BUFF, 0);
    }

    @Deprecated(forRemoval = true)
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
            this.upgrades.put(TeamUpgrades.SHARPNESS, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            this.upgrades.put(TeamUpgrades.PROTECTION, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            this.upgrades.put(TeamUpgrades.HASTE, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            this.upgrades.put(TeamUpgrades.GENERATORS, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            this.upgrades.put(TeamUpgrades.HEAL_POOL, value);
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getEndgameBuffUpgrade()) {
            this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, value);
        }

    }

    // ----- TRAPS -----

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

    // ----- INFO -----

    /**
     * Returns the game the team belongs to.
     * @return game
     */
    public @NotNull Game getGame() {
        return game;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull NamedTextColor getChatColor() {
        return this.chatColor;
    }

    public @NotNull Color getColor() {
        return this.color;
    }

    public @NotNull Location getBaseCenter() {
        return this.baseCenter.clone();
    }

    public int getBaseRadius() {
        return this.baseRadius;
    }

    public @NotNull List<Location> getSpawnpoints() {
        return this.spawnpoints.stream().map(Location::clone).toList();
    }

    public @NotNull List<Location> getBedLocations() {
        return this.bedLocations.stream().map(Location::clone).toList();
    }

    // ----- CHAT -----

    /**
     * Returns the formatted name as a Component.
     * @return formatted name
     */
    public Component getFormattedName() {
        return Component.text(this.name, this.chatColor);
    }

    // ----- STATIC -----

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
