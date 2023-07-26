package net.jandie1505.bedwars.game.team;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.game.team.traps.BedwarsTrap;
import net.jandie1505.bedwars.lobby.setup.LobbyTeamData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;

import java.util.*;

public class BedwarsTeam {
    private final Game game;
    private final String name;
    private final ChatColor chatColor;
    private final Color color;
    private final Location baseCenter;
    private final int baseRadius;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;
    private final List<Location> shopVillagerLocations;
    private final List<Location> upgradesVillagerLocations;
    private final BedwarsTrap[] primaryTraps;
    private final BedwarsTrap[] secondaryTraps;
    private int attackDamageUpgrade;
    private int protectionUpgrade;
    private int hasteUpgrade;
    private int forgeUpgrade;
    private int healPoolUpgrade;
    private int endgameBuffUpgrade;
    private boolean disableBed;

    public BedwarsTeam(Game game, LobbyTeamData teamData) {
        this.game = game;
        this.name = teamData.getName();
        this.chatColor = teamData.getChatColor();
        this.color = teamData.getColor();
        this.baseCenter = this.game.buildLocationWithWorld(teamData.getBaseCenter());
        this.baseRadius = teamData.getBaseRadius();

        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getSpawnpoints())) {
            this.spawnpoints.add(this.game.buildLocationWithWorld(location));
        }

        this.bedLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getBedLocations())) {
            this.bedLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.shopVillagerLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getShopVillagerLocations())) {
            this.shopVillagerLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.upgradesVillagerLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getUpgradesVillagerLocations())) {
            this.upgradesVillagerLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.primaryTraps = new BedwarsTrap[2];
        this.secondaryTraps = new BedwarsTrap[2];

        this.attackDamageUpgrade = 0;
        this.protectionUpgrade = 0;
        this.hasteUpgrade = 0;
        this.forgeUpgrade = 0;
        this.healPoolUpgrade = 0;
        this.endgameBuffUpgrade = 0;

        this.disableBed = false;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
        return color;
    }

    public Location getBaseCenter() {
        return baseCenter;
    }

    public int getBaseRadius() {
        return baseRadius;
    }

    public List<Location> getSpawnpoints() {
        return List.copyOf(this.spawnpoints);
    }

    public Location getRandomSpawnpoint() {

        if (this.spawnpoints.isEmpty()) {
            return null;
        }

        return this.spawnpoints.get(new Random().nextInt(this.spawnpoints.size()));
    }

    public List<Location> getBedLocations() {
        return List.copyOf(this.bedLocations);
    }

    public List<Location> getShopVillagerLocations() {
        return List.copyOf(this.shopVillagerLocations);
    }

    public List<Location> getUpgradesVillagerLocations() {
        return List.copyOf(this.upgradesVillagerLocations);
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

        for (Location bedLocation : this.getBedLocations()) {

            if (bedLocation.getBlock().getBlockData() instanceof Bed) {
                beds++;
            }

        }

        return beds;
    }

    public boolean isAlive() {
        return this.getPlayers().size() > 0;
    }

    public Game getGame() {
        return game;
    }

    public int getAttackDamageUpgrade() {
        return attackDamageUpgrade;
    }

    public void setAttackDamageUpgrade(int attackDamageUpgrade) {
        this.attackDamageUpgrade = attackDamageUpgrade;
    }

    public int getProtectionUpgrade() {
        return protectionUpgrade;
    }

    public void setProtectionUpgrade(int protectionUpgrade) {
        this.protectionUpgrade = protectionUpgrade;
    }

    public int getHasteUpgrade() {
        return hasteUpgrade;
    }

    public void setHasteUpgrade(int hasteUpgrade) {
        this.hasteUpgrade = hasteUpgrade;
    }

    public int getForgeUpgrade() {
        return forgeUpgrade;
    }

    public void setForgeUpgrade(int forgeUpgrade) {
        this.forgeUpgrade = forgeUpgrade;
    }

    public int getHealPoolUpgrade() {
        return healPoolUpgrade;
    }

    public void setHealPoolUpgrade(int healPoolUpgrade) {
        this.healPoolUpgrade = healPoolUpgrade;
    }

    public int getEndgameBuffUpgrade() {
        return endgameBuffUpgrade;
    }

    public void setEndgameBuffUpgrade(int endgameBuffUpgrade) {
        this.endgameBuffUpgrade = endgameBuffUpgrade;
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
            this.attackDamageUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            this.protectionUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            this.hasteUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            this.forgeUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            this.healPoolUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getEndgameBuffUpgrade()) {
            this.endgameBuffUpgrade = value;
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
        for (Location location : this.getBedLocations()) {
            this.game.getWorld().getBlockAt(location).setType(Material.AIR);
        }
    }

    public boolean isBedDisabled() {
        return this.disableBed;
    }

    public void disableBed() {
        this.disableBed = true;
    }

    public void enableBed() {
        this.disableBed = false;
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
