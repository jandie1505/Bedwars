package net.jandie1505.bedwars.game.game.team;

import net.chaossquad.mclib.ChatCompatibilityUtils;
import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.player.data.PlayerData;
import net.jandie1505.bedwars.game.game.team.storage.ResourceStorage;
import net.jandie1505.bedwars.game.game.team.upgrades.constants.TeamUpgrades;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
    @NotNull private final LinkedList<TrapSlot> traps;

    private boolean disableBed;

    @Nullable private Inventory teamChest;
    @Nullable private ResourceStorage resourceStorage;

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
        this.traps = new LinkedList<>();

        this.upgrades.put(TeamUpgrades.SHARPNESS, 0);
        this.upgrades.put(TeamUpgrades.PROTECTION, 0);
        this.upgrades.put(TeamUpgrades.HASTE, 0);
        this.upgrades.put(TeamUpgrades.GENERATORS, 0);
        this.upgrades.put(TeamUpgrades.HEAL_POOL, 0);
        this.upgrades.put(TeamUpgrades.ENDGAME_BUFF, 0);

        this.disableBed = false;
        this.teamChest = null;
        this.resourceStorage = null;
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

    /**
     * Returns true if the players of the team can respawn.
     * @return can respawn
     */
    public boolean canRespawn() {
        return this.getAvailableBedsCount() > 0;
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

    // ----- TRAPS -----

    /**
     * Returns the trap list the team currently has.
     * @return trap list
     */
    public @NotNull List<TrapSlot> getTraps() {
        return this.traps;
    }

    /**
     * Gets the traps from the specified slot.
     * @param slot slot id
     * @return trap slot entry
     */
    public @Nullable TrapSlot getTrap(int slot) {
        if (slot < 0 || slot >= this.traps.size()) return null;
        return this.traps.get(slot);
    }

    /**
     * Gets and removes the current trap from the list.
     * @return trap or null if the team has no traps
     */
    public @Nullable TrapSlot pullTrap() {
        return this.traps.removeFirst();
    }

    /**
     * Returns true if the team has traps.
     * @return traps
     */
    public boolean hasTraps() {
        return !this.traps.isEmpty();
    }

    // ----- TEAM CHEST -----

    /**
     * Returns the current team chest.<br/>
     * Can be null if the team does not have a team chest.
     * @return team chest inventory
     */
    public @Nullable Inventory getTeamChest() {
        return this.teamChest;
    }

    /**
     * Returns true if the team has a team chest.
     * @return team has team chest
     */
    public boolean hasTeamChest() {
        return this.teamChest != null;
    }

    /**
     * Returns the level of the team chest.<br/>
     * If the value is 0, there is no team chest.
     * @return level
     */
    public int getTeamChestLevel() {
        if (this.teamChest == null) return 0;
        double teamChestSize = this.teamChest.getSize();
        return (int) Math.ceil(teamChestSize / 9.0);
    }

    /**
     * Sets the team chest level.<br/>
     * One level is one row in the inventory.<br/>
     * The maximum level is 6. Setting a higher value will also set the level to 6.<br/>
     * To remove the team chest, set the level to 0 or below.<br/>
     * WARNING: If the new chest size is smaller than the old one, all items of the slots higher than the new size will be cleared.
     * @param level level
     */
    public void setTeamChestLevel(int level) {

        if (level <= 0) {
            this.teamChest = null;
            return;
        }

        int slotAmount = Math.min(level * 9, 54);

        Inventory oldTeamChest = this.teamChest;
        this.teamChest = Bukkit.createInventory(null, slotAmount, Component.text("Team Chest: " + this.getName(), NamedTextColor.DARK_GRAY, TextDecoration.BOLD));

        if (oldTeamChest != null) {
            for (int slot = 0; slot < Math.min(oldTeamChest.getSize(), this.teamChest.getSize()); slot++) {
                this.teamChest.setItem(slot, oldTeamChest.getItem(slot));
            }
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

    // ----- INNER CLASSES -----

    public record TrapSlot(@Nullable String first, @Nullable String second) {

        public @NotNull List<String> values() {
            List<String> values = new ArrayList<>();
            if (first != null) values.add(first);
            if (second != null) values.add(second);
            return values;
        }

        public @Nullable String get(int slot) {
            return switch (slot) {
                case 0 -> this.first;
                case 1 -> this.second;
                default -> null;
            };
        }

        public @NotNull TrapSlot modify(int slot, @NotNull String value) {
            return switch (slot) {
                case 0 -> new TrapSlot(value, this.second);
                case 1 -> new TrapSlot(this.first, value);
                default -> new TrapSlot(this.first, this.second);
            };
        }

        public boolean closed() {
            return this.values().isEmpty();
        }

        public @NotNull TrapSlot clone() {
            return new TrapSlot(this.first, this.second);
        }

    }

}
