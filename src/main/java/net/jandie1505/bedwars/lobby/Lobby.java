package net.jandie1505.bedwars.lobby;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import net.jandie1505.bedwars.Bedwars;
import net.jandie1505.bedwars.GamePart;
import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.shop.ArmorConfig;
import net.jandie1505.bedwars.game.team.BedwarsTeam;
import net.jandie1505.bedwars.game.team.TeamUpgrade;
import net.jandie1505.bedwars.game.team.TeamUpgradesConfig;
import net.jandie1505.bedwars.lobby.setup.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Lobby extends GamePart {
    private final List<MapData> maps;
    private final Map<UUID, LobbyPlayerData> players;
    private final int mapVoteButtonItemId;
    private final int teamSelectionButtonItemId;
    private final int mapButtonItemId;
    private final List<UUID> loginBypassList;
    private int timeStep;
    private int time;
    private boolean forcestart;
    private MapData selectedMap;
    private boolean mapVoting;
    private int requiredPlayers;
    private boolean timerPaused;
    private boolean lobbyBorderEnabled;
    private int[] lobbyBorder;
    private Location lobbySpawn;

    public Lobby(Bedwars plugin) {
        super(plugin);
        this.maps = new ArrayList<>();
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.mapVoteButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("mapVoteButton", -1);
        this.teamSelectionButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("teamSelectionButton", -1);
        this.mapButtonItemId = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("mapButton", -1);
        this.loginBypassList = Collections.synchronizedList(new ArrayList<>());
        this.timeStep = 0;
        this.time = 120;
        this.forcestart = false;
        this.selectedMap = null;
        this.mapVoting = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("mapVoting", false);
        this.requiredPlayers = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("requiredPlayers", 2);
        this.timerPaused = false;
        this.lobbyBorderEnabled = this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optBoolean("enable", false);
        this.lobbyBorder = new int[]{
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z1", -10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x2", 10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y2", 10),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z2", 10)
        };
        this.lobbySpawn = new Location(
                this.getPlugin().getServer().getWorlds().get(0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("x", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("y", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("z", 0),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("yaw", 0.0F),
                this.getPlugin().getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("pitch", 0.0F)
        );

        JSONArray mapArray = this.getPlugin().getMapConfig().getConfig().optJSONArray("maps");

        if (mapArray == null) {
            mapArray = new JSONArray();
        }

        int index = -1;
        for (Object object : mapArray) {
            index++;

            if (!(object instanceof JSONObject)) {
                this.getPlugin().getLogger().warning("Map Config: Index " + index + " is not a json object");
                continue;
            }

            JSONObject map = (JSONObject) object;

            String name = map.optString("name");

            if (name == null) {
                this.getPlugin().getLogger().warning("Map Config: Missing name of map with index " + index);
                continue;
            }

            String world = map.optString("world");

            if (world == null) {
                this.logMissingMapConfigItem("world", index, name);
                continue;
            }

            int respawnCooldown = map.optInt("respawnCooldown", -1);

            if (respawnCooldown < 0) {
                this.logMissingMapConfigItem("respawnCooldown", index, name);
                continue;
            }

            int maxTime = map.optInt("maxTime", -1);

            if (maxTime < 0) {
                this.logMissingMapConfigItem("maxTime", index, name);
                continue;
            }

            int spawnBlockPlaceProtectionRadius = map.optInt("spawnBlockPlaceProtectionRadius", -1);

            if (spawnBlockPlaceProtectionRadius < 0) {
                this.logMissingMapConfigItem("spawnBlockPlaceProtectionRadius", index, name);
                continue;
            }

            int villagerBlockPlaceProtectionRadius = map.optInt("villagerBlockPlaceProtectionRadius", -1);

            if (villagerBlockPlaceProtectionRadius < 0) {
                this.logMissingMapConfigItem("villagerBlockPlaceProtectionRadius", index, name);
                continue;
            }

            int mapRadius = map.optInt("mapRadius", -1);

            if (mapRadius < 0) {
                this.logMissingMapConfigItem("mapRadius", index, name);
                continue;
            }

            Location centerLocation = this.buildLocationFromJSONObject(map.optJSONObject("center", new JSONObject()), false);

            if (centerLocation == null) {
                this.logMissingMapConfigItem("center", index, name);
                continue;
            }

            List<LobbyTeamData> teams = new ArrayList<>();
            JSONArray teamsArray = map.optJSONArray("teams");

            if (teamsArray == null) {
                this.logMissingMapConfigItem("teams", index, name);
                continue;
            }

            for (Object object2 : teamsArray) {

                if (!(object2 instanceof JSONObject)) {
                    this.getPlugin().getLogger().warning("Map Config: A team of map " + name + " (" + index + ") is not a json object");
                    continue;
                }

                JSONObject team = (JSONObject) object2;

                String teamName = team.optString("name");

                if (teamName == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing name of a team of " + name + " (" + index + ")");
                    continue;
                }

                Color teamColor;

                try {
                    teamColor = Color.fromRGB(team.optInt("color", -1));
                } catch (IllegalArgumentException e) {
                    this.getPlugin().getLogger().warning("Map Config: Missing/Wrong color of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                String teamChatColorString = team.optString("chatColor");

                if (teamChatColorString == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing chatColor of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                ChatColor teamChatColor = ChatColor.valueOf(teamChatColorString);

                if (teamChatColor == null) {
                    this.getPlugin().getLogger().warning("Map Config: Wrong chatColor of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                Location baseCenter = buildLocationFromJSONObject(team.optJSONObject("baseCenter", new JSONObject()), false);

                if (baseCenter == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing baseCenter of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                int baseRadius = team.optInt("baseRadius", -1);

                if (baseRadius < 0) {
                    this.getPlugin().getLogger().warning("Map Config: Missing baseRadius of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                JSONArray teamSpawnpointArray = team.optJSONArray("spawnpoints");

                if (teamSpawnpointArray == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing spawnpoints of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> teamSpawnpoints = this.buildLocationList(teamSpawnpointArray, true);

                JSONArray bedLocationsArray = team.optJSONArray("bedLocations");

                if (bedLocationsArray == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing spawnpoints of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> teamBedLocations = this.buildLocationList(bedLocationsArray, false);

                JSONArray teamGeneratorsArray = team.optJSONArray("generators");

                if (teamGeneratorsArray == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing teamGenerators of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<LobbyGeneratorData> teamGenerators = this.buildGeneratorList(teamGeneratorsArray);

                JSONArray shopVillagerLocationArray = team.optJSONArray("shopVillagers");

                if (shopVillagerLocationArray == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing shopVillagers of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> shopVillagerLocations = this.buildLocationList(shopVillagerLocationArray, true);

                JSONArray upgradeVillagerLocationArray = team.optJSONArray("upgradeVillagers");

                if (upgradeVillagerLocationArray == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing upgradeVillagers of team " + teamName + " of map " + name + " (" + index + ")");
                    continue;
                }

                List<Location> upgradeVillagerLocations = this.buildLocationList(upgradeVillagerLocationArray, true);

                teams.add(new LobbyTeamData(teamName, teamChatColor, teamColor, teamSpawnpoints, baseCenter, baseRadius, teamBedLocations, teamGenerators, shopVillagerLocations, upgradeVillagerLocations));
            }

            JSONArray globalGeneratorArray = map.optJSONArray("globalGenerators");

            if (globalGeneratorArray == null) {
                this.logMissingMapConfigItem("teams", index, name);
                continue;
            }

            List<LobbyGeneratorData> globalGenerators = this.buildGeneratorList(globalGeneratorArray);

            List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions = new ArrayList<>();
            List<LobbyDestroyBedsTimeActionData> destroyBedsTimeActions = new ArrayList<>();
            List<LobbyWorldborderChangeTimeActionData> worldborderChangeTimeActions = new ArrayList<>();
            JSONArray timeActionArray = map.optJSONArray("timeActions");

            if (timeActionArray == null) {
                this.logMissingMapConfigItem("timeActions", index, name);
                continue;
            }

            for (Object object2 : timeActionArray) {

                if (!(object2 instanceof JSONObject)) {
                    this.getPlugin().getLogger().warning("Map Config: A timeAction of map " + name + " (" + index + ") is not a json object");
                    continue;
                }

                JSONObject timeActionData = (JSONObject) object2;

                String type = timeActionData.optString("type");

                if (type == null) {
                    this.getPlugin().getLogger().warning("Map Config: Missing type of a timeAction of " + name + " (" + index + ")");
                    continue;
                }

                int time = timeActionData.optInt("time", -1);

                if (time < 0) {
                    this.getPlugin().getLogger().warning("Map Config: Missing time of a timeAction of " + name + " (" + index + ")");
                    continue;
                }

                switch (type) {
                    case "GENERATOR_UPGRADE":

                        int generatorLevel = timeActionData.optInt("generatorLevel", -1);

                        if (generatorLevel < 0) {
                            this.getPlugin().getLogger().warning("Map Config: Wrong generatorLevel of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        int generatorType = timeActionData.optInt("generatorType", -1);

                        switch (generatorType) {
                            case 1:
                                generatorUpgradeTimeActions.add(new LobbyGeneratorUpgradeTimeActionData(1, generatorLevel, time));
                                continue;
                            case 2:
                                generatorUpgradeTimeActions.add(new LobbyGeneratorUpgradeTimeActionData(2, generatorLevel, time));
                                continue;
                            default:
                                this.getPlugin().getLogger().warning("Map Config: Wrong generatorType of a timeAction of " + name + " (" + index + ")");
                                break;
                        }

                        break;
                    case "DESTROY_BEDS":
                        destroyBedsTimeActions.add(new LobbyDestroyBedsTimeActionData(time, timeActionData.optBoolean("disableBeds", false)));
                        break;
                    case "WORLDBORDER_CHANGE":

                        int radius = timeActionData.optInt("radius", -1);

                        if (radius < 0) {
                            this.getPlugin().getLogger().warning("Map Config: Wrong radius of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        String chatMessage = timeActionData.optString("chatMessage");

                        if (chatMessage == null) {
                            this.getPlugin().getLogger().warning("Map Config: Wrong chatMessage of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        String scoreboardText = timeActionData.optString("scoreboardText");

                        if (scoreboardText == null) {
                            this.getPlugin().getLogger().warning("Map Config: Wrong scoreboardText of a timeAction of " + name + " (" + index + ")");
                            continue;
                        }

                        worldborderChangeTimeActions.add(new LobbyWorldborderChangeTimeActionData(time, radius, chatMessage, scoreboardText));
                        break;
                    default:
                        this.getPlugin().getLogger().warning("Map Config: Wrong type of a timeAction of " + name + " (" + index + ")");
                        continue;
                }

            }

            this.maps.add(new MapData(
                    name,
                    world,
                    respawnCooldown,
                    maxTime,
                    teams,
                    globalGenerators,
                    generatorUpgradeTimeActions,
                    destroyBedsTimeActions,
                    worldborderChangeTimeActions,
                    spawnBlockPlaceProtectionRadius,
                    villagerBlockPlaceProtectionRadius,
                    centerLocation,
                    mapRadius
            ));
        }

    }

    private Location buildLocationFromJSONObject(JSONObject spawnpoint, boolean enableDirections) {

        double x = spawnpoint.optDouble("x", Double.MIN_VALUE);

        if (x == Double.MIN_VALUE) {
            this.getPlugin().getLogger().warning("Map Config: Wrong x in a spawnpoint in team");
            return null;
        }

        double y = spawnpoint.optDouble("y", Double.MIN_VALUE);

        if (y == Double.MIN_VALUE) {
            this.getPlugin().getLogger().warning("Map Config: Wrong y in a spawnpoint in team");
            return null;
        }

        double z = spawnpoint.optDouble("z", Double.MIN_VALUE);

        if (z == Double.MIN_VALUE) {
            this.getPlugin().getLogger().warning("Map Config: Wrong z in a spawnpoint in team");
            return null;
        }

        if (!enableDirections) {
            return new Location(null, x, y, z);
        }

        float yaw = spawnpoint.optFloat("yaw", Float.MIN_VALUE);

        if (yaw == Float.MIN_VALUE) {
            this.getPlugin().getLogger().warning("Map Config: Wrong yaw in a spawnpoint in team");
            return new Location(null, x, y, z);
        }

        float pitch = spawnpoint.optFloat("pitch", Float.MIN_VALUE);

        if (pitch == Float.MIN_VALUE) {
            this.getPlugin().getLogger().warning("Map Config: Wrong pitch in a spawnpoint in team");
            return new Location(null, x, y, z);
        }

        return new Location(null, x, y, z, yaw, pitch);
    }

    private List<Location> buildLocationList(JSONArray locations, boolean enableDirections) {

        List<Location> returnList = new ArrayList<>();

        for (Object locationObject : locations) {

            if (!(locationObject instanceof JSONObject)) {
                this.getPlugin().getLogger().warning("Map Config: Wrong location");
                continue;
            }

            Location location = this.buildLocationFromJSONObject((JSONObject) locationObject, enableDirections);

            if (location == null) {
                this.getPlugin().getLogger().warning("Map Config: Wrong location");
                continue;
            }

            returnList.add(location);

        }

        return returnList;
    }

    private List<LobbyGeneratorData> buildGeneratorList(JSONArray generators) {

        List<LobbyGeneratorData> returnList = new ArrayList<>();

        for (Object generatorObject : generators) {

            if (!(generatorObject instanceof JSONObject)) {
                this.getPlugin().getLogger().warning("Map Config: Wrong generator");
                continue;
            }

            JSONObject generator = (JSONObject) generatorObject;

            JSONObject locationData = generator.optJSONObject("location");

            if (locationData == null) {
                this.getPlugin().getLogger().warning("Map Config: Location missing in a generator");
                continue;
            }

            double x = locationData.optDouble("x", Double.MIN_VALUE);

            if (x == Double.MIN_VALUE) {
                this.getPlugin().getLogger().warning("Map Config: Wrong x in a generator");
                continue;
            }

            double y = locationData.optDouble("y", Double.MIN_VALUE);

            if (y == Double.MIN_VALUE) {
                this.getPlugin().getLogger().warning("Map Config: Wrong y in a generator");
                continue;
            }

            double z = locationData.optDouble("z", Double.MIN_VALUE);

            if (z == Double.MIN_VALUE) {
                this.getPlugin().getLogger().warning("Map Config: Wrong z in a generator");
                continue;
            }

            Location generatorLocation = new Location(null, x, y, z);

            Material generatorMaterial = Material.getMaterial(generator.optString("material", ""));

            if (generatorMaterial == null) {
                this.getPlugin().getLogger().warning("Map Config: Wrong material in a generator");
                continue;
            }

            List<Double> generatorUpgradeSteps = new ArrayList<>();
            JSONArray generatorUpgradeStepArray = generator.optJSONArray("speed");

            if (generatorUpgradeStepArray == null) {
                this.getPlugin().getLogger().warning("Map Config: Missing speed in a generator");
                continue;
            }

            for (int i = 0; i < generatorUpgradeStepArray.length(); i++) {

                double speed = generatorUpgradeStepArray.optDouble(i, Double.MIN_VALUE);

                if (speed == Double.MIN_VALUE) {
                    this.getPlugin().getLogger().warning("Map Config: Wrong speed in a generator");
                    continue;
                }

                generatorUpgradeSteps.add(speed);

            }

            returnList.add(new LobbyGeneratorData(generatorLocation, new ItemStack(generatorMaterial), generatorUpgradeSteps));

        }

        return returnList;

    }

    private void logMissingMapConfigItem(String missingItem, int index, String mapName) {
        this.getPlugin().getLogger().warning("Map Config: Missing " + missingItem + " of map " + mapName + " (" + index + ")");
    }

    @Override
    public boolean tick() {

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            LobbyPlayerData playerData = this.players.get(playerId);

            // Enforce game mode

            if (player.getGameMode() != GameMode.ADVENTURE && !this.getPlugin().isPlayerBypassing(playerId)) {
                player.setGameMode(GameMode.ADVENTURE);
            }

            // Set Health

            if (player.getHealth() < 20) {
                player.setHealth(20);
            }

            // Set Food

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            if (player.getSaturation() < 20) {
                player.setSaturation(20);
            }

            // Display action bar

            if (this.players.size() >= this.requiredPlayers) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + " seconds"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cNot enough players (" + this.players.size() + "/" + this.requiredPlayers + ")"));
            }

            // Messages

            if ((this.time <= 5 || (this.time % 10 == 0)) && this.players.size() >= this.requiredPlayers && this.timeStep >= 20) {
                player.sendMessage("§7The game starts in " + this.time + " seconds");
            }

            // Lobby border

            if (!this.getPlugin().isPlayerBypassing(playerId) && this.lobbyBorderEnabled) {

                Location location = player.getLocation();

                if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
                    player.teleport(this.lobbySpawn);
                }

            }

            // Scoreboard

            if (this.timeStep >= 20) {
                String mapName = "---";

                if (this.selectedMap != null) {
                    mapName = this.selectedMap.getName();
                }

                Scoreboard scoreboard = this.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("lobby", Criteria.DUMMY, "");

                objective.setDisplayName("§6§lBEDWARS");

                objective.getScore("§§§§").setScore(7);

                if (this.players.size() >= 2) {

                    objective.getScore("§bStarting in " + this.time).setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §a" + this.players.size() + " / 2").setScore(4);

                } else {

                    objective.getScore("§cNot enough players").setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §c" + this.players.size() + " / " + this.requiredPlayers).setScore(4);

                }

                objective.getScore("§§").setScore(3);
                objective.getScore("§7Map: §a" + mapName).setScore(2);

                objective.getScore("§").setScore(0);

                if (this.selectedMap != null) {
                    if (playerData.getTeam() > 0) {
                        LobbyTeamData team = this.selectedMap.getTeams().get(playerData.getTeam());

                        if (team != null) {
                            objective.getScore("§7Team: " + team.getChatColor() + team.getName()).setScore(1);
                        } else {
                            objective.getScore("§7Team: §c?").setScore(1);
                        }
                    } else {
                        objective.getScore("§7Team: §a" + "---").setScore(1);
                    }
                }

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);
            }

            // Items

            if (!this.getPlugin().isPlayerBypassing(player.getUniqueId())) {

                ItemStack lobbyVoteHotbarItem = this.getPlugin().getItemStorage().getItem(this.mapVoteButtonItemId);
                ItemStack lobbyTeamSelectionHotbarItem = this.getPlugin().getItemStorage().getItem(this.teamSelectionButtonItemId);

                if (lobbyVoteHotbarItem != null && lobbyTeamSelectionHotbarItem != null) {

                    for (ItemStack item : Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length)) {

                        if (item == null || item.getType() == Material.AIR) {
                            continue;
                        }

                        if (!item.isSimilar(lobbyVoteHotbarItem) && !item.isSimilar(lobbyTeamSelectionHotbarItem)) {
                            player.getInventory().clear();
                        }

                    }

                    if (!player.getInventory().contains(lobbyVoteHotbarItem)) {
                        player.getInventory().setItem(3, lobbyVoteHotbarItem);
                    }

                    if (!player.getInventory().contains(lobbyTeamSelectionHotbarItem)) {
                        player.getInventory().setItem(5, lobbyTeamSelectionHotbarItem);
                    }

                }

            }

        }

        // ADD PLAYERS

        for (Player player : List.copyOf(this.getPlugin().getServer().getOnlinePlayers())) {

            if (this.getPlugin().isPlayerBypassing(player.getUniqueId())) {
                continue;
            }

            if (!this.players.containsKey(player.getUniqueId())) {
                this.players.put(player.getUniqueId(), new LobbyPlayerData());
            }

        }

        // Update CloudNet motd and slots

        if (this.selectedMap != null) {
            this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), this.selectedMap.getName());
        } else {
            this.updateCloudNetMotdAndSlots(this.getMaxPlayers(), "Map Voting");
        }

        // Select Map if timer is 10 or lower

        if (this.selectedMap == null && this.time <= 60) {
            this.autoSelectMap();
        }

        // TIME

        if (this.timeStep >= 20) {
            if (this.time > 0) {

                if (this.players.size() >= this.requiredPlayers) {
                    this.time--;
                } else if (this.time < 60) {
                    this.time++;
                }

            } else {
                this.getPlugin().nextStatus();
                return true;
            }
        }

        // FORCE START

        if (this.forcestart) {
            this.getPlugin().nextStatus();
            return true;
        }

        // TIME STEP

        if (this.timeStep >= 20) {
            this.timeStep = 0;
        } else {
            this.timeStep++;
        }

        return true;
    }

    private List<MapData> getHighestVotedMaps() {

        // Get map votes

        Map<MapData, Integer> mapVotes = new HashMap<>();

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData.getVote() == null) {
                continue;
            }

            if (mapVotes.containsKey(playerData.getVote())) {
                mapVotes.put(playerData.getVote(), mapVotes.get(playerData.getVote()) + 1);
            } else {
                mapVotes.put(playerData.getVote(), 1);
            }

        }

        // Get list of maps with the highest vote count

        List<MapData> highestVotedMaps = new ArrayList<>();
        int maxVotes = Integer.MIN_VALUE;

        for (Map.Entry<MapData, Integer> entry : mapVotes.entrySet()) {
            int votes = entry.getValue();
            if (votes > maxVotes) {
                maxVotes = votes;
                highestVotedMaps.clear();
                highestVotedMaps.add(entry.getKey());
            } else if (votes == maxVotes) {
                highestVotedMaps.add(entry.getKey());
            }
        }

        return highestVotedMaps;

    }

    private void autoSelectMap() {

        MapData selectedMap = null;

        if (this.mapVoting) {

            List<MapData> highestVotedMaps = this.getHighestVotedMaps();

            if (!highestVotedMaps.isEmpty()) {

                selectedMap = highestVotedMaps.get(new Random().nextInt(highestVotedMaps.size()));

            }

        }

        if (selectedMap == null) {

            if (!this.maps.isEmpty()) {

                selectedMap = this.maps.get(new Random().nextInt(this.maps.size()));

            }

        }

        this.selectMap(selectedMap);

    }

    private void displayMap() {

        for (UUID playerId : this.getPlayers().keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            if (this.selectedMap == null) {
                return;
            }

            player.sendMessage("§bThe map has been set to " + this.selectedMap.getName());

        }

    }

    private void createPartyTeams() {

        if (this.selectedMap == null) {
            return;
        }

        if (this.getPlugin().getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("partyandfriends", false)) {

            try {
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer");
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PartyManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PlayerParty");

                List<PlayerParty> parties = new ArrayList<>();

                for (UUID playerId : this.getPlayers().keySet()) {
                    Player player = this.getPlugin().getServer().getPlayer(playerId);

                    if (player == null) {
                        continue;
                    }

                    PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(playerId);

                    if (pafPlayer == null) {
                        continue;
                    }

                    PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);

                    if (!parties.contains(party)) {
                        parties.add(party);
                    }

                }

                for (PlayerParty party : parties) {

                    int maxPlayers = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("playersPerTeam", -1);

                    if (maxPlayers > 0 && party.getAllPlayers().size() > maxPlayers) {
                        continue;
                    }

                    for (LobbyTeamData team : this.selectedMap.getTeams()) {
                        int teamId = this.selectedMap.getTeams().indexOf(team);

                        if (teamId < 0) {
                            continue;
                        }

                        int playersInTeam = party.getAllPlayers().size() + this.getTeamPlayers(teamId).size();

                        if (playersInTeam > maxPlayers) {
                            continue;
                        }

                        for (PAFPlayer pafPlayer : party.getAllPlayers()) {
                            LobbyPlayerData playerData = this.players.get(pafPlayer.getUniqueId());

                            if (playerData == null) {
                                continue;
                            }

                            playerData.setTeam(teamId);

                        }

                        break;
                    }

                }

            } catch (ClassNotFoundException ignored) {
                // ignored
            }

        }

    }

    @Override
    public GamePart getNextStatus() {

        if (this.selectedMap == null) {
            this.autoSelectMap();
        }

        if (this.selectedMap == null) {
            this.getPlugin().getLogger().warning("Game stopped because automatic map selection failed");
            return null;
        }

        MapData selectedMap = this.selectedMap;

        World world = this.getPlugin().loadWorld(selectedMap.getWorld());

        if (world == null) {
            return null;
        }

        JSONObject shopConfig = this.getPlugin().getShopConfig().getConfig();

        ArmorConfig armorConfig = new ArmorConfig(
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("enableArmorSystem", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyHelmet", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyChestplate", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optBoolean("copyLeggings", false),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultHelmet", 125),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultChestplate", 126),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultLeggings", 127),
                shopConfig.optJSONObject("itemShop", new JSONObject()).optJSONObject("armorConfig", new JSONObject()).optInt("defaultBoots", 128)
        );

        TeamUpgradesConfig teamUpgradesConfig = new TeamUpgradesConfig(
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("sharpness", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("protection", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("haste", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("generators", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("healpool", new JSONObject())),
                this.buildTeamUpgrade(shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optJSONObject("dragonbuff", new JSONObject())),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("noTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("alarmTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("itsATrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("miningFatigueTrap"),
                shopConfig.optJSONObject("teamUpgrades", new JSONObject()).optInt("countermeasuresTrap")
        );

        Game game = new Game(
                this.getPlugin(),
                world,
                selectedMap.getTeams(),
                selectedMap.getGlobalGenerators(),
                selectedMap.getGeneratorUpgradeTimeActions(),
                selectedMap.getDestroyBedsTimeActions(),
                selectedMap.getWorldBorderChangeTimeActions(),
                new JSONObject(shopConfig.optJSONObject("itemShop").toString()),
                armorConfig,
                teamUpgradesConfig,
                selectedMap.getRespawnCooldown(),
                selectedMap.getMaxTime(),
                selectedMap.getSpawnBlockPlaceProtection(),
                selectedMap.getVillagerBlockPlaceProtection(),
                selectedMap.getCenterLocation(),
                selectedMap.getMapRadius()
        );

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.getPlayers().get(playerId);
            Player player = this.getPlugin().getServer().getPlayer(playerId);

            if (player != null) {
                player.sendMessage("§bMap: " + this.selectedMap.getName());
            }

            if (playerData.getTeam() < 0) {
                continue;
            }

            game.addPlayer(playerId, playerData.getTeam());
            this.players.remove(playerId);
        }

        for (UUID playerId : this.getPlayers().keySet()) {
            List<BedwarsTeam> teams = new ArrayList<>(game.getTeams());

            if (teams.isEmpty()) {
                continue;
            }

            teams.sort(Comparator.comparingInt(o -> o.getPlayers().size()));

            BedwarsTeam lowestPlayerCount = teams.get(0);

            game.addPlayer(playerId, lowestPlayerCount.getId());
            this.players.remove(playerId);
        }

        return game;
    }

    private TeamUpgrade getErrorUpgrade() {
        return new TeamUpgrade(-1, List.of(), List.of(), List.of());
    }

    private TeamUpgrade buildTeamUpgrade(JSONObject teamUpgrade) {

        int itemId = teamUpgrade.optInt("item", -1);

        if (itemId < 0) {
            this.getPlugin().getLogger().warning("Shop Config: Missing/wrong item in team upgrade");
            return this.getErrorUpgrade();
        }

        JSONArray priceListArray = teamUpgrade.optJSONArray("prices");

        if (priceListArray == null) {
            this.getPlugin().getLogger().warning("Shop Config: Missing/Wrong prices in team upgrade");
            return this.getErrorUpgrade();
        }

        List<Integer> prices = new ArrayList<>();
        List<Material> currencies = new ArrayList<>();

        for (int i = 0; i < priceListArray.length(); i++) {

            int price = priceListArray.optInt(i, -1);

            if (price < 0) {
                this.getPlugin().getLogger().warning("Shop Config: Wrong price in prices in team upgrade");
                return this.getErrorUpgrade();
            }

            prices.add(price);
            currencies.add(Material.DIAMOND);

        }

        JSONArray levelArray = teamUpgrade.optJSONArray("levels");

        List<Integer> levels = new ArrayList<>();

        if (levelArray == null) {

            for (int i = 0; i < prices.size(); i++) {
                levels.add(-1);
            }

        } else {

            for (int i = 0; i < levelArray.length(); i++) {

                int level = levelArray.optInt(i);

                if (level < 0) {
                    level = -1;
                }

                levels.add(level);

            }

        }

        return new TeamUpgrade(itemId, List.copyOf(prices), List.copyOf(currencies), List.copyOf(levels));
    }

    private void updateCloudNetMotdAndSlots(int maxPlayers, String motd) {

        if (this.getPlugin().getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("cloudnet", false)) {

            try {

                try {
                    Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
                    Class.forName("eu.cloudnetservice.modules.bridge.BridgeServiceHelper");

                    BridgeServiceHelper bridgeServiceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);

                    if (bridgeServiceHelper != null) {

                        if (maxPlayers > 0) {
                            bridgeServiceHelper.maxPlayers().set(maxPlayers);
                        }

                        if (motd != null) {
                            bridgeServiceHelper.motd().set(motd);
                        }

                    }
                } catch (ClassNotFoundException ignored) {
                    // ignored (cloudnet not installed)
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public int getMaxPlayers() {

        int teams = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("teamCount", -1);
        int playersPerTeam = this.getPlugin().getConfigManager().getConfig().optJSONObject("slotSystem", new JSONObject()).optInt("playersPerTeam", -1);

        if (teams <= 0 || playersPerTeam <= 0) {
            return -1;
        }

        return teams * playersPerTeam;
    }

    public List<UUID> getTeamPlayers(int teamId) {
        List<UUID> teamPlayers = new ArrayList<>();

        for (UUID playerId : this.getPlayers().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (teamId == playerData.getTeam()) {
                teamPlayers.add(playerId);
            }

        }

        return List.copyOf(teamPlayers);
    }

    public void forcestart() {
        this.forcestart = true;
    }

    public Map<UUID, LobbyPlayerData> getPlayers() {
        return Map.copyOf(this.players);
    }

    public boolean addPlayer(UUID playerId) {
        if (this.players.containsKey(playerId)) {
            return false;
        }

        this.players.put(playerId, new LobbyPlayerData());
        return true;
    }

    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public void addLoginBypassPlayer(UUID playerId) {
        this.loginBypassList.add(playerId);
    }

    public boolean removeLoginBypassPlayer(UUID playerId) {
        return this.loginBypassList.remove(playerId);
    }

    public List<UUID> getLoginBypassList() {
        return List.copyOf(this.loginBypassList);
    }

    public void clearLoginBypassList() {
        this.loginBypassList.clear();
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }

    public MapData getSelectedMap() {
        return this.selectedMap;
    }

    public void selectMap(MapData selectedMap) {
        this.selectedMap = selectedMap;
        this.displayMap();
        this.createPartyTeams();
    }

    public boolean isMapVoting() {
        return this.mapVoting;
    }

    public void setMapVoting(boolean mapVoting) {
        this.mapVoting = mapVoting;
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    public void setRequiredPlayers(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    public boolean isTimerPaused() {
        return timerPaused;
    }

    public void setTimerPaused(boolean timerPaused) {
        this.timerPaused = timerPaused;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMapButtonItemId() {
        return this.mapButtonItemId;
    }

    public int getMapVoteButtonItemId() {
        return this.mapVoteButtonItemId;
    }

    public int getTeamSelectionButtonItemId() {
        return teamSelectionButtonItemId;
    }

    public Location getLobbySpawn() {
        return this.lobbySpawn.clone();
    }
}
